package org.ficha01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.ficha01.LibraryBook.Status.*;

/**
 * Lab 3 — Exercício 2: State Transition Testing para LibraryBook.
 *
 * Diagrama de estados:
 *
 *   [initial] --> IN_STOCK
 *   IN_STOCK        --makeAvailable--> AVAILABLE_ON_LOAN
 *   AVAILABLE_ON_LOAN --lend---------> CHECKED_OUT
 *   CHECKED_OUT     --admonish-------> CHECKED_OUT   (self-loop)
 *   CHECKED_OUT     --checkIn--------> IN_STOCK
 *   CHECKED_OUT     --writeOff-------> LOST
 *   LOST            --> [terminal]
 *
 * Transições (0-switches):
 *   T1: IN_STOCK          --makeAvailable--> AVAILABLE_ON_LOAN
 *   T2: AVAILABLE_ON_LOAN --lend----------> CHECKED_OUT
 *   T3: CHECKED_OUT       --admonish------> CHECKED_OUT
 *   T4: CHECKED_OUT       --checkIn-------> IN_STOCK
 *   T5: CHECKED_OUT       --writeOff------> LOST
 *
 * ==========================================================================
 * Critérios de cobertura (por ordem crescente de exigência):
 *
 *  a) STATE COVERAGE:
 *     Todos os estados devem ser visitados pelo menos uma vez.
 *     É o critério mais fraco — basta que cada estado apareça num teste.
 *
 *  b) 1-SWITCH COVERAGE (Transition Coverage):
 *     Todos os pares de transições consecutivas (A→B→C) devem ser
 *     exercidos pelo menos uma vez. Subsume o state coverage.
 *     Metodologia: listar os 0-switches, expandir cada um com a(s)
 *     transição(ões) seguinte(s) possível(is).
 *
 *  c) INVALID TRANSITIOgN COVERAGE:
 *     Cada transição inválida (evento num estado onde não é permitido)
 *     deve ser tentada exatamente uma vez, em testes individuais.
 *     Espera-se IllegalStateException.
 * ==========================================================================
 */
class LibraryBookTest {

    private LibraryBook book;

    @BeforeEach
    void setUp() {
        book = new LibraryBook();   // começa sempre em IN_STOCK
    }

    // ======================================================================
    // a) STATE COVERAGE
    //
    // Objetivo: visitar todos os estados pelo menos uma vez.
    // Estados a cobrir: IN_STOCK, AVAILABLE_ON_LOAN, CHECKED_OUT, LOST
    //
    // Um único teste cobre todos os estados seguindo o caminho mais direto:
    //   IN_STOCK → AVAILABLE_ON_LOAN → CHECKED_OUT → LOST
    // ======================================================================

    /**
     * SC-1: percurso direto que visita os 4 estados.
     * IN_STOCK (inicial) → makeAvailable → AVAILABLE_ON_LOAN
     *                    → lend          → CHECKED_OUT
     *                    → writeOff      → LOST (terminal)
     */
    @Test
    void sc1_allStatesVisited() {
        assertEquals(IN_STOCK, book.getStatus());

        book.makeAvailable();
        assertEquals(AVAILABLE_ON_LOAN, book.getStatus());

        book.lend();
        assertEquals(CHECKED_OUT, book.getStatus());

        book.writeOff();
        assertEquals(LOST, book.getStatus());
    }

