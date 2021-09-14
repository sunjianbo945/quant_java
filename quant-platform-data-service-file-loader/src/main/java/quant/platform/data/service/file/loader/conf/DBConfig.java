package quant.platform.data.service.file.loader.conf;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

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

    @Bean("postgreCopyManager")
    public CopyManager getPostgreCopyManager(@Qualifier("postgreDataSource") DataSource dataSource){
        try {
            return new CopyManager(dataSource.getConnection().unwrap(BaseConnection.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
