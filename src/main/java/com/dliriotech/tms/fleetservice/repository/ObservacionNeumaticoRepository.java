package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.ObservacionNeumatico;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ObservacionNeumaticoRepository extends ReactiveCrudRepository<ObservacionNeumatico, Integer> {

    @Query("SELECT COUNT(*) FROM observaciones_neumatico AS obs " +
            "JOIN neumaticos AS neu ON obs.id_neumatico = neu.id " +
            "WHERE neu.id_equipo = :equipoId AND obs.id_estado_observacion = :estadoId")
    Mono<Long> countByEquipoIdAndEstadoObservacionId(Integer equipoId, Integer estadoId);
}