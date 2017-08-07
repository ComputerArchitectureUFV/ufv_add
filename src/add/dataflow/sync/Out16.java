package add.dataflow.sync;

/**
 * Out16 component for the ADD Accelerator Design and Deploy.<br>
 * O implements an input queue with 16 output.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Out16 extends GenericOut {

    /**
     * Object Constructor.
     */
    public Out16() {
        super(16);
        setCompName("OUT_16");
    }
}
