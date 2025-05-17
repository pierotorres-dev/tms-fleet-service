package com.dliriotech.tms.fleetservice.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ReactiveCacheService {
    <T> Flux<T> getCachedCollection(String cacheKey,
                                    Flux<T> dbFallback,
                                    TypeReference<List<T>> typeReference);

    <T> Flux<T> cacheCollection(String cacheKey, Flux<T> source);
}