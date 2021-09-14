package quant.platform.data.service.datagetter.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import quant.platform.data.service.common.utils.TimeHelpers;

import java.util.TimeZone;

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

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Bean("DefaultHttpHeaders")
    public HttpHeaders getDefaultHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        //headers.set("Content-Type", "application/json");
        headers.set("User-Agent", "Mozilla/5.0");
        return headers;
    }

    @Bean
    public HttpEntity getDefaultHttpEntity(@Qualifier("DefaultHttpHeaders") HttpHeaders headers){
        return new HttpEntity(headers);
    }

    @Bean("GMT")
    public TimeZone getGMTTimeZone(){
        return TimeZone.getTimeZone("GMT");
    }


}
