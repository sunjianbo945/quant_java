package quant.platform.data.service.common.domain.datagetter;

import quant.platform.data.service.common.utils.GeneralUtils;

import java.io.Serializable;
import java.util.Comparator;

public class BinanceHistoricalRates implements Serializable, IHistoricalRates{
    //    [
//            1499040000000,      // Open time
//            "0.01634790",       // Open
//            "0.80000000",       // High
//            "0.01575800",       // Low
//            "0.01577100",       // Close
//            "148976.11427815",  // Volume
//            1499644799999,      // Close time
//            "2434.19055334",    // Quote asset volume
//            308,                // Number of trades
//            "1756.87402397",    // Taker buy base asset volume
//            "28.46694368",      // Taker buy quote asset volume
//            "17928899.62484339" // Ignore
//    ]
    private long openTime;
    private double low;
    private double high;
    private double open;
    private double close;
    private double volume;
    private long closeTime;
    private double quoteAssetVolume;
    private int numberOfTrades;
    private double takerBuyBaseAssetVolume;
    private double takerBuyQuoteAssetVolume;
    private double ignore;

    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
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

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }

    public double getQuoteAssetVolume() {
        return quoteAssetVolume;
    }

    public void setQuoteAssetVolume(double quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }

    public int getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(int numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public double getTakerBuyBaseAssetVolume() {
        return takerBuyBaseAssetVolume;
    }

    public void setTakerBuyBaseAssetVolume(double takerBuyBaseAssetVolume) {
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
    }

    public double getTakerBuyQuoteAssetVolume() {
        return takerBuyQuoteAssetVolume;
    }

    public void setTakerBuyQuoteAssetVolume(double takerBuyQuoteAssetVolume) {
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
    }

    public double getIgnore() {
        return ignore;
    }

    public void setIgnore(double ignore) {
        this.ignore = ignore;
    }

    @Override
    public String toCSVLine(){
        return openTime+","+open+","+high+","+low+","+close+","+volume
                +","+closeTime+","+quoteAssetVolume+","+numberOfTrades+","+takerBuyBaseAssetVolume
                +","+takerBuyQuoteAssetVolume+","+ignore;
    }

//    @Override
//    public int compare(Object o1, Object o2) {
//        if(o1 instanceof BinanceHistoricalRates && o2 instanceof BinanceHistoricalRates){
//            BinanceHistoricalRates c1 = (BinanceHistoricalRates)o1;
//            BinanceHistoricalRates c2 = (BinanceHistoricalRates)o2;
//
//            if(GeneralUtils.compare(c1.getOpenTime(),c2.getOpenTime())){
//                return 0;
//            }
//            return c1.getOpenTime()>c2.getOpenTime()?1:-1;
//        }
//        return 0;
//    }

    @Override
    public int compareTo(Object o) {

        if(o instanceof BinanceHistoricalRates) {

            BinanceHistoricalRates c = (BinanceHistoricalRates) o;

            if (GeneralUtils.compare(this.getOpenTime(), c.getOpenTime())) {
                return 0;
            }
            return this.getOpenTime() > c.getOpenTime() ? 1 : -1;
        }

        return 0;
    }

}
