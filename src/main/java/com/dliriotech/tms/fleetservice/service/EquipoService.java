package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.EquipoNuevoRequest;
import com.dliriotech.tms.fleetservice.dto.EquipoResponse;
import com.dliriotech.tms.fleetservice.dto.EquipoUpdateKilometrajeRequest;
import com.dliriotech.tms.fleetservice.dto.EquipoUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EquipoService {
    Flux<EquipoResponse> getAllEquiposByEmpresaId(Integer empresaId);
    Mono<EquipoResponse> getEquipoById(Integer id);
    Mono<EquipoResponse> saveEquipo(EquipoNuevoRequest equipoNuevoRequest);
    Mono<EquipoResponse> updateEquipo(Integer id, EquipoUpdateRequest request);
    Mono<EquipoResponse> updateEquipoKilometraje(Integer id, EquipoUpdateKilometrajeRequest request);
}