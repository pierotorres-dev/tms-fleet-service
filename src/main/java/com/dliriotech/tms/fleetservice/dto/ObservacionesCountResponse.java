package com.dliriotech.tms.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionesCountResponse {
    
    private Long observacionesEquipo;
    
    private Long observacionesNeumatico;
    
    private Long totalObservaciones;
    
    public static ObservacionesCountResponse of(Long observacionesEquipo, Long observacionesNeumatico) {
        return ObservacionesCountResponse.builder()
                .observacionesEquipo(observacionesEquipo != null ? observacionesEquipo : 0L)
                .observacionesNeumatico(observacionesNeumatico != null ? observacionesNeumatico : 0L)
                .totalObservaciones((observacionesEquipo != null ? observacionesEquipo : 0L) + 
                                  (observacionesNeumatico != null ? observacionesNeumatico : 0L))
                .build();
    }
    
    public static ObservacionesCountResponse empty() {
        return ObservacionesCountResponse.builder()
                .observacionesEquipo(0L)
                .observacionesNeumatico(0L)
                .totalObservaciones(0L)
                .build();
    }
}