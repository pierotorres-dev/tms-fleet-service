package com.dliriotech.tms.fleetservice.exception;

public class ObservacionEquipoNotFoundException extends ResourceNotFoundException {
    public ObservacionEquipoNotFoundException(String id) {
        super("Observación de equipo", id);
    }
}