package it.unicam.cs.asdl.eshash;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Insieme ({@link java.util.Set}) implementato mediante hashing con
 * <em>liste di collisione</em> (separate chaining).
 * <p>Ogni cella dell'array identifica un bucket contenente la testa di una
 * lista di nodi. {@code hashCode()} determina il bucket tramite la funzione
 * {@link PrimaryHashFunction}; {@code equals()} distingue gli elementi in
 * quel bucket. Gli elementi {@code null} non sono ammessi.</p>
 * <p>La capacità parte da 16 e raddoppia quando il numero di elementi supera
 * il 75% della capacità. Il ridimensionamento richiede di ricalcolare
 * l'indice di <strong>tutti</strong> gli elementi. L'iteratore è fail-fast:
 * se cambia strutturalmente l'insieme, segnala l'alterazione.</p>
 * <p><strong>Obiettivo didattico:</strong> contratti Java Set, collisioni,
 * chaining, resize, complessità media attesa e iterazione su array di liste.</p>
 * @param <E> tipo degli elementi conservati nell’insieme
 */
public class CollisionListResizableHashTable<E> implements Set<E> {

    /*
     * La capacità iniziale. E' una potenza di due e quindi la capacità sarà
     * sempre una potenza di due, in quanto ogni resize raddoppia la tabella.
     */
    private static final int INITIAL_CAPACITY = 16;

    /*
     * Fattore di bilanciamento di default. Tipico valore.
     */
    private static final double LOAD_FACTOR = 0.75;

    /*
     * Numero di elementi effettivamente presenti nella hash table in questo
     * momento. ATTENZIONE: questo valore è diverso dalla capacity, che è la
     * lunghezza attuale dell'array di Object che rappresenta la tabella.
     */
    private int size;

    /*
     * L'idea è che l'elemento in posizione i della tabella hash è un bucket che
     * contiene null oppure il puntatore al primo nodo di una lista concatenata
     * di elementi. La classe interna Node<E> rappresenta ciascun nodo
     * della catena e contiene un riferimento al nodo successivo.
     * 
     * ATTENZIONE: la tabella hash vera e propria può essere solo un generico
     * array di Object e non di Node<E> per una impossibilità del compilatore di
     * accettare di creare array a runtime con un tipo generics. Ciò infatti
     * comporterebbe dei problemi nel sistema di check dei tipi Java che, a
     * run-time, potrebbe eseguire degli assegnamenti in violazione del tipo
     * effettivo della variabile. Quindi usiamo un array di Object che
     * riempiremo sempre con null o con puntatori a oggetti di tipo Node<E>.
     * 
     * Per inserire un elemento nella tabella possiamo usare il polimorfismo di
     * Object:
     * 
     * this.table[i] = new Node<E>(item, next);
     * 
     * ma quando dobbiamo prendere un elemento dalla tabella saremo costretti a
     * fare un cast esplicito:
     * 
     * Node<E> myNode = (Node<E>) this.table[i];
     * 
     * Ci sarà dato un warning di cast non controllato, ma possiamo eliminarlo
     * con l’annotazione @SuppressWarnings("unchecked") sul punto in cui
     * il cast è necessario e sappiamo che il bucket contiene un Node<E>.
     */
    private Object[] table;

    /*
     * Funzion di hash primaria usata da questa hash table. Va inizializzata nel
     * costruttore all'atto di creazione dell'oggetto.
     */
    private final PrimaryHashFunction phf;

    /*
     * Contatore del numero di modifiche. Serve per rendere l'iterator
     * fail-fast.
     */
    private int modCount;

    // I due metodi seguenti sono di comodo per gestire la capacity e la soglia
    // oltre la quale bisogna fare il resize.

    /* Numero di elementi della tabella corrente */
    private int getCurrentCapacity() {
        return this.table.length;
    };

    /*
     * Valore corrente soglia oltre la quale si deve fare la resize,
     * getCurrentCapacity * LOAD_FACTOR
     */
    private int getCurrentThreshold() {
        return (int) (getCurrentCapacity() * LOAD_FACTOR);
    }

