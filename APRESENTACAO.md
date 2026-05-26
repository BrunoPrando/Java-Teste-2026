# 🎯 Script de Apresentação — Simulador de Financiamentos

---

## ⏱️ Estrutura de Tempo Sugerida (20-30 min)

| Bloco | Tempo | Conteúdo |
|-------|-------|---------|
| Abertura | 2 min | Contextualizar o problema |
| Arquitetura | 4 min | Mostrar estrutura do projeto |
| Demonstração ao vivo | 8 min | Rodar, testar, mostrar Swagger |
| Código-chave | 8 min | Lógica financeira + testes |
| Cobertura (Jacoco) | 3 min | Rodar `mvn verify` ao vivo |
| Encerramento | 2 min | Decisões técnicas + perguntas |

---

## 📣 Script Falado (passo a passo)

### BLOCO 1 — Abertura
> *"O desafio foi construir uma API backend Java para simular financiamentos com juros compostos.
> A ideia central é: o cliente entra com o valor, a taxa mensal e o prazo — e recebe de volta
> uma memória de cálculo completa mês a mês, além do total de juros e o valor final.
> Tudo persiste no banco para consultas futuras."*

---

### BLOCO 2 — Mostrar Estrutura
Abra o projeto no terminal ou IDE e mostre:

```bash
tree src/main/java/com/simulador
```

> *"Segui uma arquitetura limpa em 4 camadas: Resource (controller), Service (lógica de negócio),
> Repository (acesso a dados via Panache) e Model/DTO.
> Nenhuma regra de negócio no Resource, nenhum SQL manual."*

**Destaque no código:**
- `SimulacaoResource.java` → só recebe, valida e delega
- `SimulacaoService.java` → onde mora o cálculo real

---

### BLOCO 3 — Demonstração ao Vivo

**Passo 1 — Subir a aplicação:**
```bash
./mvnw quarkus:dev
```

**Passo 2 — Abrir Swagger UI:**
```
http://localhost:8080/swagger-ui
```
> *"Aqui o avaliador já vê o contrato da API sem nenhuma configuração adicional.
> Isso é Spec-Driven Development: o código gera a spec automaticamente via OpenAPI/Swagger."*

**Passo 3 — Fazer o POST via Swagger ou curl:**
```bash
curl -s -X POST http://localhost:8080/api/simulacoes \
  -H "Content-Type: application/json" \
  -d '{"valorInicial": 1000.00, "taxaMensal": 1.5, "prazoMeses": 12}' | jq .
```

> *"Olhem a resposta: temos o ID gerado, o total final, o total de juros, e cada mês
> com saldo inicial, juros incidentes e saldo final. Tudo com 6 casas decimais — BigDecimal."*

**Passo 4 — Consultar por ID:**
```bash
curl -s http://localhost:8080/api/simulacoes/1 | jq .
```

**Passo 5 — Testar erro 404:**
```bash
curl -s http://localhost:8080/api/simulacoes/999 | jq .
```
> *"Retorno HTTP 404 com mensagem clara. Não vaza stack trace."*

**Passo 6 — Testar validação 400:**
```bash
curl -s -X POST http://localhost:8080/api/simulacoes \
  -H "Content-Type: application/json" \
  -d '{"taxaMensal": 1.5}' | jq .
```

---

### BLOCO 4 — Mostrar o Código-Chave

**Abra `SimulacaoService.java` e explique o método `calcularMemoria`:**

> *"Aqui está o coração do sistema. Cada iteração representa um mês.
> O saldo inicial do mês seguinte é exatamente o saldo final do mês anterior —
> isso é a definição de juros compostos.*
>
> *Usei BigDecimal com MathContext de 20 dígitos e arredondamento HALF_EVEN,
> que é o padrão bancário — evita viés de arredondamento acumulado ao longo dos meses."*

**Mostre o DTO com validações:**

> *"O Bean Validation garante que se alguém mandar taxa zero ou prazo negativo,
> a aplicação rejeita com 400 antes mesmo de chegar no Service."*

---

### BLOCO 5 — Cobertura de Testes (Jacoco)

```bash
./mvnw clean verify
```

Após rodar, abra:
```
target/jacoco-report/index.html
```

> *"Temos testes de unidade no Service e testes de integração no Resource usando REST-Assured.
> Cobrem cenários de sucesso, borda (valores mínimos), e erro (404, 400).
> O Jacoco reporta a cobertura — critério eliminatório do desafio."*

---

### BLOCO 6 — Encerramento

> *"Para resumir as principais decisões:*
> - *BigDecimal em todo lugar monetário — nunca double ou float*
> - *H2 embutido — zero Docker, roda 100% nativo como exigido*
> - *Swagger automático via SmallRye OpenAPI*
> - *Camadas bem separadas — Resource, Service, Repository*
> - *Testes com cobertura acima de 80%"*

---

## 💡 Dicas para a Apresentação

### Antes de apresentar
- [ ] Rode `./mvnw clean verify` para garantir que tudo compila e testa
- [ ] Deixe o servidor já rodando com `./mvnw quarkus:dev`
- [ ] Tenha o Swagger aberto no browser
- [ ] Instale `jq` para formatar respostas JSON no terminal (`apt install jq` ou `brew install jq`)

### Durante a apresentação
- **Fale enquanto digita** — não fique em silêncio esperando o resultado
- **Mostre os logs do Quarkus** — demonstra que não há Docker/scripts externos
- **Evite scrollar código rápido** — pare em cada método e explique em 2 frases
- **Se algo der erro**, mantenha a calma e debug ao vivo — mostra maturidade

### Perguntas frequentes do avaliador

| Pergunta | Resposta sugerida |
|---------|-------------------|
| Por que Panache e não JPA puro? | Reduz boilerplate sem perder flexibilidade; `findByIdOptional` torna o código expressivo |
| Por que BigDecimal e não double? | Double tem representação binária inexata — R$0.10 + R$0.20 ≠ R$0.30. Em finanças isso é crítico |
| Como garantir 80% de cobertura? | Combinei testes de serviço (lógica pura) + testes de integração REST-Assured que cobrem o Resource e o ExceptionMapper |
| Por que H2 file e não in-memory em prod? | In-memory perde dados ao reiniciar; file persiste — consultável entre sessões |
| O que mudaria em produção? | Trocaria H2 por PostgreSQL mudando só o `application.properties` e a dependência do driver |

---

## 🧪 Comandos de Referência Rápida

```bash
# Subir em dev
./mvnw quarkus:dev

# Rodar só os testes
./mvnw test

# Build completo com cobertura
./mvnw clean verify

# Ver relatório de cobertura
open target/jacoco-report/index.html   # macOS
xdg-open target/jacoco-report/index.html  # Linux

# Testar manualmente
curl -X POST http://localhost:8080/api/simulacoes \
  -H "Content-Type: application/json" \
  -d '{"valorInicial":1000,"taxaMensal":1.5,"prazoMeses":12}' | jq .

curl http://localhost:8080/api/simulacoes/1 | jq .
```
