package com.dliriotech.tms.fleetservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicatePlacaException extends BaseException {
    public DuplicatePlacaException(String placa) {
        super("La placa '" + placa + "' ya está registrada.", HttpStatus.CONFLICT, "FLEET-EQP-DUP-001");
    }
}