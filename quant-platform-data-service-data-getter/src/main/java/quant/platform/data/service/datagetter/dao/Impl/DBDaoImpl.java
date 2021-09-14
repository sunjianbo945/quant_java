package quant.platform.data.service.datagetter.dao.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.datagetter.dao.IDBDao;

import java.util.ArrayList;
import java.util.List;

@Component
public class DBDaoImpl implements IDBDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<DataSourceModel> getTblDataSource() {
        List<DataSourceModel> ret = new ArrayList<>();

        String sql = "select distinct exchange_name, ticker, pricing_currency, interval from tbl_data_source;";

        ret= jdbcTemplate.query(sql, new BeanPropertyRowMapper<DataSourceModel>(DataSourceModel.class));

        return ret;
    }

    @Override
    public List<DataSourceModel> getExchangeTradePairs(String exchangeName) {
        List<DataSourceModel> ret = new ArrayList<>();

        String sql = "select distinct exchange_name, ticker, pricing_currency, interval " +
                "from tbl_data_source where exchange_name = ? ;";

        ret= jdbcTemplate.query(sql, new Object[]{exchangeName},
                new BeanPropertyRowMapper<DataSourceModel>(DataSourceModel.class));

        return ret;
    }

}
