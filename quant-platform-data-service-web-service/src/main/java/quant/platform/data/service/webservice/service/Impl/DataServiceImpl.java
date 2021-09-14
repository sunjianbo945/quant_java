package quant.platform.data.service.webservice.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quant.platform.data.service.common.domain.HistoricalDataModel;
import quant.platform.data.service.common.rollup.RollUpTimeSeriesIntervals;
import quant.platform.data.service.common.utils.DataSourceModelHelpers;
import quant.platform.data.service.webservice.data.IDataDao;
import quant.platform.data.service.webservice.service.IDataService;

import java.util.List;

@Service
public class DataServiceImpl implements IDataService {
    @Autowired
    private IDataDao dataDao;

    public String getDataForSingleTickerInSpecificExchange(
            String exchange, String ticker, String priceCurrency, boolean compact, long start, long end, int interval
    ) throws JsonProcessingException {
        //Get time series data between "start" (inclusive) and "end" (exclusive)
        List<HistoricalDataModel> results =
                dataDao.getDataForSingleTickerInSpecificExchange(exchange, ticker, priceCurrency, start, end);

        List<HistoricalDataModel> rollUpResults = this.rollUpTimeSeries(
                results, dataDao.getRawDataInterval(exchange, ticker, priceCurrency), interval
        );

        return assemblyJsonArray(rollUpResults, compact);
    }

    private String assemblyJsonArray(List<HistoricalDataModel> results, boolean compact) throws JsonProcessingException {
        String jsonArray = "";
        if(compact){
            //Return only numbers, no json headers (field names). ie [5,6,7,8,9,1]
            jsonArray = jsonArray.concat("[");
            long position = 0;
            for (HistoricalDataModel model: results){
                if(position++ != 0){
                    jsonArray = jsonArray.concat(",");
                }
                jsonArray = jsonArray.concat(model.compactJsonArray());
            }
            jsonArray = jsonArray.concat("]");
        }else{
            //Return json format data. ie {a: 5, b: 6, c:908}
            ObjectMapper objectMapper = new ObjectMapper();
            jsonArray = objectMapper.writeValueAsString(results);
        }
        return jsonArray;
    }

    private List<HistoricalDataModel> rollUpTimeSeries(
            List<HistoricalDataModel> rawTimeSeries, int rawDataInterval, int desiredInterval
    ){
        return RollUpTimeSeriesIntervals.rollUpToCertainInterval(rawTimeSeries,rawDataInterval,desiredInterval);
    }
}
