package quant.platform.data.service.webservice.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import quant.platform.data.service.common.utils.IntervalHelpers;
import quant.platform.data.service.webservice.service.IDataService;

import java.util.Optional;

@RestController
public class DataWebController {
    @Autowired
    private IDataService dataService;

    @RequestMapping(value = "/{exchange}/{ticker}/{priceCurrency}/*", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public String getDataForSingleTickerInSpecificExchange(
            @PathVariable String exchange,
            @PathVariable String ticker,
            @PathVariable String priceCurrency,
            @RequestParam("compact")    Optional<Boolean>   compact,
            @RequestParam("start")      Optional<Long>      start,
            @RequestParam("end")        Optional<Long>      end,
            @RequestParam("interval")   Optional<Integer>   interval
    ) throws JsonProcessingException {
        String json = dataService.getDataForSingleTickerInSpecificExchange(
                exchange, ticker, priceCurrency, getCompact(compact), getStart(start), getEnd(end), getInterval(interval)
        );

        System.out.printf("String size is: %d", json.length());
        return json;
    }

    /***
     * Get whether return compact format jsonArray or not. Default set to true to save traffic
     * @param compact
     * @return
     */
    private boolean getCompact(Optional<Boolean> compact){
        if(compact.isPresent()){
            return compact.get();
        }else{
            return true;
        }
    }

    /***
     * Get start time_stamp. Default set to 0
     * @param start
     * @return
     */
    private long getStart(Optional<Long> start){
        //Set min return value to 0000-01-01 00:00:00.000 GMT to avoid out of range issue
        //Set max return value to 3000-01-01 00:00:00.000 GMT to avoid out of range issue
        long fixed_start = -2208988800000L;
        if(start.isPresent()){
            fixed_start = Long.max(start.get(), -2208988800000L);
        }
        return Long.min(fixed_start, 32503680000000L);
    }

    /***
     * Get end time_stamp. Default set to Long.MAX_VALUE
     * @param end
     * @return
     */
    private long getEnd(Optional<Long> end){
        //Set min return value to 0000-01-01 00:00:00.000 GMT to avoid out of range issue
        //Set max return value to 3000-01-01 00:00:00.000 GMT to avoid out of range issue
        long fixed_end = 32503680000000L;
        if(end.isPresent()){
            fixed_end = Long.min(end.get(), 32503680000000L);
        }
        return Long.max(fixed_end, -2208988800000L);
    }

    /***
     * Get interval between time series ticks. Must be one in valid interval list. If not specified, set to be 5 minutes
     * @param interval
     * @return
     */
    private int getInterval(Optional<Integer> interval){
        if(interval.isPresent()){
            IntervalHelpers.validateInterval(interval.get());
            return interval.get();
        }else{
            return 600;
        }
    }
}
