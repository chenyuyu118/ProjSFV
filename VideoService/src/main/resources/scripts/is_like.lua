-- 查询是否喜欢一系列视频
for i=1, #ARGV do
    redis.call("ZADD", KEYS[2], 0, ARGV[i])
end
local length = redis.call("ZCARD", KEYS[1])
if length > #ARGV then
    length = #ARGV
end
local flag = redis.call("ZINTERSTORE", KEYS[3], length, KEYS[2], KEYS[1])
if flag == 0 then
    return {}
end
local result = redis.call("ZRANGE", KEYS[3], 0, -1)
redis.call("DEL", KEYS[2])
redis.call("DEL", KEYS[3])
return result