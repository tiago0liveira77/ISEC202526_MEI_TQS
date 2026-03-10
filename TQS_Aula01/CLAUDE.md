# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Visão Geral do Projeto

Projeto Java com Maven para a disciplina TQS (Técnicas e Qualidade de Software) — ISEC MEI 2025-2026. Foco em técnicas de teste de software: ECT, BVA, Decision Tables e State Transition Testing.

- **Package principal:** `org.ficha01`
- **Versão JUnit:** `5.11.4` (não usar 6.x — incompatível com o runner do IntelliJ)

## Comandos Maven

```bash
# Compilar
mvn clean compile

# Correr todos os testes
mvn test

# Correr uma classe de testes específica
mvn test -Dtest=NomeDaClasse

# Correr um método de teste específico
mvn test -Dtest=NomeDaClasse#nomeDoMetodo

# Empacotar
mvn clean package
```

## Stack Tecnológica

- **Java 21**
- **Maven**
- **JUnit Jupiter 5.11.4** — `org.junit.jupiter:junit-jupiter:5.11.4`

## Estrutura do Projeto

```
src/main/java/org/ficha01/   # Código fonte
src/test/java/org/ficha01/   # Testes (mesmo package que o código fonte)
```

## Convenções de Teste

Usar `@Test` de `org.junit.jupiter.api.Test`. Asserções de `org.junit.jupiter.api.Assertions`.

---

## Lab 2 — Tabelas de Design de Testes (ECT + BVA)

### Exercício 1: IdTriangle

**Partições ECT (baseadas no output):**

| ID | Classe | Condição | Válida? | Resultado Esperado |
|----|--------|----------|---------|-------------------|
| C1 | Equilátero | a == b == c | Válida | EQUILATERAL |
| C2 | Isósceles | Exatamente dois lados iguais + desigualdade triangular satisfeita | Válida | ISOSCELES |
| C3 | Escaleno | Todos os lados diferentes + desigualdade triangular satisfeita | Válida | SCALENE |
| C4 | Não é triângulo | Desigualdade triangular violada (ex: a+b ≤ c) | Input válido, geometria inválida | NOTTRIANGLE |
| C5 | Lado inválido | Qualquer lado ≤ 0 | Inválida | IllegalArgumentException |

**Desigualdade triangular:** a+b > c E a+c > b E b+c > a

**Fronteiras BVA (2-value):**
- Valor do lado: 0 (inválido) / 1 (mínimo válido)
- Desigualdade triangular: a+b = c → NOTTRIANGLE / a+b = c+1 → triângulo válido

**Casos de teste derivados:**

| Teste | a | b | c | ECT/BVA | Esperado |
|-------|---|---|---|---------|----------|
| TC1 | 3 | 3 | 3 | C1 | EQUILATERAL |
| TC2 | 3 | 3 | 4 | C2 (a==b) | ISOSCELES |
| TC3 | 3 | 4 | 4 | C2 (b==c) | ISOSCELES |
| TC4 | 4 | 3 | 4 | C2 (a==c) | ISOSCELES |
| TC5 | 3 | 4 | 5 | C3 | SCALENE |
| TC6 | 1 | 2 | 4 | C4 | NOTTRIANGLE |
| TC7 | 0 | 1 | 1 | C5 | exception |
| TC8 | -1 | 1 | 1 | C5 | exception |
| TC9 | 1 | 1 | 1 | BVA lado mín | EQUILATERAL |
| TC10 | 0 | 4 | 4 | BVA lado=0 | exception |
| TC11 | 3 | 4 | 7 | BVA a+b=c | NOTTRIANGLE |
| TC12 | 3 | 4 | 6 | BVA a+b=c+1 | SCALENE |

---

### Exercício 2: NextDate

**Partições ECT por variável:**

