package com.dliriotech.tms.fleetservice.exception;

import lombok.Getter;

@Getter
public class ObservacionUpdateException extends RuntimeException {
    private final String errorCode;
    private final String operation;

    private ObservacionUpdateException(String errorCode, String message, String operation) {
        super(message);
        this.errorCode = errorCode;
        this.operation = operation;
    }

    private ObservacionUpdateException(String errorCode, String message, String operation, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.operation = operation;
    }

    public static ObservacionUpdateException notFound(Integer observacionId) {
        return new ObservacionUpdateException(
                "FLEET-OBS-UPD-001",
                String.format("Observación no encontrada con ID: %d", observacionId),
                "buscar_observacion"
        );
    }

    public static ObservacionUpdateException invalidRequest(String field, Object value) {
        return new ObservacionUpdateException(
                "FLEET-OBS-UPD-002",
                String.format("Campo inválido '%s' con valor: %s", field, value),
                "validar_request"
        );
    }

    public static ObservacionUpdateException noFieldsToUpdate() {
        return new ObservacionUpdateException(
                "FLEET-OBS-UPD-003",
                "No se proporcionaron campos para actualizar",
                "validar_campos"
        );
    }

    public static ObservacionUpdateException stateTransitionNotAllowed(String currentState, String newState) {
        return new ObservacionUpdateException(
                "FLEET-OBS-UPD-004",
                String.format("Transición de estado no permitida de '%s' a '%s'", currentState, newState),
                "validar_transicion_estado"
        );
    }

    public static ObservacionUpdateException finalStateModification(String currentState) {
        return new ObservacionUpdateException(
                "FLEET-OBS-UPD-005",
                String.format("No se puede modificar una observación en estado final: '%s'", currentState),
                "validar_estado_final"
        );
    }

    public static ObservacionUpdateException databaseError(String operation, Throwable cause) {
        return new ObservacionUpdateException(
                "FLEET-OBS-UPD-006",
                String.format("Error de base de datos al %s", operation),
                operation,
                cause
        );
    }

    public static ObservacionUpdateException masterDataError(String operation, Throwable cause) {
        return new ObservacionUpdateException(
                "FLEET-OBS-UPD-007",
                String.format("Error al obtener datos maestros: %s", operation),
                operation,
                cause
        );
    }
}
