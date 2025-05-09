package com.dliriotech.tms.fleetservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("tipos_observaciones_neumaticos")
public class TipoObservacionNeumatico {
    @Id
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer activo;
}