| Variável | ID | Intervalo/Valor | Válida? | Notas |
|----------|----|-----------------|---------|-------|
| month | M1 | Meses de 31 dias: {1,3,5,7,8,10,12} | Válida | |
| month | M2 | Meses de 30 dias: {4,6,9,11} | Válida | |
| month | M3 | Fevereiro: {2} | Válida | Comportamento depende do ano |
| month | M4 | < 1 ou > 12 | Inválida | |
| day | D1 | 1–27 | Válida | Seguro para qualquer mês |
| day | D2 | 28 | Válida | Fronteira para fev. não-bissexto |
| day | D3 | 29 | Válida | Fronteira para fev. bissexto |
| day | D4 | 30 | Válida | Fronteira para meses de 30 dias |
| day | D5 | 31 | Válida | Fronteira para meses de 31 dias |
| day | D6 | < 1 ou > 31 | Inválida | |
| year | Y1 | Ano não-bissexto em [1900–2025] | Válida | |
| year | Y2 | Ano bissexto em [1900–2025] | Válida | |
| year | Y3 | < 1900 ou > 2025 | Inválida | |

**Regra do ano bissexto:** divisível por 4, exceto séculos — a não ser que divisível por 400.
(1900 **não** é bissexto; 2000 **é** bissexto)

**Fronteiras BVA (2-value):**
- year: 1899 (inválido) / 1900 (válido) / 2025 (válido) / 2026 (inválido)
- month: 0 (inválido) / 1 (válido) / 12 (válido) / 13 (inválido)
- day: 0 (inválido) / 1 (válido) / último dia do mês (válido) / último+1 (inválido)

**Casos de teste derivados (each-choice + BVA):**

| Teste | month | day | year | ECT/BVA | Esperado |
|-------|-------|-----|------|---------|----------|
| TC1 | 1 | 15 | 2023 | M1,D1,Y1 | 2023-01-16 |
| TC2 | 1 | 31 | 2023 | M1,D5,Y1 | 2023-02-01 |
| TC3 | 4 | 30 | 2023 | M2,D4,Y1 | 2023-05-01 |
| TC4 | 4 | 31 | 2023 | M2,D6 | null |
| TC5 | 2 | 15 | 2023 | M3,D1,Y1 | 2023-02-16 |
| TC6 | 2 | 28 | 2023 | M3,D2,Y1 | 2023-03-01 |
| TC7 | 2 | 29 | 2023 | M3,D3,Y1 | null |
| TC8 | 2 | 28 | 2024 | M3,D2,Y2 | 2024-02-29 |
| TC9 | 2 | 29 | 2024 | M3,D3,Y2 | 2024-03-01 |
| TC10 | 12 | 31 | 2024 | rollover ano | 2025-01-01 |
| TC11 | 12 | 31 | 2025 | Y1 limite max | null |
| TC12 | 0 | 15 | 2023 | M4 | null |
| TC13 | 13 | 15 | 2023 | M4 | null |
| TC14 | 1 | 0 | 2023 | D6 | null |
| TC15 | 1 | 32 | 2023 | D6 | null |
| TC16 | 1 | 15 | 1899 | Y3 | null |
| TC17 | 1 | 15 | 2026 | Y3 | null |
| TC18 | 1 | 15 | 1900 | BVA year mín | 1900-01-16 |
| TC19 | 1 | 15 | 2025 | BVA year máx | 2025-01-16 |
| TC20 | 2 | 28 | 1900 | BVA 1900 não-biss. | 1900-03-01 |
| TC21 | 2 | 29 | 1900 | BVA 1900 não-biss. | null |

---

## Lab 3 — Decision Tables e State Transition Testing

### Decision Tables (DT)

**O que são:** Tabelas de decisão modelam comportamento condicional complexo. Cada coluna é uma **regra** que associa um conjunto de condições a um conjunto de ações. Cada regra pode originar um caso de teste.

**Estrutura da tabela:**

```
               | R1 | R2 | R3 | ...
Condições (Cx) |  T |  F |  - | ...   ← T=true, F=false, -=don't care
Ações (Ax)     |  x |    |  x | ...   ← x=executar esta ação
```

