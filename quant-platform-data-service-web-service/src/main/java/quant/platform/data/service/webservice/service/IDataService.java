package quant.platform.data.service.webservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IDataService {
    public String getDataForSingleTickerInSpecificExchange(
            String exchange, String ticker, String priceCurrency, boolean compact, long start, long end, int interval
    ) throws JsonProcessingException;
}
