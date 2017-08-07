package add.dataflow.sync;

/**
 * Not component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for the bitwise inversion of the input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Not extends GenericUn {

    /**
     * Object Constructor.
     */
    public Not() {
        super();
        setCompName("NOT");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * bitwise inversion of the parameter.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case the value
     * of the bitwise inversion of the parameter.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString(~data));
        return (int) ~data;
    }
}