- **Condition Stub**: lista de condições booleanas relevantes
- **Action Stub**: lista de ações possíveis (outputs/resultados)
- **Regra**: coluna que combina valores de condições com ações resultantes
- **`-` (don't care)**: o valor desta condição não importa para esta regra
- **Regras impossíveis**: combinações de condições que não podem ocorrer em simultâneo — devem ser eliminadas

**Processo de derivação de testes:**
1. Identificar todas as condições relevantes
2. Enumerar todas as combinações possíveis (2^n para n condições booleanas)
3. Eliminar combinações impossíveis
4. Para cada regra válida, derivar um caso de teste concreto

**Exemplo — Triângulo (simplificado):**

| Condition Stub | R1 | R2 | R3 | R4 | R7 | R9 | R10 | R11 |
|----------------|----|----|----|----|----|----|-----|-----|
| C1: a < b+c    | F  | T  | T  | T  | T  | T  | T   | T   |
| C2: b < a+c    | -  | F  | T  | T  | T  | T  | T   | T   |
| C3: c < b+a    | -  | -  | F  | T  | T  | T  | T   | T   |
| C4: a = b      | -  | -  | -  | T  | T  | F  | F   | F   |
| C5: a = c      | -  | -  | -  | T  | F  | T  | F   | F   |
| C6: b = c      | -  | -  | -  | T  | F  | F  | T   | F   |
| A1: not triangle| x  | x  | x  |    |    |    |     |     |
| A2: Scalene    |    |    |    |    |    |    |     | x   |
| A3: Isosceles  |    |    |    |    | x  | x  | x   |     |
| A4: Equilateral|    |    |    | x  |    |    |     |     |

> R5 (a=b, a=c, b=c não todos T com apenas 2 iguais) e R8 são **impossíveis** e foram removidas.

**Casos de teste derivados (um por regra):**

| TC | a  | b  | c  | Output       |
|----|----|----|----|--------------|
| T1 | 4  | 1  | 2  | Not triangle |
| T2 | 2  | 9  | 6  | Not triangle |
| T3 | 3  | 4  | 8  | Not triangle |
| T4 | 10 | 10 | 10 | Equilateral  |
| T5 | 1  | 9  | 9  | Isosceles    |
| T6 | 8  | 2  | 8  | Isosceles    |
| T7 | 7  | 7  | 6  | Isosceles    |
| T8 | 2  | 3  | 4  | Scalene      |

---

### State Transition Testing

O comportamento do sistema depende não só do input atual mas também do **histórico de inputs** (estado atual).

**Representações de uma máquina de estados:**
- Diagrama gráfico
- Tabela de transições reduzida (só transições válidas)
- Tabela de transições completa (inclui transições inválidas como `undefined`)

**Critérios de cobertura clássicos (ISTQB) — por ordem crescente de exigência:**

| Critério | Definição | Subsume |
|----------|-----------|---------|
| **State Coverage** | Todos os estados visitados ≥ 1 vez | — |
| **Transition Coverage (0-switch)** | Todas as transições exercidas ≥ 1 vez | State Coverage |
| **N-switch Coverage** | Todos os N-switches percorridos ≥ 1 vez | (N-1)-switch Coverage |
| **Invalid Transition Coverage** | Todas as transições inválidas tentadas (1 teste por transição inválida) | — |

**O que é um N-switch:**
- Um N-switch é uma sequência de transições com exatamente N+1 transições (N estados intermédios)
- 0-switch: `A --(E1)--> B` (uma transição direta)
- 1-switch: `A --(E1)--> B --(E2)--> C` (dois passos consecutivos)
- 2-switch: `A --> B --> C --> D`

**Como derivar 1-switches:**
1. Listar todos os 0-switches (transições válidas)
2. Para cada 0-switch `A → B`, expandir com cada transição possível a partir de B: `A → B → C`

**Invalid Transition Coverage:**
- Usar a tabela de transições **completa** para identificar todas as transições `undefined`
- Criar **1 teste por transição inválida** (não se podem encadear num único teste)
- Cada teste navega até ao estado correto e tenta o evento inválido

---

## Lab 4 — Cobertura de Grafos (Structural Coverage)

### Definições de Critérios de Cobertura de Grafos

**Node Coverage (NC) — Cobertura de Nós**
- **TR:** Cada nó n ∈ N deve ser visitado por pelo menos um caminho de teste.
- É o critério mais fraco. Basta que cada nó apareça em algum caminho.

**Edge Coverage (EC) — Cobertura de Arestas**
- **TR:** Cada aresta (u, v) ∈ E deve ser percorrida por pelo menos um caminho de teste.
- Subsume Node Coverage: cobrir todas as arestas implica visitar todos os nós.
- Mais forte que NC porque exige que cada **transição** entre nós seja exercida.

**Edge-Pair Coverage (EPC) — Cobertura de Pares de Arestas**
- **TR:** Cada par de arestas consecutivas (caminho de comprimento 2) — ou seja, cada subcaminho [u, v, w] tal que (u,v) ∈ E e (v,w) ∈ E — deve ser percorrido por pelo menos um caminho de teste. Para nós sem arestas de saída (nós finais), considera-se o subcaminho de comprimento 1 que termina nesse nó.
- Subsume Edge Coverage.
- Mais forte que EC porque exige que cada **par de transições consecutivas** seja exercido.

**Hierarquia de subsunção:**
```
Edge-Pair Coverage (EPC)
        ↓ subsume
  Edge Coverage (EC)
        ↓ subsume
  Node Coverage (NC)
```

---

### Prime Path Coverage (PPC) — Cobertura de Caminhos Primos

**Definições:**
- **Caminho simples:** nenhum nó se repete, *exceto* que o primeiro e o último podem ser iguais (ciclo simples).
- **Prime path:** caminho simples **maximal** — não é subpath próprio de nenhum outro caminho simples.
- **TR:** cada prime path deve ser percorrido por pelo menos um caminho de teste (diretamente ou com sidetrip).
- Subsume Edge-Pair Coverage.

**Algoritmo para identificar prime paths:**

1. Expandir caminhos simples a partir de cada nó, nunca repetindo nós (exceto para fechar ciclo).
2. Um caminho é **prime** se não puder ser estendido em nenhuma direção:
   - **Para a frente:** todos os sucessores do último nó já estão no caminho, ou é nó terminal.
   - **Para trás:** todos os predecessores do primeiro nó já estão no caminho, ou não há predecessores.
3. Se conseguires adicionar um nó antes do início **ou** depois do fim mantendo o caminho simples → **não é prime** (é subpath de outro).

**Regras práticas:**
- **Ciclos:** cada rotação do ponto de partida origina um prime path distinto. Ex: [1,2,4,6,1] e [2,4,6,1,2] são ambos prime.
- **Caminhos até nós terminais:** rastrear para trás a partir do nó terminal até bloquear (predecessor já no caminho).
- **Caminhos que terminam "presos":** nós cujo único sucessor já está no caminho (ex: nó 3 em [4,6,1,2,3] — só tem 3→2 e 2 já está).

**Verificação rápida:** `[2,4,5,6,1,7]` é prime?
- Para trás: pred(2) = {1, 3}. Tentar 3→2: [3,2,4,5,6,1,7] — válido e simples.
- Logo [2,4,5,6,1,7] é subpath de [3,2,4,5,6,1,7] → **NÃO é prime**.

---

### Exercício 1 — Afirmações sobre critérios de cobertura

**a) "Se T satisfaz edge-pair coverage, também satisfaz node coverage"**

