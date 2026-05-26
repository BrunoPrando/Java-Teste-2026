package com.simulador.service;

import com.simulador.dto.SimulacaoRequestDTO;
import com.simulador.dto.SimulacaoResponseDTO;
import com.simulador.model.MemoriaCalculo;
import com.simulador.model.Simulacao;
import com.simulador.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_EVEN);
    private static final int ESCALA = 6;

    @Inject
    SimulacaoRepository repository;

    @Transactional
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO request) {
        BigDecimal taxaDecimal = request.taxaMensal.divide(BigDecimal.valueOf(100), MC);
        List<MemoriaCalculo> memoria = calcularMemoria(request.valorInicial, taxaDecimal, request.prazoMeses);

        BigDecimal saldoFinal  = memoria.get(memoria.size() - 1).saldoFinal;
        BigDecimal totalJuros  = saldoFinal.subtract(request.valorInicial).setScale(ESCALA, RoundingMode.HALF_EVEN);

        Simulacao simulacao = new Simulacao();
        simulacao.valorInicial    = request.valorInicial.setScale(ESCALA, RoundingMode.HALF_EVEN);
        simulacao.taxaMensal      = request.taxaMensal.setScale(ESCALA, RoundingMode.HALF_EVEN);
        simulacao.prazoMeses      = request.prazoMeses;
        simulacao.valorTotalFinal = saldoFinal.setScale(ESCALA, RoundingMode.HALF_EVEN);
        simulacao.valorTotalJuros = totalJuros;

        for (MemoriaCalculo m : memoria) {
            m.simulacao = simulacao;
            simulacao.memoriaCalculo.add(m);
        }

        repository.persist(simulacao);
        return toDTO(simulacao);
    }

    public SimulacaoResponseDTO buscarPorId(Long id) {
        Simulacao simulacao = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Simulação com ID " + id + " não encontrada."));
        return toDTO(simulacao);
    }

    public List<SimulacaoResponseDTO> listarTodas() {
        return repository.listAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private List<MemoriaCalculo> calcularMemoria(BigDecimal valorInicial, BigDecimal taxa, int prazo) {
        List<MemoriaCalculo> lista = new ArrayList<>();
        BigDecimal saldo = valorInicial.setScale(ESCALA, RoundingMode.HALF_EVEN);

        for (int mes = 1; mes <= prazo; mes++) {
            BigDecimal juros     = saldo.multiply(taxa, MC).setScale(ESCALA, RoundingMode.HALF_EVEN);
            BigDecimal novoSaldo = saldo.add(juros).setScale(ESCALA, RoundingMode.HALF_EVEN);

            MemoriaCalculo m = new MemoriaCalculo();
            m.mes          = mes;
            m.saldoInicial = saldo;
            m.jurosMes     = juros;
            m.saldoFinal   = novoSaldo;
            lista.add(m);
            saldo = novoSaldo;
        }
        return lista;
    }

    private SimulacaoResponseDTO toDTO(Simulacao s) {
        SimulacaoResponseDTO dto = new SimulacaoResponseDTO();
        dto.id              = s.id;
        dto.valorInicial    = s.valorInicial;
        dto.taxaMensal      = s.taxaMensal;
        dto.prazoMeses      = s.prazoMeses;
        dto.valorTotalFinal = s.valorTotalFinal;
        dto.valorTotalJuros = s.valorTotalJuros;
        dto.criadoEm        = s.criadoEm;

        dto.memoriaCalculo = s.memoriaCalculo.stream().map(m -> {
            SimulacaoResponseDTO.MemoriaCalculoDTO mDTO = new SimulacaoResponseDTO.MemoriaCalculoDTO();
            mDTO.mes          = m.mes;
            mDTO.saldoInicial = m.saldoInicial;
            mDTO.jurosMes     = m.jurosMes;
            mDTO.saldoFinal   = m.saldoFinal;
            return mDTO;
        }).toList();

        return dto;
    }
}
