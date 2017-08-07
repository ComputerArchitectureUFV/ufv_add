package add.dataflow.sync;

/**
 * Out4 component for the ADD Accelerator Design and Deploy.<br>
 * The component implements an output queue with 4 input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Out4 extends GenericOut {

    /**
     * Object Constructor.
     */
    public Out4() {
        super(4);
        setCompName("OUT_4");
    }
}
