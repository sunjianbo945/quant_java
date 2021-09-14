package quant.platform.data.service.common.utils;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneralUtilsTest {

    @Test
    public void testCompare(){
        Integer i1 = new Integer(5);
        Integer i2 = new Integer(5);
        Double d3  = new Double(5.0);

        assertThat(GeneralUtils.compare(i1,i2)).isTrue();
        assertThat(GeneralUtils.compare(i1,d3)).isFalse();
        assertThat(GeneralUtils.compare(i1,5)).isTrue();
        assertThat(GeneralUtils.compare(5,i1)).isTrue();

        assertThat(GeneralUtils.compare(null,null)).isTrue();
        assertThat(GeneralUtils.compare(null,1)).isFalse();




    }
}
