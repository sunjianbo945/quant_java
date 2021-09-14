package quant.platform.data.service.dbvalidator.validator;

import quant.platform.data.service.dbvalidator.domian.ValidatorResultRow;

import java.text.ParseException;
import java.util.List;

public interface IQuantPlatformDbValidator {
    public String getExchangeName();
    public List<ValidatorResultRow> validateDbData();
}
