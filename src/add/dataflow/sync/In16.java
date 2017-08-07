package add.dataflow.sync;

/**
 * In16 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an input queue with 16 output.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class In16 extends GenericIn {

    /**
     * Object Constructor.
     */
    public In16() {
        super(16);
        setCompName("IN_16");
    }
}
