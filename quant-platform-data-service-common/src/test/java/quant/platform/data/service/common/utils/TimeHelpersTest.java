package quant.platform.data.service.common.utils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class TimeHelpersTest {


    @Test
    public void testToGMT(){

        TimeHelpers t = new TimeHelpers();

        String formatter ="yyyy-MM-dd'T'HH:mm:ss'Z'";

        t.setFormatter(formatter);
        //Tuesday, April 3, 2018 12:53:45 AM
        long l = 1522716825;

        String result = t.convertLongToGMTDateString(l*1000L);

        String specificGMTTimezoneStr = t.convertLongToSpecificTimezoneTimeDateString(l*1000L, "GMT");

        assertEquals(result,"2018-04-03T00:53:45Z");
        assertEquals(specificGMTTimezoneStr,"2018-04-03T00:53:45Z");
    }

    @Test
    public void testToLocalTime(){

        TimeHelpers t = new TimeHelpers();

        String formater ="yyyy-MM-dd HH:mm:ss";

        t.setFormatter(formater);

        //Monday, April 2, 2018 8:53:45 PM GMT-04:00 DST
        long l = 1522716825;

        String result = t.convertLongToLocalTimeDateString(l*1000L);

        //Only test get local in NYC time
        if(TimeZone.getDefault().getDisplayName().equals(TimeZone.getTimeZone("America/New_York").getDisplayName())) {
            assertEquals(result, "2018-04-02 20:53:45");
        }
    }


    @Test
    public void testNoFormatter(){

        TimeHelpers t = new TimeHelpers();

        //Monday, April 2, 2018 8:53:45 PM GMT-04:00 DST
        long l = 1522716825;

        String result = t.convertLongToLocalTimeDateString(l*1000L);

        assertEquals(result,"2018-04-02");

    }

    @Test
    public void testNextTime(){
        TimeHelpers t = new TimeHelpers();
        String formater ="yyyy-MM-dd'T'HH:mm:ss'Z'";

        t.setFormatter(formater);

        String date = "2018-04-02";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Date time = t.nextTime(sdf.parse(date), Calendar.HOUR,1);

            String result = t.convertLongToGMTDateString(time.getTime());

            assertEquals(result,"2018-04-02T01:00:00Z");

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testTrimDateTimeToHourLong(){
        //Wednesday, April 18, 2018 4:59:59.999 AM
        long time = 1524027599999L;
        String right = "2018-04-18T04:00:00Z";
        String next = "2018-04-18T05:00:00Z";

        //            1524024000000
        TimeHelpers th = TimeHelpers.getUTCTimeConverter();
        long ret = TimeHelpers.floorDateTimeToHourLong(time);

        assertTrue(right.equals(th.convertLongToGMTDateString(ret)));
        //Wednesday, April 18, 2018 4:00:00.001 AM
        time = 1524024000001L;

        ret = TimeHelpers.floorDateTimeToHourLong(time);
        assertTrue(right.equals(th.convertLongToGMTDateString(ret)));
        //Wednesday, April 18, 2018 4:00:00 AM
        time = 1524024000000L;
        ret = TimeHelpers.floorDateTimeToHourLong(time);
        assertTrue(right.equals(th.convertLongToGMTDateString(ret)));


        time = 1524027600000L;
        ret = TimeHelpers.floorDateTimeToHourLong(time);
        System.out.println(th.convertLongToGMTDateString(ret));
        assertTrue(next.equals(th.convertLongToGMTDateString(ret)));


    }

    @Test
    public void testTrimDateTimeToMinLong() {
        long time = 1518220814789L;
        long ret = TimeHelpers.floorMillsecLongToMinLong(time);
        TimeHelpers th = TimeHelpers.getUTCTimeConverter();
        System.out.println(th.convertLongToGMTDateString(time));

        String str = "2018-02-10T00:00:00Z";
        System.out.println(th.convertLongToGMTDateString(ret));
        assertTrue(str.equals(th.convertLongToGMTDateString(ret)));
    }

}
