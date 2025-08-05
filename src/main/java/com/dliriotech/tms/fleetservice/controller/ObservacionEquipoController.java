package com.dliriotech.tms.fleetservice.controller;

import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoNuevoRequest;
import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoResponse;
import com.dliriotech.tms.fleetservice.dto.ObservacionEquipoUpdateRequest;
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

    @GetMapping(value = "/equipo/{equipoId}/observaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ObservacionEquipoResponse> getObservacionesByEquipo(
            @PathVariable Integer equipoId,
            @RequestParam(value = "estado", required = false) String estado) {
        if ("pendiente".equalsIgnoreCase(estado)) {
            return observacionEquipoService.getAllObservacionesPendientesAndByEquipoId(equipoId);
        }
        return observacionEquipoService.getAllObservacionesByEquipoId(equipoId);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ObservacionEquipoResponse> createObservacionEquipo(@Valid @RequestBody ObservacionEquipoNuevoRequest request) {
        return observacionEquipoService.saveObservacion(request);
    }

    @PatchMapping(value = "/equipo/{observacionEquipoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ObservacionEquipoResponse> updateObservacionEquipo(
            @PathVariable Integer observacionEquipoId,
            @Valid @RequestBody ObservacionEquipoUpdateRequest request) {
        return observacionEquipoService.updateObservacion(observacionEquipoId, request);
    }
}