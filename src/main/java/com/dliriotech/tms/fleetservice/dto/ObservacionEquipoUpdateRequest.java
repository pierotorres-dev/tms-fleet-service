package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionEquipoUpdateRequest {
    private Integer estadoObservacionId;

    private String comentarioResolucion;

    private Integer usuarioResolucionId;
}
