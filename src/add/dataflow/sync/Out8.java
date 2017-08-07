package add.dataflow.sync;

/**
 * Out8 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an output queue with 8 input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Out8 extends GenericOut {

    /**
     * Object Constructor.
     */
    public Out8() {
        super(8);
        setCompName("OUT_8");
    }
}
