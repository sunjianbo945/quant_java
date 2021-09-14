package quant.platform.data.service.webservice.data;

import quant.platform.data.service.common.domain.HistoricalDataModel;

import java.util.List;
import java.util.Optional;

public interface IDataDao {
    public List<HistoricalDataModel> getDataForSingleTickerInSpecificExchange(
            String exchange, String ticker, String priceCurrency, long start, long end
    );

    public int getRawDataInterval(String exchange, String ticker, String priceCurrency);
}
