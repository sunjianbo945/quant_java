package quant.platform.data.service.datagetter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.datagetter.service.Impl.WebAPIService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class QuantPlatformDataGetter implements CommandLineRunner{

    @Autowired
    private List<WebAPIService> services;
    @Autowired
    @Qualifier("DefaultTimeConverter")
    private TimeHelpers timeHelpers;

    @Autowired
    @Qualifier("GMTTimeConverter")
    private TimeHelpers gmtTimeHelpers;

    @Value("${args.mode}")
    private String mode;

    @Value("${args.start-date}")
    private String startDate;

    @Value("${args.end-date}")
    private String endDate;

    @Value("${args.date}")
    private String date;

    @Value("${args.hours}")
    private String hours;


    public static void main (String[] args){
        SpringApplication.run(QuantPlatformDataGetter.class,args);
    }


    @Override
    public void run(String... args) throws Exception {
        //20150415 is a problem
//        mode = "range";
//        hours = "2";
////        date = "2017-06-01";
//        startDate="2017-06-01";
//        endDate="2018-04-21";
        switch (mode){
            case "today":
                Date time = new Date();
                runOneDay(timeHelpers.convertLongToGMTDateString(timeHelpers.nextTime(time,Calendar.DAY_OF_MONTH,-1).getTime()));
                break;
            case "oneday":
                runOneDay(date);
                break;
            case "range":
                runRange();
                break;
            case "hourly":
                runHourBack(hours);
                break;
            default:
                throw new Exception("Unknown mode");

        }

    }

    public void runHourBack(String hours) throws Exception {

        if(hours.equalsIgnoreCase("")) {
            throw new Exception("Unknown hours");
        }else {

            int hourInt = Integer.valueOf(hours);

            System.out.println(String.format("Run on %s hours back", hourInt));

            for (WebAPIService service : services) {

                service.LoadHourlyAllCryptocurrencyDataByIntoFile(hourInt);
            }
        }

    }

    public void runOneDay(String date) throws Exception {

        if(date.equalsIgnoreCase("")) {
            throw new Exception("Unknown date");
        }else {
            System.out.println("Run on date " + date);

            for (WebAPIService service : services) {
                service.loadDailyAllCryptocurrencyDataByMinIntoFile(date);
            }
        }
    }

    public void runRange(){

        try {
             Date start = timeHelpers.parse(startDate,TimeZone.getDefault());
             Date end = timeHelpers.parse(endDate,TimeZone.getDefault());

            while(start.before(end)) {

                String date = timeHelpers.convertLongToLocalTimeDateString(start.getTime());
                System.out.println("Run on date "+ date);

                long timer_start = System.currentTimeMillis();
                for(WebAPIService service: services) {
                    service.loadDailyAllCryptocurrencyDataByMinIntoFile(date);
                    System.out.println("loading data takes " +
                            (System.currentTimeMillis() - timer_start) + " ms");
                }
                start = timeHelpers.nextTime(start, Calendar.DAY_OF_MONTH,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

