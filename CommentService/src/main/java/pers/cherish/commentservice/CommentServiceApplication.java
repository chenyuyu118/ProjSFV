package pers.cherish.commentservice;

import io.swagger.v3.oas.models.media.Schema;
import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

@MapperScan("pers.cherish.commentservice.mapper")
public class CommentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApplication.class, args);
    }


    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(
                    pathItem -> pathItem.addParametersItem(
                            new io.swagger.v3.oas.models.parameters.Parameter()
                                    .name("token")
                                    .in("header")
                                    .required(true)
                                    .schema(new Schema().type("string"))
                    )
                    );
        };
    }
}