**VERDADEIRO.** A hierarquia de subsunção é:

```
Edge-Pair Coverage (EPC)
        ↓ subsume
  Edge Coverage (EC)
        ↓ subsume
  Node Coverage (NC)
```

Cobrir todos os pares de arestas implica obrigatoriamente cobrir todos os nós.

---

**b) "Se T_edge satisfaz edge coverage e T_node satisfaz node coverage, então T_node ⊆ T_edge"**

**FALSO.** T_edge e T_node são conjuntos de caminhos de teste **independentes**, escolhidos separadamente para satisfazer critérios distintos. Não existe nenhuma relação de inclusão garantida entre os dois conjuntos.

---

**c) "Se T_edge satisfaz edge coverage e T_node satisfaz node coverage, então se T_node revela um defeito, T_edge também o revela"**

**FALSO.** Embora EC subsuma NC como critério estrutural, isso **não garante** que T_edge detete os mesmos defeitos que T_node. Um defeito pode exigir uma sequência específica de nós/arestas com valores de input concretos. T_node pode exercitar essa sequência acidentalmente, enquanto T_edge usa caminhos diferentes para cobrir todas as arestas, nunca atingindo a condição que provoca o defeito. A subsunção de critérios refere-se à cobertura estrutural, não à capacidade de deteção de defeitos de conjuntos específicos de testes.

