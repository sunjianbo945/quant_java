package quant.platform.data.service.file.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import quant.platform.data.service.file.loader.service.FileLoaderService;
import quant.platform.data.service.common.utils.TimeHelpers;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class QuantPlateformDataFileLoader implements CommandLineRunner{

    @Autowired
    @Qualifier("DefaultTimeConverter")
    TimeHelpers defaultTimeHelpers;

    @Autowired
    @Qualifier("GMTTimeConverter")
    TimeHelpers gmtTimeHelpers;

    @Autowired
    List<FileLoaderService> services;


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

    public static void main(String [] args){
        SpringApplication.run(QuantPlateformDataFileLoader.class,args);
    }


    @Override
    public void run(String... args) throws Exception {

//        mode = "range";
//        hours = "2";
////        date = "2018-04-15";
//        startDate="2017-06-01";
//        endDate="2018-04-21";

        switch (mode){
            case "today":
                Date time = new Date();
                runOneDay(defaultTimeHelpers.convertLongToGMTDateString(TimeHelpers.nextTime(time,Calendar.DAY_OF_MONTH,-1).getTime()));
                break;
            case "oneday":
                runOneDay(date);
                break;
            case "range":
                runRange();
                break;
            case "hourly":
                runHoursBack(hours);
                break;
            default:
                throw new Exception("Unknown mode");

        }

    }

    private void runRange() {
        try {
            Date start = defaultTimeHelpers.parse(startDate, TimeZone.getDefault());
            Date end = defaultTimeHelpers.parse(endDate,TimeZone.getDefault());

            while(start.before(end)) {

                String date = defaultTimeHelpers.convertLongToLocalTimeDateString(start.getTime());
                System.out.println("Run on date "+ date);

                long timer_start = System.currentTimeMillis();
                for(FileLoaderService service: services) {
                    service.loadDailyFiles(date);
                    System.out.println("loading data takes " +
                            (System.currentTimeMillis() - timer_start) + " ms");
                }
                start = TimeHelpers.nextTime(start, Calendar.DAY_OF_MONTH,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void runOneDay(String date) throws Exception {

        if(date.equalsIgnoreCase("")) {
            throw new Exception("Unknown date");
        }else {
            System.out.println("Run on date " + date);

            for (FileLoaderService service : services) {
                service.loadDailyFiles(date);
            }
        }
    }

    private void runHoursBack(String hours) throws Exception {

        if(hours.equalsIgnoreCase("")) {
            throw new Exception("Unknown hours");
        }else {
            int hourInt = Integer.valueOf(hours);
            Date now = new Date();
            Date hoursAgo = TimeHelpers.nextTime(now, Calendar.HOUR, -1 * hourInt);
            long startTime = TimeHelpers.floorDateTimeToHourLong(hoursAgo.getTime());
            Date start = new Date(startTime);
            Date end = TimeHelpers.nextTime(now, Calendar.HOUR, -1);

            System.out.println(String.format("Now local time %s, GMT is %s, Start to run time from %s to %s",
                    gmtTimeHelpers.convertLongToLocalTimeDateString(now.getTime()),
                    gmtTimeHelpers.convertLongToGMTDateString(now.getTime()),
                    gmtTimeHelpers.convertLongToGMTDateString(start.getTime()),
                    gmtTimeHelpers.convertLongToGMTDateString(TimeHelpers.floorDateTimeToHourLong(end.getTime()))
            ));
            for (FileLoaderService service : services) {
                while(start.before(end)){
                    service.loadHourlyFiles(gmtTimeHelpers.convertLongToGMTDateString(start.getTime()));
                    start = TimeHelpers.nextTime(start,Calendar.HOUR,1);
                }
            }
        }

    }
}
