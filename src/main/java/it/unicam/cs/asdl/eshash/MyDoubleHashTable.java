package it.unicam.cs.asdl.eshash;

/**
 * Tabella hash con indirizzamento aperto e doppio hashing, a capacità fissa. La capacità della
 * tabella è fissata al momento della creazione e non può essere modificata
 * successivamente.
 * <p>
 * La tabella memorizza oggetti non nulli e non accetta duplicati secondo
 * il metodo {@link Object#equals(Object)}. La posizione iniziale e il passo
 * della sequenza di scansione sono calcolati a partire da
 * {@link Object#hashCode()}.
 * </p>
 * <p>
 * Diversamente dalla tabella con chaining, qui NON si effettua resize.
 * Il passo di scansione deve essere coprimo con la capacità, anche se
 * la dimensione passata al costruttore è composta.
 * La cancellazione è gestita mediante una sentinella interna che rappresenta
 * una cella cancellata.
 * </p>
 */
public class MyDoubleHashTable {

    // sentinella
    private static final Object DELETED = new Object();
    // tabella hash
    private final Object[] table;
    // funzione hash primaria
    private final PrimaryHashFunction h1;
    // funzione hash usata per calcolare il passo nell'indirizzamento aperto
    private final SecondaryHashFunction h2;
    // numero di elementi correnti nella tabella
    private int size;

    /**
     * Crea una nuova tabella hash con capacità fissa. Per una dimensione
     * composta il passo viene corretto affinché sia coprimo con la capacità.
     *
     * @param initialSize la dimensione iniziale della tabella
     * @throws IllegalArgumentException se {@code initialSize <= 0}
     */
    public MyDoubleHashTable(int initialSize) {
        if (initialSize <= 0)
            throw new IllegalArgumentException("Initial size must be positive");

        this.table = new Object[initialSize];
        this.h1 = new PrimaryHashFunction();
        this.h2 = new SecondaryHashFunction();
        this.size = 0;
    }

    /**
     * Restituisce il numero di elementi attualmente presenti nella tabella.
     *
     * @return il numero di elementi presenti
     */
    public int size() {
        return size;
    }

    /**
     * Restituisce la capacità della tabella.
     *
     * @return la lunghezza dell'array interno
     */
    public int capacity() {
        return table.length;
    }

    /**
     * Verifica se un elemento è presente nella tabella.
     *
     * @param element l'elemento da cercare
     * @return {@code true} se l'elemento è presente, {@code false} altrimenti
     * @throws NullPointerException se {@code element} è {@code null}
     */
    public boolean contains(Object element) {
        // TODO O1: scansione per cercare una chiave.
        // Una cella DELETED non interrompe la ricerca; null sì.
        throw new UnsupportedOperationException("TODO: contains");
    }

    /**
     * Inserisce un elemento nella tabella.
     * <p>
     * Se l'elemento è già presente secondo {@link Object#equals(Object)},
     * l'inserimento non viene effettuato.
     * </p>
     *
     * @param element l'elemento da inserire
     * @return {@code true} se l'elemento è stato inserito, {@code false}
     *         se era già presente
     * @throws NullPointerException se {@code element} è {@code null}
     * @throws FullHashTableException se la tabella è piena
     */
    public boolean insert(Object element) {
        // TODO O2: inserimento con scansione completa, controllo duplicati
        // e riuso della PRIMA cella DELETED incontrata.
        // Non inserire subito in DELETED: il duplicato potrebbe trovarsi dopo.
        throw new UnsupportedOperationException("TODO: insert");
    }

    /**
     * Cancella un elemento dalla tabella, se presente.
     *
     * @param element l'elemento da cancellare
     * @return {@code true} se l'elemento è stato cancellato, {@code false}
     *         se non era presente
     * @throws NullPointerException se {@code element} è {@code null}
     */
    public boolean delete(Object element) {
        // TODO O3: cancellazione logica mediante la sentinella DELETED.
        // Non usare null: interromperesti la ricerca di altre chiavi.
        throw new UnsupportedOperationException("TODO: delete");
    }

    // TODO è possibile aggiungere metodi privati per fini di implementazione

    // ATTENZIONE: Non modificare il codice qui sotto

    /**
     * Calcola il passo della scansione e garantisce che sia coprimo con la
     * capacità {@code m}. La sequenza degli indici è
     * {@code (h1(k) + i * step) mod m}, con {@code 0 <= i < m}.
     *
     * <p>Il passo candidato è {@code 1 + floorMod(k, m-1)} per {@code m > 1}.
     * Non sempre però è coprimo con una capacità composta. Per esempio,
     * con {@code m = 10} e {@code k = 1}:</p>
     * <ul>
     *   <li>{@code h1(1) = 1} e passo candidato {@code 2};</li>
     *   <li>la scansione {@code 1, 3, 5, 7, 9, 1, ...} raggiunge soltanto
     *       cinque posizioni, perché {@code MCD(2, 10) = 2};</li>
     *   <li>il primo passo successivo coprimo è {@code 3} e la scansione
     *       diventa {@code 1, 4, 7, 0, 3, 6, 9, 2, 5, 8},
     *       raggiungendo tutte le dieci celle.</li>
     * </ul>
     * <p>In generale la scansione visita {@code m / MCD(step,m)} celle
     * distinte. Il metodo cerca quindi un passo coprimo con {@code m}.
     * Per {@code m == 1} restituisce {@code 1}.</p>
     *
     * <p><strong>Metodo già implementato:</strong> non fa parte dei TODO.
     * È richiamato dalle operazioni di ricerca, inserimento e cancellazione.</p>
     *
     * @param key codice hash dell'elemento (può essere negativo)
     * @param m capacità della tabella, strettamente positiva
     * @return passo positivo che permette una scansione completa
     */
    private int validStep(int key, int m) {
        if (m == 1) return 1;
        int step = h2.hash(key, m);
        while (gcd(step, m) != 1) step = step % (m - 1) + 1;
        return step;
    }

    private static int gcd(int a, int b) {
        while (b != 0) { int r = a % b; a = b; b = r; }
        return a;
    }

    /**
     * Funzione hash primaria classica:
     *
     * <pre>
     * h1(k) = k mod m
     * </pre>
     */
    private static class PrimaryHashFunction {

        /**
         * Calcola la posizione iniziale per una chiave.
         *
         * @param key la chiave
         * @param m la dimensione della tabella
         * @return un valore tra {@code 0} e {@code m - 1}
         */
        public int hash(int key, int m) {
            return Math.floorMod(key, m);
        }
    }

    /**
     * Funzione hash secondaria classica:
     *
     * <pre>
     * h2(k) = 1 + (k mod (m - 1))
     * </pre>
     */
    private static class SecondaryHashFunction {

        /**
         * Calcola il passo della sequenza di scansione.
         *
         * @param key la chiave
         * @param m la dimensione della tabella
         * @return un valore tra {@code 1} e {@code m - 1}
         */
        public int hash(int key, int m) {
            if (m == 1)
                return 1;
            return 1 + Math.floorMod(key, m - 1);
        }
    }
}