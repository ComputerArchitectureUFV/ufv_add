package add.dataflow.sync;

/**
 * SubI component for the ADD Accelerator Design and Deploy.<br>
 The component is responsible for subtracting the input by a id
 (immediate).<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class SubI extends GenericI {

    /**
     * Object Constructor.
     */
    public SubI() {
        super();
        setCompName("SUBI");
    }

    /**
     * Method responsible for the component computation: in this case performs a
 subtraction of the parameter by an (immediate) id.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case the value
 of the subtraction of the parameter by the id.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        return (int) (data - getImmediate());
    }
}
