local v1 = redis.call('SISMEMBER', KEYS[1], ARG[1])
if v1 == 1 then return 1
else
    local v2 = redis.call("SISMEMBER", KEYS[2], ARG[2])
    if v2 == 1 then return 2
    end
    return 0
end