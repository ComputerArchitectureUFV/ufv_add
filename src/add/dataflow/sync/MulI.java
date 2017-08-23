package add.dataflow.sync;

/**
 * MulI component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for multiplying the input by a (immediate)
 * id.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class MulI extends GenericI {

    /**
     * Object Constructor.
     */
    public MulI() {
        super();
        setCompName("MULI");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * multiplying of the parameter by an (immediate) id.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case the value
     * of the multiplication of the parameter by the id.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        return (int) (data * getImmediate());
    }
}
