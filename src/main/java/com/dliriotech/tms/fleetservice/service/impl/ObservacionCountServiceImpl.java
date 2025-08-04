package com.dliriotech.tms.fleetservice.service.impl;

import com.dliriotech.tms.fleetservice.constants.EstadoObservacionConstants;
import com.dliriotech.tms.fleetservice.repository.ObservacionEquipoRepository;
import com.dliriotech.tms.fleetservice.repository.ObservacionNeumaticoRepository;
import com.dliriotech.tms.fleetservice.service.ObservacionCountService;
import com.dliriotech.tms.fleetservice.service.ObservacionMasterDataCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Implementación del servicio de conteo de observaciones.
 * Utiliza programación reactiva para consultas paralelas y optimiza
 * el rendimiento mediante el uso de cache para el estado "Pendiente".
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ObservacionCountServiceImpl implements ObservacionCountService {

    private final ObservacionEquipoRepository observacionEquipoRepository;
    private final ObservacionNeumaticoRepository observacionNeumaticoRepository;
    private final ObservacionMasterDataCacheService observacionMasterDataCacheService;

    @Override
    public Mono<Long> contarObservacionesPendientesPorEquipo(Integer equipoId) {
        // Obtener el ID del estado "Pendiente" usando cache
        return observacionMasterDataCacheService.getEstadoObservacionIdByNombre(EstadoObservacionConstants.PENDIENTE)
                .flatMap(estadoPendienteId -> {
                    // Ejecutar ambas consultas en paralelo para optimizar el rendimiento
                    Mono<Long> countEquipoMono = observacionEquipoRepository
                            .countByEquipoIdAndEstadoId(equipoId, estadoPendienteId)
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnSubscribe(s -> log.debug("Contando observaciones de equipo pendientes para equipo {}", equipoId));

                    Mono<Long> countNeumaticoMono = observacionNeumaticoRepository
                            .countByEquipoIdAndEstadoObservacionId(equipoId, estadoPendienteId)
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnSubscribe(s -> log.debug("Contando observaciones de neumático pendientes para equipo {}", equipoId));

                    // Combinar los resultados y sumar los conteos
                    return Mono.zip(countEquipoMono, countNeumaticoMono)
                            .map(tuple -> {
                                Long totalCount = tuple.getT1() + tuple.getT2();
                                log.debug("Total observaciones pendientes para equipo {}: {} (Equipo: {}, Neumático: {})", 
                                         equipoId, totalCount, tuple.getT1(), tuple.getT2());
                                return totalCount;
                            });
                })
                .doOnError(error -> log.error("Error al contar observaciones pendientes para equipo {}: {}", 
                                            equipoId, error.getMessage()))
                .onErrorReturn(0L); // En caso de error, retornar 0 para evitar fallar toda la consulta
    }
}
