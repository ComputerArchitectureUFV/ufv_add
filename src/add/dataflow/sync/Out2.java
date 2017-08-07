package add.dataflow.sync;

/**
 * Out2 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an output queue with 2 input..<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Out2 extends GenericOut {

    /**
     * Object Constructor.
     */
    public Out2() {
        super(2);
        setCompName("OUT_2");
    }
}
