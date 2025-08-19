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
public class HistorialEquipoKilometrajeResponse {
    private Integer id;

    private Integer equipoId;

    private Integer kilometrajeAnterior;

    private Integer kilometrajeNuevo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaActualizacion;

    private Integer usuarioId;
}