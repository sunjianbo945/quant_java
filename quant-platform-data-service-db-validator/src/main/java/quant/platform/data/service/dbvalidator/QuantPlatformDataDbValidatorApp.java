package quant.platform.data.service.dbvalidator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import quant.platform.data.service.dbvalidator.service.QuantPlatformDbValidatorService;

@SpringBootApplication
@PropertySource({
        "classpath:sql/sql.properties",
        "classpath:application.yml" //if same key, this will 'win'
})
public class QuantPlatformDataDbValidatorApp implements CommandLineRunner {
    @Autowired
    QuantPlatformDbValidatorService service;

    public static void main (String[] args){
        SpringApplication.run(QuantPlatformDataDbValidatorApp.class,args);
    }

    @Override
    public void run(String... args) {
        service.runValidators();
    }
}
