package it.unicam.cs.asdl.eshash;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;

/**
 * Test aggiuntivi orientati agli invarianti e ai casi limite.
 * <p>Rispetto ai test delle singole operazioni, questi verificano sequenze
 * di operazioni: molti errori sulle tabelle hash emergono soltanto quando
 * inserimenti, collisioni, cancellazioni e re-inserimenti si alternano.</p>
 */
class HashInvariantiDidatticiTest {
    /** Collisioni forzate: tutte le chiavi condividono il medesimo hashCode. */
    static final class Chiave {
        final int valore;
        final int hash;
        Chiave(int valore, int hash) { this.valore = valore; this.hash = hash; }
        @Override public int hashCode() { return hash; }
        @Override public boolean equals(Object o) {
            return o instanceof Chiave && ((Chiave) o).valore == valore;
        }
    }

    /** Il contratto equals/hashCode e la gestione delle collisioni sono distinti. */
    @Test void chainingCollisioniEModifiche() {
        CollisionListResizableHashTable<Chiave> t = new CollisionListResizableHashTable<>(new DivisionPrimaryHashFunction());
        for (int i=0;i<40;i++) assertTrue(t.add(new Chiave(i, -1)));
        assertEquals(40,t.size());
        for (int i=0;i<40;i++) assertTrue(t.contains(new Chiave(i,-1)));
        for (int i=0;i<40;i+=2) assertTrue(t.remove(new Chiave(i,-1)));
        assertEquals(20,t.size());
        for (int i=0;i<40;i++) assertEquals(i%2==1,t.contains(new Chiave(i,-1)));
    }

    /** Un resize deve ricollocare anche gli elementi il cui bucket è cambiato. */
    @Test void chainingResizeERimozione() {
        CollisionListResizableHashTable<Integer> t = new CollisionListResizableHashTable<>(new DivisionPrimaryHashFunction());
        for (int i=0;i<150;i++) assertTrue(t.add(i));
        for (int i=0;i<150;i++) assertTrue(t.contains(i));
        assertEquals(150,t.size());
        for (int i=0;i<150;i++) assertTrue(t.remove(i));
        assertTrue(t.isEmpty());
    }

    /** La modifica dell'insieme invalida l'iteratore anche dopo clear(). */
    @Test void chainingIteratorFailFastDopoClear() {
        CollisionListResizableHashTable<Integer> t = new CollisionListResizableHashTable<>(new DivisionPrimaryHashFunction());
        t.add(12);
        Iterator<Integer> it=t.iterator();
        t.clear();
        assertThrows(ConcurrentModificationException.class, it::next);
    }

    /** Due iterazioni senza modifiche producono gli stessi elementi, non necessariamente nello stesso ordine. */
    @Test void chainingIteratorOgniElementoUnaVolta() {
        CollisionListResizableHashTable<Integer> t = new CollisionListResizableHashTable<>(new MultiplicationPrimaryHashFunction());
        Set<Integer> expected=new HashSet<>();
        for (int i=0;i<100;i++) { t.add(i);expected.add(i); }
        Set<Integer> actual=new HashSet<>();
        for (Integer i:t) assertTrue(actual.add(i),"Elemento ripetuto durante l'iterazione");
        assertEquals(expected,actual);
    }

    /** Un passo non coprimo con m farebbe visitare solo una parte della tabella. */
    @Test void apertoCapacitaCompostaEScansioneCompleta() {
        MyDoubleHashTable t=new MyDoubleHashTable(12);
        for (int i=0;i<12;i++) assertTrue(t.insert(new Chiave(i, 5)));
        assertEquals(12,t.size());
        for (int i=0;i<12;i++) assertTrue(t.contains(new Chiave(i,5)));
        assertThrows(FullHashTableException.class,()->t.insert(new Chiave(99,5)));
    }

    /** Prima di riusare una cella DELETED è indispensabile cercare eventuali duplicati oltre di essa. */
    @Test void apertoDuplicatoDopoTombstone() {
        MyDoubleHashTable t=new MyDoubleHashTable(11);
        assertTrue(t.insert(new Chiave(1,3)));
        assertTrue(t.insert(new Chiave(2,3)));
        assertTrue(t.delete(new Chiave(1,3)));
        assertFalse(t.insert(new Chiave(2,3)));
        assertEquals(1,t.size());
        assertTrue(t.insert(new Chiave(3,3)));
        assertEquals(2,t.size());
    }

    /** Un oggetto distinto ma equivalente per equals deve individuare la stessa chiave. */
    @Test void apertoEqualsESentinella() {
        MyDoubleHashTable t=new MyDoubleHashTable(13);
        assertTrue(t.insert(new Chiave(42,-2147483648)));
        assertTrue(t.contains(new Chiave(42,-2147483648)));
        assertTrue(t.delete(new Chiave(42,-2147483648)));
        assertFalse(t.contains(new Chiave(42,-2147483648)));
    }

    /** Il percorso di scansione deve rimanere valido dopo molti riusi. */
    @Test void apertoSequenzaMista() {
        MyDoubleHashTable t=new MyDoubleHashTable(17);
        for(int i=0;i<17;i++) assertTrue(t.insert(new Chiave(i, -123)));
        for(int i=0;i<17;i+=2) assertTrue(t.delete(new Chiave(i,-123)));
        for(int i=100;i<109;i++) assertTrue(t.insert(new Chiave(i,-123)));
        assertEquals(17,t.size());
        for(int i=1;i<17;i+=2) assertTrue(t.contains(new Chiave(i,-123)));
        for(int i=100;i<109;i++) assertTrue(t.contains(new Chiave(i,-123)));
    }
}
