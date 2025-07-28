package com.dliriotech.tms.fleetservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("esquemas_equipo")
public class EsquemaEquipo {

    @Id
    private Integer id;

    @Column("id_tipo_equipo")
    private Integer tipoEquipoId;

    @Column("nombre_esquema")
    private String nombreEsquema;

    @Column("total_posiciones")
    private Integer totalPosiciones;
}
