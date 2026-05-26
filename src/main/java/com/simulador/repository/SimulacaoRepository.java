package com.simulador.repository;

import com.simulador.model.Simulacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepository<Simulacao> {
    // Herda findById, persist, listAll, etc. do Panache
}
