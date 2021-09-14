package quant.platform.data.service.etl.processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.etl.processor.service.WebApiFileProcessor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class QuantPlatformDataEtlProcessor implements CommandLineRunner {


    @Autowired
    private List<WebApiFileProcessor> writers;

    @Autowired
    @Qualifier("DefaultTimeConverter")
    TimeHelpers timeHelpers;

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


    public static void main(String[] args){
        SpringApplication.run(QuantPlatformDataEtlProcessor.class,args);

    }

    @Override
    public void run(String... args) throws Exception {
//        mode = "range";
//        hours = "2";
////        date = "2018-04-15";
//        startDate="2017-06-01";
//        endDate="2018-04-21";
////        mode = "oneday";
////        date = "2017-12-04";

        switch (mode){
            case "today":
                Date time = new Date();
                runOneDay(timeHelpers.convertLongToGMTDateString(TimeHelpers.nextTime(time,Calendar.DAY_OF_MONTH,-1).getTime()));
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

            for (WebApiFileProcessor writer : writers) {
                writer.transferAllCryptocurrencyHourlyFile(hourInt,null,null);
            }
        }

    }

    private void runRange() {

        try {
            Date start = timeHelpers.parse(startDate, TimeZone.getDefault());
            Date end = timeHelpers.parse(endDate,TimeZone.getDefault());

            while(start.before(end)) {

                String date = timeHelpers.convertLongToLocalTimeDateString(start.getTime());
                System.out.println("Run on date "+ date);

                long timer_start = System.currentTimeMillis();
                for(WebApiFileProcessor writer: writers) {

                    writer.transferAllCryptocurrencyDilyFile(date,null,null);
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

            for (WebApiFileProcessor writer : writers) {
                writer.transferAllCryptocurrencyDilyFile(date,null,null);
            }
        }
    }


}
