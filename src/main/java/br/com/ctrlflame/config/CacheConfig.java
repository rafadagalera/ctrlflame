package br.com.ctrlflame.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Spring will use its default cache implementation
    // For production, consider using Redis or another distributed cache
} 