package com.jsdoop.server.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Configuration
//@EnableCaching
public class MyRedisConfiguration {
    private final String HOSTNAME;

    private final int PORT;

//    private final int DATABASE;

//    private final String PASSWORD;

//    private final long TIMEOUT;
    
    public MyRedisConfiguration(
            @Value("${redis.hostname}") String hostname,
            @Value("${redis.port}") int port
//            ,
//            @Value("${redis.database}") int database,
//            @Value("${redis.password}") String password,
//            @Value("${redis.timeout}") long timeout
        ) {

            this.HOSTNAME = hostname;
            this.PORT = port;
//            this.DATABASE = database;
//            this.PASSWORD = password;
//            this.TIMEOUT = timeout;
        }
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory(GenericObjectPoolConfig genericObjectPoolConfig) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(HOSTNAME);
        config.setPort(PORT);
//        config.setDatabase(DATABASE);
//        config.setPassword(PASSWORD);

        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()//LettuceClientConfiguration.builder()
//            .commandTimeout(Duration.ofMillis(TIMEOUT))
        		.poolConfig(genericObjectPoolConfig)
            .build();
        


        return new LettuceConnectionFactory(config, clientConfig);
    }
    
//	@Bean
//	public RedisTemplate<String, byte[]> redisTemplate(RedisConnectionFactory connectionFactory) {
//	    RedisTemplate<String, byte[]> template = new RedisTemplate<>();
//	    template.setConnectionFactory(connectionFactory);
//	    // Add some specific configuration here. Key serializers, etc.
//	    
//	    return template;
//	}
	
    @Bean
    public RedisTemplate<String, byte[]> messagePackRedisTemplate(
        @Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory
    ) {

        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setEnableDefaultSerializer(false);
        
        template.setEnableTransactionSupport(true);
        ///////////////////////
	
	    
	    template.afterPropertiesSet();
        ///////////////////////
        
        return template;
    }
    
    //https://www.programmersought.com/article/4916471544/
    @Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(512);
        genericObjectPoolConfig.setMaxIdle(512);
        genericObjectPoolConfig.setMinIdle(16);
        genericObjectPoolConfig.setTestOnBorrow(true);
        genericObjectPoolConfig.setTestOnReturn(true);
	    genericObjectPoolConfig.setTestWhileIdle(true);
	    genericObjectPoolConfig.setTestOnCreate(true); ///
	    genericObjectPoolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
	    genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
	    genericObjectPoolConfig.setNumTestsPerEvictionRun(3);
	    genericObjectPoolConfig.setBlockWhenExhausted(true);	
        return genericObjectPoolConfig;
    }

//    @Bean
//    public ObjectMapper messagePackObjectMapper() {
//        return new ObjectMapper(new MessagePackFactory())
//            .registerModule(new JavaTimeModule())
//            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//    }
}
