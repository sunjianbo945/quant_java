package quant.platform.data.service.common.domain;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CurrencyModelTest {
    private DataSource getDataSource(){
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("db_foundation_cryptocurrency");
        dataSource.setPortNumber(6666);
        dataSource.setUser("postgres");
        dataSource.setPassword("admin");
        return dataSource;
    }

    @Test
    public void getCurrencyNameTest(){
        CurrencyModel validCurrencyUSD = new CurrencyModel(1,"usd");
        CurrencyModel validCurrencyBTC = new CurrencyModel(1001,"btc");
        CurrencyModel invalidCurrencyABC = new CurrencyModel(3,"abc");
        String sql = "SELECT * FROM public.tbl_currency";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());

        List<CurrencyModel> currencies = jdbcTemplate.query(sql, new BeanPropertyRowMapper<CurrencyModel>(CurrencyModel.class));

        assertThat(currencies.contains(validCurrencyUSD)).isTrue();
        assertThat(currencies.contains(validCurrencyBTC)).isTrue();
        assertThat(currencies.contains(invalidCurrencyABC)).isFalse();
    }
}
