package pers.cherish.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.userservice.service.ProfileService;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/user/profile")
@Tags(value = {
        @Tag(name = "用户"),
})
public class ProfileController {

    private final ProfileService profileService;

    @Value("${variable.profile-counter-key}")
    private String profileCounterKey;

    @Autowired(required = false)
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户个人头像", description = "更新用户个人头像")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Object> updateProfile(@PathVariable Long id, @RequestBody String newProfile) {
        final String profileId = UUID.randomUUID().toString();
        profileService.updateProfile(profileId, id, newProfile);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户历史头像", description = "获取用户历史头像")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Object> getHistoricalProfile(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", profileService.getHistoricalProfiles(id)));
    }
}
