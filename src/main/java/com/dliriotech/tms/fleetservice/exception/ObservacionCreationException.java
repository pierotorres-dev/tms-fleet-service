package com.dliriotech.tms.fleetservice.exception;

import lombok.Getter;

@Getter
public class ObservacionCreationException extends RuntimeException {
    private final String errorCode;
    private final String operation;

    private ObservacionCreationException(String errorCode, String message, String operation) {
        super(message);
        this.errorCode = errorCode;
        this.operation = operation;
    }

    private ObservacionCreationException(String errorCode, String message, String operation, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.operation = operation;
    }

    public static ObservacionCreationException invalidRequest(String field, Object value) {
        return new ObservacionCreationException(
                "FLEET-OBS-CRE-001",
                String.format("Campo inválido '%s' con valor: %s", field, value),
                "validar_request"
        );
    }

    public static ObservacionCreationException tipoObservacionNotFound(Integer tipoObservacionId) {
        return new ObservacionCreationException(
                "FLEET-OBS-CRE-002",
                String.format("Tipo de observación no encontrado con ID: %d", tipoObservacionId),
                "validar_tipo_observacion"
        );
    }

    public static ObservacionCreationException estadoObservacionNotFound(String estadoNombre) {
        return new ObservacionCreationException(
                "FLEET-OBS-CRE-003",
                String.format("Estado de observación '%s' no encontrado", estadoNombre),
                "obtener_estado_pendiente"
        );
    }

    public static ObservacionCreationException databaseError(String operation, Throwable cause) {
        return new ObservacionCreationException(
                "FLEET-OBS-CRE-004",
                String.format("Error de base de datos al %s", operation),
                operation,
                cause
        );
    }

    public static ObservacionCreationException masterDataError(String operation, Throwable cause) {
        return new ObservacionCreationException(
                "FLEET-OBS-CRE-005",
                String.format("Error al obtener datos maestros: %s", operation),
                operation,
                cause
        );
    }
}
