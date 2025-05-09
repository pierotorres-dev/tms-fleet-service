package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionEquipoRequest {
    private Integer equipoId;
    private LocalDateTime fecha;
    private Integer tipoObservacionId;
    private String descripcion;
    private Integer estadoId;
    private LocalDateTime fechaResolucion;
    private String comentarioResolucion;
    private Integer usuarioResolucion;
    private Integer usuarioId;
}