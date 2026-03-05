# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Visão Geral do Projeto

Projeto Java com Maven para a disciplina TQS (Técnicas e Qualidade de Software) — ISEC MEI 2025-2026. Foco em técnicas de teste de software: ECT (Equivalence Class Testing) e BVA (Boundary Value Analysis).

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
