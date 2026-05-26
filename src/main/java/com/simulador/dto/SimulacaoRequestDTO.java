package com.simulador.dto;

import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Dados de entrada para simulação de financiamento")
public class SimulacaoRequestDTO {

    @Schema(
        description = "Valor principal do financiamento",
        example = "1000.00",
        required = true
    )
    @NotNull(message = "valorInicial é obrigatório")
    @DecimalMin(value = "0.01", message = "valorInicial deve ser maior que zero")
    public BigDecimal valorInicial;

    @Schema(
        description = "Taxa de juros mensal em percentual (ex: 1.5 para 1,5% ao mês)",
        example = "1.5",
        required = true
    )
    @NotNull(message = "taxaMensal é obrigatório")
    @DecimalMin(value = "0.01", message = "taxaMensal deve ser maior que zero")
    @DecimalMax(value = "100.0", message = "taxaMensal não pode exceder 100%")
    public BigDecimal taxaMensal;

    @Schema(
        description = "Prazo do contrato em meses (ex: 12 para 1 ano)",
        example = "12",
        required = true
    )
    @NotNull(message = "prazoMeses é obrigatório")
    @Min(value = 1, message = "prazoMeses deve ser pelo menos 1")
    @Max(value = 600, message = "prazoMeses não pode exceder 600")
    public Integer prazoMeses;
}
