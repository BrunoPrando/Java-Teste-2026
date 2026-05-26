package com.simulador.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "simulacoes")
public class Simulacao extends PanacheEntity {

    @Column(nullable = false, precision = 19, scale = 6)
    public BigDecimal valorInicial;

    @Column(nullable = false, precision = 10, scale = 6)
    public BigDecimal taxaMensal;

    @Column(nullable = false)
    public Integer prazoMeses;

    @Column(nullable = false, precision = 19, scale = 6)
    public BigDecimal valorTotalFinal;

    @Column(nullable = false, precision = 19, scale = 6)
    public BigDecimal valorTotalJuros;

    @Column(nullable = false)
    public LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "simulacao", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy("mes ASC")
    public List<MemoriaCalculo> memoriaCalculo = new ArrayList<>();
}
