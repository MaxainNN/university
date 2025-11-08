package io.mkalugin.university.config;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Конфигурация управления транзакциями и обработки ошибок кэширования.
 */
@Configuration
@EnableTransactionManagement(order = 0)
public class CacheConfig implements CachingConfigurer {

    /**
     * Обработчик ошибок кэширования.
     * Логирует ошибки без прерывания работы приложения.
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }
}
