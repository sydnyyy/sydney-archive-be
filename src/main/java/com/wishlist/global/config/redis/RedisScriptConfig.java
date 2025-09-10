package com.wishlist.global.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

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
            if redis.call('SCARD', KEYS[1]) == 0 then
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
            if redis.call('EXISTS', KEYS[2]) == 1 then
                return 0
            end

            redis.call('SET', KEYS[2], ARGV[1])

            local sessions = redis.call('SMEMBERS', KEYS[1])
            if #sessions == 0 then
                return 0
            end

            local timestamp = tonumber(ARGV[2])
            for i, sessionId in ipairs(sessions) do
                redis.call('ZADD', KEYS[3], timestamp, sessionId)
            end

            return #sessions
        """);
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> maintainSessionScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
            local status = redis.call('GET', KEYS[1])
            
            if status == 'PENDING' then
                redis.call('SET', KEYS[1], ARGV[1])
                redis.call('SET', KEYS[2], ARGV[2], 'EX', ARGV[3])
                return 1
            else
                return 0
            end
            """);
        script.setResultType(Long.class);
        return script;
    };
}
