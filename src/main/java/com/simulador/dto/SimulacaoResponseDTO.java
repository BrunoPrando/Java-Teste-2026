package com.simulador.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SimulacaoResponseDTO {

    public Long id;
    public BigDecimal valorInicial;
    public BigDecimal taxaMensal;
    public Integer prazoMeses;
    public BigDecimal valorTotalFinal;
    public BigDecimal valorTotalJuros;
    public LocalDateTime criadoEm;
    public List<MemoriaCalculoDTO> memoriaCalculo;

    public static class MemoriaCalculoDTO {
        public Integer mes;
        public BigDecimal saldoInicial;
        public BigDecimal jurosMes;
        public BigDecimal saldoFinal;
    }
}
