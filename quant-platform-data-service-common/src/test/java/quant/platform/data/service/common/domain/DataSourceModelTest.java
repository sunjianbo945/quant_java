package quant.platform.data.service.common.domain;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DataSourceModelTest {
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
        String sql = "SELECT * FROM public.tbl_data_source";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());

        List<DataSourceModel> dataSourceList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<DataSourceModel>(DataSourceModel.class));

        DataSourceModel coinbaseBTCUSD60Model = new DataSourceModel();
        coinbaseBTCUSD60Model.setExchangeName("coinbase");
        coinbaseBTCUSD60Model.setTicker("btc");
        coinbaseBTCUSD60Model.setPricingCurrency("usd");
        coinbaseBTCUSD60Model.setInterval(60);

        assertThat(dataSourceList.contains(coinbaseBTCUSD60Model)).isTrue();
    }
}
