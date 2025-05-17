package com.dliriotech.tms.fleetservice.infrastructure.cache;

import com.dliriotech.tms.fleetservice.exception.CacheOperationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
    public <T> Flux<T> getCachedCollection(String cacheKey, Flux<T> dbFallback,
                                           TypeReference<List<T>> typeReference) {
        return redisTemplate.opsForValue().get(cacheKey)
                .cast(List.class)
                .flatMapMany(cachedList -> {
                    try {
                        log.info("Obteniendo datos desde caché: {}", cacheKey);
                        return deserializeOnCorrectScheduler(cachedList, typeReference, cacheKey);
                    } catch (Exception e) {
                        throw new CacheOperationException(cacheKey);
                    }
                })
                .onErrorResume(error -> {
                    if (error instanceof CacheOperationException) {
                        log.error("Error específico de caché: {}", error.getMessage());
                        return Flux.error(error);
                    }
                    log.error("Error al recuperar datos de caché {}: {}", cacheKey, error.getMessage());
                    return Flux.empty();
                })
                .switchIfEmpty(cacheCollection(cacheKey, dbFallback));
    }

    @Override
    public <T> Flux<T> cacheCollection(String cacheKey, Flux<T> source) {
        Duration ttl = Duration.ofHours(cacheTtlHours);
        return source
                .collectList()
                .flatMapMany(list -> {
                    if (list.isEmpty()) {
                        log.warn("No se encontraron datos para cachear: {}", cacheKey);
                        return Flux.empty();
                    }

                    return redisTemplate.opsForValue()
                            .set(cacheKey, list, ttl)
                            .then(Mono.just(list))
                            .flatMapMany(Flux::fromIterable)
                            .doOnComplete(() -> log.info("Datos almacenados en caché: {}", cacheKey));
                });
    }

    private <T> Flux<T> deserializeOnCorrectScheduler(List<?> cachedList,
                                                      TypeReference<List<T>> typeRef,
                                                      String cacheKey) {
        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.convertValue(cachedList, typeRef);
                    } catch (Exception e) {
                        log.warn("Error al convertir datos de caché {}: {}", cacheKey, e.getMessage());
                        return List.<T>of();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }
}