package quant.platform.data.service.common.utils;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.Arrays;

public class IntervalHelpers {
    /***
     * This field represents the allowed intervals of consecutive time series data points
     * 60:      1 minute
     * 300:     5 minutes
     * 900:     15 minutes
     * 3600:    1 hour
     * 21600:   6 hours
     * 86400:   1 day
     */
    private static final int[] VALID_INTERVALS_IN_SECONDS = {60, 300, 900, 3600, 21600, 86400};

    /***
     * Check whether an interval is valid
     * @param interval
     * @return
     */
    public static boolean isValidInterval(int interval){
        for(int validInterval: VALID_INTERVALS_IN_SECONDS){
            if(validInterval == interval){
                return true;
            }
        }
        return false;
    }

    public static void validateInterval(int interval){
        for(int validInterval: VALID_INTERVALS_IN_SECONDS){
            if(validInterval == interval){
                return;
            }
        }
        throw new ValueException(
                "Invalid Interval Specified! Interval: " + interval + " Possible values are " +
                        Arrays.toString(VALID_INTERVALS_IN_SECONDS)
        );
    }
}
