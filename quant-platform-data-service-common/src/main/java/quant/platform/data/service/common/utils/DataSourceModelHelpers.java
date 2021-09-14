package quant.platform.data.service.common.utils;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.domain.HistoricalDataModel;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

public class DataSourceModelHelpers {
    private static final String getIntervalSQL = "SELECT interval FROM public.tbl_data_source WHERE exchange_name=? " +
            "AND ticker=? AND pricing_currency=?";

    private static final String getAllDataSourcesFromExchangeSQL =
            "SELECT * FROM public.tbl_data_source WHERE exchange_name=?";

    public static int getInterval(
            JdbcTemplate jdbcTemplate, String exchangeName, String ticker, String pricingCurrency
    ){
        try{
            return jdbcTemplate.queryForObject(
                    getIntervalSQL,
                    new Object[]{exchangeName, ticker, pricingCurrency},
                    Integer.class
            );
        } catch (EmptyResultDataAccessException e) {
            System.out.print(
                    "Invalid combination of [exchange_name, ticker, pricing_currency]: [" + exchangeName
                    + "," + ticker + "," + pricingCurrency + "]"
            );
            throw e;
        }
    }

    public static int getInterval(
            DataSource dataSource, String exchangeName, String ticker, String pricingCurrency
    ){
        return getInterval(new JdbcTemplate(dataSource), exchangeName, ticker, pricingCurrency);
    }

    public static List<DataSourceModel> getAllDataSourcesFromExchange(
            JdbcTemplate jdbcTemplate, String exchangeName
    ){
        return jdbcTemplate.query(
                getAllDataSourcesFromExchangeSQL,
                new Object[]{exchangeName},
                new BeanPropertyRowMapper<DataSourceModel>(DataSourceModel.class)
        );
    }
}
