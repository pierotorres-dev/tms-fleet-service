package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.dto.EsquemaEquipoResponse;
import com.dliriotech.tms.fleetservice.dto.EstadoEquipoResponse;
import com.dliriotech.tms.fleetservice.dto.TipoEquipoResponse;
import com.dliriotech.tms.fleetservice.exception.EquipoException;
import com.dliriotech.tms.fleetservice.repository.EsquemaEquipoRepository;
import com.dliriotech.tms.fleetservice.repository.TipoEquipoRepository;
import com.dliriotech.tms.fleetservice.service.EquipoEntityCacheService;
import com.dliriotech.tms.fleetservice.service.EstadoEquipoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipoEntityCacheServiceImpl implements EquipoEntityCacheService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final EstadoEquipoService estadoEquipoService;
    private final TipoEquipoRepository tipoEquipoRepository;
    private final EsquemaEquipoRepository esquemaEquipoRepository;

    @Value("${app.cache.ttl-hours}")
    private long cacheTtlHours;

    @Value("${app.cache.prefixes.estado-equipo:cache:estadoEquipo}")
    private String estadoCachePrefix;

    @Value("${app.cache.prefixes.tipo-equipo:cache:tipoEquipo}")
    private String tipoCachePrefix;

    @Value("${app.cache.prefixes.esquema-equipo:cache:esquemaEquipo}")
    private String esquemaCachePrefix;

    @Override
    public Mono<EstadoEquipoResponse> getEstadoEquipo(Integer estadoId) {
        String cacheKey = buildCacheKey(estadoCachePrefix, estadoId);
        
        return getCachedEntity(cacheKey, EstadoEquipoResponse.class)
                .switchIfEmpty(estadoEquipoService.getAllEstadoEquipo()
                        .filter(estado -> estado.getId().equals(estadoId))
                        .next()
                        .switchIfEmpty(Mono.error(new EquipoException(
                                "FLEET-EQP-NF-001", "Estado de equipo no encontrado: " + estadoId)))
                        .flatMap(estado -> cacheEntity(cacheKey, estado)
                                .thenReturn(estado)))
                .doOnSuccess(estado -> log.debug("Estado de equipo {} obtenido desde cache/DB", estadoId))
                .doOnError(error -> log.error("Error al obtener estado de equipo {}: {}", estadoId, error.getMessage()));
    }

    @Override
    public Mono<TipoEquipoResponse> getTipoEquipo(Integer tipoEquipoId) {
        String cacheKey = buildCacheKey(tipoCachePrefix, tipoEquipoId);
        
        return getCachedEntity(cacheKey, TipoEquipoResponse.class)
                .switchIfEmpty(tipoEquipoRepository.findById(tipoEquipoId)
                        .switchIfEmpty(Mono.error(new EquipoException(
                                "FLEET-EQP-NF-002", "Tipo de equipo no encontrado: " + tipoEquipoId)))
                        .map(tipoEquipo -> TipoEquipoResponse.builder()
                                .id(tipoEquipo.getId())
                                .nombre(tipoEquipo.getNombre())
                                .descripcion(tipoEquipo.getDescripcion())
                                .build())
                        .flatMap(tipoResponse -> cacheEntity(cacheKey, tipoResponse)
                                .thenReturn(tipoResponse)))
                .doOnSuccess(tipo -> log.debug("Tipo de equipo {} obtenido desde cache/DB", tipoEquipoId))
                .doOnError(error -> log.error("Error al obtener tipo de equipo {}: {}", tipoEquipoId, error.getMessage()));
    }

    @Override
    public Mono<EsquemaEquipoResponse> getEsquemaEquipo(Integer esquemaEquipoId) {
        String cacheKey = buildCacheKey(esquemaCachePrefix, esquemaEquipoId);
        
        return getCachedEntity(cacheKey, EsquemaEquipoResponse.class)
                .switchIfEmpty(esquemaEquipoRepository.findById(esquemaEquipoId)
                        .switchIfEmpty(Mono.error(new EquipoException(
                                "FLEET-EQP-NF-003", "Esquema de equipo no encontrado: " + esquemaEquipoId)))
                        .map(esquemaEquipo -> EsquemaEquipoResponse.builder()
                                .id(esquemaEquipo.getId())
                                .nombreEsquema(esquemaEquipo.getNombreEsquema())
                                .totalPosiciones(esquemaEquipo.getTotalPosiciones())
                                .build())
                        .flatMap(esquemaResponse -> cacheEntity(cacheKey, esquemaResponse)
                                .thenReturn(esquemaResponse)))
                .doOnSuccess(esquema -> log.debug("Esquema de equipo {} obtenido desde cache/DB", esquemaEquipoId))
                .doOnError(error -> log.error("Error al obtener esquema de equipo {}: {}", esquemaEquipoId, error.getMessage()));
    }

    @Override
    public Mono<Void> invalidateEstadoEquipo(Integer estadoId) {
        String cacheKey = buildCacheKey(estadoCachePrefix, estadoId);
        return redisTemplate.delete(cacheKey)
                .doOnSuccess(result -> log.debug("Cache invalidado para estado de equipo {}", estadoId))
                .then();
    }

    @Override
    public Mono<Void> invalidateTipoEquipo(Integer tipoEquipoId) {
        String cacheKey = buildCacheKey(tipoCachePrefix, tipoEquipoId);
        return redisTemplate.delete(cacheKey)
                .doOnSuccess(result -> log.debug("Cache invalidado para tipo de equipo {}", tipoEquipoId))
                .then();
    }

    @Override
    public Mono<Void> invalidateEsquemaEquipo(Integer esquemaEquipoId) {
        String cacheKey = buildCacheKey(esquemaCachePrefix, esquemaEquipoId);
        return redisTemplate.delete(cacheKey)
                .doOnSuccess(result -> log.debug("Cache invalidado para esquema de equipo {}", esquemaEquipoId))
                .then();
    }

    private String buildCacheKey(String prefix, Integer id) {
        return prefix + ":id:" + id;
    }

    private <T> Mono<T> getCachedEntity(String cacheKey, Class<T> entityType) {
        return redisTemplate.opsForValue().get(cacheKey)
                .cast(Object.class)
                .flatMap(cachedData -> Mono.fromCallable(() -> {
                    try {
                        return objectMapper.convertValue(cachedData, entityType);
                    } catch (Exception e) {
                        log.warn("Error al deserializar datos de cache {}: {}", cacheKey, e.getMessage());
                        return null;
                    }
                }).subscribeOn(Schedulers.boundedElastic()))
                .filter(entity -> entity != null)
                .doOnNext(entity -> log.debug("Entidad obtenida desde cache: {}", cacheKey))
                .onErrorResume(error -> {
                    log.warn("Error al recuperar entidad desde cache {}: {}", cacheKey, error.getMessage());
                    return Mono.empty();
                });
    }

    private <T> Mono<Void> cacheEntity(String cacheKey, T entity) {
        Duration ttl = Duration.ofHours(cacheTtlHours);
        return redisTemplate.opsForValue()
                .set(cacheKey, entity, ttl)
                .doOnSuccess(result -> log.debug("Entidad almacenada en cache: {}", cacheKey))
                .doOnError(error -> log.warn("Error al almacenar entidad en cache {}: {}", cacheKey, error.getMessage()))
                .onErrorResume(error -> Mono.empty()) // No fallar si el cache falla
                .then();
    }
}
