package quant.platform.data.service.file.loader.dao.Impl;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import quant.platform.data.service.common.constant.DataServiceConstants;
import quant.platform.data.service.common.utils.CurrencyHelpers;
import quant.platform.data.service.common.utils.ExchangeHelpers;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.file.loader.dao.DBExchangeDao;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Component("DBBinanceExchangeDaoImpl")
public class DBBinanceExchangeDaoImpl extends DBExchangeDao {

    @Autowired
    @Qualifier("postgreJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("postgreCopyManager")
    CopyManager copyManager;


    @Autowired
    public DBBinanceExchangeDaoImpl(@Qualifier("DefaultTimeConverter") TimeHelpers defaultTimeHelper,
                                    @Qualifier("GMTTimeConverter")TimeHelpers gmtTimeHelper) {
        super(defaultTimeHelper,gmtTimeHelper);
    }

    @Override
    public String getExchangeName(){
        return DataServiceConstants.EXCHANGE_NAME_BINANCE;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    @Override
    public long loadFile(String filePath){

        try{
            File f = new File(filePath);
            if(!f.exists()){
                System.out.println(String.format("file %s does not exist", filePath));
                return -1L;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(String.format("loading file %s", filePath));

        String sql = " COPY tbl_binance FROM  STDIN CSV HEADER NULL as 'NULL'";

        FileReader file = null;
        long ret = Long.MIN_VALUE;
        try {
            file = new FileReader(filePath);
            ret = copyManager.copyIn(sql,file);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public int delete(long start,long end, String ticker, String pricingCurrency) {
        int ret=0;
        try {
            int exchangeId = ExchangeHelpers.getExchangeIdFromExchangeName(jdbcTemplate, this.getExchangeName());
            int currencyId = CurrencyHelpers.getCurrencyIdFromTicker(jdbcTemplate, ticker);
            int priceCurrencyId = CurrencyHelpers.getCurrencyIdFromTicker(jdbcTemplate, pricingCurrency);


            String s = super.gmtTimeHelper.convertLongToGMTDateString(start);
            String e = super.gmtTimeHelper.convertLongToGMTDateString(end);
            String sql = "set timezone = 'GMT' ; " +
                    " delete from tbl_binance where time_stamp " +
                    " between cast(? as timestamp) and cast(? as timestamp) and exchange_id = ? " +
                    " and currency_id =? and pricing_currency_id = ? ;";

            ret = jdbcTemplate.update(sql,new Object[]{s,e,exchangeId,currencyId,priceCurrencyId});

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

        return ret;
    }




}
