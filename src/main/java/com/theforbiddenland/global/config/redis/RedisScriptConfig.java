package com.theforbiddenland.global.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

@Configuration
public class RedisScriptConfig {

    @Bean
    public DefaultRedisScript<Long> addSessionScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
            redis.call('SADD', KEYS[1], ARGV[1])
            redis.call('EXPIRE', KEYS[1], ARGV[2])
            redis.call('SET', KEYS[2], ARGV[3], 'EX', ARGV[4])
            return 1
        """);
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> updateTTLScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
            redis.call('EXPIRE', KEYS[1], ARGV[1])
            redis.call('EXPIRE', KEYS[2], ARGV[2])
            return 1
        """);
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> removeSessionScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
            redis.call('SREM', KEYS[1], ARGV[1])
            if redis.call('EXISTS', KEYS[1]) == 0 or redis.call('SCARD', KEYS[1]) == 0 then
                redis.call('DEL', KEYS[1])
                redis.call('DEL', KEYS[2])
            end
            return 1
        """);
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> terminateSignalExpiryScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
            return redis.call('SETNX', KEYS[1], ARGV[1])
        """);
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> maintainSessionScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
            local zsetScore = redis.call('ZSCORE', KEYS[1], ARGV[1])
            
            if not zsetScore then
                redis.call('SET', KEYS[2], ARGV[2], 'EX', ARGV[3])
                redis.call('EXPIRE', KEYS[3], ARGV[4])
                return 1
            else
                return 0
            end
            """);
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> terminateSessionScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
            local exists = redis.call('ZSCORE', KEYS[1], ARGV[1])
            if not exists then
                local timestamp = tonumber(ARGV[2])
                redis.call('ZADD', KEYS[1], timestamp, ARGV[1])
                redis.call('XADD', KEYS[2], '*', 'sid', ARGV[1])
                return 1
            else
                return 0
            end
        """);
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<List> terminateCheckScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText("""
            local zsetKey = KEYS[1]
            local streamKey = KEYS[2]
            local lockKey = KEYS[3]

            local now = tonumber(ARGV[1])
            local safeMillis = tonumber(ARGV[2])
            local serverId = ARGV[3]
            local lockExpireMillis = tonumber(ARGV[4])
        
            local lockSet = redis.call('SET', lockKey, serverId, 'PX', lockExpireMillis, 'NX')
            if not lockSet then
                return {}
            end

            local expiredSids = redis.call('ZRANGEBYSCORE', zsetKey, 0, now - safeMillis)
            local processed = {}

            for _, sid in ipairs(expiredSids) do
                local mainKey = "WS:MAIN:" .. sid
                local signalKey = "WS:TERMINATE_SIGNAL:" .. sid
        
                local exists = redis.call('EXISTS', mainKey)
                local sessions = {}
                if exists == 1 then
                    sessions = redis.call('SMEMBERS', mainKey)
                end
     
                redis.call('ZREM', zsetKey, sid)
        
                if exists == 1 and #sessions > 0 then
                    redis.call('ZADD', zsetKey, now, sid)
                    redis.call('XADD', streamKey, '*', 'sid', sid)
                else
                    redis.call('DEL', mainKey)
                    redis.call('DEL', signalKey)
                end

                table.insert(processed, sid)
            end
        
            if redis.call('GET', lockKey) == serverId then
                redis.call('DEL', lockKey)
            end
        
            return processed
        """);
        script.setResultType(List.class);
        return script;
    }
}
