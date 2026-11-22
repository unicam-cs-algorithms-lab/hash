package it.unicam.cs.asdl.eshash;

/**
 * Eccezione lanciata quando si tenta di inserire un elemento in una tabella
 * hash piena.
 */
public class FullHashTableException extends RuntimeException {

    /**
     * Crea una nuova eccezione con messaggio standard.
     */
    public FullHashTableException() {
        super("Hash table is full");
    }
}
