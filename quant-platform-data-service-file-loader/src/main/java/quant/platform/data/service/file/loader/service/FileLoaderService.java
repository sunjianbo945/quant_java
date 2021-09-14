package quant.platform.data.service.file.loader.service;

import quant.platform.data.service.common.domain.DataSourceModel;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.file.loader.dao.DBExchangeDao;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class FileLoaderService {


    DBExchangeDao dao;

    public FileLoaderService(DBExchangeDao exchangeDao){
        this.dao=exchangeDao;
    }


    public void loadDailyFiles(String date){
        List<DataSourceModel> requests = dao.getDataSourceModel(this.getExchangeName());
        for(DataSourceModel req :requests){
            loadFile(null,date,req);
        }
    }


    public void loadHourlyFiles(String dateTime){
        List<DataSourceModel> requests = dao.getDataSourceModel(this.getExchangeName());
        for(DataSourceModel req :requests){
            loadFile(null,dateTime,req);
        }
    }


    public String getDefaultFilePath(String root,String date, DataSourceModel request){

        String path = null;

        if(date.contains("T")) {
            path = String.format(root, this.getExchangeName(), date.split("T")[0]) +
                    String.format("%s_%s_%s_%s_%s.csv", this.getExchangeName(),
                            request.getTicker(), request.getPricingCurrency(),
                            date.replaceAll(":","-"), request.getInterval());
        }else{
            path = String.format(root, this.getExchangeName(), date) +
                    String.format("%s_%s_%s_%s_%s.csv", this.getExchangeName(),
                            request.getTicker(), request.getPricingCurrency(), date, request.getInterval());
        }
        return path;
    }

    public void loadFile(String filePath, String date, DataSourceModel request){

        if(filePath==null){
            filePath = getDefaultInputFilePath(date,request);
        }

        try{
            File f = new File(filePath);
            if(!f.exists()){
                System.out.println(String.format("file %s does not exist", filePath));
                return ;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        if(date.contains("T")) {
            dao.deleteHourly(date, request.getTicker(), request.getPricingCurrency());
        }else {
            dao.deleteDaily(date, request.getTicker(), request.getPricingCurrency());
        }
        dao.loadFile(filePath);
    }

    public abstract String getExchangeName();
    public abstract String getDefaultInputFilePath(String date, DataSourceModel request);

}
