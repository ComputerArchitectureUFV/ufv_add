package add.dataflow.sync;

/**
 * ShlI component for the ADD Accelerator Design and Deploy.<br>
 The component is responsible for moving all the bits of the input to the left
 N times, where N is equal to the value of a (immediate) id.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class ShlI extends GenericI {

    /**
     * Object Constructor.
     */
    public ShlI() {
        super();
        setCompName("SHLI");
    }

    /**
     * Method responsible for the component computation: in this case, it moves
 all the bits of the parameter to the left N times, where N is equal to
 the value of a (immediate) id.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case, it moves
 all the bits of the parameter to the left N times, where N is equal to
 the value of a (immediate) id.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        return (int) (data << getImmediate());
    }

}
