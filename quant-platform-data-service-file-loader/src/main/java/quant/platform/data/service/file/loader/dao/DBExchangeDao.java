package quant.platform.data.service.file.loader.dao;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.utils.TimeHelpers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public abstract class DBExchangeDao {

    public TimeHelpers gmtTimeHelper;
    public TimeHelpers defaultTimeHelper;


    public abstract String getExchangeName();

    public abstract JdbcTemplate getJdbcTemplate();

    public abstract long loadFile(String filePath);

    public abstract int delete(long start,long end, String ticker, String pricingCurrency);

    public DBExchangeDao(TimeHelpers defaultTimeHelper, TimeHelpers gmtTimeHelper){
        this.gmtTimeHelper = gmtTimeHelper;
        this.defaultTimeHelper = defaultTimeHelper;
    }

    public int deleteDaily(String date, String ticker, String pricingCurrency){
        try {
            Date startOfDate = defaultTimeHelper.parse(date, TimeZone.getTimeZone("GMT"));
            Date endOfDate = TimeHelpers.nextTime(startOfDate, TimeZone.getTimeZone("GMT"),Calendar.DAY_OF_MONTH, 1);
            endOfDate = TimeHelpers.nextTime(endOfDate, TimeZone.getTimeZone("GMT"),Calendar.MINUTE, -1);

            this.delete(startOfDate.getTime(),endOfDate.getTime(),ticker,pricingCurrency);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int deleteHourly(String dateTime, String ticker, String pricingCurrency){

        try {
            Date startOfDate = gmtTimeHelper.parse(dateTime,TimeZone.getTimeZone("GMT"));
            Date endOfDate = TimeHelpers.nextTime(startOfDate, TimeZone.getTimeZone("GMT"), Calendar.HOUR, 1);
            endOfDate = TimeHelpers.nextTime(endOfDate, TimeZone.getTimeZone("GMT"), Calendar.SECOND, -1);
            this.delete(startOfDate.getTime(),endOfDate.getTime(),ticker,pricingCurrency);

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;

    }

    public List<DataSourceModel> getDataSourceModel(String exchangeName) {

        String sql = "select exchange_name, ticker, pricing_currency, interval from tbl_data_source " +
                " where exchange_name = ? ;";

        List<DataSourceModel> ret =getJdbcTemplate().query(sql,new Object[]{exchangeName},
                new BeanPropertyRowMapper<DataSourceModel>(DataSourceModel.class));

        return ret;
    }

}
