package com.dliriotech.tms.fleetservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fecha;
    private TipoObservacionResponse tipoObservacionResponse;
    private String descripcion;
    private EstadoObservacionResponse estadoObservacionResponse;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaResolucion;
    private String comentarioResolucion;
    private Integer usuarioResolucion;
    private UserInfoResponse usuarioInfo;
}