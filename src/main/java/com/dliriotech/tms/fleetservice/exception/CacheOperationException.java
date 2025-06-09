package com.dliriotech.tms.fleetservice.exception;

import org.springframework.http.HttpStatus;

public class CacheOperationException extends FleetServiceException {
    public CacheOperationException(String resource) {
        super(String.format("Error al procesar caché para %s", resource),
                HttpStatus.INTERNAL_SERVER_ERROR, "FLEET-CACHE-OPE-001");
    }
}