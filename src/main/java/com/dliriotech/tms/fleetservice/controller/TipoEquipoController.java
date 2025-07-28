package com.dliriotech.tms.fleetservice.controller;

import com.dliriotech.tms.fleetservice.dto.EsquemaEquipoResponse;
import com.dliriotech.tms.fleetservice.service.EsquemaEquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/tipos-equipos")
@RequiredArgsConstructor
public class TipoEquipoController {

    private final EsquemaEquipoService esquemaEquipoService;

    @GetMapping(value = "/{tipoEquipoId}/esquemas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<EsquemaEquipoResponse> getEsquemasByTipoEquipoId(@PathVariable Integer tipoEquipoId) {
        return esquemaEquipoService.getEsquemasByTipoEquipoId(tipoEquipoId);
    }
}