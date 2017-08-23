package add.dataflow.sync;

/**
 * SltI component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for returning the value 1 if the input is less
 * than the (immediate) id.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class SltI extends GenericI {

    /**
     * Object Constructor.
     */
    public SltI() {
        super();
        setCompName("SLTI");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * comparison if parameter is less than the (immediate) id.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case 1 or 0
     * depending on the comparison between the parameter and the id.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        return (int) ((data < getImmediate()) ? 1 : 0);
    }
}
