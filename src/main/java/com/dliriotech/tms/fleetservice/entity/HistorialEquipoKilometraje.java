package com.dliriotech.tms.fleetservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("historial_kilometraje_equipo")
public class HistorialEquipoKilometraje {
    @Id
    private Integer id;

    @Column("id_equipo")
    private Integer equipoId;

    @Column("kilometraje_anterior")
    private Integer kilometrajeAnterior;

    @Column("kilometraje_nuevo")
    private Integer kilometrajeNuevo;

    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column("id_usuario")
    private Integer usuarioId;
}