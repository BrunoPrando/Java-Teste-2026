package com.simulador.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "memoria_calculo")
public class MemoriaCalculo extends PanacheEntity {

    @Column(nullable = false)
    public Integer mes;

    @Column(nullable = false, precision = 19, scale = 6)
    public BigDecimal saldoInicial;

    @Column(nullable = false, precision = 19, scale = 6)
    public BigDecimal jurosMes;

    @Column(nullable = false, precision = 19, scale = 6)
    public BigDecimal saldoFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulacao_id", nullable = false)
    public Simulacao simulacao;
}
