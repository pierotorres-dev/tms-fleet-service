package com.dliriotech.tms.fleetservice.controller;

import com.dliriotech.tms.fleetservice.dto.EquipoConObservacionesResponse;
import com.dliriotech.tms.fleetservice.dto.EquipoNuevoRequest;
import com.dliriotech.tms.fleetservice.dto.EquipoResponse;
import com.dliriotech.tms.fleetservice.dto.EquipoUpdateKilometrajeRequest;
import com.dliriotech.tms.fleetservice.dto.EquipoUpdateRequest;
import com.dliriotech.tms.fleetservice.service.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @GetMapping(value = "/empresa/{empresaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<EquipoResponse> getAllEquiposByEmpresaId(@PathVariable Integer empresaId) {
        return equipoService.getAllEquiposByEmpresaId(empresaId);
    }

    @GetMapping(value = "/empresa/{empresaId}/con-observaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<EquipoConObservacionesResponse> getAllEquiposConObservacionesByEmpresaId(@PathVariable Integer empresaId) {
        return equipoService.getAllEquiposConObservacionesByEmpresaId(empresaId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<EquipoResponse> getEquipoById(@PathVariable Integer id) {
        return equipoService.getEquipoById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EquipoResponse> saveEquipo(@Valid @RequestBody EquipoNuevoRequest request) {
        return equipoService.saveEquipo(request);
    }

    @PatchMapping(value = "/{id}/configuracion", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<EquipoResponse> updateEquipo(
            @PathVariable Integer id,
            @Valid @RequestBody EquipoUpdateRequest request) {
        return equipoService.updateEquipo(id, request);
    }

    @PatchMapping(value = "/{id}/kilometraje", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<EquipoResponse> updateEquipoKilometraje(
            @PathVariable Integer id,
            @Valid @RequestBody EquipoUpdateKilometrajeRequest request) {
        return equipoService.updateEquipoKilometraje(id, request);
    }
}