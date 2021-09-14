package quant.platform.data.service.etl.processor.service.Impl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import quant.platform.data.service.common.constant.DataServiceConstants;
import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.domain.HistoricalDataModel;
import quant.platform.data.service.common.utils.CurrencyHelpers;
import quant.platform.data.service.common.utils.ExchangeHelpers;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.etl.processor.dao.IDBDao;
import quant.platform.data.service.etl.processor.service.WebApiFileProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Component
public class WebCoinBaseApiFileProcessorImpl extends WebApiFileProcessor {

    @Value("${file-path.coinbase.input-file}")
    private String inputFilePathRoot;

    @Value("${file-path.coinbase.output-file}")
    private String outputFilePathRoot;


    @Autowired
    public WebCoinBaseApiFileProcessorImpl(@Qualifier("DefaultTimeConverter")TimeHelpers defaultTimeHelper,
                                           @Qualifier("GMTTimeConverter") TimeHelpers gmtTimeHelper, IDBDao dao){
        super(defaultTimeHelper, gmtTimeHelper,dao);
    }

    @Override
    public String getDefaultInputFilePath(DataSourceModel request, String date) {
        return getDefaultFilePath(inputFilePathRoot,request,date);
    }

    @Override
    public String getDefaultOutputFilePath(DataSourceModel request, String date) {
        return getDefaultFilePath(outputFilePathRoot,request,date);
    }

    @Override
    public String getExchangeName() {
        return DataServiceConstants.EXCHANGE_NAME_COIN_BASE;
    }


    public List<HistoricalDataModel> loadFile(String filePath, DataSourceModel requestInfo) {

        if(requestInfo==null) {
            System.out.println("loading information is null");
            return null;
        }

        if(filePath==null){
            System.out.println("input file path is null");
            return null;
        }

        String ticker = requestInfo.getTicker();

        String pricingCurrency = requestInfo.getPricingCurrency();

        List<HistoricalDataModel> ret = new ArrayList<>();

        try{
            File f = new File(filePath);
            if(!f.exists()){
                System.out.println(String.format("file %s does not exist", filePath));
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        Set<Long> timeStampSet = new HashSet();
        try(
                Reader reader = Files.newBufferedReader(Paths.get(filePath));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader("time_stamp", "low", "high", "open","close","trade_volume")
                        .withSkipHeaderRecord()
                        .withIgnoreHeaderCase()
                        .withTrim());
        ){

            int exchangeId = ExchangeHelpers.getExchangeIdFromExchangeName(super.dao.getJdbcTemplate(),
                    this.getExchangeName());
            int currencyId = CurrencyHelpers.getCurrencyIdFromTicker(super.dao.getJdbcTemplate(), ticker);
            int priceCurrencyId = CurrencyHelpers.getCurrencyIdFromTicker(super.dao.getJdbcTemplate(), pricingCurrency);

            for (CSVRecord csvRecord : csvParser) {
                // Accessing values by the names assigned to each column
                long time = TimeHelpers.floorUnixLongToMinLong(Long.parseLong(csvRecord.get("time_stamp")));
                if(timeStampSet.contains(time)){
                    continue;
                }else{
                    timeStampSet.add(time);
                }

                double low = Double.parseDouble(csvRecord.get("low"));
                double high = Double.parseDouble(csvRecord.get("high"));
                double open = Double.parseDouble(csvRecord.get("open"));
                double close = Double.parseDouble(csvRecord.get("close"));
                double tradeVolume = Double.parseDouble(csvRecord.get("trade_volume"));

                HistoricalDataModel model = new HistoricalDataModel();
                model.setClose(close);
                model.setHigh(high);
                model.setLow(low);
                model.setOpen(open);
                model.setTimeStamp(new Timestamp(time*1000));
                model.setTradeVolume(tradeVolume);
                model.setExchangeId(exchangeId);
                model.setCurrencyId(currencyId);
                model.setPricingCurrencyId(priceCurrencyId);
                model.setBackFilled(0);
                ret.add(model);
            }
        } catch (Exception e) {
            System.out.println(requestInfo.toString() + " loading failed");
            e.printStackTrace();
        }

        return ret;
    }


}