    /**
     * Costruisce un insieme vuoto con capacità iniziale 16.
     * @param phf strategia per il calcolo del bucket (non null)
     * @throws NullPointerException se phf è null
     */
    public CollisionListResizableHashTable(PrimaryHashFunction phf) {
        this.phf = java.util.Objects.requireNonNull(phf, "Funzione hash primaria obbligatoria");
        this.table = new Object[INITIAL_CAPACITY];
        this.size = 0;
        this.modCount = 0;
    }

    /** Numero di elementi distinti presenti, non numero di bucket. */
    @Override
    public int size() {
        return this.size;
    }

    /** Indica se l’insieme è vuoto: la capacità può essere comunque positiva. */
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /** Ricerca la chiave nel solo bucket determinato dall’hash. All’interno della lista di collisione il confronto deve usare equals(). */
    @Override
    public boolean contains(Object o) {
        // TODO: completare rispettando il contratto Set e gli invarianti.
        /*
         * ATTENZIONE: usare l'hashCode dell'oggetto e la funzione di hash
         * primaria passata all'atto della creazione: il bucket in cui cercare
         * l'oggetto o è la posizione
         * this.phf.hash(o.hashCode(),this.getCurrentCapacity)
         * 
         * In questa posizione, se non vuota, si deve cercare l'elemento o
         * utilizzando il metodo equals() su tutti gli elementi della lista
         * concatenata lì presente
         * 
         */
        return false;
    }

