package pers.cherish.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.response.MyResponse;
import pers.cherish.userservice.model.Profile;
import pers.cherish.userservice.model.UserVo;
import pers.cherish.userservice.service.ProfileService;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/user/profile")
@Tags(value = {
        @Tag(name = "用户"),
})
public class ProfileController {

    private final ProfileService profileService;

    private RabbitTemplate rabbitTemplate;

    @Value("${variable.rabbit.user-exchange-key}")
    private String userExchangeKey;
    @Value("${variable.rabbit.user-update-routing-key}")
    private String userUpdateRoutingKey;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired(required = false)
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户个人头像", description = "更新用户个人头像")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    @PermissionConfirm
    public ResponseEntity<MyResponse<String >> updateProfile(@PathVariable Long id, @RequestBody String newProfile) {
        final String profileId = UUID.randomUUID().toString();
        UserVo userVo = new UserVo();
        userVo.setProfile(profileId);
        userVo.setId(id);
        rabbitTemplate.convertAndSend(userExchangeKey, userUpdateRoutingKey, userVo);
        profileService.updateProfile(profileId, id, newProfile);
        return ResponseEntity.ok(MyResponse.ofData(profileId));
    }

    // 从历史头像中更新头像
    @PutMapping("/historical/{id}")
    @Operation(summary = "更新从历史头像")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "成功")
            }
    )
    @PermissionConfirm
    public ResponseEntity<MyResponse<Void>> updateProfileHistorical(@PathVariable Long id, @RequestBody String profileId) {
        if (profileService.isExist(id, profileId)) {
            profileService.updateProfileHistorical(id, profileId);
            return ResponseEntity.ok(MyResponse.ofMessage("success"));
        } else {
            return ResponseEntity.status(407).body(MyResponse.ofMessage("历史头像不存在"));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户历史头像", description = "获取用户历史头像")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<MyResponse<List<Profile>>> getHistoricalProfile(@PathVariable Long id) {
        return ResponseEntity.ok(MyResponse.ofData(profileService.getHistoricalProfiles(id)));
    }


    // 获取用户当前头像
    @GetMapping("/current/{id}")
    @Operation(summary = "获取用户当前头像", description = "获取用户当前头像")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Object> getCurrentProfile(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", profileService.getCurrentProfile(id)));
    }
}
