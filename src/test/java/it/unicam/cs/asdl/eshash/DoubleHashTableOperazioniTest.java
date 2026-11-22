package it.unicam.cs.asdl.eshash;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test didattici delle operazioni fondamentali dell’hashing doppio.
 * Verificano uguaglianza logica, collisioni, cancellazioni e riuso degli slot.
 */

public class DoubleHashTableOperazioniTest {

    @Test
    public void testInsertAndContains() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        assertTrue(table.insert("ciao"));
        assertTrue(table.contains("ciao"));
        assertFalse(table.contains("altro"));
        assertEquals(1, table.size());
        assertEquals(31, table.capacity());
    }

    @Test
    public void testDuplicateInsertByEquals() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        String a = new String("test");
        String b = new String("test");

        assertTrue(table.insert(a));
        assertFalse(table.insert(b));
        assertEquals(1, table.size());
    }

    @Test
    public void testDeleteExistingElement() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        table.insert("elemento");

        assertTrue(table.delete("elemento"));
        assertFalse(table.contains("elemento"));
        assertEquals(0, table.size());
    }

    @Test
    public void testDeleteMissingElement() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        assertFalse(table.delete("missing"));
        assertEquals(0, table.size());
    }

    @Test
    public void testCollisionAndSearchAfterDeletion() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        table.insert(Integer.valueOf(1));
        table.insert(Integer.valueOf(32));

        assertTrue(table.delete(Integer.valueOf(1)));
        assertTrue(table.contains(Integer.valueOf(32)));
    }

    @Test
    public void testReuseDeletedCell() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        table.insert(Integer.valueOf(1));
        table.delete(Integer.valueOf(1));

        assertTrue(table.insert(Integer.valueOf(32)));
        assertTrue(table.contains(Integer.valueOf(32)));
        assertEquals(1, table.size());
    }

    @Test
    public void testNullNotAccepted() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        assertThrows(NullPointerException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                table.insert(null);
            }
        });

        assertThrows(NullPointerException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                table.contains(null);
            }
        });

        assertThrows(NullPointerException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                table.delete(null);
            }
        });
    }

    @Test
    public void testInvalidInitialSize() {
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                new MyDoubleHashTable(0);
            }
        });
    }
}