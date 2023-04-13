-- 查询是否收藏视频
for i=1, #ARGV do
    redis.call("SADD", KEYS[2], ARGV[i])
end
local length = redis.call("SCARD", KEYS[1])
if length > #ARGV then
    length = #ARGV
end
local result = redis.call("SINTER", KEYS[2], KEYS[1])
redis.call("DEL", KEYS[2])
return result;