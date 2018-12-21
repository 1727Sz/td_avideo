package td.h;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.Annotation;

/**
 * Created by sanlion on 17-3-3.
 */
@Configuration
@EnableSwagger2
@Controller
public class OnlineHelpDocConfiguration {

    @GetMapping("/doc")
    public String doc() {
        return "redirect:swagger-ui.html";
    }

    @GetMapping("/")
    String index() {
        return "redirect:/doc";
    }


    @Value("${server.port}") private int port;

    @Bean
    public Docket all() {
        return apidoc(
                Api.class,
                "999.all",
                "所有的可用Api都在这里能找到");
    }


    private Docket apidoc(Class<? extends Annotation> annotation, String version, String description) {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(true)
                .groupName(version)
                .select().apis(RequestHandlerSelectors.withClassAnnotation(annotation)).build()
                .apiInfo(new ApiInfo(
                        ":" + port,
                        description,
                        version,
                        "http://abc.example.com/",
                        new Contact("xxx.xx", "", ""),
                        "apache 2.0",
                        ""))
                .useDefaultResponseMessages(false);
    }
}
