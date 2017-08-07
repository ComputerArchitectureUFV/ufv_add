package add.dataflow.sync;

/**
 * AccMul component for the ADD Accelerator Design and Deploy.<br>
 * The component implements a multiplication accumulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class AccMul extends GenericAcc {

    /**
     * Object Constructor.
     */
    public AccMul() {
        super();
        acc = 1;
        setCompName("ACC_MUL");
    }

    /**
     * Method responsible for actions required when "Reset" occurs.
     *
     */
    @Override
    public void reset() {
        immediate = id;
        counter = immediate;
        acc = 1;
    }

    /**
     * Method that accumulates the input value with the stored. In this case, it
     * multiplies the value stored by the input and stores it.
     *
     */
    @Override
    protected void accumulate(int data) {
        acc = acc * data;
        setString(Integer.toString(id), Integer.toString(immediate));
    }
}
