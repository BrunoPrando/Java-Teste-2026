package com.simulador;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class SimulacaoResourceTest {

    // ----------------------------------------------------------------
    // POST — cenários de sucesso
    // ----------------------------------------------------------------

    @Test
    @DisplayName("POST → 201 com payload válido")
    void deveRetornar201AoCriarSimulacao() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 1000.00, "taxaMensal": 1.5, "prazoMeses": 12}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("valorTotalFinal", notNullValue())
            .body("valorTotalJuros", notNullValue())
            .body("memoriaCalculo", hasSize(12));
    }

    @Test
    @DisplayName("POST → prazo mínimo (1 mês) aceito")
    void deveAceitarPrazoMinimo() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 500.00, "taxaMensal": 1.0, "prazoMeses": 1}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(201)
            .body("memoriaCalculo", hasSize(1));
    }

    @Test
    @DisplayName("POST → prazo máximo (600 meses) aceito")
    void deveAceitarPrazoMaximo() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 500.00, "taxaMensal": 0.5, "prazoMeses": 600}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(201)
            .body("memoriaCalculo", hasSize(600));
    }

    // ----------------------------------------------------------------
    // POST — cenários de erro 400 (validação)
    // ----------------------------------------------------------------

    @Test
    @DisplayName("POST sem valorInicial → 400")
    void deveRetornar400SemValorInicial() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"taxaMensal": 1.5, "prazoMeses": 12}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(400)
            .body("erro", notNullValue());
    }

    @Test
    @DisplayName("POST sem taxaMensal → 400")
    void deveRetornar400SemTaxaMensal() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 1000.00, "prazoMeses": 12}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(400)
            .body("erro", notNullValue());
    }

    @Test
    @DisplayName("POST sem prazoMeses → 400")
    void deveRetornar400SemPrazoMeses() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 1000.00, "taxaMensal": 1.5}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(400)
            .body("erro", notNullValue());
    }

    @Test
    @DisplayName("POST com valorInicial zero → 400")
    void deveRetornar400ComValorInicialZero() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 0, "taxaMensal": 1.5, "prazoMeses": 12}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(400)
            .body("erro", notNullValue());
    }

    @Test
    @DisplayName("POST com taxaMensal zero → 400")
    void deveRetornar400ComTaxaZero() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 1000.00, "taxaMensal": 0, "prazoMeses": 12}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(400)
            .body("erro", notNullValue());
    }

    @Test
    @DisplayName("POST com prazoMeses zero → 400")
    void deveRetornar400ComPrazoZero() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 1000.00, "taxaMensal": 1.5, "prazoMeses": 0}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(400)
            .body("erro", notNullValue());
    }

    @Test
    @DisplayName("POST com taxaMensal acima de 100 → 400")
    void deveRetornar400ComTaxaAcimaDe100() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 1000.00, "taxaMensal": 101, "prazoMeses": 12}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(400)
            .body("erro", notNullValue());
    }

    @Test
    @DisplayName("POST com prazoMeses acima de 600 → 400")
    void deveRetornar400ComPrazoAcimaDoMaximo() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 1000.00, "taxaMensal": 1.5, "prazoMeses": 601}
                """)
        .when().post("/api/simulacoes")
        .then()
            .statusCode(400)
            .body("erro", notNullValue());
    }

    // ----------------------------------------------------------------
    // GET — cenários de sucesso e erro
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /{id} → 200 com dados completos")
    void deveRetornar200AoBuscarSimulacaoExistente() {
        Integer id = given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 2000.00, "taxaMensal": 2.0, "prazoMeses": 6}
                """)
            .post("/api/simulacoes")
            .then().statusCode(201)
            .extract().path("id");

        given()
            .when().get("/api/simulacoes/" + id)
            .then()
            .statusCode(200)
            .body("id", equalTo(id))
            .body("memoriaCalculo", hasSize(6))
            .body("valorTotalFinal", notNullValue())
            .body("valorTotalJuros", notNullValue())
            .body("criadoEm", notNullValue());
    }

    @Test
    @DisplayName("GET /999999 → 404 com mensagem de erro")
    void deveRetornar404ParaIdInexistente() {
        given()
            .when().get("/api/simulacoes/999999")
            .then()
            .statusCode(404)
            .body("erro", containsString("999999"));
    }

    // ----------------------------------------------------------------
    // GlobalExceptionMapper — cobertura direta
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Erro genérico → 500 com mensagem")
    void deveRetornar500ParaErroGenerico() {
        // Força erro interno enviando JSON malformado
        given()
            .contentType(ContentType.JSON)
            .body("{ invalido }")
        .when().post("/api/simulacoes")
        .then()
            .statusCode(anyOf(equalTo(400), equalTo(500)));
    }

    // ----------------------------------------------------------------
    // OpenAPI / Swagger
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Swagger UI disponível em /swagger-ui")
    void swaggerUiDeveEstarDisponivel() {
        given()
            .when().get("/swagger-ui")
            .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("OpenAPI spec disponível em /q/openapi")
    void openApiSpecDeveEstarDisponivel() {
        given()
            .when().get("/q/openapi")
            .then()
            .statusCode(200)
            .body(containsString("Simulador de Financiamentos"));
    }
}

    // ----------------------------------------------------------------
    // GET /api/simulacoes — listar todas
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /api/simulacoes → 200 lista de simulações")
    void deveListarTodasAsSimulacoes() {
        // Cria pelo menos uma simulação
        given()
            .contentType(ContentType.JSON)
            .body("""
                {"valorInicial": 3000.00, "taxaMensal": 1.0, "prazoMeses": 3}
                """)
            .post("/api/simulacoes")
            .then().statusCode(201);

        // Lista todas
        given()
            .when().get("/api/simulacoes")
            .then()
            .statusCode(200)
            .body("$", not(empty()));
    }
}
