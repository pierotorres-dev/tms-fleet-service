package com.dliriotech.tms.fleetservice.exception;

import org.springframework.http.HttpStatus;

public class FleetServiceException extends BaseException {
    protected FleetServiceException(String message, String code) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, code);
    }

    protected FleetServiceException(String message, HttpStatus status, String code) {
        super(message, status, code);
    }
}