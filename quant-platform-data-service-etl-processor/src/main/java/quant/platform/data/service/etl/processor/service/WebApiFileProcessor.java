package quant.platform.data.service.etl.processor.service;


import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.domain.HistoricalDataModel;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.etl.processor.dao.IDBDao;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public abstract class WebApiFileProcessor {

    public IDBDao dao;
    public TimeHelpers gmtTimeHelper;
    public TimeHelpers defaultTimeHelper;


    public abstract String getExchangeName();
    public abstract String getDefaultInputFilePath(DataSourceModel request,String date);
    public abstract String getDefaultOutputFilePath(DataSourceModel request,String date);
    public abstract List<HistoricalDataModel> loadFile(String inputFilePath, DataSourceModel requestInfo);



    public String getDefaultFilePath(String root,DataSourceModel request,String date){
        String path = null;
        if(date.contains("T")) {
            path = String.format(root, date.split("T")[0]) + String.format("%s_%s_%s_%s_%s.csv",
                    this.getExchangeName(), request.getTicker(),
                    request.getPricingCurrency(), date.replaceAll(":","-"), request.getInterval());
        }else{
            path = String.format(root, date) + String.format("%s_%s_%s_%s_%s.csv",
                    this.getExchangeName(), request.getTicker(),
                    request.getPricingCurrency(), date, request.getInterval());
        }
        return path;
    }


    public WebApiFileProcessor(TimeHelpers defaultTimeHelper , TimeHelpers gmtTimeHelper, IDBDao dao){
        this.gmtTimeHelper = gmtTimeHelper;
        this.defaultTimeHelper=defaultTimeHelper;
        this.dao = dao;
    }


    public List<String> transferAllCryptocurrencyDilyFile(String date, String inputFilePath, String outputFilePath){

        List<DataSourceModel> requests = dao.getDataSourceModel(this.getExchangeName());

        List<String> ret = new ArrayList<>();
        for(DataSourceModel request : requests){
            System.out.println(String.format("loading %s , %s, %s",request.getExchangeName(),
                    request.getTicker(),request.getPricingCurrency()));
            String output = transfer(request,date,inputFilePath,outputFilePath);
            System.out.println(String.format("finish %s , %s, %s, with output file path %s"
                    ,request.getExchangeName(), request.getTicker(),request.getPricingCurrency(),output));

            if(output!=null)
                ret.add(output);
        }
        return ret;
    }

    public String transfer(DataSourceModel request, String date, String inputFilePath, String outputFilePath) {
        if(request ==null){
            System.out.println(String.format("no request!"));
            return null;
        }
        //make sure we have input File path
        if(inputFilePath==null)
        {
            inputFilePath = this.getDefaultInputFilePath(request,date);
        }


        List<HistoricalDataModel> content = this.loadFile(inputFilePath, request);
        List<HistoricalDataModel> processData= null;
        try {
            processData = processData(content, date, request.getInterval());
        }catch (Exception e){
            System.out.println("Cannot process the input file : "+ inputFilePath);
            e.printStackTrace();
            return null;
        }

        //make sure we have the ouput File path
        if (outputFilePath == null) {
            outputFilePath = this.getDefaultOutputFilePath(request,date);
        }
        String outputfileLoc = write(processData, outputFilePath);
        return outputfileLoc;
    }

    public List<HistoricalDataModel> processData(List<HistoricalDataModel> content, String date, int interval) throws Exception {
        if(content==null||content.size()==0){
            return null;
        }
        long startPoint =0L;

        int totalFileMinutePointsSize = 0;

        try {
            if(date.contains("T")){
                startPoint = gmtTimeHelper.convertStringToLongGMT(date);
                totalFileMinutePointsSize = 60*60/interval;
            }else{
                startPoint = defaultTimeHelper.convertStringToLongGMT(date);
                totalFileMinutePointsSize = 24*60*60/interval;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Collections.sort(content, new Comparator<HistoricalDataModel>() {
            @Override
            public int compare(HistoricalDataModel o1, HistoricalDataModel o2) {
                if(o1.getTimeStamp().getTime()==o2.getTimeStamp().getTime()){
                    return 0;
                }
                return o1.getTimeStamp().getTime()>o2.getTimeStamp().getTime()?1:-1;
            }
        });
        List<HistoricalDataModel> ret = new ArrayList<>();

        //if the first point is after the starting point
        while(startPoint<content.get(0).getTimeStamp().getTime()){
            HistoricalDataModel model = new HistoricalDataModel();
            model.cloneFromModel(content.get(0));
            model.setTimeStamp(new Timestamp(startPoint));
            model.setProxyField();
            ret.add(model);
            startPoint=startPoint+interval*1000;
        }

        ret.add(content.get(0));

        for (int i =1;i<content.size();i++){
            if(ret.size()>totalFileMinutePointsSize){
                throw new Exception("input file has bug!");
            }

            HistoricalDataModel pre = ret.get(ret.size()-1);
            HistoricalDataModel curr = content.get(i);

            if(curr.getTimeStamp().getTime()-pre.getTimeStamp().getTime()==interval*1000){
                ret.add(curr);
            }else{
                HistoricalDataModel proxy = new HistoricalDataModel();
                proxy.cloneFromModel(pre);
                proxy.setTimeStamp(new Timestamp(pre.getTimeStamp().getTime()+interval*1000));
                proxy.setProxyField();
                ret.add(proxy);
                i--;
            }
        }

        while(ret.size()<totalFileMinutePointsSize){
            HistoricalDataModel last = ret.get(ret.size()-1);
            HistoricalDataModel proxy = new HistoricalDataModel();
            proxy.cloneFromModel(last);
            proxy.setTimeStamp(new Timestamp(last.getTimeStamp().getTime()+interval*1000));
            proxy.setProxyField();
            ret.add(proxy);
        }

        return ret;
    }

    public String write(List<HistoricalDataModel> content, String outputFile){

        if(content==null || content.size()==0){
            System.out.println(String.format("WebCoinBaseApiFileProcessorImpl -> Nothing to write") );
            return null;
        }

        try{

            File dir = new File(outputFile);

            if(outputFile.contains(".")){
                dir = dir.getParentFile();
            }

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File output = new File(outputFile);

            output.createNewFile();


            FileWriter writer = new FileWriter(output);

            String header = "time_stamp,exchange_id,currency_id,pricing_currency_id,open,close," +
                    "high,low,trade_volume,back_filled\n";

            writer.write(header);
            writer.flush();

            Collections.sort(content, new Comparator<HistoricalDataModel>() {
                @Override
                public int compare(HistoricalDataModel o1, HistoricalDataModel o2) {
                    if(o1.getTimeStamp().getTime()==o2.getTimeStamp().getTime()){
                        return 0;
                    }
                    return o1.getTimeStamp().getTime()>o2.getTimeStamp().getTime()?1:-1;
                }
            });
            for (HistoricalDataModel r : content) {
                writer.write(r.outputCSVLine());
                writer.write("\n");
            }
            writer.flush();
            writer.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println(String.format("FileProcessorImpl -> writing has completed" ));
        return outputFile;

    }

    public List<String> transferAllCryptocurrencyHourlyFile(int hourInt,String inputFilePath, String outputFilePath){

        List<DataSourceModel> requests = dao.getDataSourceModel(this.getExchangeName());

        List<String> ret = new ArrayList<>();
        for(DataSourceModel request : requests){

            try {
                Date now = new Date();
                Date hoursAgo = gmtTimeHelper.nextTime(now, TimeZone.getTimeZone("GMT"), Calendar.HOUR, -1 * hourInt);
                long startTime = TimeHelpers.floorDateTimeToHourLong(hoursAgo.getTime());
                Date start = new Date(startTime);
                Date end = gmtTimeHelper.nextTime(now, TimeZone.getTimeZone("GMT"), Calendar.HOUR, -1);

                System.out.println(String.format("Now local time %s, GMT is %s, Start to run time from %s to %s",
                        gmtTimeHelper.convertLongToLocalTimeDateString(now.getTime()),
                        gmtTimeHelper.convertLongToGMTDateString(now.getTime()),
                        gmtTimeHelper.convertLongToGMTDateString(startTime),
                        gmtTimeHelper.convertLongToGMTDateString(end.getTime())
                ));
                while(start.before(end)) {
                    String output = transfer(request, gmtTimeHelper.convertLongToGMTDateString(start.getTime())
                            , inputFilePath, outputFilePath);
                    System.out.println(String.format("finish %s , %s, %s, %s with output file path %s",
                            request.getExchangeName(), request.getTicker(), request.getPricingCurrency(),
                            gmtTimeHelper.convertLongToGMTDateString(start.getTime()), output));

                    if (output != null)
                        ret.add(output);
                    start = gmtTimeHelper.nextTime(start,TimeZone.getTimeZone("GMT"),Calendar.HOUR,1);
                }
            }catch (Exception e ){
                System.out.println(String.format("%s , %s, %s load failed",
                        request.getExchangeName(), request.getTicker(), request.getPricingCurrency()));
                e.printStackTrace();
            }
        }
        return ret;


    }
}
