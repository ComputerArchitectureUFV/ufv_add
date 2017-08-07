package add.dataflow.sync;

/**
 * DivI component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for dividing the input by a (immediate) id.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class DivI extends GenericI {

    /**
     * Object Constructor.
     */
    public DivI() {
        super();
        setCompName("DIVI");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * division of the parameter by an (immediate) id.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case the value
     * of the division of the parameter by the id.
     */
    @Override
    public int compute(int data) {
        if (immediate == 0) {
            setString(Integer.toString(id), Integer.toString(immediate));
            return 0;
        } else {
            setString(Integer.toString(id), Integer.toString(immediate));
            return (int) (data / immediate);
        }
    }
}
