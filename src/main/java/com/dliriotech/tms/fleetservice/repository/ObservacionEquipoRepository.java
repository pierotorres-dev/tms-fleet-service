package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.ObservacionEquipo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ObservacionEquipoRepository extends ReactiveCrudRepository<ObservacionEquipo, Integer> {
    Flux<ObservacionEquipo> findByEquipoId(Integer equipoId);

    @Query("UPDATE observaciones_equipos SET id_estado_observacion = :nuevoEstadoId WHERE id_equipo = :equipoId")
    Mono<Integer> updateEstadoByEquipoId(Integer equipoId, Integer nuevoEstadoId);
}