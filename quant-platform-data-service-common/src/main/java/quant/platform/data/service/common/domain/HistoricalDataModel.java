package quant.platform.data.service.common.domain;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import quant.platform.data.service.common.utils.GeneralUtils;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.common.constant.DataServiceConstants;

import java.sql.Timestamp;

import static java.lang.Double.min;
import static java.lang.Double.max;


public class HistoricalDataModel {
    private Timestamp timeStamp;
    private Integer exchangeId;
    private Integer currencyId;
    private Integer pricingCurrencyId;
    private Double open;
    private Double close;
    private Double high;
    private Double low;
    private Double tradeVolume;
    private Integer backFilled;

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Integer exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Integer getPricingCurrencyId() {
        return pricingCurrencyId;
    }

    public void setPricingCurrencyId(Integer pricingCurrencyId) {
        this.pricingCurrencyId = pricingCurrencyId;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(Double tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public Integer getBackFilled() { return backFilled; }

    public void setBackFilled(Integer backFilled) { this.backFilled = backFilled; }

    @Override
    public String toString() {
        return
                timeStamp.toString() +
                "," + exchangeId +
                "," + currencyId +
                "," + pricingCurrencyId +
                "," + open +
                "," + close +
                "," + high +
                "," + low +
                "," + tradeVolume +
                "," + backFilled;
    }

    public String compactJsonArray(){
        return
                "{" + timeStamp.getTime() +
                "," + open +
                "," + close +
                "," + high +
                "," + low +
                "," + tradeVolume +
                "}";
    }

    public void cloneFromModel(HistoricalDataModel anotherModel){
        this.timeStamp = anotherModel.timeStamp;
        this.exchangeId = anotherModel.exchangeId;
        this.currencyId = anotherModel.currencyId;
        this.pricingCurrencyId = anotherModel.pricingCurrencyId;
        this.open = anotherModel.open;
        this.close = anotherModel.close;
        this.high = anotherModel.high;
        this.low = anotherModel.low;
        this.tradeVolume = anotherModel.tradeVolume;
        this.backFilled = anotherModel.backFilled;
    }

    public void setProxyField(){
        this.open = null;
        this.close = null;
        this.high = null;
        this.low = null;
        this.tradeVolume = null;
        this.backFilled = 1;
    }

    //No exchangeId, currencyId or pricingCurrencyId check here
    public void rollUpWithAnotherModel(HistoricalDataModel anotherModel){
        // MUST BE VERY CAREFUL! Here we take care of rolling up forward and backward
        if(this.timeStamp.getTime() < anotherModel.timeStamp.getTime()){
            //forward roll up
            if(this.open == null) this.open = anotherModel.open;
            if(anotherModel.close != null) this.close = anotherModel.close;
        } else if(this.timeStamp.getTime() > anotherModel.timeStamp.getTime()){
            //backward roll up
            if(this.close == null) this.close = anotherModel.close;
            if(anotherModel.open != null) this.open = anotherModel.open;
            this.timeStamp = anotherModel.timeStamp;
        } else{
            throw new ValueException("should not merge object \"HistoricalDataModel\" with the same time stamp!");
        }
        this.high = GeneralUtils.maxWithNull(this.high, anotherModel.high);
        this.low = GeneralUtils.minWithNull(this.low, anotherModel.low);
        this.tradeVolume = GeneralUtils.addWithNull(this.tradeVolume, anotherModel.tradeVolume);
        if(anotherModel.backFilled > 0){
            this.backFilled = 1;
        };
    }

    public String outputCSVLine(){
        TimeHelpers timeConverter=TimeHelpers.getUTCTimeConverter();

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                timeConverter.convertLongToGMTDateString(timeStamp.getTime()),
                exchangeId,
                currencyId,
                pricingCurrencyId,
                open==null?"NULL":open,
                close==null?"NULL":close,
                high==null?"NULL":high,
                low==null?"NULL":low,
                tradeVolume==null?"NULL":tradeVolume,
                backFilled);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof HistoricalDataModel)){
            return false;
        }

        HistoricalDataModel model = (HistoricalDataModel) obj;
        if(
                this.timeStamp.equals(model.timeStamp)
                && this.exchangeId.equals(model.exchangeId)
                && this.currencyId.equals(model.currencyId)
                && this.pricingCurrencyId.equals(model.pricingCurrencyId)
                && GeneralUtils.compareWithTolerance(this.open,model.open,DataServiceConstants.MIN_TOLERANCE_OF_ERROR)
                && GeneralUtils.compareWithTolerance(this.close,model.close,DataServiceConstants.MIN_TOLERANCE_OF_ERROR)
                && GeneralUtils.compareWithTolerance(this.high,model.high,DataServiceConstants.MIN_TOLERANCE_OF_ERROR)
                && GeneralUtils.compareWithTolerance(this.low,model.low,DataServiceConstants.MIN_TOLERANCE_OF_ERROR)
                && GeneralUtils.compareWithTolerance(this.tradeVolume,model.tradeVolume,DataServiceConstants.MIN_TOLERANCE_OF_ERROR)
                && this.backFilled.equals(model.backFilled)
                ){
            return true;
        }else{
            return false;
        }
    }
}
