package quant.platform.data.service.common.domain;

import org.junit.Test;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public class ExchangeModelTest {
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
    public void getExchangeNameTest(){
        ExchangeModel validExchange = new ExchangeModel(1,"coinbase");
        ExchangeModel invalidExchange = new ExchangeModel(2,"wholefoods");
        String sql = "SELECT * FROM public.tbl_exchange";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());

        List<ExchangeModel> exchanges = jdbcTemplate.query(sql, new BeanPropertyRowMapper<ExchangeModel>(ExchangeModel.class));

        assertThat(exchanges.contains(validExchange)).isTrue();
        assertThat(exchanges.contains(invalidExchange)).isFalse();
    }
}
