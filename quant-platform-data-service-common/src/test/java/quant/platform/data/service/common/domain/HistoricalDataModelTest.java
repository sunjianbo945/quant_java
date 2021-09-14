package quant.platform.data.service.common.domain;

import org.junit.Test;
import quant.platform.data.service.common.domain.HistoricalDataModel;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

public class HistoricalDataModelTest {
    @Test
    public void testModel(){
        HistoricalDataModel model = new HistoricalDataModel();
        model.setTimeStamp(new Timestamp(1522716885000L));
        System.out.println(model.toString());
    }

    @Test
    public void testNull(){
        assertThat(null==null).isTrue();
    }

    @Test
    public void testInt(){
        assertThat(new Integer(5)==new Integer(5)).isFalse();

        assertThat(new Integer(5).equals(new Integer(5))).isTrue();
        assertThat(new Integer(5).equals(5)).isTrue();
        assertThat(new Integer(5)==5).isTrue();


    }
}
