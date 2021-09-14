package quant.platform.data.service.etl.processor.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import quant.platform.data.service.common.utils.TimeHelpers;

@Configuration
public class AppConfig {

    @Bean("GMTTimeConverter")
    public TimeHelpers gmtTimeConverter(){
        String formatter = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        TimeHelpers converter = new TimeHelpers();
        converter.setFormatter(formatter);
        return converter;
    }

    @Bean("DefaultTimeConverter")
    public TimeHelpers defaultTimeConverter(){
        return new TimeHelpers();
    }


}
