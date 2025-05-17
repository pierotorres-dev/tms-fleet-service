package com.dliriotech.tms.fleetservice.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveRedisCacheService implements ReactiveCacheService {
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.ttl-hours}")
    private long cacheTtlHours;

    @Override
    public <T> Flux<T> getCachedCollection(String cacheKey, Flux<T> dbFallback, TypeReference<List<T>> typeReference) {
        return redisTemplate.opsForValue().get(cacheKey)
                .cast(List.class)
                .flatMapMany(cachedList -> {
                    log.info("Obteniendo datos desde caché: {}", cacheKey);
                    try {
                        List<T> typedList = objectMapper.convertValue(cachedList, typeReference);
                        return Flux.fromIterable(typedList);
                    } catch (Exception e) {
                        log.warn("Error al convertir datos de caché {}: {}", cacheKey, e.getMessage());
                        return Flux.empty();
                    }
                })
                .switchIfEmpty(cacheCollection(cacheKey, dbFallback))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public <T> Flux<T> cacheCollection(String cacheKey, Flux<T> source) {
        Duration ttl = Duration.ofHours(cacheTtlHours);
        return source.collectList()
                .flatMapMany(list -> {
                    if (!list.isEmpty()) {
                        return redisTemplate.opsForValue().set(cacheKey, list, ttl)
                                .thenMany(Flux.fromIterable(list))
                                .doOnComplete(() -> log.info("Datos almacenados en caché: {}", cacheKey));
                    }
                    log.warn("No se encontraron datos para cachear: {}", cacheKey);
                    return Flux.empty();
                });
    }
}