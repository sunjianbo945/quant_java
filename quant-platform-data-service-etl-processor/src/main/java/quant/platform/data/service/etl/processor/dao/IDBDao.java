package quant.platform.data.service.etl.processor.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import quant.platform.data.service.common.domain.DataSourceModel;

import java.util.List;

public interface IDBDao {

    List<DataSourceModel> getDataSourceModel(String exchangeName);
    JdbcTemplate getJdbcTemplate();
}
