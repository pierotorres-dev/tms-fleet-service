package com.dliriotech.tms.fleetservice.exception;

public class EquipoNotFoundException extends ResourceNotFoundException {
    public EquipoNotFoundException(String id) {
        super("Equipo", id);
    }
}