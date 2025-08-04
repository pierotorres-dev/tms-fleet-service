package com.dliriotech.tms.fleetservice.service;

import reactor.core.publisher.Mono;

/**
 * Servicio especializado para operaciones de conteo de observaciones.
 * Separamos las responsabilidades de conteo de la lógica principal de negocio
 * siguiendo el principio de Responsabilidad Única (SRP).
 */
public interface ObservacionCountService {
    
    /**
     * Cuenta el total de observaciones pendientes para un equipo específico.
     * Incluye tanto observaciones de equipo como de neumáticos.
     * 
     * @param equipoId ID del equipo
     * @return Mono con el conteo total de observaciones pendientes
     */
    Mono<Long> contarObservacionesPendientesPorEquipo(Integer equipoId);
}
