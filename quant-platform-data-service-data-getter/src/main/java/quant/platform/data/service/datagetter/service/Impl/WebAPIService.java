package quant.platform.data.service.datagetter.service.Impl;

import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.domain.datagetter.IHistoricalRates;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.datagetter.dao.IDBDao;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public abstract class WebAPIService {

    public TimeHelpers timeHelpers;
    public IDBDao dao;

    public WebAPIService(TimeHelpers timeHelpers,IDBDao dao){
        this.timeHelpers = timeHelpers;
        this.dao=dao;
    }


    public abstract String getWebExchangeName();
    /***
     *
     * @param currency
     * @param pricingCurrency
     * @param date
     * @return
     */
    public abstract String loadDailyCryptocurrencyDataByMinIntoFile(String currency, String pricingCurrency, String date, int interval);

    /***
     *
     * @param date
     * @return
     */
    public List<String> loadDailyAllCryptocurrencyDataByMinIntoFile(String date){

        List<String> ret = new ArrayList<>();
        List<DataSourceModel> pairs = dao.getExchangeTradePairs(this.getWebExchangeName());
        if(pairs!=null && pairs.size()>0){
            for(DataSourceModel p : pairs){
                String path = loadDailyCryptocurrencyDataByMinIntoFile(p.getTicker(),p.getPricingCurrency(),date,
                        p.getInterval());
                if(path!=null)
                    ret.add(path);
            }
        }
        return ret;
    }

    /***
     *
     * @param hours
     * @return
     */
    public List<String> LoadHourlyAllCryptocurrencyDataByIntoFile(int hours){

        List<String> ret = new ArrayList<>();
        List<DataSourceModel> pairs = dao.getExchangeTradePairs(this.getWebExchangeName());
        if(pairs!=null && pairs.size()>0){
            for(DataSourceModel p : pairs){
                try{
                    //get required time information
                    Date now = new Date();
                    Date hoursAgo = TimeHelpers.nextTime(now, TimeZone.getTimeZone("GMT"),Calendar.HOUR,-1*hours);
                    long startTime = TimeHelpers.floorDateTimeToHourLong(hoursAgo.getTime());
                    Date start = new Date(startTime);
                    Date end = TimeHelpers.nextTime(now,TimeZone.getTimeZone("GMT"), Calendar.HOUR,-1);

                    System.out.println(String.format("Now local time %s, GMT is %s, Start to run time from %s to %s",
                            timeHelpers.convertLongToLocalTimeDateString(now.getTime()),
                            timeHelpers.convertLongToGMTDateString(now.getTime()),
                            timeHelpers.convertLongToGMTDateString(start.getTime()),
                            timeHelpers.convertLongToGMTDateString(TimeHelpers.floorDateTimeToHourLong(end.getTime()))
                            ));
                    while(start.before(end)){
                        String path = LoadHourlyCryptocurrencyDataByMinIntoFile(p.getTicker(),
                                p.getPricingCurrency(),
                                timeHelpers.convertLongToGMTDateString(start.getTime()),
                                p.getInterval());
                        if(path!=null)
                            ret.add(path);
                        start = TimeHelpers.nextTime(start,TimeZone.getTimeZone("GMT"), Calendar.HOUR,1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return ret;

    }

    /***
     *
     * @param currency
     * @param pricingCurrency
     * @param dateTime
     * @param interval
     * @return
     */
    public abstract String LoadHourlyCryptocurrencyDataByMinIntoFile(String currency, String pricingCurrency, String dateTime,
                                                                 int interval);

    public File getOutputFile(String root,String dateTime, String currency,String pricingCurrency,int interval) throws Exception {

        String path = null;
        TimeHelpers helper = new TimeHelpers();
        if(dateTime.contains("T")){
            // dateTime should look like 2018-01-01T00:00:00Z
            path = String.format(root,dateTime.split("T")[0]);
        }else if(helper.canParseString(dateTime)){
            // dateTime should look like 2018-01-01
            path = String.format(root,dateTime);
        }else{
            throw new Exception(String.format("The input datatime is %s cannot be used to create directory", dateTime));
        }

        File dir = new File(path);


        if(!dir.exists()){
            dir.mkdirs();
        }

        if(dateTime.contains("T")) {
            path = String.format("%s%s_%s_%s_%s_%s.csv", path, this.getWebExchangeName()
                    , currency, pricingCurrency, dateTime.replaceAll(":","-"), interval);
        }else{
            path = String.format("%s%s_%s_%s_%s_%s.csv", path, this.getWebExchangeName()
                    , currency, pricingCurrency, dateTime, interval);
        }
        File output = new File(path);

        output.createNewFile();

        return output;
    }

    public void writeToFile(List<? extends IHistoricalRates> content, File outputFile, String header) throws Exception {

        if(content==null || content.size()==0 || outputFile==null) {
            throw new Exception("writeToFile cannot write");
        }
        try{
            FileWriter writer = new FileWriter(outputFile);

            writer.write(header);
            writer.flush();

            Collections.sort(content);

            for (IHistoricalRates r : content) {
                writer.write(r.toCSVLine());
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }

    }

}
