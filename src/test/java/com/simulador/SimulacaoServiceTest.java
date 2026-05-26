package com.simulador;

import com.simulador.dto.SimulacaoRequestDTO;
import com.simulador.dto.SimulacaoResponseDTO;
import com.simulador.service.SimulacaoService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

@QuarkusTest
class SimulacaoServiceTest {

    @Inject
    SimulacaoService service;

    @Test
    @DisplayName("Deve calcular juros compostos corretamente")
    void deveCalcularJurosCompostosCorretamente() {
        // R$1.000 a 1,5% ao mês por 12 meses
        // Fórmula: 1000 * (1.015)^12 ≈ 1195.618...
        SimulacaoRequestDTO req = new SimulacaoRequestDTO();
        req.valorInicial = new BigDecimal("1000.00");
        req.taxaMensal = new BigDecimal("1.5");
        req.prazoMeses = 12;

        SimulacaoResponseDTO resp = service.simular(req);

        Assertions.assertNotNull(resp.id);
        // Valor total final deve ser maior que o inicial
        Assertions.assertTrue(resp.valorTotalFinal.compareTo(req.valorInicial) > 0);
        // Total de juros deve ser positivo
        Assertions.assertTrue(resp.valorTotalJuros.compareTo(BigDecimal.ZERO) > 0);
        // Memória de cálculo deve ter 12 entradas
        Assertions.assertEquals(12, resp.memoriaCalculo.size());
        // Primeiro mês: juros = 1000 * 0.015 = 15.00
        Assertions.assertEquals(0,
            resp.memoriaCalculo.get(0).jurosMes.compareTo(new BigDecimal("15.000000")));
    }

    @Test
    @DisplayName("Deve persistir e recuperar simulação por ID")
    void devePersistirERecuperarSimulacao() {
        SimulacaoRequestDTO req = new SimulacaoRequestDTO();
        req.valorInicial = new BigDecimal("5000.00");
        req.taxaMensal = new BigDecimal("2.0");
        req.prazoMeses = 6;

        SimulacaoResponseDTO criada = service.simular(req);
        SimulacaoResponseDTO recuperada = service.buscarPorId(criada.id);

        Assertions.assertEquals(criada.id, recuperada.id);
        Assertions.assertEquals(0, criada.valorTotalFinal.compareTo(recuperada.valorTotalFinal));
        Assertions.assertEquals(6, recuperada.memoriaCalculo.size());
    }

    @Test
    @DisplayName("Deve lançar exceção para ID inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        Assertions.assertThrows(
            jakarta.ws.rs.NotFoundException.class,
            () -> service.buscarPorId(999999L)
        );
    }

    @Test
    @DisplayName("Deve usar BigDecimal (precisão financeira)")
    void deveUsarBigDecimalParaPrecisaoFinanceira() {
        SimulacaoRequestDTO req = new SimulacaoRequestDTO();
        req.valorInicial = new BigDecimal("0.01");
        req.taxaMensal = new BigDecimal("0.01");
        req.prazoMeses = 1;

        SimulacaoResponseDTO resp = service.simular(req);

        // Não deve ser zero nem negativo
        Assertions.assertTrue(resp.valorTotalFinal.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Memória de cálculo deve ser sequencial e crescente")
    void memoriaDeveSerSequencialECrescente() {
        SimulacaoRequestDTO req = new SimulacaoRequestDTO();
        req.valorInicial = new BigDecimal("10000.00");
        req.taxaMensal = new BigDecimal("1.0");
        req.prazoMeses = 3;

        SimulacaoResponseDTO resp = service.simular(req);

        for (int i = 0; i < resp.memoriaCalculo.size(); i++) {
            Assertions.assertEquals(i + 1, resp.memoriaCalculo.get(i).mes);
        }
        // Saldo deve crescer a cada mês
        Assertions.assertTrue(
            resp.memoriaCalculo.get(1).saldoFinal.compareTo(
            resp.memoriaCalculo.get(0).saldoFinal) > 0
        );
    }
}
