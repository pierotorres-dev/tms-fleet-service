package com.dliriotech.tms.fleetservice.controller;

import com.dliriotech.tms.fleetservice.dto.TipoEquipoResponse;
import com.dliriotech.tms.fleetservice.service.TipoEquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final TipoEquipoService tipoEquipoService;

    @GetMapping(value = "/tipos-equipos-total", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TipoEquipoResponse> getAllTiposEquipo() {
        return tipoEquipoService.getAllTiposEquipo();
    }

    @GetMapping(value = "/{empresaId}/tipos-equipos-activos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TipoEquipoResponse> getTiposEquipoActivosByEmpresaId(@PathVariable Integer empresaId) {
        return tipoEquipoService.getTiposEquipoActivosByEmpresaId(empresaId);
    }
}