    // ======================================================================
    // b) 1-SWITCH COVERAGE
    //
    // Objetivo: cobrir todos os pares consecutivos de transições.
    // Um N-switch é uma sequência de N+1 transições encadeadas.
    // O 1-switch coverage subsume o transition coverage (0-switch).
    //
    // 0-switches (transições individuais):
    //   T1: IN_STOCK          → AVAILABLE_ON_LOAN  (makeAvailable)
    //   T2: AVAILABLE_ON_LOAN → CHECKED_OUT        (lend)
    //   T3: CHECKED_OUT       → CHECKED_OUT        (admonish)
    //   T4: CHECKED_OUT       → IN_STOCK           (checkIn)
    //   T5: CHECKED_OUT       → LOST               (writeOff)
    //
    // 1-switches derivados (expandindo cada 0-switch com o seguinte possível):
    //   De T1 (→ AVAILABLE_ON_LOAN): só T2 é possível
    //     SW1:  IN_STOCK → AVAIL_ON_LOAN → CHECKED_OUT
    //   De T2 (→ CHECKED_OUT): T3, T4 ou T5 são possíveis
    //     SW2:  AVAIL_ON_LOAN → CHECKED_OUT → CHECKED_OUT   (admonish)
    //     SW3:  AVAIL_ON_LOAN → CHECKED_OUT → IN_STOCK      (checkIn)
    //     SW4:  AVAIL_ON_LOAN → CHECKED_OUT → LOST          (writeOff)
    //   De T3 (→ CHECKED_OUT self-loop): T3, T4 ou T5 são possíveis
    //     SW5:  CHECKED_OUT → CHECKED_OUT → CHECKED_OUT     (admonish→admonish)
    //     SW6:  CHECKED_OUT → CHECKED_OUT → IN_STOCK        (admonish→checkIn)
    //     SW7:  CHECKED_OUT → CHECKED_OUT → LOST            (admonish→writeOff)
    //   De T4 (→ IN_STOCK): só T1 é possível
    //     SW8:  CHECKED_OUT → IN_STOCK → AVAILABLE_ON_LOAN  (checkIn→makeAvailable)
    //   De T5 (→ LOST): estado terminal, sem transições de saída
    //     (nenhum 1-switch possível)
    //
    // Total: 8 1-switches a cobrir.
    //
    // Test suite mínima (2 testes cobrem todos os 8):
    //   TC1 cobre: SW1, SW2, SW3, SW5, SW7, SW8
    //   TC2 cobre: SW1, SW2, SW4, SW6, SW8
    //   Juntos cobrem SW1..SW8 ✓
    // ======================================================================

    /**
     * TC1 — 1-switch coverage (parte 1)
     * Caminho: IS → AOL → CO → IS → AOL → CO → CO → CO → LOST
     * Eventos: makeAvailable → lend → checkIn → makeAvailable → lend → admonish → admonish → writeOff
     *
     * 1-switches cobertos:
     *   SW1 (IS→AOL→CO), SW3 (AOL→CO→IS), SW8 (CO→IS→AOL),
     *   SW2 (AOL→CO→CO), SW5 (CO→CO→CO), SW7 (CO→CO→LOST)
     */
    @Test
    void tc1_oneSwitchCoverage_part1() {
        // IS → AOL → CO  [SW1]
        book.makeAvailable();
        book.lend();
        assertEquals(CHECKED_OUT, book.getStatus());

        // CO → IS  [parte de SW3]
        book.checkIn();
        assertEquals(IN_STOCK, book.getStatus());

        // IS → AOL → CO  [SW8 completo: CO→IS→AOL; SW1 repetido]
        book.makeAvailable();
        book.lend();
        assertEquals(CHECKED_OUT, book.getStatus());

        // CO → CO → CO → LOST  [SW2, SW5, SW7]
        book.admonish();
        assertEquals(CHECKED_OUT, book.getStatus());

        book.admonish();
        assertEquals(CHECKED_OUT, book.getStatus());

        book.writeOff();
        assertEquals(LOST, book.getStatus());
    }

    /**
     * TC2 — 1-switch coverage (parte 2)
     * Caminho: IS → AOL → CO → CO → IS → AOL → CO → LOST
     * Eventos: makeAvailable → lend → admonish → checkIn → makeAvailable → lend → writeOff
     *
     * 1-switches cobertos:
     *   SW1 (IS→AOL→CO), SW2 (AOL→CO→CO), SW6 (CO→CO→IS),
     *   SW8 (CO→IS→AOL), SW4 (AOL→CO→LOST)
     */
    @Test
    void tc2_oneSwitchCoverage_part2() {
        // IS → AOL → CO  [SW1]
        book.makeAvailable();
        book.lend();
        assertEquals(CHECKED_OUT, book.getStatus());

        // CO → CO → IS  [SW2 + SW6]
        book.admonish();
        assertEquals(CHECKED_OUT, book.getStatus());

        book.checkIn();
        assertEquals(IN_STOCK, book.getStatus());

        // CO → IS → AOL → CO  [SW8, SW1 repetido]
        book.makeAvailable();
        book.lend();
        assertEquals(CHECKED_OUT, book.getStatus());

        // AOL → CO → LOST  [SW4]
        book.writeOff();
        assertEquals(LOST, book.getStatus());
    }

