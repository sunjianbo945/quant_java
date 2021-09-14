package quant.platform.data.service.common.rollup;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import quant.platform.data.service.common.domain.HistoricalDataModel;
import quant.platform.data.service.common.utils.IntervalHelpers;

import java.util.ArrayList;
import java.util.List;

/***
 * This is the class that helps rollup raw data to certain intervals of time. For instance, if we stored minute data
 * and user want data by 5 minutes, we would need this method to roll up the time series
 */
public class RollUpTimeSeriesIntervals {
    /***
     *  VERY IMPORTANTLY! Here we assume that the data has been BACK FILLED so no missing data point should happen!
     *  Also data tails will be dropped. For instance, if we roll up minute data to 5 minute data and raw time series
     *  contains 67 data poins, you will get 13 data points back and the 2 on the tail will be dropped
     *
     * @param rawTimeSeries     Time series to be rolled up
     * @param rawDataInterval
     * @param desiredInterval
     * @return
     */
    public static List<HistoricalDataModel> rollUpToCertainInterval(
            List<HistoricalDataModel> rawTimeSeries,
            int rawDataInterval,
            int desiredInterval
    ) {
        IntervalHelpers.validateInterval(rawDataInterval);
        IntervalHelpers.validateInterval(desiredInterval);
        if (desiredInterval < rawDataInterval) {
            throw new ValueException(
                    "Desired interval " + desiredInterval + " is larger than raw data interval " + rawDataInterval
            );
        } else if (desiredInterval == rawDataInterval) {
            return rawTimeSeries;
        } else {
            List<HistoricalDataModel> newTimeSeries = new ArrayList<>();

            if (rawTimeSeries.size() > 0) {
                int numOfObjectsToFoldForEachDesiredInterval = desiredInterval / rawDataInterval;
                HistoricalDataModel model = new HistoricalDataModel();
                int index = 0;
                for (; index < rawTimeSeries.size(); index++) {
                    if (index % numOfObjectsToFoldForEachDesiredInterval == 0) {
                        if (index != 0) {
                            newTimeSeries.add(model);
                            model = new HistoricalDataModel();
                        }
                        model.cloneFromModel(rawTimeSeries.get(index));
                    } else {
                        model.rollUpWithAnotherModel(rawTimeSeries.get(index));
                    }
                }

                //Remember to append the last one
                if(index % numOfObjectsToFoldForEachDesiredInterval == 0){
                    newTimeSeries.add(model);
                }
            }
            return newTimeSeries;
        }
    }
}