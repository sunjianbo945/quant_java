package quant.platform.data.service.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({
        "classpath:sql/sql.properties",
        "classpath:application.yml" //if same key, this will 'win'
})
public class QuantPlatformDataWebServiceApp {
    public static void main(String[] args){
        SpringApplication.run(QuantPlatformDataWebServiceApp.class,args);
    }
}
