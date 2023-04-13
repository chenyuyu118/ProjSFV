package pers.cherish;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;


import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
@MapperScan("pers.cherish.mapper")
public class VideoServiceApplication {

    @Value("#{'${variable.public-path}'.split(',')}")
    private String[] publicPath;
    public static void main(String[] args) {
        SpringApplication.run(VideoServiceApplication.class, args);
    }


    @Bean
    public OpenApiCustomizer consumerTypeHeaderOpenAPICustomizer() {
        final Set<String> collect = Arrays.stream(publicPath).collect(Collectors.toSet());
        return openApi -> {
            final List<String> list = openApi.getPaths().keySet().stream()
                    .filter(path -> !collect.contains(path))
                    .toList();
            list.forEach(path -> openApi.getPaths().get(path).addParametersItem(new HeaderParameter()
                    .name("token")
                    .description("用户令牌")
                    .schema(new Schema().type("string"))
                    .required(false)));
        };
    }

    @Bean(name = "getIsLike")
    public RedisScript<List> getIsLike() {
        return RedisScript.of(new ClassPathResource("scripts/is_like.lua"), List.class);
    }

    @Bean(name = "getIsDislike")
    public RedisScript<List> getIsDislike() {
        return RedisScript.of(new ClassPathResource("scripts/is_dislike.lua"), List.class);
    }

    @Bean(name = "getIsCollect")
    public RedisScript<List> getIsCollect() {
        return RedisScript.of(new ClassPathResource("scripts/is_collect.lua"), List.class);
    }
}
