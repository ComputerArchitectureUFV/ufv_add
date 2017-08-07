package add.dataflow.sync;

/**
 * In2 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an input queue with 2 output.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class In2 extends GenericIn {

    /**
     * Object Constructor.
     */
    public In2() {
        super(2);
        setCompName("IN_2");
    }
}
