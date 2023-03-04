package pers.cherish.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.commons.aop.UserAspect;
import pers.cherish.userservice.domain.UserDTORegister;
import pers.cherish.userservice.model.User;
import pers.cherish.userservice.model.UserDTO;
import pers.cherish.userservice.model.UserVo;
import pers.cherish.userservice.service.UserService;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Tags(value = {
        @Tag(name = "用户"),
})
@Import(UserAspect.class)
public class UserController {
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    private final RabbitTemplate rabbitTemplate;

    @Value("${variable.user-counter-key}")
    private String userCounterKey;
    @Value("${variable.salt}")
    private String salt;
    @Value("${variable.token-expire-time}")
    private Long tokenExpireTime;
    @Value("${variable.rabbit.user-exchange-key}")
    private String userExchange;
    @Value("${variable.rabbit.user-register-routing-key}")
    private String userRegisterRoutingKey;
    @Value("${variable.rabbit.user-update-routing-key}")
    private String userUpdateRoutingKey;
    @Autowired
    public UserController(UserService userService, StringRedisTemplate stringRedisTemplate, RabbitTemplate rabbitTemplate) {
        this.userService = userService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("")
    @Operation(summary = "用户注册", description = "用户注册")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "注册失败")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @Schema(implementation = UserDTORegister.class)
            )
    )
    public ResponseEntity<Map<String ,String >> register(@RequestBody UserDTORegister userDTORegister) {
        if (userService.getUserByName(userDTORegister.getUserName()) != null) {
            return ResponseEntity.status(400).body(Map.of("message", "用户名已存在"));
        }
        Long userId = stringRedisTemplate.opsForValue().increment(userCounterKey);
        String profileId = UUID.randomUUID().toString();
        userDTORegister.setId(userId);
        userDTORegister.setProfileId(profileId);
        final Long registerId = userService.register(userDTORegister);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userDTORegister, userVo);
        userVo.setProfile(profileId);
        rabbitTemplate.convertAndSend(userExchange, userRegisterRoutingKey, userVo);
        return ResponseEntity.ok(Map.of("message", "注册成功", "data", registerId.toString()));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "登录失败,用户名或密码错误")
    })
    public ResponseEntity<Map<String, Object>> login(Long macAddress,
                                                     String username, String password) {
        final User login = userService.login(username, password);
        if (login == null) {
            return ResponseEntity.ok(Map.of("message", "账号密码错误"));
        } else {
            long expireTime = 1;
            String userName = login.getUserName();
            String id = login.getId().toString();
            String token = userName + ";" + id + ";";
            if (macAddress != null) {
                token += macAddress;
                expireTime = tokenExpireTime;
            }
            token += ";";
            token =  DigestUtils.md5Hex(token + salt);
            stringRedisTemplate.opsForValue().set("token:" + token, id, expireTime, TimeUnit.HOURS);
            return ResponseEntity.ok(Map.of("data", Map.of("token", token)));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "获取用户信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
    })
    @Parameters({
            @Parameter(name = "token", required = true, description = "token", in = ParameterIn.HEADER,
                    schema = @Schema(type = "string")),
    })
    @PermissionConfirm
    public ResponseEntity<Map<String, UserDTO>> getInfo(@PathVariable("id") Long id) {
        return ResponseEntity.ok(Map.of("data", userService.getInfo(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "注销用户", description = "注销用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注销成功"),
    })
    @PermissionConfirm
    public ResponseEntity<Map<String, Long>> cancelUser(@PathVariable("id")Long id) {
        userService.cancelAccount(id);
        final UserVo userVo = new UserVo();
        userVo.setId(id);
        rabbitTemplate.convertAndSend(userExchange, "user.logout", userVo);
        return ResponseEntity.ok(Map.of("message", id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "更新用户信息")
    @Parameters({
            @Parameter(name = "filedName", required = true, description = "更新的字段名", in = ParameterIn.QUERY,
                    schema = @Schema(type = "string", allowableValues = {"userName", "password", "phoneNumber", "email", "vxNumber", "qqNumber", "signature"})),
            @Parameter(name = "value", required = true, description = "更新的值", in = ParameterIn.QUERY,
                    schema = @Schema(type = "string"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
    })
    @PermissionConfirm
    public ResponseEntity<Map<String , String >> updateUser(@PathVariable("id")Long id, String filedName, String value) {
        switch  (filedName) {
            case "userName" -> {
                if (userService.getUserByName(value) != null) {
                    return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
                }
                userService.updateUserName(id, value);
            }
            case "password" -> userService.updateUserPassword(id, value);
            case "phoneNumber" -> userService.updatePhoneNumber(id, value);
            case "email" -> userService.updateEmail(id, value);
            case "vxNumber" -> userService.updateVxNumber(id, value);
            case "qqNumber" -> userService.updateQqNumber(id, value);
            case "signature" -> userService.updateSignature(id, value);
        }
        if (filedName.equals("userName") || filedName.equals("signature")) {
            final UserVo userVo = new UserVo();
            userVo.setId(id);
            userVo.setFiledValue(filedName, value);
            rabbitTemplate.convertAndSend(userExchange, userUpdateRoutingKey, userVo);
        }
        return ResponseEntity.ok(Map.of("message", "update success"));
    }

    // TODO 这里以后用缓存来做
    @GetMapping("/search/{user}")
    @Operation(summary = "搜索用户", description = "搜索用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
    })
    public ResponseEntity<Map<String, Object>> searchUser(@PathVariable("user")String user,
                                                          @RequestParam(value = "page", required = false, defaultValue = "1")Integer page) {
        return ResponseEntity.ok(Map.of("data", userService.searchUser(user, page)));
    }

    @GetMapping("/logout/{id}")
    @Operation(summary = "用户登出", description = "用户登出")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登出成功"),
    })
    @Parameter(name = "token", required = true, description = "token", in = ParameterIn.HEADER,
            schema = @Schema(type = "string"))
    @PermissionConfirm
    public ResponseEntity<String> logout(@PathVariable("id")Long id,
                                         @Parameter(hidden = true) HttpServletRequest request) {
        final String token = request.getHeader("token");
        final String s = stringRedisTemplate.opsForValue().get("token:" + token);
        if (Objects.equals(s, id.toString())) {
            stringRedisTemplate.delete("token:" + token);
            return ResponseEntity.ok("logout success");
        } else {
            return ResponseEntity.badRequest().body("token error");
        }
    }

}
