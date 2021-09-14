package quant.platform.data.service.common.utils;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DataSourceModelHelpersTest {
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
    public void intervalGetTest(){
        int interval = DataSourceModelHelpers.getInterval(
                this.getDataSource(), "coinbase", "btc", "usd"
        );
        assertThat(interval).isEqualTo(60);

        assertThatThrownBy(() -> {
            DataSourceModelHelpers.getInterval(
                    this.getDataSource(),"wholefoods", "milk", "egg"
            );})
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
