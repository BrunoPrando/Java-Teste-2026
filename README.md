G# Simulador de Financiamentos — Java / Quarkus

API REST para simulação de financiamentos com juros compostos, memória de cálculo detalhada e persistência em banco H2 embutido.

---

## Pré-Requisitos

| Ferramenta | Versão Mínima |
|-----------|---------------|
| Java (JDK) | 23+ |
| Maven | 3.8.6+ |
| (Nenhum Docker necessário) | — |

Verifique com:
```bash
java -version
mvn -version
```

---

## Compilar e Executar

### 1. Modo Desenvolvimento (recomendado para avaliação)
```bash
mvn quarkus:dev
```

Após iniciar, acesse a interface da API no browser:
```
http://localhost:8080/swagger-ui
```

### 2. Build e execução JAR
```bash
mvn clean package -DskipTests
java -jar target\quarkus-app\quarkus-run.jar
```

---

## Executar Testes + Relatório de Cobertura (Jacoco)

```bash
mvn clean verify
```

O relatório HTML de cobertura é gerado em:
```
target\jacoco-report\index.html
```

Para abrir o relatório após os testes:
```bash
start target\jacoco-report\index.html
```

---

## Endpoints da API

> Acesse o Swagger UI para testar os endpoints de forma visual:
> **http://localhost:8080/swagger-ui**

### Criar Simulação
```
POST http://localhost:8080/api/simulacoes
Content-Type: application/json

{
  "valorInicial": 1000.00,
  "taxaMensal": 1.5,
  "prazoMeses": 12
}
```

### Consultar Simulação
```
GET http://localhost:8080/api/simulacoes/{id}
```

### OpenAPI Spec (JSON)
```
http://localhost:8080/q/openapi
```

---

## Exemplo de Request/Response

**Request:**
```json
{
  "valorInicial": 1000.00,
  "taxaMensal": 1.5,
  "prazoMeses": 3
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "valorInicial": 1000.000000,
  "taxaMensal": 1.500000,
  "prazoMeses": 3,
  "valorTotalFinal": 1045.678375,
  "valorTotalJuros": 45.678375,
  "criadoEm": "2024-01-15T10:30:00",
  "memoriaCalculo": [
    { "mes": 1, "saldoInicial": 1000.000000, "jurosMes": 15.000000, "saldoFinal": 1015.000000 },
    { "mes": 2, "saldoInicial": 1015.000000, "jurosMes": 15.225000, "saldoFinal": 1030.225000 },
    { "mes": 3, "saldoInicial": 1030.225000, "jurosMes": 15.453375, "saldoFinal": 1045.678375 }
  ]
}
```

---

## Estrutura do Projeto

```
src/
├── main/java/com/simulador/
│   ├── model/
│   │   ├── Simulacao.java          # Entidade JPA principal
│   │   └── MemoriaCalculo.java     # Entidade de memória mês a mês
│   ├── dto/
│   │   ├── SimulacaoRequestDTO.java   # Entrada com validações
│   │   └── SimulacaoResponseDTO.java  # Saída completa
│   ├── repository/
│   │   └── SimulacaoRepository.java   # Panache Repository
│   ├── service/
│   │   └── SimulacaoService.java      # Lógica de negócio / cálculo
│   └── resource/
│       ├── SimulacaoResource.java     # Controller REST
│       └── GlobalExceptionMapper.java # Tratamento de erros
└── test/java/com/simulador/
    ├── SimulacaoServiceTest.java        # Testes unitários de serviço
    ├── SimulacaoResourceTest.java       # Testes de integração REST
    └── GlobalExceptionMapperTest.java   # Testes do tratamento de erros
```

---

## Decisões Técnicas

- **BigDecimal** em todos os campos monetários → sem perda de precisão (double/float não usado)
- **MathContext(20, HALF_EVEN)** → arredondamento bancário padrão
- **H2 embutido** → arquivo local em modo dev, in-memory em testes
- **Panache** → simplifica Repository sem boilerplate
- **Validação manual** no Resource → compatível com Java 23
- **OpenAPI/Swagger** → exposto em `/swagger-ui`
- **Jacoco** → cobertura reportada no build `verify`
