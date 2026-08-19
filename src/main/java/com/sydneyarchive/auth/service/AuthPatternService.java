package com.sydneyarchive.auth.service;

import com.sydneyarchive.global.config.auth.PatternProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthPatternService {

    private static final String PATTERN_KEY_PREFIX = "auth:pattern:userid:";
    private final PatternProperties patternProperties;

    private final StringRedisTemplate redisTemplate;

    public boolean validatePattern(String userId, String pattern, String patternSlot) {
        if (userId == null || pattern == null ||  patternSlot == null) {
            return false;
        }

        int patternMaxLength = patternProperties.unit() * patternProperties.multiple();
        int patternSwitchThreshold = (int) (patternMaxLength * patternProperties.switchThreshold());

        if (pattern.isBlank() || pattern.length() > patternMaxLength) {
            return false;
        }

        if (!pattern.matches("[0-9a-z]+")) {
            return false;
        }

        String patternKey = PATTERN_KEY_PREFIX + userId;

        String script = """
                local patternKey = KEYS[1]

                local requestedPattern = ARGV[1]
                local requestedLength = string.len(requestedPattern)
                
                local requestedSlot = tonumber(ARGV[2])
                local slot = requestedSlot % 2
                local oppositeSlot = 1 - slot

                local patternUnit = tonumber(ARGV[3])
                local switchThreshold = tonumber(ARGV[4])

                local patternField = "slot-" .. slot .. ":pattern"
                local visitField0 = "slot-" .. slot .. ":patternVisit:0"
                local visitField1 = "slot-" .. slot .. ":patternVisit:1"
                local visitField2 = "slot-" .. slot .. ":patternVisit:2"
                local countField = "slot-" .. slot .. ":count"
                local oppositePatternField = "slot-" .. oppositeSlot .. ":pattern"
                local oppositeCountField = "slot-" .. oppositeSlot .. ":count"

                local storedPattern = redis.call("HGET", patternKey, patternField)

                local visitGroup = math.floor((requestedLength - 1) / patternUnit)

                local bitIndex = (requestedLength - 1) % patternUnit
                local bitValue = bit.lshift(1, bitIndex)

                local function setInitialVisit()
                    if visitGroup == 0 then
                        redis.call("HSET", patternKey, visitField0, bitValue)
                    elseif visitGroup == 1 then
                        redis.call("HSET", patternKey, visitField1, bitValue)
                    else
                        redis.call("HSET", patternKey, visitField2, bitValue)
                    end
                end

                if not storedPattern then
                    if requestedSlot == 0 then
                        redis.call("HSET", patternKey, patternField, requestedPattern)
                        setInitialVisit()
                        redis.call("HSET", patternKey, countField, 1)
                        return 1
                    end
                
                     local oppositePattern = redis.call("HGET", patternKey, oppositePatternField)
                     if not oppositePattern then
                        return 0
                     end
                
                     local oppositeCount = tonumber(redis.call("HGET", patternKey, oppositeCountField) or "0")
                     if oppositeCount <= switchThreshold then
                        return 0
                     end
                
                     redis.call("HSET", patternKey, patternField, requestedPattern)
                     setInitialVisit()
                     redis.call("HSET", patternKey, countField, 1)
                     return 1
                end

                local storedLength = string.len(storedPattern)
                local patternMatched
                if storedLength <= requestedLength then
                    patternMatched = string.sub(requestedPattern, 1, storedLength) == storedPattern
                else
                    patternMatched = string.sub(storedPattern, 1, requestedLength) == requestedPattern
                end

                if not patternMatched then
                    local oppositePattern = redis.call("HGET", patternKey, oppositePatternField)
           
                    if not oppositePattern then
                        return 0
                    end

                    local oppositeCount = tonumber(redis.call("HGET", patternKey, oppositeCountField) or "0")
                    if oppositeCount <= switchThreshold then
                        return 0
                    end

                    redis.call("HSET", patternKey, patternField, requestedPattern)
                    redis.call("HSET", patternKey, visitField0, 0, visitField1, 0, visitField2, 0)
                    setInitialVisit()

                     redis.call("HSET", patternKey, countField, 1)
                     return 1
                end

                if requestedLength > storedLength then
                    redis.call("HSET", patternKey, patternField, requestedPattern)
                end

                local currentVisit
                if visitGroup == 0 then
                    currentVisit = tonumber(redis.call("HGET", patternKey, visitField0) or "0")
                elseif visitGroup == 1 then
                    currentVisit = tonumber(redis.call("HGET", patternKey, visitField1) or "0")
                else
                    currentVisit = tonumber(redis.call("HGET", patternKey, visitField2) or "0")
                end
           
                if bit.band(currentVisit, bitValue) ~= 0 then
                    return 0
                end

                local newVisit = bit.bor(currentVisit, bitValue)
                if visitGroup == 0 then
                    redis.call("HSET", patternKey, visitField0, newVisit)
                elseif visitGroup == 1 then
                    redis.call("HSET", patternKey, visitField1, newVisit)
                else
                    redis.call("HSET", patternKey, visitField2, newVisit)
                end

                redis.call("HINCRBY", patternKey, countField, 1)
      
                return 1
                """;

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(patternKey),
                pattern,
                patternSlot,
                String.valueOf(patternProperties.unit()),
                String.valueOf(patternSwitchThreshold)
        );

        return result == 1L;
    }

    public void deletePattern(String userId) {
        redisTemplate.delete(PATTERN_KEY_PREFIX + userId);
    }
}
