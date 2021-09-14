package quant.platform.data.service.dbvalidator.validator.impl;

import org.springframework.stereotype.Component;

@Component
public class BinanceValidator extends CoinbaseValidator{
    @Override
    public String getExchangeName() {
        return "binance";
    }
}
