package com.dliriotech.tms.fleetservice.controller;

import com.dliriotech.tms.fleetservice.dto.EquipoRequest;
import com.dliriotech.tms.fleetservice.dto.EquipoResponse;
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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<EquipoResponse> getEquipoById(@PathVariable Integer id) {
        return equipoService.getEquipoById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EquipoResponse> saveEquipo(@Valid @RequestBody EquipoRequest request) {
        return equipoService.saveEquipo(request);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<EquipoResponse> updateEquipo(
            @PathVariable Integer id,
            @Valid @RequestBody EquipoRequest request) {
        return equipoService.updateEquipo(id, request);
    }

    @PatchMapping(value = "/{id}/estado/{estadoId}")
    public Mono<EquipoResponse> updateEstadoEquipo(
            @PathVariable Integer id,
            @PathVariable Integer estadoId) {
        return equipoService.updateEstadoEquipo(id, estadoId);
    }
}