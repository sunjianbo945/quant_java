package quant.platform.data.service.common.utils;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class IntervalHelpersTest {
    @Test
    public void validateIntervalTest(){
        assertThat(IntervalHelpers.isValidInterval(60)).isTrue();
        assertThat(IntervalHelpers.isValidInterval(55)).isFalse();

        //Should throw message telling people what are correct intervals
        assertThatThrownBy(() -> {IntervalHelpers.validateInterval(56);}).isInstanceOf(ValueException.class)
                .hasMessageContaining("60");
    }
}
