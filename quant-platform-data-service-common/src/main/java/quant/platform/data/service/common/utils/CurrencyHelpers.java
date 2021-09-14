package quant.platform.data.service.common.utils;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class CurrencyHelpers {
    private static final String getCurrencyIdFromTickerSQL = "SELECT currency_id FROM public.tbl_currency WHERE ticker=?";

    public static int getCurrencyIdFromTicker(DataSource ds, String name){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        return getCurrencyIdFromTicker(jdbcTemplate, name);
    }

    public static int getCurrencyIdFromTicker(JdbcTemplate jdbcTemplate, String name){
        try{
            return jdbcTemplate.queryForObject(getCurrencyIdFromTickerSQL, new Object[]{name}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            System.out.print("Invalid currency ticker: " + name);
            throw e;
        }
    }
}
