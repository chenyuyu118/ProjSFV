-- 查询是否点赞评论
for i=1, #ARGV do
    redis.call("SADD", KEYS[2], ARGV[i])
end
local result = redis.call("SINTER", KEYS[2], KEYS[1])
redis.call("DEL", KEYS[2])
return result;