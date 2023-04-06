package pers.cherish.userservice.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.cherish.annotation.PermissionConfirm;
import pers.cherish.response.MyResponse;
import pers.cherish.userservice.domain.Relation;
import pers.cherish.userservice.domain.UserBasicInfo;
import pers.cherish.userservice.model.UserVo;
import pers.cherish.userservice.service.FriendService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequestMapping("/user/")
@RestController
@Tags(value = {
        @Tag(name = "用户"),
})
public class FriendsController {
    @Value("${variable.page.friend-page-size}")
    private static int friendPageSize = 10;
    @Value("${variable.page.fans-page-size}")
    private static int fansPageSize = 10;
    @Value("${variable.page.blocked-page-size}")
    private static int blockedPageSize = 10;
    private final FriendService friendService;
    private final StringRedisTemplate redisTemplate;
    private final MongoTemplate mongoTemplate;
    private final RabbitTemplate rabbitTemplate;
    @Value("${variable.rabbit.user-friend-exchange-key}")
    private String userFriendExchangeKey;
    @Value("${variable.rabbit.user-friend-routing-key-prefix}")
    private String userFriendRoutingKeyPrefix;
    @Value("${variable.rabbit.user-fan&follow-exchange-key}")
    private String userFanExchangeKey;
    @Value("${variable.rabbit.user-fan-routing-key-prefix}")
    private String userFanRoutingKeyPrefix;
    @Value("${variable.rabbit.user-follow-routing-key-prefix}")
    private String userFollowRoutingKeyPrefix;


    @Autowired
    public FriendsController(FriendService friendService, StringRedisTemplate redisTemplate,
                             MongoTemplate mongoTemplate, RabbitTemplate rabbitTemplate ) {
        this.friendService = friendService;
        this.redisTemplate = redisTemplate;
        this.mongoTemplate = mongoTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/friends/{id}")
    @Operation(summary = "获取好友列表", description = "获取好友列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Map<String , List<UserVo>>> getFriends(@PathVariable Long id, @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        String collectionName = id + "-friends";
        if (mongoTemplate.collectionExists(collectionName)) {
            final Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.skip((long) (page - 1) * friendPageSize),
                    Aggregation.limit((long) page * friendPageSize),
                    Aggregation.lookup("user", "_id", "_id", "friend"),
                    Aggregation.unwind("$friend"),
                    Aggregation.project("friend").andExclude("_id"),
                    Aggregation.replaceRoot("friend")
            );
            final AggregationResults<UserVo> aggregate = mongoTemplate.aggregate(aggregation, collectionName, UserVo.class);
//            final FindIterable<UserVo> userVos = mongoTemplate.getCollection(collectionName).find(UserVo.class)
//                    .skip(page * (friendPageSize - 1)).limit(page * friendPageSize);
//            List<UserVo> result = new ArrayList<>();
//            userVos.forEach(result::add);
            return ResponseEntity.ok(Map.of("data", aggregate.getMappedResults()));
        } else {
            final JSONObject meg = JSONObject.of("id", id);
            rabbitTemplate.convertAndSend(userFriendExchangeKey, userFriendRoutingKeyPrefix + "init", meg);
            final List<UserVo> friendList = friendService.getFriendList(id, page, friendPageSize);
            return ResponseEntity.ok(Map.of("data", friendList));
        }
    }

