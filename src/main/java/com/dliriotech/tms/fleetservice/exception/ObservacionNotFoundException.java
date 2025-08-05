package com.dliriotech.tms.fleetservice.exception;

import lombok.Getter;

@Getter
public class ObservacionNotFoundException extends RuntimeException {
    private final String errorCode;
    private final String operation;

    private ObservacionNotFoundException(String errorCode, String message, String operation) {
        super(message);
        this.errorCode = errorCode;
        this.operation = operation;
    }

    public static ObservacionNotFoundException forEquipo(Integer equipoId) {
        return new ObservacionNotFoundException(
                "FLEET-OBS-NF-001",
                String.format("No se encontraron observaciones para el equipo con ID: %d", equipoId),
                "buscar_observaciones_equipo"
        );
    }

    public static ObservacionNotFoundException forEquipoAndEstado(Integer equipoId, String estado) {
        return new ObservacionNotFoundException(
                "FLEET-OBS-NF-002",
                String.format("No se encontraron observaciones en estado '%s' para el equipo con ID: %d", estado, equipoId),
                "buscar_observaciones_pendientes"
        );
    }

    public static ObservacionNotFoundException forObservacionId(Integer observacionId) {
        return new ObservacionNotFoundException(
                "FLEET-OBS-NF-003",
                String.format("No se encontró observación con ID: %d", observacionId),
                "buscar_observacion_por_id"
        );
    }
}
