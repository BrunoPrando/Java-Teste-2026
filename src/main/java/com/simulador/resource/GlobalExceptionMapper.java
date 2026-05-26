package com.simulador.resource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {

        if (e instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("erro", e.getMessage())).build();
        }

        if (e instanceof ConstraintViolationException cve) {
            String mensagens = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", mensagens)).build();
        }

        if (e instanceof WebApplicationException wae) {
            return wae.getResponse();
        }

        String mensagem = e.getMessage() != null ? e.getMessage() : "Erro desconhecido";
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("erro", "Erro interno: " + mensagem)).build();
    }
}
