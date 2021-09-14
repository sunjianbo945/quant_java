package quant.platform.data.service.dbvalidator.domian;

public class ValidatorResultRow {
    private String exchangeName;
    private String ticker;
    private String pricingCurrency;
    private Integer interval;
    private Integer dataCount;
    private String earlistTimeStamp;
    private String latestTimeStamp;
    private String upToDate;
    private String noMissing;

    public String getNoMissing() { return noMissing; }

    public void setNoMissing(String noMissing) { this.noMissing = noMissing; }

    public Integer getDataCount() { return dataCount; }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getPricingCurrency() {
        return pricingCurrency;
    }

    public void setPricingCurrency(String pricingCurrency) {
        this.pricingCurrency = pricingCurrency;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getEarlistTimeStamp() {
        return earlistTimeStamp;
    }

    public void setEarlistTimeStamp(String earlistTimeStamp) {
        this.earlistTimeStamp = earlistTimeStamp;
    }

    public String getLatestTimeStamp() {
        return latestTimeStamp;
    }

    public void setLatestTimeStamp(String latestTimeStamp) {
        this.latestTimeStamp = latestTimeStamp;
    }

    public String getUpToDate() {
        return upToDate;
    }

    public void setUpToDate(String upToDate) {
        this.upToDate = upToDate;
    }
}
