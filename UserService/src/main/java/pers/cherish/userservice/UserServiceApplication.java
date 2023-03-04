package pers.cherish.userservice;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.cherish.userservice.model.UserVo;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@MapperScan("pers.cherish.userservice.mapper")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableRabbit
public class UserServiceApplication {
    @Value("#{'${variable.public-path}'.split(',')}")
    private String[] publicPath;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
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

//    @Bean
//    public OpenAPI UserOpenAPI() {
//        return new OpenAPI().addServersItem(new Server()
//                .add
//        )
//    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        final Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        final DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("userVo", UserVo.class);
        classMapper.setIdClassMapping(idClassMapping);
        jsonMessageConverter.setClassMapper(classMapper);
        jsonMessageConverter.setUseProjectionForInterfaces(true);
        return jsonMessageConverter;
    }



}
