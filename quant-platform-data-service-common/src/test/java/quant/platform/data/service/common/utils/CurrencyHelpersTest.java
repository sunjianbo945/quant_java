package quant.platform.data.service.common.utils;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CurrencyHelpersTest {
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
        int id = CurrencyHelpers.getCurrencyIdFromTicker(this.getDataSource(),"usd");
        assertThat(id).isEqualTo(1);

        id = CurrencyHelpers.getCurrencyIdFromTicker(this.getDataSource(),"btc");
        assertThat(id).isEqualTo(1001);

        assertThatThrownBy(() -> {CurrencyHelpers.getCurrencyIdFromTicker(this.getDataSource(),"abc");}).isInstanceOf(EmptyResultDataAccessException.class);
    }
}
