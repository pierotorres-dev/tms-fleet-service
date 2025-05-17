package com.dliriotech.tms.fleetservice.controller;

import com.dliriotech.tms.fleetservice.dto.EstadoEquipoResponse;
import com.dliriotech.tms.fleetservice.dto.EstadoObservacionResponse;
import com.dliriotech.tms.fleetservice.dto.TipoObservacionNeumaticoResponse;
import com.dliriotech.tms.fleetservice.service.EstadoEquipoService;
import com.dliriotech.tms.fleetservice.service.EstadoObservacionService;
import com.dliriotech.tms.fleetservice.service.TipoObservacionNeumaticoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/catalogos")
@RequiredArgsConstructor
public class CatalogoController {

    private final TipoObservacionNeumaticoService tipoObservacionNeumaticoService;
    private final EstadoObservacionService estadoObservacionService;
    private final EstadoEquipoService estadoEquipoService;

    @GetMapping(value = "/tipo-observacion-neumatico", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TipoObservacionNeumaticoResponse> getAllTipoObservacionNeumatico() {
        return tipoObservacionNeumaticoService.getAllTipoObservacionNeumatico();
    }

    @GetMapping(value = "/estado-observacion", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<EstadoObservacionResponse> getAllEstadoObservacion() {
        return estadoObservacionService.getAllEstadoObservacion();
    }

    @GetMapping(value = "/estado-equipo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<EstadoEquipoResponse> getAllEstadoEquipo() {
        return estadoEquipoService.getAllEstadoEquipo();
    }
}