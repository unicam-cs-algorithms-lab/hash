package it.unicam.cs.asdl.eshash;

/**
 * Implementa una funzione hash primaria che usa il metodo della divisione.
 * 
 * @author Luca Tesei
 *
 */
public class DivisionPrimaryHashFunction implements PrimaryHashFunction {

    /** Crea una funzione hash senza stato. */
    public DivisionPrimaryHashFunction() { }

    /*
     * (non-Javadoc)
     * 
     * @see it.unicam.cs.asdl.eshash.PrimaryHashFunction#hash(int, int)
     */
    @Override
    public int hash(int key, int m) {
        return Math.floorMod(key, m);
    }

}
