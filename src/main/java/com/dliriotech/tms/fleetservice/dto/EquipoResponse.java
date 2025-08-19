package com.dliriotech.tms.fleetservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoResponse {
    private Integer id;
    private String placa;
    private String negocio;
    private TipoEquipoResponse tipoEquipoResponse;
    private EsquemaEquipoResponse esquemaEquipoResponse;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInspeccion;
    private Integer kilometraje;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaActualizacionKilometraje;
    private EstadoEquipoResponse estadoEquipoResponse;
    private Integer empresaId;
}