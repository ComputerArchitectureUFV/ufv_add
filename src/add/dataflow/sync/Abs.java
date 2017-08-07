package add.dataflow.sync;

/**
 * Abs component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for delivering the absolute value of the
 * input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Abs extends GenericUn {

    /**
     * Object Constructor.
     */
    public Abs() {
        super();
        setCompName("ABS");
    }

    /**
     * Method responsible for the component computation.
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case, returns
     * the absolute value of the parameter.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString((data >= 0) ? data : data * -1));
        return (data >= 0) ? data : data * -1;
    }
}
