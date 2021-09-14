package quant.platform.data.service.common.domain.datagetter;

import quant.platform.data.service.common.utils.GeneralUtils;
import java.io.Serializable;


public class CoinBaseHistoricalRates implements Serializable,IHistoricalRates {

    private long time;
    private double low;
    private double high;
    private double open;
    private double close;
    private double volume;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "GDAXHistoricalRates{" +
                "time=" + time +
                ", low=" + low +
                ", high=" + high +
                ", open=" + open +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }
    @Override
    public String toCSVLine(){
        return time+","+low+","+high+","+open+","+close+","+volume;
    }

//    @Override
//    public int compare(Object o1, Object o2) {
//        if(o1 instanceof CoinBaseHistoricalRates && o2 instanceof CoinBaseHistoricalRates){
//            CoinBaseHistoricalRates c1 = (CoinBaseHistoricalRates)o1;
//            CoinBaseHistoricalRates c2 = (CoinBaseHistoricalRates)o2;
//
//            if(GeneralUtils.compare(c1.getTime(),c2.getTime())){
//                return 0;
//            }
//            return c1.getTime()>c2.getTime()?1:-1;
//        }
//        return 0;
//    }

    @Override
    public int compareTo(Object o) {

        if(o instanceof CoinBaseHistoricalRates) {

            CoinBaseHistoricalRates c = (CoinBaseHistoricalRates) o;

            if (GeneralUtils.compare(this.getTime(), c.getTime())) {
                return 0;
            }
            return this.getTime() > c.getTime() ? 1 : -1;
        }

        return 0;
    }
}
