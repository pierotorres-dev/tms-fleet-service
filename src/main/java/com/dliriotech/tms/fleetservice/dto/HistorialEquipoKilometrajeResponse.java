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
public class HistorialEquipoKilometrajeResponse {
    private Integer id;

    private Integer equipoId;

    private Integer kilometrajeAnterior;

    private Integer kilometrajeNuevo;

    private LocalDateTime fechaActualizacion;

    private Integer usuarioId;
}