package quant.platform.data.service.dbvalidator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.utils.CurrencyHelpers;
import quant.platform.data.service.common.utils.DataSourceModelHelpers;
import quant.platform.data.service.common.utils.ExchangeHelpers;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.dbvalidator.domian.ValidatorResultRow;
import quant.platform.data.service.dbvalidator.validator.IQuantPlatformDbValidator;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class CoinbaseValidator implements IQuantPlatformDbValidator {
    @Value("${sql.selectDataCount}")
    private String selectDataCountSQL;

    @Value("${sql.selectMaxTimeStamp}")
    private String selectMaxTimeStampSQL;

    @Value("${sql.selectMinTimeStamp}")
    private String selectMinTimeStampSQL;

    @Autowired
    @Qualifier("postgreJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Override
    public String getExchangeName() {
        return "coinbase";
    }

    @Override
    public List<ValidatorResultRow> validateDbData() {
        List<ValidatorResultRow> results = new ArrayList<>();
        String tableName = "tbl_" + this.getExchangeName();

        List<DataSourceModel> CryptoDataSources = DataSourceModelHelpers.getAllDataSourcesFromExchange(
                jdbcTemplate, this.getExchangeName()
        );
        int exchangeId = ExchangeHelpers.getExchangeIdFromExchangeName(jdbcTemplate, this.getExchangeName());

        for(DataSourceModel ds: CryptoDataSources) {
            ValidatorResultRow resultRow = new ValidatorResultRow();

            String ticker = ds.getTicker();
            String pricingCurrency = ds.getPricingCurrency();
            Integer interval = ds.getInterval();

            resultRow.setExchangeName(this.getExchangeName());
            resultRow.setTicker(ticker);
            resultRow.setPricingCurrency(pricingCurrency);
            resultRow.setInterval(ds.getInterval());

            int currencyId = CurrencyHelpers.getCurrencyIdFromTicker(jdbcTemplate, ticker);
            int pricingCurrencyId = CurrencyHelpers.getCurrencyIdFromTicker(jdbcTemplate, pricingCurrency);

            // Get data count of each datasource
            String dataCountSql = String.format(selectDataCountSQL, tableName);
            Integer dataCount = jdbcTemplate.queryForObject(
                    dataCountSql, new Object[]{exchangeId, currencyId, pricingCurrencyId}, Integer.class
            );
            resultRow.setDataCount(dataCount);

            // Get max time stamp of each datasource
            String maxTimeSql = String.format(selectMaxTimeStampSQL, tableName);
            Timestamp maxTimestamp = jdbcTemplate.queryForObject(
                    maxTimeSql, new Object[]{exchangeId, currencyId, pricingCurrencyId}, Timestamp.class
            );
            String maxGMTStr = TimeHelpers.getUTCTimeConverter().convertLongToGMTDateString(
                    maxTimestamp.getTime()
            );
            resultRow.setLatestTimeStamp(maxGMTStr);

            // Get min time stamp of each datasource
            String minTimeSql = String.format(selectMinTimeStampSQL, tableName);
            Timestamp minTimestamp = jdbcTemplate.queryForObject(
                    minTimeSql, new Object[]{exchangeId, currencyId, pricingCurrencyId}, Timestamp.class
            );
            String minGMTStr = TimeHelpers.getUTCTimeConverter().convertLongToGMTDateString(
                    minTimestamp.getTime()
            );
            resultRow.setEarlistTimeStamp(minGMTStr);

            // Up to date is default to current hour
            try {
                Date now = new Date();
                Date hoursAgo = TimeHelpers.getUTCTimeConverter().nextTime(now, Calendar.HOUR, -1);
                long lastDataBatchStart = TimeHelpers.floorDateTimeToHourLong(hoursAgo.getTime());
                if(lastDataBatchStart < maxTimestamp.getTime()){
                    resultRow.setUpToDate("<font style=\"color:green\">Yes</font>");
                } else{
                    resultRow.setUpToDate("<font style=\"color:red\">No</font>");
                }
            } catch (ParseException ex){
                resultRow.setUpToDate("<font style=\"color:yellow\">Time Cast Problem</font>");
            }

            // No missing for data in between
            Long expectedRows = (maxTimestamp.getTime() - minTimestamp.getTime())/((long) interval)/1000 + 1;
            if(expectedRows == (long) dataCount){
                resultRow.setNoMissing("<font style=\"color:green\">Yes</font>");
            } else {
                resultRow.setNoMissing("<font style=\"color:red\">No</font>");
            }

            results.add(resultRow);
        }
        return results;
    }
}
