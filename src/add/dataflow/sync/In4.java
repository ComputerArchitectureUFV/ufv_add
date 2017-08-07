package add.dataflow.sync;

/**
 * In4 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an input queue with 4 output.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class In4 extends GenericIn {

    /**
     * Object Constructor.
     */
    public In4() {
        super(4);
        setCompName("IN_4");
    }
}
