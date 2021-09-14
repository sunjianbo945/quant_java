package quant.platform.data.service.common.domain;

public class ExchangeModel {
    private int exchangeId;
    private String exchangeName;

    public ExchangeModel(){}

    public ExchangeModel(int id, String name){
        this.exchangeId = id;
        this.exchangeName = name;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!ExchangeModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final ExchangeModel other = (ExchangeModel) obj;
        return (this.exchangeId == other.exchangeId) && (this.exchangeName.equals(other.exchangeName));
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
}
