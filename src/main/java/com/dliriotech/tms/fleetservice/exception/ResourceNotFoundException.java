package com.dliriotech.tms.fleetservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends FleetServiceException {
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s con id %s no encontrado", resource, id),
                HttpStatus.NOT_FOUND, "FLEET-404");
    }
}