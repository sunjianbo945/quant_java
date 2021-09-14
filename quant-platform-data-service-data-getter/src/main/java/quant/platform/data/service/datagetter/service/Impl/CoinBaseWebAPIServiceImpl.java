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
import quant.platform.data.service.common.domain.datagetter.CoinBaseHistoricalRates;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.datagetter.dao.IDBDao;


import java.io.File;
import java.util.*;

@Component
public class CoinBaseWebAPIServiceImpl extends WebAPIService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpEntity httpEntity;

    @Autowired
    @Qualifier("DefaultTimeConverter")
    private TimeHelpers defaultTimeHelpers;

    @Value("${url.coinbase}")
    private String coinBaseUrl;
    @Autowired
    @Qualifier("GMT")
    private TimeZone gmtTimeZone;

    @Value("${output-file.path.coinbase}")
    private String rootPath;

    @Autowired
    public CoinBaseWebAPIServiceImpl(@Qualifier("GMTTimeConverter") TimeHelpers timeHelpers, IDBDao dao) {
        super(timeHelpers, dao);
    }

    @Override
    public String getWebExchangeName() {
        return DataServiceConstants.EXCHANGE_NAME_COIN_BASE;
    }

    @Override
    public String LoadHourlyCryptocurrencyDataByMinIntoFile(String currency, String pricingCurrency, String dateTime, int interval) {
        List<CoinBaseHistoricalRates> result = new ArrayList<>();
        String path = null;

        try {
            Date gmtStartTime = timeHelpers.parse(dateTime,gmtTimeZone);
            Date gmtStartOneHourLater = timeHelpers.nextTime(gmtStartTime,TimeZone.getTimeZone("GMT"),Calendar.HOUR,1);

            String url = String.format(coinBaseUrl, currency, pricingCurrency,
                    timeHelpers.convertLongToGMTDateString(gmtStartTime.getTime()),
                    timeHelpers.convertLongToGMTDateString(gmtStartOneHourLater.getTime()));

            int tryTime = 0;
            List<CoinBaseHistoricalRates> response = null;

            do {
                Thread.sleep(1000);
                if(tryTime>0){
                    System.out.println(String.format("Trying url %s %s more time",url,tryTime));
                }
                response = getCoinBaseHistoricalRatesResponse(url);
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

    @Override
    public String loadDailyCryptocurrencyDataByMinIntoFile(String currency, String pricingCurrency, String date,
                                                           int interval){

        List<CoinBaseHistoricalRates> result = new ArrayList<>();
        String path = null;
        try {
            Date curr = defaultTimeHelpers.parse(date,gmtTimeZone);

            Date end = TimeHelpers.nextTime(curr,TimeZone.getTimeZone("GMT"),Calendar.DAY_OF_MONTH,1);

            Date next = TimeHelpers.nextTime(curr,TimeZone.getTimeZone("GMT"),Calendar.HOUR,5);

            System.out.println(String.format("getDailyCryptocurrencyDataByMin -> Loading ticker = %s," +
                    " pricing currency = %s , on date = %s" ,currency,pricingCurrency,date));
            do{
                String url = String.format(coinBaseUrl, currency, pricingCurrency,
                        timeHelpers.convertLongToGMTDateString(curr.getTime()),
                        timeHelpers.convertLongToGMTDateString(next.getTime()));

                int tryTime = 0;
                List<CoinBaseHistoricalRates> response = null;

                do {
                    Thread.sleep(1000);
                    if(tryTime>0){
                        System.out.println(String.format("Trying url %s %s more time",url,tryTime));
                    }
                    response = getCoinBaseHistoricalRatesResponse(url);
                    tryTime++;
                }while(response==null && tryTime<10);

                if(response==null){
                    System.out.println(String.format("url : %s does not work any more",url));
                }else {
                    result.addAll(response);
                }
                curr = next;
                next =  TimeHelpers.nextTime(curr,TimeZone.getTimeZone("GMT"),Calendar.HOUR,5);
                next = next.before(end)?next:end;

            }while (curr.before(end));

            path = write(result, currency, pricingCurrency, date, interval);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return path;
    }

    public List<CoinBaseHistoricalRates> getCoinBaseHistoricalRatesResponse(String url)  {

        List<CoinBaseHistoricalRates> result = new ArrayList<>();

        HttpEntity<String> jsonString = null;

        JSONArray itemSubArray=null;
        try{
            System.out.println("running url : "+url);
            jsonString = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

            // jsonString is a string variable that holds the JSON
            JSONArray itemArray = new JSONArray(jsonString.getBody());
            for (int i = 0; i < itemArray.length(); i++) {
                itemSubArray = itemArray.getJSONArray(i);
                if (itemSubArray.length() != 6) {
                    throw new Exception("retrived data has issue : " + itemSubArray);
                }
                CoinBaseHistoricalRates rate = new CoinBaseHistoricalRates();

                rate.setTime(itemSubArray.getLong(0));
                rate.setLow(itemSubArray.getDouble(1));
                rate.setHigh(itemSubArray.getDouble(2));
                rate.setOpen(itemSubArray.getDouble(3));
                rate.setClose(itemSubArray.getDouble(4));
                rate.setVolume(itemSubArray.getDouble(5));
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


    public String write(List<CoinBaseHistoricalRates> content, String currency, String pricingCurrency, String dateTime, int interval) throws Exception {

        if(content==null || content.size()==0){
            System.out.println(String.format("CoinBaseFileWriterImpl -> " +
                    "ticker = %s, pricing currency =%s, " +
                    "date on %s -> No content retrieved from the web" ,currency , pricingCurrency , dateTime));
            return null;
        }

        File output = null;

        try{
            output = getOutputFile(rootPath,dateTime,currency,pricingCurrency,interval);

            String header = "time_stamp,low,high,open,close,trade_volume\n";

            this.writeToFile(content,output,header);

        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }

        System.out.println(String.format("CoinBaseFileWriterImpl -> " +
                "ticker = %s, pricing currency =%s, " +
                "date on %s -> writing has completed" ,currency , pricingCurrency , dateTime));

        return output.getAbsolutePath();
    }


}
