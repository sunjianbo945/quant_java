package quant.platform.data.service.datagetter.view;

public class DataInput {
    private String exchangeName;
    private String ticker;
    private String pricingCurrency;
    private String date;
    private int interval;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "DataInput{" +
                "exchangeName='" + exchangeName + '\'' +
                ", ticker='" + ticker + '\'' +
                ", pricingCurrency='" + pricingCurrency + '\'' +
                ", date='" + date + '\'' +
                ", interval=" + interval +
                '}';
    }
}
