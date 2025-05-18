package com.dliriotech.tms.fleetservice.exception;

import com.dliriotech.tms.fleetservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Order(-2)
@Slf4j
class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final Map<Class<? extends Throwable>, Function<Throwable, ErrorDetails>> exceptionHandlers;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties.Resources resources,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.exceptionHandlers = configureExceptionHandlers();
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Map<Class<? extends Throwable>, Function<Throwable, ErrorDetails>> configureExceptionHandlers() {
        Map<Class<? extends Throwable>, Function<Throwable, ErrorDetails>> handlers = new HashMap<>();

        handlers.put(BaseException.class, ex -> {
            BaseException baseEx = (BaseException) ex;
            return new ErrorDetails(baseEx.getStatus(), baseEx.getCode(), baseEx.getMessage());
        });

        handlers.put(ResourceNotFoundException.class, ex ->
                new ErrorDetails(HttpStatus.NOT_FOUND, ((BaseException)ex).getCode(), ex.getMessage()));

        handlers.put(CacheOperationException.class, ex ->
                new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, ((BaseException)ex).getCode(), ex.getMessage()));

        handlers.put(CatalogOperationException.class, ex ->
                new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, ((BaseException)ex).getCode(), ex.getMessage()));

        handlers.put(ObservacionEquipoException.class, ex ->
                new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, ((BaseException)ex).getCode(), ex.getMessage()));

        handlers.put(ObservacionEquipoNotFoundException.class, ex ->
                new ErrorDetails(HttpStatus.NOT_FOUND, ((BaseException)ex).getCode(), ex.getMessage()));

        handlers.put(Exception.class, ex ->
                new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, "FLEET-SYS-ERR-001",
                        "Error interno del servidor: " + ex.getMessage()));

        return handlers;
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        log.error("Error procesando solicitud: {}", error.getMessage(), error);

        ErrorDetails errorDetails = exceptionHandlers.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(error))
                .findFirst()
                .map(entry -> entry.getValue().apply(error))
                .orElse(new ErrorDetails(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "FLEET-SYS-ERR-001",
                        "Error interno del servidor"
                ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorDetails.code())
                .message(errorDetails.message())
                .path(request.path())
                .timestamp(LocalDateTime.now(ZoneId.of("America/Lima")))
                .build();

        return ServerResponse.status(errorDetails.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

    private record ErrorDetails(HttpStatus status, String code, String message) {}
}