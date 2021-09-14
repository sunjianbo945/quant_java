package quant.platform.data.service.common.rollup;

import org.assertj.core.data.Percentage;
import org.junit.Test;
import quant.platform.data.service.common.domain.HistoricalDataModel;
import quant.platform.data.service.common.utils.TimeHelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;


public class RollUpTimeSeriesIntervalsTest {

    @Test
    public void rollUpTest() throws IOException, ParseException {
        List<HistoricalDataModel> result = getListFromCSV("rollup/btc_usd.csv");

        List<HistoricalDataModel> fineMinutesData = RollUpTimeSeriesIntervals.rollUpToCertainInterval(
                result, 60, 300
        );
        assertThat(fineMinutesData.size()).isEqualTo(288);
        List<HistoricalDataModel> fifteenMinutesData = RollUpTimeSeriesIntervals.rollUpToCertainInterval(
                fineMinutesData, 300, 900
        );
        assertThat(fifteenMinutesData.size()).isEqualTo(96);
        List<HistoricalDataModel> hourlyData = RollUpTimeSeriesIntervals.rollUpToCertainInterval(
                fifteenMinutesData, 900, 3600
        );
        assertThat(hourlyData.size()).isEqualTo(24);
        List<HistoricalDataModel> sixHourData = RollUpTimeSeriesIntervals.rollUpToCertainInterval(
                hourlyData, 3600, 21600
        );
        assertThat(sixHourData.size()).isEqualTo(4);
        List<HistoricalDataModel> dailyData = RollUpTimeSeriesIntervals.rollUpToCertainInterval(
                sixHourData, 21600, 86400
        );
        assertThat(dailyData.size()).isEqualTo(1);
        List<HistoricalDataModel> dailyDataOneShotRollup = RollUpTimeSeriesIntervals.rollUpToCertainInterval(
                result, 60, 86400
        );
        assertThat(isListEqual(dailyData, dailyDataOneShotRollup)).isTrue();
    }

    @Test
    public void rollUpWithNullTest() throws IOException, ParseException {
        List<HistoricalDataModel> result = getListFromCSV("rollup/btc_usd_with_null.csv");

        List<HistoricalDataModel> fineMinutesData = RollUpTimeSeriesIntervals.rollUpToCertainInterval(
                result, 60, 300
        );
        assertThat(fineMinutesData.get(1).getHigh()).isEqualTo(7090.7);
        assertThat(fineMinutesData.get(1).getLow()).isEqualTo(7064.29);
        assertThat(fineMinutesData.get(1).getOpen()).isEqualTo(7080.1);
        assertThat(fineMinutesData.get(1).getClose()).isEqualTo(7065.07);
        assertThat(fineMinutesData.get(1).getTradeVolume()).isCloseTo(80.40860624000001,Percentage.withPercentage(0.00001));

        assertThat(fineMinutesData.get(0).getHigh()).isNull();
        assertThat(fineMinutesData.get(0).getLow()).isNull();
        assertThat(fineMinutesData.get(0).getOpen()).isNull();
        assertThat(fineMinutesData.get(0).getClose()).isNull();
        assertThat(fineMinutesData.get(0).getTradeVolume()).isNull();
    }

    private boolean isListEqual(List<HistoricalDataModel> list1, List<HistoricalDataModel> list2){
        if(list1.size() != list2.size()){
            return false;
        }
        for(int index = 0; index < list1.size(); index++){
            if(!list1.get(index).equals(list2.get(index))){
                return false;
            }
        }
        return true;
    }

    private List<HistoricalDataModel> getListFromCSV(String fileName) throws IOException, ParseException {
        List<HistoricalDataModel> result = new ArrayList<>();
        TimeHelpers timeHelpers = TimeHelpers.getUTCTimeConverter();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        BufferedReader br = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));

        String line = br.readLine(); //headers
        line = br.readLine();
        while(line != null){
            String[] attributes = line.split(",");
            HistoricalDataModel model = new HistoricalDataModel();
            model.setTimeStamp(new Timestamp(timeHelpers.convertStringToLongGMT(attributes[0])));
            model.setExchangeId(Integer.valueOf(attributes[1]));
            model.setCurrencyId(Integer.valueOf(attributes[2]));
            model.setPricingCurrencyId(Integer.valueOf(attributes[3]));
            model.setOpen(attributes[4].equalsIgnoreCase("null")?null:Double.valueOf(attributes[4]));
            model.setClose(attributes[5].equalsIgnoreCase("null")?null:Double.valueOf(attributes[5]));
            model.setHigh(attributes[6].equalsIgnoreCase("null")?null:Double.valueOf(attributes[6]));
            model.setLow(attributes[7].equalsIgnoreCase("null")?null:Double.valueOf(attributes[7]));
            model.setTradeVolume(attributes[8].equalsIgnoreCase("null")?null:Double.valueOf(attributes[8]));
            model.setBackFilled(Integer.valueOf(attributes[9]));
            result.add(model);
            line = br.readLine();
        }

        return result;
    }

}