---

### Exercício 2 — Grafo I

```
N={1,2,3,4,5,6,7}  N0={1}  Nf={7}
E={(1,2),(1,7),(2,3),(2,4),(3,2),(4,5),(4,6),(5,6),(6,1)}

Candidate Test Paths:
  t0 = [1,2,4,5,6,1,7]
  t1 = [1,2,3,2,4,6,1,7]
```

**a) Grafo (arestas de saída por nó):**
```
1 → 2, 7
2 → 3, 4
3 → 2   (back edge — ciclo pequeno)
4 → 5, 6
5 → 6
6 → 1   (back edge — ciclo principal)
7       (nó final, sem saídas)
```

**b) TR para Edge-Pair Coverage (12 requisitos):**

| # | Par (u,v,w) | Nó intermédio |
|---|-------------|---------------|
| 1 | (6,1,2) | 1 |
| 2 | (6,1,7) | 1 |
| 3 | (1,2,3) | 2 |
| 4 | (1,2,4) | 2 |
| 5 | (3,2,3) | 2 |
| 6 | (3,2,4) | 2 |
| 7 | (2,3,2) | 3 |
| 8 | (2,4,5) | 4 |
| 9 | (2,4,6) | 4 |
| 10 | (4,5,6) | 5 |
| 11 | (4,6,1) | 6 |
| 12 | (5,6,1) | 6 |

**c) {t0, t1} satisfaz EPC?**

| TR | t0 | t1 |
|----|----|----|
| (6,1,2) | ✗ | ✗ |
| (6,1,7) | ✓ | ✓ |
| (1,2,3) | ✗ | ✓ |
| (1,2,4) | ✓ | ✗ |
| **(3,2,3)** | ✗ | ✗ |
| (3,2,4) | ✗ | ✓ |
| (2,3,2) | ✗ | ✓ |
| (2,4,5) | ✓ | ✗ |
| (2,4,6) | ✗ | ✓ |
| (4,5,6) | ✓ | ✗ |
| (4,6,1) | ✗ | ✓ |
| (5,6,1) | ✓ | ✗ |

**NÃO satisfaz EPC.** Faltam: **(6,1,2)** e **(3,2,3)**.

**d) Simple path [3,2,4,5,6] vs test path [1,2,3,2,4,6,1,2,4,5,6,1,7]:**

- **Tour direto:** NÃO. O caminho de teste não contém [3,2,4,5,6] como subsequência contígua (após 3,2,4 surge 6 em vez de 5).
- **Tour com sidetrip:** SIM. A sequência é coberta com um desvio no nó 4:
  - 3(idx2) → 2(3) → 4(4) → **[sidetrip: 4→6→1→2→4]** → 5(9) → 6(10)
  - O sidetrip é **[4,6,1,2,4]**: o caminho sai do nó 4, percorre 6→1→2 e regressa a 4 antes de avançar para 5.

**e) Prime paths:**

| # | Prime Path | Tipo |
|---|-----------|------|
| PP1 | [2,3,2] | ciclo |
| PP2 | [1,2,4,6,1] | ciclo |
| PP3 | [1,2,4,5,6,1] | ciclo |
| PP4 | [2,4,6,1,2] | ciclo |
| PP5 | [2,4,5,6,1,2] | ciclo |
| PP6 | [3,2,4,6,1,7] | acíclico |
| PP7 | [3,2,4,5,6,1,7] | acíclico |
| PP8 | [4,6,1,2,3] | acíclico |
| PP9 | [4,5,6,1,2,3] | acíclico |

**f) Caminhos que satisfazem EC mas não PPC:**

{t0, t1} satisfaz edge coverage (cobrem as 9 arestas) mas NÃO satisfaz prime path coverage.
Prime paths não percorridos por {t0, t1}: PP4 [2,4,6,1,2], PP5 [2,4,5,6,1,2], PP7 [3,2,4,5,6,1,7], PP8 [4,6,1,2,3], PP9 [4,5,6,1,2,3].
