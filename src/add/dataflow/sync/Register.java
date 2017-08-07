package add.dataflow.sync;

/**
 * Register component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for pass the input to the output when a clock
 * pulse occurs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Register extends GenericUn {

    /**
     * Object Constructor.
     */
    public Register() {
        super();
        setCompName("REG");
    }
}
