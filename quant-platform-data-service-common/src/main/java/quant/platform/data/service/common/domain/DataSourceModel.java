package quant.platform.data.service.common.domain;

public class DataSourceModel {

    private String exchangeName;
    private String ticker;
    private String pricingCurrency;
    private int interval;

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exhcangeName) {
        this.exchangeName = exhcangeName;
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

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof DataSourceModel)){
            return false;
        }

        DataSourceModel model = (DataSourceModel) obj;
        if(
                this.exchangeName.equals(model.exchangeName)
                && this.ticker.equals(model.ticker)
                && this.pricingCurrency.equals(model.pricingCurrency)
                && this.interval == model.interval
                ){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "DataSourceModel{" +
                "exchangeName='" + exchangeName + '\'' +
                ", ticker='" + ticker + '\'' +
                ", pricingCurrency='" + pricingCurrency + '\'' +
                ", interval=" + interval +
                '}';
    }
}
