package com.simulador;

import com.simulador.resource.GlobalExceptionMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GlobalExceptionMapperTest {

    private GlobalExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new GlobalExceptionMapper();
    }

    @Test
    @DisplayName("NotFoundException → 404")
    void deveRetornar404ParaNotFoundException() {
        Response response = mapper.toResponse(
            new NotFoundException("Simulação com ID 99 não encontrada.")
        );
        assertEquals(404, response.getStatus());
        assertTrue(response.getEntity().toString().contains("erro"));
    }

    @Test
    @DisplayName("ConstraintViolationException → 400")
    void deveRetornar400ParaConstraintViolationException() {
        Response response = mapper.toResponse(
            new ConstraintViolationException("Dados inválidos", Set.of())
        );
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("WebApplicationException → repassa o status original")
    void deveRepassarWebApplicationException() {
        Response response = mapper.toResponse(
            new WebApplicationException(Response.status(409).build())
        );
        assertEquals(409, response.getStatus());
    }

    @Test
    @DisplayName("Exception genérica → 500")
    void deveRetornar500ParaExcecaoGenerica() {
        Response response = mapper.toResponse(
            new RuntimeException("Erro inesperado")
        );
        assertEquals(500, response.getStatus());
        assertTrue(response.getEntity().toString().contains("Erro interno"));
    }

    @Test
    @DisplayName("Exception com mensagem nula → 500 sem NPE")
    void deveRetornar500ParaExcecaoSemMensagem() {
        Response response = mapper.toResponse(
            new RuntimeException((String) null)
        );
        assertEquals(500, response.getStatus());
    }
}
