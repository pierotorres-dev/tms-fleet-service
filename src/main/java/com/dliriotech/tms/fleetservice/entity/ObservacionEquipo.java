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
@Table("observaciones_equipos")
public class ObservacionEquipo {
    @Id
    private Integer id;

    @Column("id_equipo")
    private Integer equipoId;

    private LocalDateTime fecha;

    @Column("tipo_observacion")
    private String tipo;

    private String descripcion;

    @Column("id_estado_observacion")
    private Integer estadoId;

    @Column("fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column("comentario_resolucion")
    private String comentarioResolucion;

    @Column("id_usuario_resolucion")
    private Integer usuarioResolucion;

    @Column("id_usuario")
    private Integer usuarioId;
}