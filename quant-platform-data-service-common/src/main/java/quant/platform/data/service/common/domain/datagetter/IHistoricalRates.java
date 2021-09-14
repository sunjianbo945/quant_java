package quant.platform.data.service.common.domain.datagetter;


import java.util.Comparator;

public interface IHistoricalRates extends Comparable {
    String toCSVLine();
}
