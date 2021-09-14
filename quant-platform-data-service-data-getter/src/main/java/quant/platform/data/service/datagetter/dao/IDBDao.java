package quant.platform.data.service.datagetter.dao;

import quant.platform.data.service.common.domain.DataSourceModel;

import java.util.List;

public interface IDBDao {
    List<DataSourceModel> getTblDataSource();
    List<DataSourceModel> getExchangeTradePairs(String exchangeName);
}
