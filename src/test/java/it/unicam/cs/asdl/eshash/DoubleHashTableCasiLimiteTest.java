package it.unicam.cs.asdl.eshash;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test dei casi limite dell’hashing doppio: oggetti personalizzati,
 * hash negativi, sequenze di scansione e gestione delle celle cancellate.
 */
public class DoubleHashTableCasiLimiteTest {

    private static class Student {
        private final int id;
        private final String name;

        Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Student))
                return false;

            Student other = (Student) obj;
            return this.id == other.id;
        }
    }

    @Test
    public void testCustomEqualsAndHashCode() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        Student s1 = new Student(10, "Mario");
        Student s2 = new Student(10, "Luigi");

        assertTrue(table.insert(s1));
        assertFalse(table.insert(s2));
        assertTrue(table.contains(new Student(10, "Altro")));
        assertEquals(1, table.size());
    }

    @Test
    public void testDeleteWithEquivalentObject() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        Student s1 = new Student(20, "Anna");
        Student s2 = new Student(20, "Anna clone");

        assertTrue(table.insert(s1));
        assertTrue(table.delete(s2));
        assertFalse(table.contains(s1));
        assertEquals(0, table.size());
    }

    @Test
    public void testNegativeHashCode() {
        MyDoubleHashTable table = new MyDoubleHashTable(31);

        assertTrue(table.insert(new Student(-1, "Negativo")));
        assertTrue(table.insert(new Student(-32, "Altro negativo")));

        assertTrue(table.contains(new Student(-1, "X")));
        assertTrue(table.contains(new Student(-32, "Y")));
    }

    @Test
    public void testFillTableCompletely() {
        MyDoubleHashTable table = new MyDoubleHashTable(101);

        for (int i = 0; i < 101; i++)
            assertTrue(table.insert(new Student(i, "S" + i)));

        assertEquals(101, table.size());

        for (int i = 0; i < 101; i++)
            assertTrue(table.contains(new Student(i, "Other" + i)));
    }

    @Test
    public void testFullTableException() {
        final MyDoubleHashTable table = new MyDoubleHashTable(31);

        for (int i = 0; i < 31; i++)
            table.insert(new Student(i, "S" + i));

        assertThrows(FullHashTableException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                table.insert(new Student(1000, "Too much"));
            }
        });
    }

    @Test
    public void testDeleteAllAndReuseAllPositions() {
        MyDoubleHashTable table = new MyDoubleHashTable(101);

        for (int i = 0; i < 101; i++)
            table.insert(new Student(i, "S" + i));

        for (int i = 0; i < 101; i++)
            assertTrue(table.delete(new Student(i, "X" + i)));

        assertEquals(0, table.size());

        for (int i = 1000; i < 1101; i++)
            assertTrue(table.insert(new Student(i, "T" + i)));

        assertEquals(101, table.size());

        for (int i = 1000; i < 1101; i++)
            assertTrue(table.contains(new Student(i, "Y" + i)));
    }

    @Test
    public void stressTestLargePrimeSize() {
        MyDoubleHashTable table = new MyDoubleHashTable(1009);

        for (int i = 0; i < 800; i++)
            assertTrue(table.insert(new Student(i * 31, "S" + i)));

        for (int i = 0; i < 800; i += 2)
            assertTrue(table.delete(new Student(i * 31, "D" + i)));

        for (int i = 1; i < 800; i += 2)
            assertTrue(table.contains(new Student(i * 31, "C" + i)));

        for (int i = 0; i < 800; i += 2)
            assertFalse(table.contains(new Student(i * 31, "C" + i)));

        for (int i = 100000; i < 100400; i++)
            assertTrue(table.insert(new Student(i, "N" + i)));

        for (int i = 100000; i < 100400; i++)
            assertTrue(table.contains(new Student(i, "M" + i)));
    }

    @Test
    public void testCapacityOne() {
        final MyDoubleHashTable table = new MyDoubleHashTable(1);

        assertTrue(table.insert("only"));
        assertTrue(table.contains("only"));
        assertFalse(table.insert(new String("only")));

        assertThrows(FullHashTableException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                table.insert("another");
            }
        });

        assertTrue(table.delete("only"));
        assertEquals(0, table.size());
        assertTrue(table.insert("new"));
        assertTrue(table.contains("new"));
    }
}