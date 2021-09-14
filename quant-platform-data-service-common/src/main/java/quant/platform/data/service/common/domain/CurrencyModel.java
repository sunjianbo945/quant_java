package quant.platform.data.service.common.domain;

public class CurrencyModel {
    private int currencyId;
    private String ticker;

    public CurrencyModel(){}

    public CurrencyModel(int id, String name){
        this.currencyId = id;
        this.ticker = name;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!CurrencyModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final CurrencyModel other = (CurrencyModel) obj;
        return (this.currencyId == other.currencyId) && (this.ticker.equals(other.ticker));
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
