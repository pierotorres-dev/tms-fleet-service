package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoUpdateRequest {

    private String placa;
    private String negocio;
    private Integer tipoEquipoId;
    private Integer esquemaEquipoId;
    private Integer estadoId;
}