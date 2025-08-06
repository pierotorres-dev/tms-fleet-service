package com.dliriotech.tms.fleetservice.exception;

import org.springframework.http.HttpStatus;

public class EquipoNotFoundException extends BaseException {
    public EquipoNotFoundException(String id) {
        super("Equipo con id " + id + " no encontrado",
                HttpStatus.NOT_FOUND,
                "FLEET-EQP-NFD-001");
    }
}