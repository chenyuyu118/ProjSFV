package pers.cherish.userservice.listener;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import pers.cherish.userservice.domain.FriendProjection;
import pers.cherish.userservice.domain.LongWrapper;
import pers.cherish.userservice.model.UserVo;
import pers.cherish.userservice.service.FriendService;
import pers.cherish.userservice.service.UserService;

import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Component
public class UserListener {

    private FriendService friendService;

    private MongoTemplate mongoTemplate;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    public void setFriendService(FriendService friendService) {
        this.friendService = friendService;
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "user.friend", durable = "true"),
                    exchange = @Exchange(value = "${variable.rabbit.user-friend-exchange-key}", type = "topic"),
                    key = {"user.friends.*"}
            )
    })
    public void listenUserFriend(MessageProperties messageProperties, FriendProjection meg) {
        final String[] split = messageProperties.getReceivedRoutingKey().split("\\.");
        String operate = split[split.length - 1];
        switch (operate) {
            case "init" -> {
                List<LongWrapper> friendList = friendService.getFriendIdListForCache(meg.getId())
                        .stream().map(LongWrapper::new).toList();
                if (friendList.size() != 0 && !mongoTemplate.collectionExists(meg.getId() + "-friends")) {
                    mongoTemplate.insert(friendList, meg.getId() + "-friends");
                } else if (friendList.size() != 0 && mongoTemplate.collectionExists(meg.getId() + "-friends")) {
                    mongoTemplate.insert(friendList, meg.getId() + "-friends");
                }
            }
            case "add" -> {
                friendService.relateWithOther(meg.getId(), meg.getOtherId(), FriendService.RelationMessage.ACCEPT);
                mongoTemplate.insert(LongWrapper.of(meg.getOtherId()), meg.getId() + "-friends");
                mongoTemplate.insert(LongWrapper.of(meg.getId()), meg.getOtherId() + "-friends");
            }
            case "delete" -> {
                friendService.relateWithOther(meg.getId(), meg.getOtherId(), FriendService.RelationMessage.DELETE);
                mongoTemplate.remove(LongWrapper.of(meg.getOtherId()), meg.getId() + "-friends");
            }
        }
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "user.simple", durable = "true"),
                    exchange = @Exchange(value = "${variable.rabbit.user-exchange-key}", type = "topic"),
                    key = {"user.*"}
            )
    })
    public void listenUser(MessageProperties messageProperties, UserVo userVo) {
        final String[] split = messageProperties.getReceivedRoutingKey().split("\\.");
        String operate = split[split.length - 1];
        switch (operate) {
            case "register" -> {
                System.out.println("listenUser: " + userVo);
                if (!mongoTemplate.exists(query(where("_id").is(userVo.getId())), "user")) {
                    mongoTemplate.insert(userVo, "user");
                } else {
                    mongoTemplate.remove(query(where("_id").is(userVo.getId())), "user");
                    mongoTemplate.insert(userVo, "user");
                }
            }
            case "delete" ->
                mongoTemplate.updateFirst(query(where("_id").is(userVo.getId())),
                        update("isDeleted", true), "user");
            case "update" -> {
                final Map<String, String> map = userVo.gNotNullFieldMap();
                mongoTemplate.updateFirst(query(where("_id").is(userVo.getId())),
                        update(map.get("field"), map.get("value")), "user");
            }
            case "init" -> {
                final List<UserVo> userVo1 = userService.getAllUserVo();
                if (userVo1.size() != 0 && !mongoTemplate.collectionExists("user")) {
                    mongoTemplate.insert(userVo1, "user");
                }
            }
        }
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "user.follow", durable = "true"),
                    exchange = @Exchange(value = "${variable.rabbit.user-fan&follow-exchange-key}", type = "topic"),
                    key = {"user.follows.*", "user.fans.*"}
                    )
            }
    )
    public void listenUserFollow(MessageProperties messageProperties, FriendProjection meg) {
        final String[] split = messageProperties.getReceivedRoutingKey().split("\\.");
        String operate = split[split.length - 1];
        switch (operate) {
            case "init" -> {
                List<LongWrapper> followList = friendService.getFollowsIdListForCache(meg.getId())
                        .stream().map(LongWrapper::new).toList();
                List<LongWrapper> fansIdListForCache = friendService.getFansIdListForCache(meg.getId())
                        .stream().map(LongWrapper::new).toList();
                if (followList.size() != 0 && !mongoTemplate.collectionExists(meg.getId() + "-follows")) {
                    mongoTemplate.insert(followList, meg.getId() + "-follows");
                } else if (followList.size() != 0 && mongoTemplate.collectionExists(meg.getId() + "-follows")) {
                    mongoTemplate.getCollection(meg.getId() + "-follows").drop();
                    mongoTemplate.insert(followList, meg.getId() + "-follows");
                }
                if (fansIdListForCache.size() != 0 && !mongoTemplate.collectionExists(meg.getId() + "-fans")) {
                    mongoTemplate.insert(fansIdListForCache, meg.getId() + "-fans");
                } else if (fansIdListForCache.size() != 0 && mongoTemplate.collectionExists(meg.getId() + "-fans")) {
                    mongoTemplate.dropCollection(meg.getId() + "-fans");
                    mongoTemplate.insert(fansIdListForCache, meg.getId() + "-fans");
                }
            }
            case "add" -> {
                friendService.follow(meg.getId(), meg.getOtherId());
                String collectionName = meg.getId() + "-follows";
                if (!mongoTemplate.exists(query(where("_id").is(meg.getOtherId())), collectionName)) {
                    mongoTemplate.insert(LongWrapper.of(meg.getOtherId()),
                            collectionName);
                }
                String collectionName1 = meg.getOtherId() + "-fans";
                if (!mongoTemplate.exists(query(where("_id").is(meg.getId())), collectionName1)) {
                    mongoTemplate.insert(LongWrapper.of(meg.getId()),
                            collectionName1);
                }
            }
            case "delete" -> {
                friendService.unfollow(meg.getId(), meg.getOtherId());
                mongoTemplate.remove(LongWrapper.of(meg.getOtherId()),
                        meg.getId() + "-follows");
                mongoTemplate.remove(LongWrapper.of(meg.getId()),
                        meg.getOtherId() + "-fans");
            }
        }
    }
}
