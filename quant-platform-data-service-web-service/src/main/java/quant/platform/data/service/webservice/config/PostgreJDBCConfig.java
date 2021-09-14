package quant.platform.data.service.webservice.config;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PostgreJDBCConfig {
    @Value("${postgre.datasource.serverName}")
    private String serverName;

    @Value("${postgre.datasource.databaseName}")
    private String databaseName;

    @Value("${postgre.datasource.portNumber}")
    private int portNumber;

    @Value("${postgre.datasource.username}")
    private String username;

    @Value("${postgre.datasource.password}")
    private String password;

    @Bean("postgreDataSource")
    public DataSource getPostgreDataSource(){
        PGPoolingDataSource dataSource = new PGPoolingDataSource();
        dataSource.setServerName(serverName);
        dataSource.setDatabaseName(databaseName);
        dataSource.setPortNumber(portNumber);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean("postgreJdbcTemplate")
    public JdbcTemplate getPostgreJdbcTemplate(@Qualifier("postgreDataSource") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
