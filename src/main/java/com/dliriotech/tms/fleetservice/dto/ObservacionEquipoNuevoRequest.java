package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionEquipoNuevoRequest {
    private Integer equipoId;
    private Integer tipoObservacionId;
    private String descripcion;
    private Integer usuarioId;
}