package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.entity.EstadoObservacion;
import com.dliriotech.tms.fleetservice.exception.ObservacionEquipoException;
import com.dliriotech.tms.fleetservice.repository.EstadoObservacionRepository;
import com.dliriotech.tms.fleetservice.service.ObservacionMasterDataCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObservacionMasterDataCacheServiceImpl implements ObservacionMasterDataCacheService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final EstadoObservacionRepository estadoObservacionRepository;

    private static final String ESTADO_OBSERVACION_BYNAME_PREFIX = "cache:estadoObservacion:byName:";

    @Override
    public Mono<Integer> getEstadoObservacionIdByNombre(String nombre) {
        String cacheKey = ESTADO_OBSERVACION_BYNAME_PREFIX + nombre;

        return redisTemplate.opsForValue().get(cacheKey)
                .cast(Integer.class)
                .switchIfEmpty(
                        estadoObservacionRepository.findByNombre(nombre)
                                .map(EstadoObservacion::getId)
                                .flatMap(id -> {
                                    // Guardar en cache por 24 horas
                                    return redisTemplate.opsForValue()
                                            .set(cacheKey, id, Duration.ofHours(24))
                                            .thenReturn(id);
                                })
                                .doOnError(error -> log.error("Error al obtener ID de estado de observación para nombre {}: {}",
                                        nombre, error.getMessage()))
                                .switchIfEmpty(Mono.error(new ObservacionEquipoException(
                                        "No se encontró estado de observación con nombre: " + nombre)))
                );
    }

    @Override
    public Mono<Void> invalidateEstadoObservacionIdByNombreCache(String nombre) {
        String cacheKey = ESTADO_OBSERVACION_BYNAME_PREFIX + nombre;
        return redisTemplate.delete(cacheKey)
                .doOnSuccess(v -> log.debug("Cache de estado observación by name {} invalidado", nombre))
                .then();
    }
}
