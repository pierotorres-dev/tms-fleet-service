package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.EsquemaEquipoResponse;
import com.dliriotech.tms.fleetservice.dto.EstadoEquipoResponse;
import com.dliriotech.tms.fleetservice.dto.TipoEquipoResponse;
import reactor.core.publisher.Mono;

/**
 * Servicio especializado para el cache de entidades relacionadas a equipos.
 * Implementa el patrón Repository para el cache, separando la lógica de cache
 * de la lógica de negocio principal.
 */
public interface EquipoEntityCacheService {

    /**
     * Obtiene un estado de equipo desde cache o base de datos.
     * @param estadoId ID del estado
     * @return Mono con el estado de equipo
     */
    Mono<EstadoEquipoResponse> getEstadoEquipo(Integer estadoId);

    /**
     * Obtiene un tipo de equipo desde cache o base de datos.
     * @param tipoEquipoId ID del tipo de equipo
     * @return Mono con el tipo de equipo
     */
    Mono<TipoEquipoResponse> getTipoEquipo(Integer tipoEquipoId);

    /**
     * Obtiene un esquema de equipo desde cache o base de datos.
     * @param esquemaEquipoId ID del esquema de equipo
     * @return Mono con el esquema de equipo
     */
    Mono<EsquemaEquipoResponse> getEsquemaEquipo(Integer esquemaEquipoId);

    /**
     * Invalida el cache para un estado específico.
     * @param estadoId ID del estado a invalidar
     * @return Mono que indica la finalización
     */
    Mono<Void> invalidateEstadoEquipo(Integer estadoId);

    /**
     * Invalida el cache para un tipo de equipo específico.
     * @param tipoEquipoId ID del tipo de equipo a invalidar
     * @return Mono que indica la finalización
     */
    Mono<Void> invalidateTipoEquipo(Integer tipoEquipoId);

    /**
     * Invalida el cache para un esquema de equipo específico.
     * @param esquemaEquipoId ID del esquema de equipo a invalidar
     * @return Mono que indica la finalización
     */
    Mono<Void> invalidateEsquemaEquipo(Integer esquemaEquipoId);
}
