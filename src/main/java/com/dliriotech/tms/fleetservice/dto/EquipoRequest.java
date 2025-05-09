package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoRequest {

    private String placa;
    private String negocio;
    private String equipo;

    private LocalDate fechaInspeccion;

    private Integer kilometraje;

    private Integer estadoId;

    private Integer empresaId;
}
