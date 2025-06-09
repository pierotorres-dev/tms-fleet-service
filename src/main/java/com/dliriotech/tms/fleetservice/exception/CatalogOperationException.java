package com.dliriotech.tms.fleetservice.exception;

import org.springframework.http.HttpStatus;

public class CatalogOperationException extends FleetServiceException {
    public CatalogOperationException(String catalog) {
        super(String.format("Error al obtener catálogo de %s", catalog),
                HttpStatus.INTERNAL_SERVER_ERROR, "FLEET-CAT-OPE-001");
    }
}