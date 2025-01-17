---
--- Generated by Luanalysis
--- Created by t.
--- DateTime: 2024/7/12 11:09
---
local voucherId = ARGV[1]
local userId = ARGV[2]
local oderId = ARGV[3]

local stockKey = "seckill:stock:" .. voucherId
local orderKey = "seckill:order:" .. voucherId

if(tonumber(redis.call("get", stockKey)) <= 0) then
    -- 库存不足
    return 1
end

if (redis.call("sismember",orderKey,userId)==1) then
    -- 重复下单
    return 2
end

-- 减库存
redis.call("incrby", stockKey, -1)
-- 下单，保存用户
redis.call("sadd", orderKey, userId)
-- 发送消息到队列中
redis.call("xadd","stream.orders","*","userId",userId,"voucherId",voucherId,"id",oderId)
return 0