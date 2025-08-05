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
public class EquipoConObservacionesResponse {
    private Integer id;
    private String placa;
    private String negocio;
    private TipoEquipoResponse tipoEquipoResponse;
    private EsquemaEquipoResponse esquemaEquipoResponse;
    private LocalDate fechaInspeccion;
    private Integer kilometraje;
    private LocalDate fechaActualizacionKilometraje;
    private EstadoEquipoResponse estadoEquipoResponse;
    private Integer empresaId;
    private ObservacionesCountResponse observacionesCount;
}