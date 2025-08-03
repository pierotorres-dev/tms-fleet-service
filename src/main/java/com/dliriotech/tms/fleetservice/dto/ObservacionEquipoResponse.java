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
public class ObservacionEquipoResponse {
    private Integer id;
    private Integer equipoId;
    private LocalDateTime fecha;
    private TipoObservacionResponse tipoObservacionResponse;
    private String descripcion;
    private EstadoObservacionResponse estadoObservacionResponse;
    private LocalDateTime fechaResolucion;
    private String comentarioResolucion;
    private Integer usuarioResolucion;
    private Integer usuarioId;
}
