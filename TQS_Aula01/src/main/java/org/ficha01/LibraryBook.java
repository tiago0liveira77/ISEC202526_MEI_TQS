package org.ficha01;

/**
 * Representa um livro de biblioteca com um ciclo de vida definido por um
 * diagrama de estados (Lab 3 - Exercício 2).
 *
 * Estados:
 *   IN_STOCK         - livro disponível em stock (estado inicial)
 *   AVAILABLE_ON_LOAN - livro disponível para empréstimo
 *   CHECKED_OUT      - livro emprestado
 *   LOST             - livro perdido (estado terminal)
 *
 * Transições válidas:
 *   IN_STOCK        --makeAvailable--> AVAILABLE_ON_LOAN
 *   AVAILABLE_ON_LOAN --lend---------> CHECKED_OUT
 *   CHECKED_OUT     --admonish-------> CHECKED_OUT  (self-loop: advertência ao utilizador)
 *   CHECKED_OUT     --checkIn--------> IN_STOCK
 *   CHECKED_OUT     --writeOff-------> LOST
 *
 * Qualquer outra transição lança IllegalStateException.
 */
public class LibraryBook {

    public enum Status {
        IN_STOCK, AVAILABLE_ON_LOAN, CHECKED_OUT, LOST
    }

    private Status status;

    public LibraryBook() {
        this.status = Status.IN_STOCK;
    }

    public Status getStatus() {
        return status;
    }

    /** IN_STOCK → AVAILABLE_ON_LOAN */
    public void makeAvailable() {
        if (status != Status.IN_STOCK)
            throw new IllegalStateException("makeAvailable inválido no estado: " + status);
        status = Status.AVAILABLE_ON_LOAN;
    }

    /** AVAILABLE_ON_LOAN → CHECKED_OUT */
    public void lend() {
        if (status != Status.AVAILABLE_ON_LOAN)
            throw new IllegalStateException("lend inválido no estado: " + status);
        status = Status.CHECKED_OUT;
    }

    /** CHECKED_OUT → CHECKED_OUT (self-loop: advertência ao utilizador) */
    public void admonish() {
        if (status != Status.CHECKED_OUT)
            throw new IllegalStateException("admonish inválido no estado: " + status);
        // estado mantém-se
    }

    /** CHECKED_OUT → IN_STOCK */
    public void checkIn() {
        if (status != Status.CHECKED_OUT)
            throw new IllegalStateException("checkIn inválido no estado: " + status);
        status = Status.IN_STOCK;
    }

    /** CHECKED_OUT → LOST */
    public void writeOff() {
        if (status != Status.CHECKED_OUT)
            throw new IllegalStateException("writeOff inválido no estado: " + status);
        status = Status.LOST;
    }
}
