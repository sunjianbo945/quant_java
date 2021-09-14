package quant.platform.data.service.datagetter.service.Impl;

import org.codehaus.jettison.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import quant.platform.data.service.common.constant.DataServiceConstants;
import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.domain.datagetter.BinanceHistoricalRates;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.datagetter.dao.IDBDao;

import java.io.File;
import java.util.*;

@Component
public class BinanceWebAPIServiceImpl extends WebAPIService {
//reference : https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md
//section : Kline/Candlestick data
//    [
//            1499040000000,      // Open time
//            "0.01634790",       // Open
//            "0.80000000",       // High
//            "0.01575800",       // Low
//            "0.01577100",       // Close
//            "148976.11427815",  // Volume
//            1499644799999,      // Close time
//            "2434.19055334",    // Quote asset volume
//            308,                // Number of trades
//            "1756.87402397",    // Taker buy base asset volume
//            "28.46694368",      // Taker buy quote asset volume
//            "17928899.62484339" // Ignore
//    ]

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpEntity httpEntity;

    @Value("${url.binance}")
    private String binanceUrl;
    @Autowired
    @Qualifier("GMT")
    private TimeZone gmtTimeZone;

    @Autowired
    @Qualifier("DefaultTimeConverter")
    private TimeHelpers defaultTimeHelpers;

    @Value("${output-file.path.binance}")
    private String rootPath;


    @Autowired
    public BinanceWebAPIServiceImpl(@Qualifier("GMTTimeConverter")TimeHelpers timeHelpers, IDBDao dao) {
        super(timeHelpers, dao);
    }

    @Override
    public String getWebExchangeName() {
        return DataServiceConstants.EXCHANGE_NAME_BINANCE;
    }

