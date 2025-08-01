package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EsquemaEquipoResponse {
    private Integer id;
    private String nombreEsquema;
    private Integer totalPosiciones;
}