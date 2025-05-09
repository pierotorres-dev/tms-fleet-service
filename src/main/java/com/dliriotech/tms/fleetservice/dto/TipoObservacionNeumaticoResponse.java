package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoObservacionNeumaticoResponse {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer activo;
}