    // ======================================================================
    // c) INVALID TRANSITION COVERAGE
    //
    // Objetivo: tentar cada transição inválida exatamente uma vez.
    // Uma transição inválida é qualquer evento disparado num estado em que
    // esse evento não tem transição definida. Espera-se IllegalStateException.
    //
    // Tabela de transições completa (✓ = válida, ✗ = inválida):
    //
    //  Estado              | makeAvailable | lend | admonish | checkIn | writeOff
    //  IN_STOCK            |      ✓        |  ✗   |    ✗     |    ✗    |    ✗
    //  AVAILABLE_ON_LOAN   |      ✗        |  ✓   |    ✗     |    ✗    |    ✗
    //  CHECKED_OUT         |      ✗        |  ✗   |    ✓     |    ✓    |    ✓
    //  LOST                |      ✗        |  ✗   |    ✗     |    ✗    |    ✗
    //
    // Total de transições inválidas: 4 + 4 + 2 + 5 = 15 testes
    // ======================================================================

    // --- Transições inválidas a partir de IN_STOCK (4) ---

    @Test
    void inv_inStock_lend() {
        // IN_STOCK: lend só é válido em AVAILABLE_ON_LOAN
        assertThrows(IllegalStateException.class, () -> book.lend());
    }

    @Test
    void inv_inStock_admonish() {
        // IN_STOCK: admonish só é válido em CHECKED_OUT
        assertThrows(IllegalStateException.class, () -> book.admonish());
    }

    @Test
    void inv_inStock_checkIn() {
        // IN_STOCK: checkIn só é válido em CHECKED_OUT
        assertThrows(IllegalStateException.class, () -> book.checkIn());
    }

    @Test
    void inv_inStock_writeOff() {
        // IN_STOCK: writeOff só é válido em CHECKED_OUT
        assertThrows(IllegalStateException.class, () -> book.writeOff());
    }

    // --- Transições inválidas a partir de AVAILABLE_ON_LOAN (4) ---

    @Test
    void inv_availableOnLoan_makeAvailable() {
        // Navegar até AVAILABLE_ON_LOAN
        book.makeAvailable();
        assertEquals(AVAILABLE_ON_LOAN, book.getStatus());
        // makeAvailable só é válido em IN_STOCK
        assertThrows(IllegalStateException.class, () -> book.makeAvailable());
    }

    @Test
    void inv_availableOnLoan_admonish() {
        book.makeAvailable();
        // admonish só é válido em CHECKED_OUT
        assertThrows(IllegalStateException.class, () -> book.admonish());
    }

    @Test
    void inv_availableOnLoan_checkIn() {
        book.makeAvailable();
        // checkIn só é válido em CHECKED_OUT
        assertThrows(IllegalStateException.class, () -> book.checkIn());
    }

    @Test
    void inv_availableOnLoan_writeOff() {
        book.makeAvailable();
        // writeOff só é válido em CHECKED_OUT
        assertThrows(IllegalStateException.class, () -> book.writeOff());
    }

    // --- Transições inválidas a partir de CHECKED_OUT (2) ---

    @Test
    void inv_checkedOut_makeAvailable() {
        // Navegar até CHECKED_OUT
        book.makeAvailable();
        book.lend();
        assertEquals(CHECKED_OUT, book.getStatus());
        // makeAvailable só é válido em IN_STOCK
        assertThrows(IllegalStateException.class, () -> book.makeAvailable());
    }

    @Test
    void inv_checkedOut_lend() {
        book.makeAvailable();
        book.lend();
        // lend só é válido em AVAILABLE_ON_LOAN
        assertThrows(IllegalStateException.class, () -> book.lend());
    }

    // --- Transições inválidas a partir de LOST (5) ---

    @Test
    void inv_lost_makeAvailable() {
        // Navegar até LOST
        book.makeAvailable();
        book.lend();
        book.writeOff();
        assertEquals(LOST, book.getStatus());
        // LOST é terminal — nenhum evento é válido
        assertThrows(IllegalStateException.class, () -> book.makeAvailable());
    }

    @Test
    void inv_lost_lend() {
        book.makeAvailable();
        book.lend();
        book.writeOff();
        assertThrows(IllegalStateException.class, () -> book.lend());
    }

    @Test
    void inv_lost_admonish() {
        book.makeAvailable();
        book.lend();
        book.writeOff();
        assertThrows(IllegalStateException.class, () -> book.admonish());
    }

    @Test
    void inv_lost_checkIn() {
        book.makeAvailable();
        book.lend();
        book.writeOff();
        assertThrows(IllegalStateException.class, () -> book.checkIn());
    }

    @Test
    void inv_lost_writeOff() {
        book.makeAvailable();
        book.lend();
        book.writeOff();
        assertThrows(IllegalStateException.class, () -> book.writeOff());
    }
}
