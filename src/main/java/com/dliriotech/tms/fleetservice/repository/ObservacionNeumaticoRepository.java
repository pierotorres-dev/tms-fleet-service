package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.ObservacionNeumatico;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ObservacionNeumaticoRepository extends ReactiveCrudRepository<ObservacionNeumatico, Integer> {
}