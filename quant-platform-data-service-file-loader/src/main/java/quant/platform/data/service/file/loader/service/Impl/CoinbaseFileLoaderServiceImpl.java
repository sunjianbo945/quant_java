package quant.platform.data.service.file.loader.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import quant.platform.data.service.file.loader.dao.DBExchangeDao;
import quant.platform.data.service.file.loader.service.FileLoaderService;
import quant.platform.data.service.common.constant.DataServiceConstants;
import quant.platform.data.service.common.domain.DataSourceModel;


@Component
public class CoinbaseFileLoaderServiceImpl extends FileLoaderService {

    @Autowired
    @Value("${inputfile}")
    private String inputFileRoot;

    @Override
    public String getExchangeName() {
        return DataServiceConstants.EXCHANGE_NAME_COIN_BASE;
    }

    @Autowired
    public CoinbaseFileLoaderServiceImpl(@Qualifier("DBCoinbaseExchangeDaoImpl") DBExchangeDao exchangeDao) {
        super(exchangeDao);
    }
    @Override
    public String getDefaultInputFilePath(String date, DataSourceModel request){
        return getDefaultFilePath(inputFileRoot,date,request);
    }


}
