package add.dataflow.sync;

/**
 * ModI component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for calculating the rest of the integer division
 * of the input by a id (immediate).<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class ModI extends GenericI {

    /**
     * Object Constructor.
     */
    public ModI() {
        super();
        setCompName("%I");
    }

    /**
     * Method responsible for the component computation: in this case, it
     * returns the rest of the division of the parameter by the id.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case, it returns
     * the rest of the division of the parameter by the id.
     */
    @Override
    public int compute(int data) {
        if (immediate == 0) {
            setString(Integer.toString(id), Integer.toString(immediate));
            return 0;
        } else {
            setString(Integer.toString(id), Integer.toString(immediate));
            return (int) (data % immediate);
        }
    }
}
