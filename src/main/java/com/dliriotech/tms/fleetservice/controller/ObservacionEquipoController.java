package com.dliriotech.tms.fleetservice.controller;

import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoRequest;
import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoResponse;
import com.dliriotech.tms.fleetservice.service.ObservacionEquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/observaciones-equipo")
@RequiredArgsConstructor
public class ObservacionEquipoController {

    private final ObservacionEquipoService observacionEquipoService;

    @GetMapping(value = "/equipo/{equipoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ObservacionEquipoResponse> getAllObservacionesByEquipoId(@PathVariable Integer equipoId) {
        return observacionEquipoService.getAllObservacionesByEquipoId(equipoId);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ObservacionEquipoResponse> saveObservacion(@Valid @RequestBody ObservacionEquipoRequest request) {
        return observacionEquipoService.saveObservacion(request);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ObservacionEquipoResponse> updateObservacion(
            @PathVariable Integer id,
            @Valid @RequestBody ObservacionEquipoRequest request) {
        return observacionEquipoService.updateObservacion(id, request);
    }

    @PatchMapping(value = "/equipo/{equipoId}/estado/{estadoId}")
    public Mono<Integer> updateEstadoObservacionesByEquipoId(
            @PathVariable Integer equipoId,
            @PathVariable Integer estadoId) {
        return observacionEquipoService.updateEstadoObservacionesByEquipoId(equipoId, estadoId);
    }
}