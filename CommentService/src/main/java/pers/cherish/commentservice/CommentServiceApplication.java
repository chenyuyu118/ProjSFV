package pers.cherish.commentservice;

import io.swagger.v3.oas.models.media.Schema;
import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.Set;

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

    @Bean(name = "getCommentIsLike")
    public RedisScript<List> getCommentIsLike() {
        return RedisScript.of(new ClassPathResource("scripts/comment_isLike.lua"), List.class);
    }
}
