package add.dataflow.sync;

/**
 * Out32 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an output queue with 32 input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Out32 extends GenericOut {

    /**
     * Object Constructor.
     */
    public Out32() {
        super(32);
        setCompName("OUT_32");
    }
}
