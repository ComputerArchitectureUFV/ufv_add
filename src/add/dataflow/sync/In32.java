package add.dataflow.sync;

/**
 * In32 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an input queue with 32 output.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class In32 extends GenericIn {

    /**
     * Object Constructor.
     */
    public In32() {
        super(32);
        setCompName("IN_32");
    }
}