    @Override
    public String loadDailyCryptocurrencyDataByMinIntoFile(String currency, String pricingCurrency, String date, int interval) {

        List<BinanceHistoricalRates> result = new ArrayList<>();
        String path = null;
        try {
            Date curr = defaultTimeHelpers.parse(date,gmtTimeZone);

            Date end = timeHelpers.nextTime(curr,TimeZone.getTimeZone("GMT"), Calendar.DAY_OF_MONTH,1);

            end = timeHelpers.nextTime(end,TimeZone.getTimeZone("GMT"), Calendar.MINUTE,-1);

            List<BinanceHistoricalRates> response = null;

            System.out.println(String.format("getDailyCryptocurrencyDataByMin -> Loading ticker = %s," +
                            " pricing currency = %s , on date = %s",
                    currency.toUpperCase(),pricingCurrency.toUpperCase(),date));
            while(curr.before(end)) {

                int tryTime = 0;

                //12 hours later -1 mins later
                Date next = timeHelpers.nextTime(curr,TimeZone.getTimeZone("GMT"), Calendar.HOUR,12);
                next = timeHelpers.nextTime(next,TimeZone.getTimeZone("GMT"), Calendar.MINUTE,-1);

                String url = String.format(binanceUrl, currency.toUpperCase(), pricingCurrency.toUpperCase(),
                        curr.getTime(),
                        next.getTime());

                do {
                    Thread.sleep(1000);
                    if (tryTime > 0) {
                        System.out.println(String.format("Trying url %s %s more time", url, tryTime));
                    }
                    response = getBinanceHistoricalRatesResponse(url);
                    tryTime++;
                } while (response == null && tryTime < 10);

                if (response == null) {
                    System.out.println(String.format("url : %s does not work any more", url));
                } else {
                    result.addAll(response);
                }

                curr = timeHelpers.nextTime(curr,TimeZone.getTimeZone("GMT"), Calendar.HOUR,12);
            }
            path = write(result, currency, pricingCurrency, date, interval);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return path;

    }

    private String write(List<BinanceHistoricalRates> content, String currency, String pricingCurrency, String dateTime,
                         int interval) throws Exception {

        if(content==null || content.size()==0){
            System.out.println(String.format("BinanceFileWriterImpl -> " +
                    "ticker = %s, pricing currency =%s, " +
                    "date on %s -> Nothing to write" ,currency , pricingCurrency , dateTime));
            return null;
        }
        File output = null;

        try{
            output = getOutputFile(rootPath,dateTime,currency,pricingCurrency,interval);

            String header = "open_time,open,high,low,close,volume,close_time,quote_asset_volume,number_of_trades," +
                    "taker_buy_base_asset_volume,taker_buy_quote_asset_volume,ignore\n";

            this.writeToFile(content,output,header);

        }catch(Exception e){
            e.printStackTrace();
        }


        System.out.println(String.format("BinanceFileWriterImpl -> " +
                "ticker = %s, pricing currency =%s, " +
                "date on %s -> writing has completed" ,currency , pricingCurrency , dateTime));

        return output.getAbsolutePath();

    }

    /**
     *
     * @param url
     * @return
     */
    private List<BinanceHistoricalRates> getBinanceHistoricalRatesResponse(String url) {

        List<BinanceHistoricalRates> result = new ArrayList<>();

        HttpEntity<String> jsonString = null;

        JSONArray itemSubArray=null;
        try{
            System.out.println("running url : "+ url);
            jsonString = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            // jsonString is a string variable that holds the JSON
            JSONArray itemArray = new JSONArray(jsonString.getBody());
            for (int i = 0; i < itemArray.length(); i++) {
                itemSubArray = itemArray.getJSONArray(i);
                if (itemSubArray.length() != 12) {
                    throw new Exception("retrived data has issue : " + itemSubArray);
                }
                BinanceHistoricalRates rate = new BinanceHistoricalRates();

                rate.setOpenTime(itemSubArray.getLong(0));
                rate.setOpen(itemSubArray.getDouble(1));
                rate.setHigh(itemSubArray.getDouble(2));
                rate.setLow(itemSubArray.getDouble(3));
                rate.setClose(itemSubArray.getDouble(4));
                rate.setVolume(itemSubArray.getDouble(5));
                rate.setCloseTime(itemSubArray.getLong(6));
                rate.setQuoteAssetVolume(itemSubArray.getDouble(7));
                rate.setNumberOfTrades(itemSubArray.getInt(8));
                rate.setTakerBuyBaseAssetVolume(itemSubArray.getDouble(9));
                rate.setTakerBuyQuoteAssetVolume(itemSubArray.getDouble(10));
                rate.setIgnore(itemSubArray.getDouble(11));
                //System.out.println(rate.toString());
                result.add(rate);
            }
        }catch (Exception e) {
            System.out.println(String.format("exception url : %s",url));
            if(jsonString!=null)
                System.out.println("response is " + jsonString.getBody());
            System.out.println("Problem item sub array + " + itemSubArray);
            e.printStackTrace();
            return null;
        }
        return result;

    }

    @Override
    public String LoadHourlyCryptocurrencyDataByMinIntoFile(String currency, String pricingCurrency, String dateTime, int interval) {
        List<BinanceHistoricalRates> result = new ArrayList<>();
        String path = null;

        try {
            Date gmtStartTime = timeHelpers.parse(dateTime,gmtTimeZone);
            Date gmtStartOneHourLater = timeHelpers.nextTime(gmtStartTime,TimeZone.getTimeZone("GMT"), Calendar.MINUTE,59);

            String url = String.format(binanceUrl, currency.toUpperCase(), pricingCurrency.toUpperCase(),
                    gmtStartTime.getTime(),
                    gmtStartOneHourLater.getTime());

            int tryTime = 0;
            List<BinanceHistoricalRates> response = null;

            do {
                Thread.sleep(1000);
                if(tryTime>0){
                    System.out.println(String.format("Trying url %s %s more time",url,tryTime));
                }
                response = getBinanceHistoricalRatesResponse(url);
                tryTime++;
            }while(response==null && tryTime<10);

            if(response==null){
                System.out.println(String.format("url : %s does not work any more",url));
            }else {
                path = write(response,currency,pricingCurrency,dateTime,interval);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }


}
