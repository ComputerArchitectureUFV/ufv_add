package add.dataflow.sync;

/**
 * In8 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an input queue with 8 output.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class In8 extends GenericIn {

    /**
     * Object Constructor.
     */
    public In8() {
        super(8);
        setCompName("IN_8");
    }
}
