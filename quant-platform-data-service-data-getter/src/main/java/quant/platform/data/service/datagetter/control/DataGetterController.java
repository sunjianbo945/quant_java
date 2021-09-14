package quant.platform.data.service.datagetter.control;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import quant.platform.data.service.datagetter.service.Impl.WebAPIService;
import quant.platform.data.service.datagetter.view.DataInput;
import quant.platform.data.service.datagetter.view.DataInputValidator;

import java.util.List;
import java.util.Optional;

@RestController
public class DataGetterController {

    @Autowired
    List<WebAPIService> services;
    @Autowired
    DataInputValidator validator;


    @RequestMapping(value = "/data-getter*", method = RequestMethod.PUT)
    @ResponseBody
    public void getData(@RequestParam(name = "exchangeName",required = true) String exchangeName ,
                        @RequestParam(name = "ticker") Optional<String> ticker,
                        @RequestParam(name = "pricingCurrency") Optional<String> pricingCurrency,
                        @RequestParam(name = "date", required = true) String date){
        DataInput input = new DataInput();
        input.setDate(date);
        input.setExchangeName(exchangeName);
        input.setTicker(ticker.isPresent()?ticker.get():null);
        input.setPricingCurrency(ticker.isPresent()?pricingCurrency.get():null);

        if(validator.valide(input)){
            for(WebAPIService service : services){
                if(service.getWebExchangeName().equals(exchangeName)){
                    if(ticker.isPresent() && pricingCurrency.isPresent()){
                        service.loadDailyCryptocurrencyDataByMinIntoFile(ticker.get(),pricingCurrency.get(),date,60);
                    }else{
                        service.loadDailyAllCryptocurrencyDataByMinIntoFile(date);
                    }
                }
            }
        }
    }

}
