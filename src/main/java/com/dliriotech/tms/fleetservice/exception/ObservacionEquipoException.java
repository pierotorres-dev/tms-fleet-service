package com.dliriotech.tms.fleetservice.exception;

import org.springframework.http.HttpStatus;

public class ObservacionEquipoException extends FleetServiceException {
    public ObservacionEquipoException(String code, String message) {
        super(message, code);
    }

    public ObservacionEquipoException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "FLEET-OBS-OPE-001");
    }
}