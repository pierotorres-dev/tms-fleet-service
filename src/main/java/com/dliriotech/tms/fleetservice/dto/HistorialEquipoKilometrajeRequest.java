package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEquipoKilometrajeRequest {
    private Integer id;

    private Integer equipoId;

    private Integer kilometrajeAnterior;

    private Integer kilometrajeNuevo;

    private Integer usuarioId;
}