    @PostMapping("/friends/{id}")
    @Operation(summary = "添加好友", description = "添加好友")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "添加好友请求体",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class), schemaProperties = {
                    @SchemaProperty(name = "id", schema = @Schema(type = "string", description = "好友id")),
                    @SchemaProperty(name = "message", schema = @Schema(type = "string", description = "请求信息",
                            allowableValues = {"request", "accept", "reject", "delete"}))
            }, examples = {
                    @ExampleObject(name = "添加好友", value = "{\"id\": \"1\", \"message\": \"request\"}"),
                    @ExampleObject(name = "接受好友", value = "{\"id\": \"1\", \"message\": \"accept\"}"),
                    @ExampleObject(name = "拒绝好友", value = "{\"id\": \"1\", \"message\": \"reject\"}"),
                    @ExampleObject(name = "删除好友", value = "{\"id\": \"1\", \"message\": \"delete\"}")
            }))
    @PermissionConfirm
    public ResponseEntity<Object> relateWithOther(@PathVariable Long id, @RequestBody Map<String, String> req) {
        final String message = req.get("message");
        switch (message) {
            case "request" -> {
                String key = "message:friend:" + req.get("id");
                String meg = "req:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                redisTemplate.opsForHash().put(key, String.valueOf(id), meg);
            }
            case "accept" -> {
                String myKey = "message:friend:" + id;
                String myMeg = (String) redisTemplate.opsForHash().get(myKey, req.get("id"));
                if (myMeg == null) {
                    return ResponseEntity.badRequest().body("对方未发送好友请求");
                }
                myMeg += ":close:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                redisTemplate.opsForHash().put(myKey, req.get("id"), myMeg);
                String otherKey = "message:friend:" + req.get("id");
                String meg = "acp:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                rabbitTemplate.convertAndSend(userFriendExchangeKey, userFriendRoutingKeyPrefix + "add",
                        JSONObject.of("id", id, "otherId", req.get("id")));
//                friendService.relateWithOther(id, Long.parseLong(req.get("id")), FriendService.RelationMessage.ACCEPT);
                redisTemplate.opsForHash().put(otherKey, String.valueOf(id), meg);
            }
            case "reject" -> {
                String myKey = "message:friend:" + id;
                String myMeg = (String) redisTemplate.opsForHash().get(myKey, req.get("id"));
                if (myMeg == null) {
                    return ResponseEntity.badRequest().body("对方未发送好友请求");
                }
                myMeg += ":rej:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                redisTemplate.opsForHash().putIfAbsent(myKey, req.get("id"), myMeg);
                String key = "message:friend:" + req.get("id");
                String meg = "rej:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                redisTemplate.opsForHash().entries(key).put(id, meg);
            }
            case "delete" ->
                rabbitTemplate.convertAndSend(userFriendExchangeKey, userFriendRoutingKeyPrefix + "delete",
                        JSONObject.of("id", id, "otherId", req.get("id")));
        }
        return ResponseEntity.ok().body(Map.of("message", "success"));
    }

    @GetMapping("/friends/message/{id}")
    @Operation(summary = "获取好友请求消息", description = "获取好友消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    @PermissionConfirm
    public ResponseEntity<Object> getFriendRequestMessage(@PathVariable Long id) {
        String key = "message:friend:" + id;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return ResponseEntity.ok().body(Map.of("data", entries));
    }

    @GetMapping("/fans/{id}")
    @Operation(summary = "获取粉丝列表", description = "获取粉丝列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Map<String , List<UserVo>>> getFans(@PathVariable Long id, @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        String collectionName = id + "-fans";
        if (mongoTemplate.collectionExists(collectionName)) {
            final Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.skip((long) (page - 1) * fansPageSize),
                    Aggregation.limit((long) fansPageSize * page),
                    Aggregation.lookup("user", "_id", "_id", "fans"),
                    Aggregation.unwind("fans"),
                    Aggregation.project("fans").andExclude("_id"),
                    Aggregation.replaceRoot("fans")
            );
            final AggregationResults<UserVo> userVos = mongoTemplate.aggregate(aggregation, collectionName, UserVo.class);
            return ResponseEntity.ok(Map.of("data", userVos.getMappedResults()));
        } else {
            final JSONObject meg = JSONObject.of("id", id);
            rabbitTemplate.convertAndSend(userFanExchangeKey, userFanRoutingKeyPrefix + "init", meg);
            return ResponseEntity.ok(Map.of("data",
                    friendService.getFans(id, page, fansPageSize)));
        }
    }

    @GetMapping("/subscriber/{id}")
    @Operation(summary = "获取关注列表", description = "获取关注列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Map<String , List<UserVo>>> getFollows(@PathVariable Long id, @RequestParam(value = "page",
            required = false, defaultValue = "1") int page) {
        String collectionName = id + "-follows";
        if (mongoTemplate.collectionExists(collectionName)) {
            final Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.skip((long) (page - 1) * fansPageSize),
                    Aggregation.limit((long) fansPageSize * page),
                    Aggregation.lookup("user", "_id", "_id", "follows"),
                    Aggregation.unwind("follows"),
                    Aggregation.project("follows").andExclude("_id"),
                    Aggregation.replaceRoot("follows")
            );
            final AggregationResults<UserVo> userVos = mongoTemplate.aggregate(aggregation, collectionName, UserVo.class);
            return ResponseEntity.ok(Map.of("data", userVos.getMappedResults()));
        } else {
            final JSONObject meg = JSONObject.of("id", id);
            rabbitTemplate.convertAndSend(userFanExchangeKey, userFollowRoutingKeyPrefix + "init", meg);
            return ResponseEntity.ok(Map.of("data",
                    friendService.getFollows(id, page, fansPageSize)));
        }
    }

    @PostMapping("/subscriber/{id}")
    @Operation(summary = "关注", description = "关注")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "关注的用户id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
            schemaProperties = {
                    @SchemaProperty(name = "id", schema = @Schema(type = "string"))
            }, examples = {
                    @ExampleObject(name = "example", value = "{\"id\": \"1\"}")
            }))
    public ResponseEntity<Object> follow(@PathVariable Long id, @RequestBody Map<String, String> req) {
        if (mongoTemplate.findOne(Query.query(where("_id").is(req.get("id"))), UserVo.class, "user") == null)
            return ResponseEntity.badRequest().body(Map.of("message", "用户不存在"));
        rabbitTemplate.convertAndSend(userFanExchangeKey, userFollowRoutingKeyPrefix + "add",
                JSONObject.of("id", id, "otherId", req.get("id")));
//        friendService.follow(id, Long.parseLong(req.get("id")));
        return ResponseEntity.ok().body(Map.of("message", "success"));
    }

    @DeleteMapping("/subscriber/{id}")
    @Operation(summary = "取消关注", description = "取消关注")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Object> unfollow(@PathVariable Long id, @RequestBody Map<String, String> req) {
        rabbitTemplate.convertAndSend(userFanExchangeKey, userFollowRoutingKeyPrefix + "delete",
                JSONObject.of("id", id, "otherId", req.get("id")));
        return ResponseEntity.ok().body(Map.of("message", "success"));
    }

    @GetMapping("/blocked/{id}")
    @Operation(summary = "获取黑名单列表", description = "获取黑名单列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Map<String , List<UserVo>>> getBlockedList(@PathVariable Long id, @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        return ResponseEntity.ok(Map.of("data",
                friendService.getBlockedList(id, page, blockedPageSize)));
    }

    @PostMapping("/blocked/{id}")
    @Operation(summary = "拉黑", description = "拉黑")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Object> setUserBlockedList(@PathVariable Long id, @RequestBody Map<String, String> req) {
        String message = req.get("message");
        switch (message) {
            case "free" -> friendService.deleteFromBlockedList(id, Long.parseLong(req.get("id")));
            case "req" -> friendService.addToBlockedList(id, Long.parseLong(req.get("id")));
        }
        return ResponseEntity.ok().body(Map.of("message", "success"));
    }

    @GetMapping("/basicInfo/{id}")
    @Operation(summary = "获取用户基本信息", description = "获取用户基本信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
    })
    public ResponseEntity<Map<String, Object>> getBasicInfo(@PathVariable Long id) {
        if (mongoTemplate.collectionExists("user")) {
            UserBasicInfo basicInfo = new UserBasicInfo();
            String from1 = id + "-follows";
            String from2 = id + "-fans";
            final List<UserVo> userVos = mongoTemplate.find(Query.query(where("id").is(id)), UserVo.class, "user");
            BeanUtils.copyProperties(userVos.get(0), basicInfo);
            final long followCount = mongoTemplate.getCollection(from1).countDocuments();
            long fansCount = mongoTemplate.getCollection(from2).countDocuments();
            basicInfo.setFollowsCount(followCount);
            basicInfo.setFansCount(fansCount);
            return ResponseEntity.ok(Map.of("data", basicInfo));
        } else {
            rabbitTemplate.convertAndSend(userFanExchangeKey, userFanRoutingKeyPrefix + "init",
                    JSONObject.of("id", id));
            return ResponseEntity.ok(Map.of("data", friendService.getBasicInfo(id)));
        }
    }

    // 获取用户相关的好友信息
    @GetMapping("/relation/{id}/{otherId}")
    @PermissionConfirm
    public ResponseEntity<MyResponse<List<Boolean>>> getAllRelations(@PathVariable long id, @PathVariable long otherId) {
        List<Boolean> result = new ArrayList<>();
        //        String collectionName = id + "-follows";
//        if (mongoTemplate.collectionExists(collectionName)) {
//            boolean exists1 = mongoTemplate.exists(Query.query(where("id").is(otherId)), collectionName);
//            result.add(0, exists1);
//        } else {
//
//        }
        Relation relation = friendService.getUserRelation(id, otherId);
        result.add(0, relation.isFriend(id, otherId));
        result.add(1, relation.isFollow(id, otherId));
        result.add(2, friendService.isBlockedFriend(id, otherId));
        return ResponseEntity.ok(MyResponse.ofData(result));
    }
}
