package quant.platform.data.service.dbvalidator.service;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import quant.platform.data.service.common.email.GoogleMail;
import quant.platform.data.service.common.utils.TimeHelpers;
import quant.platform.data.service.dbvalidator.domian.ValidatorResultRow;
import quant.platform.data.service.dbvalidator.validator.IQuantPlatformDbValidator;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuantPlatformDbValidatorService {
    @Autowired
    private List<IQuantPlatformDbValidator> validators;

    @Value("${gmail.username}")
    private String username;

    @Value("${gmail.password}")
    private String password;

    @Value("${gmail.to}")
    private String to;

    @Value("${gmail.subject}")
    private String subject;

    public void runValidators(){
        List<ValidatorResultRow> list = new ArrayList<>();

        for(IQuantPlatformDbValidator validator: validators){
            list.addAll(validator.validateDbData());
        }

        String html = generateEmailBody(list);
        GoogleMail.doSendMail(username, password, to, subject, html);
    }

    private String generateEmailBody(List<ValidatorResultRow> list){
        /*  first, get and initialize an engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class",ClasspathResourceLoader.class.getName());
        ve.init();
        /*  next, get the Template  */
        Template t = ve.getTemplate( "velocity/template.vm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("objs", list);

        Date date = new Date();
        String formatter = "yyyy-MM-dd HH:mm:ss";
        TimeHelpers timeHelpers = TimeHelpers.getUTCTimeConverter();
        timeHelpers.setFormatter(formatter);
        String GMTStr = timeHelpers.convertLongToGMTDateString(date.getTime());
        String NYCStr = timeHelpers.convertLongToSpecificTimezoneTimeDateString(date.getTime(), "America/New_York");
        String BJStr = timeHelpers.convertLongToSpecificTimezoneTimeDateString(date.getTime(), "Asia/Shanghai");

        context.put("gmtTime", GMTStr);
        context.put("nycTime", NYCStr);
        context.put("bjTime", BJStr);

        /* now render the template into a StringWriter */
        StringWriter writer = new StringWriter();
        t.merge( context, writer );
        /* show the World */
        String html = writer.toString();
        //System.out.println(html);
        return html;
    }
}
