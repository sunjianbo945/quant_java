package quant.platform.data.service.etl.processor.dao.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import quant.platform.data.service.common.domain.DataSourceModel;

import quant.platform.data.service.etl.processor.dao.IDBDao;

import java.util.List;

@Component
public class DBDaoImpl implements IDBDao {

    @Autowired
    @Qualifier("postgreJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate(){return jdbcTemplate;}

    @Override
    public List<DataSourceModel> getDataSourceModel(String exchangeName) {

        String sql = "select exchange_name, ticker, pricing_currency, interval from tbl_data_source where" +
                " exchange_name = ? ;";
        List<DataSourceModel> ret =jdbcTemplate.query(sql,new Object[]{exchangeName},
                new BeanPropertyRowMapper<DataSourceModel>(DataSourceModel.class));

        return ret;
    }
}
