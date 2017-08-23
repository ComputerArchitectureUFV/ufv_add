package add.dataflow.sync;

/**
 * OrI component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for the logical operation "OR" between the input
 * and a id (immediate)<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class OrI extends GenericI {

    /**
     * Object Constructor.
     */
    public OrI() {
        super();
        setCompName("ORI");
    }

    /**
     * Method responsible for the component computation: in this case it
     * performs the logical operation "OR" between the parameter and the
     * (immediate) id.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case the result
     * of the logical operation "OR" between the parameter and the id.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        return (int) (data | getImmediate());
    }

}
