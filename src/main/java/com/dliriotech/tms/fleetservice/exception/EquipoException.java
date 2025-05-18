package com.dliriotech.tms.fleetservice.exception;

public class EquipoException extends FleetServiceException {
    public EquipoException(String code, String message) {
        super(message, code);
    }
}