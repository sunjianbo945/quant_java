package quant.platform.data.service.common.utils;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class ExchangeHelpersTest {
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
    public void exchangeIdGetTest(){
        int id = ExchangeHelpers.getExchangeIdFromExchangeName(this.getDataSource(),"coinbase");
        assertThat(id).isEqualTo(1);

        assertThatThrownBy(() -> {ExchangeHelpers.getExchangeIdFromExchangeName(this.getDataSource(),"wholefoods");})
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
