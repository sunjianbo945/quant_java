package quant.platform.data.service.etl.processor.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    @Bean("postgreDataSource")
    @ConfigurationProperties(prefix = "spring.postgre.datasource")
    public DataSource getPostgreDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("postgreJdbcTemplate")
    public JdbcTemplate getPostgreJdbcTemplate(@Qualifier("postgreDataSource") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