    /** Crea un iteratore sull’insieme, senza ordine prestabilito. Deve visitare ogni elemento una volta e rilevare modifiche strutturali. */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Operazione non supportata");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Operazione non supportata");
    }

    /** Inserisce una nuova chiave se non è già presente. Al superamento della soglia esegue il rehashing, senza duplicare elementi. */
    @Override
    public boolean add(E e) {
        // TODO: completare rispettando il contratto Set e gli invarianti.
        /*
         * ATTENZIONE: usare l'hashCode dell'oggetto e la funzione di hash
         * primaria passata all'atto della creazione: il bucket in cui inserire
         * l'oggetto o è la posizione
         * this.phf.hash(o.hashCode(),this.getCurrentCapacity)
         * 
         * In questa posizione, se non vuota, si deve inserire l'elemento o
         * nella lista concatenata lì presente. Se vuota, si crea la lista
         * concatenata e si inserisce l'elemento, che sarà l'unico.
         * 
         */
        // ATTENZIONE, si inserisca prima il nuovo elemento e poi si controlli
        // se bisogna fare resize(), cioè se this.size >
        // this.getCurrentThreshold()
        return false;
    }

    /*
     * Raddoppia la tabella corrente e riposiziona tutti gli elementi. Da
     * chiamare quando this.size diventa maggiore di getCurrentThreshold()
     */
    private void resize() {
        // TODO: completare rispettando il contratto Set e gli invarianti.
    }

    /** Elimina l’elemento equivalente secondo equals() e collega il predecessore al successore; non ridimensiona verso il basso. */
    @Override
    public boolean remove(Object o) {
        // TODO: completare rispettando il contratto Set e gli invarianti.
        /*
         * ATTENZIONE: usare l'hashCode dell'oggetto e la funzione di hash
         * primaria passata all'atto della creazione: il bucket in cui cercare
         * l'oggetto o è la posizione
         * this.phf.hash(o.hashCode(),this.getCurrentCapacity)
         * 
         * In questa posizione, se non vuota, si deve cercare l'elemento o
         * utilizzando il metodo equals() su tutti gli elementi della lista
         * concatenata lì presente. Se presente, l'elemento deve essere
         * eliminato dalla lista concatenata
         * 
         */
        // ATTENZIONE: la rimozione, in questa implementazione, **non** comporta
        // mai una resize "al ribasso", cioè un dimezzamento della tabella se si
        // scende sotto il fattore di bilanciamento desiderato.
        return false;
    }

    /** Verifica che tutti gli elementi di c siano presenti; la collezione vuota dà true. */
    @Override
    public boolean containsAll(Collection<?> c) {
        // utilizzare un iteratore della collection e chiamare il metodo
        // contains
        if (c == null)
            throw new NullPointerException("ContainsAll di collection null");
        Iterator<?> iter = c.iterator();
        while (iter.hasNext()) {
            Object item = iter.next();
            if (item == null)
                throw new NullPointerException(
                        "ContainsAll di collection che contiene elementi null");
            if (!this.contains(item))
                return false;
        }
        // Tutti gli elementi sono risultati presenti e non nulli
        return true;
    }

    /** Inserisce tutti gli elementi della collezione; restituisce true se almeno uno era nuovo. */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        // utilizzare un iteratore della collection e chiamare il metodo add
        if (c == null)
            throw new NullPointerException("AddAll di collection null");
        boolean changed = false;
        Iterator<? extends E> iter = c.iterator();
        while (iter.hasNext()) {
            // utilizzo il polimorfismo da sottoclasse sul tipo E
            E item = iter.next();
            if (item == null)
                throw new NullPointerException(
                        "AddAll di collection che contiene elementi null");
            // Usare OR non cortocircuitato: ogni elemento va elaborato,
            // anche dopo aver già modificato l'insieme.
            changed = changed | this.add(item);
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Operazione non supportata");
    }

    /** Rimuove tutti gli elementi della collezione c; restituisce true se cambia l’insieme. */
    @Override
    public boolean removeAll(Collection<?> c) {
        // utilizzare un iteratore della collection e chiamare il metodo remove
        if (c == null)
            throw new NullPointerException("RemoveAll di collection null");
        boolean changed = false;
        Iterator<?> iter = c.iterator();
        while (iter.hasNext()) {
            // utilizzo il polimorfismo di Object
            Object item = iter.next();
            if (item == null)
                throw new NullPointerException(
                        "RemoveAll di collection che contiene elementi null");
            // Usare OR non cortocircuitato: ogni elemento va elaborato,
            // anche dopo aver già effettuato una rimozione.
            changed = changed | this.remove(item);
        }
        return changed;
    }

    /** Svuota l’insieme e ripristina la capacità iniziale. Questa è una modifica strutturale, perciò non deve riabilitare iteratori creati in precedenza. */
    @Override
    public void clear() {
        // Ritorno alla situazione iniziale
        this.table = new Object[INITIAL_CAPACITY];
        this.size = 0;
        this.modCount++;
    }

    /**
     * Nodo di una catena. La visibilità protected permette l’ispezione
     * dei nodi nei test, senza renderli parte dell’API pubblica.
     * @param <E> tipo dell’elemento memorizzato
     */
    protected static class Node<E> {
        /** Elemento conservato nel nodo. */
        protected E item;

        /** Riferimento al nodo seguente; null sull’ultimo nodo. */
        protected Node<E> next;

        /*
         * Crea un nodo "singolo" equivalente a una lista con un solo elemento.
         */
        Node(E item, Node<E> next) {
            this.item = item;
            this.next = next;
        }
    }

    /*
     * L’iteratore visita i bucket dell’array e, all’interno di ciascuno,
     * scorre la catena di collisioni. L’ordine non è garantito.
     * Classe che realizza un iteratore per questa hash table. L'ordine in cui
     * vengono restituiti gli oggetti presenti non è rilevante, ma ogni oggetto
     * presente deve essere restituito dall'iteratore una e una sola volta.
     * L'iteratore deve essere fail-fast, cioè deve lanciare una eccezione
     * ConcurrentModificationException se a una chiamata di next() si "accorge"
     * che la tabella è stata cambiata rispetto a quando l'iteratore è stato
     * creato.
     */
    private class Itr implements Iterator<E> {

        // TODO inserire le variabili che servono

        private int numeroModificheAtteso;

        private Itr() {
            // TODO implementare il resto
            this.numeroModificheAtteso = modCount;
        }

        @Override
        public boolean hasNext() {
            // TODO: completare rispettando il contratto Set e gli invarianti.
            return false;
        }

        @Override
        public E next() {
            // TODO: completare rispettando il contratto Set e gli invarianti.
            return null;
        }

    }

    /** Accesso protetto per i test didattici.
     * @return array dei bucket, contenente null o riferimenti a Node
     */
    protected Object[] getTable() {
        return this.table;
    }

    /** Accesso protetto per i test didattici.
     * @return funzione hash primaria scelta nel costruttore
     */
    protected PrimaryHashFunction getPhf() {
        return this.phf;
    }

}
