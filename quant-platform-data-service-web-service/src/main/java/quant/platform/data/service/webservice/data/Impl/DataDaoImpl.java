package quant.platform.data.service.webservice.data.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.domain.HistoricalDataModel;
import quant.platform.data.service.common.utils.CurrencyHelpers;
import quant.platform.data.service.common.utils.DataSourceModelHelpers;
import quant.platform.data.service.common.utils.ExchangeHelpers;
import quant.platform.data.service.webservice.data.IDataDao;

import java.sql.Timestamp;
import java.util.List;

@Component
public class DataDaoImpl implements IDataDao {
    @Autowired
    @Qualifier("postgreJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Value("${sql.selectDataForSingleTickerInSpecificExchange}")
    private String selectDataForSingleTickerInSpecificExchangeSQL;

    @Override
    public List<HistoricalDataModel> getDataForSingleTickerInSpecificExchange(
            String exchange, String ticker, String priceCurrency, long start, long end
    ){
        // Get exchange/currency ids. These functions also validates that exchange or currency is valid
        int exchangeId = ExchangeHelpers.getExchangeIdFromExchangeName(jdbcTemplate, exchange);
        int currencyId = CurrencyHelpers.getCurrencyIdFromTicker(jdbcTemplate, ticker);
        int priceCurrencyId = CurrencyHelpers.getCurrencyIdFromTicker(jdbcTemplate, priceCurrency);

        //Already validated exchange name here
        String tableName = "tbl_" + exchange.toLowerCase();
        String sql = String.format(selectDataForSingleTickerInSpecificExchangeSQL, tableName);

        return jdbcTemplate.query(
                sql,
                new Object[]{exchangeId, currencyId, priceCurrencyId, new Timestamp(start), new Timestamp(end)},
                new BeanPropertyRowMapper<HistoricalDataModel>(HistoricalDataModel.class)
        );
    }

    @Override
    public int getRawDataInterval(String exchange, String ticker, String priceCurrency) {
        return DataSourceModelHelpers.getInterval(jdbcTemplate, exchange, ticker, priceCurrency);
    }
}
