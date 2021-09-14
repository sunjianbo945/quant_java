package quant.platform.data.service.common.utils;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ExchangeHelpers {
    private static final String getCurrencyIdFromTickerSQL = "SELECT exchange_id FROM public.tbl_exchange WHERE exchange_name=?";

    public static int getExchangeIdFromExchangeName(DataSource ds, String name){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        return getExchangeIdFromExchangeName(jdbcTemplate, name);
    }

    public static int getExchangeIdFromExchangeName(JdbcTemplate jdbcTemplate, String name){
        try{
            return jdbcTemplate.queryForObject(getCurrencyIdFromTickerSQL, new Object[]{name}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            System.out.print("Invalid exchange name: " + name);
            //e.printStackTrace();
            throw e;
        }
    }
}
