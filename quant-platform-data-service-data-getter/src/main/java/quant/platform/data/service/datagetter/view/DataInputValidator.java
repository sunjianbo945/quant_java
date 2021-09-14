package quant.platform.data.service.datagetter.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import quant.platform.data.service.common.utils.CurrencyHelpers;
import quant.platform.data.service.common.utils.ExchangeHelpers;
import quant.platform.data.service.common.utils.TimeHelpers;

@Component
public class DataInputValidator {
    @Autowired
    @Qualifier("postgreJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("DefaultTimeConverter")
    TimeHelpers defaultTimeConverter;

    public boolean valide(DataInput in){

        boolean flag = true;

        try {
            flag = defaultTimeConverter.canParseString(in.getDate());

            int exchangeId = ExchangeHelpers.getExchangeIdFromExchangeName(jdbcTemplate, in.getExchangeName());

            int currencyId = in.getTicker()!=null?CurrencyHelpers.getCurrencyIdFromTicker(jdbcTemplate, in.getTicker()):0;
            int priceCurrencyId = in.getPricingCurrency()!=null?
                    CurrencyHelpers.getCurrencyIdFromTicker(jdbcTemplate, in.getPricingCurrency()):0;

        }catch (Exception e){
            System.out.println(in.toString());
            flag=false;
            e.printStackTrace();
        }finally {
            return flag;
        }

    }
}
