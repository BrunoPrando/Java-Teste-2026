package com.simulador.resource;

import com.simulador.dto.SimulacaoRequestDTO;
import com.simulador.dto.SimulacaoResponseDTO;
import com.simulador.service.SimulacaoService;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/api/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(
    name = "Simulações",
    description = "Operações para simular financiamentos com juros compostos. " +
                  "Informe o valor do empréstimo, a taxa mensal e o prazo para obter " +
                  "o valor total a pagar, o total de juros e a evolução mês a mês."
)
public class SimulacaoResource {

    @Inject SimulacaoService service;
    @Inject Validator validator;

    // ----------------------------------------------------------------
    // 1. POST — Criar simulação
    // ----------------------------------------------------------------
    @POST
    @Operation(
        summary = "Criar nova simulação de financiamento",
        description = """
            ## Como usar

            Preencha os três campos abaixo e clique em **Execute**:

            | Campo | Descrição | Exemplo |
            |-------|-----------|---------|
            | `valorInicial` | Valor do empréstimo em reais | `1000.00` |
            | `taxaMensal` | Taxa de juros mensal em % | `1.5` (= 1,5% ao mês) |
            | `prazoMeses` | Duração do contrato em meses | `12` (= 1 ano) |

            ## O que você recebe de volta

            - **id** — identificador único da simulação (use no GET para consultar depois)
            - **valorTotalFinal** — total a pagar ao fim do contrato
            - **valorTotalJuros** — quanto você paga só de juros
            - **memoriaCalculo** — evolução mês a mês com saldo inicial, juros e saldo final

            ## Regras de validação

            - `valorInicial` deve ser maior que **R$ 0,01**
            - `taxaMensal` deve estar entre **0,01%** e **100%**
            - `prazoMeses` deve estar entre **1** e **600** meses
            """
    )
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Simulação criada com sucesso",
                     content = @Content(schema = @Schema(implementation = SimulacaoResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Dados de entrada inválidos — verifique os campos obrigatórios"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response criarSimulacao(SimulacaoRequestDTO request) {
        Set<ConstraintViolation<SimulacaoRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String mensagens = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", mensagens)).build();
        }
        SimulacaoResponseDTO response = service.simular(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    // ----------------------------------------------------------------
    // 2. GET /{id} — Consultar por ID
    // ----------------------------------------------------------------
    @GET
    @Path("/{id}")
    @Operation(
        summary = "Consultar simulação existente",
        description = """
            ## Como usar

            Informe o **id** retornado ao criar a simulação e clique em **Execute**.

            Você receberá todos os dados da simulação, incluindo a memória de cálculo \
            completa com a evolução mês a mês do saldo devedor.

            > Se o id não existir, a API retorna **404 Not Found**.
            """
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Simulação encontrada",
                     content = @Content(schema = @Schema(implementation = SimulacaoResponseDTO.class))),
        @APIResponse(responseCode = "404", description = "Simulação não encontrada para o ID informado")
    })
    public Response buscarSimulacao(@PathParam("id") Long id) {
        SimulacaoResponseDTO response = service.buscarPorId(id);
        return Response.ok(response).build();
    }

    // ----------------------------------------------------------------
    // 3. GET — Listar todas
    // ----------------------------------------------------------------
    @GET
    @Operation(
        summary = "Listar todas as simulações",
        description = """
            ## Como usar

            Clique em **Execute** para ver todas as simulações já realizadas.

            Retorna uma lista com todas as simulações persistidas no banco,
            incluindo a memória de cálculo completa de cada uma.

            > Se ainda não houver simulações, retorna uma lista vazia `[]`.
            """
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Lista de simulações retornada com sucesso"),
        @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Response listarSimulacoes() {
        List<SimulacaoResponseDTO> lista = service.listarTodas();
        return Response.ok(lista).build();
    }
}
