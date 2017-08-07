package add.dataflow.sync;

/**
 * Sub component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for subtracting the inputs.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Sub extends GenericBin {

    /**
     * Object Constructor.
     */
    public Sub() {
        super();
        setCompName("SUB");
    }

    /**
     * Method responsible for the component computation: in this case performs a
     * subtraction of the parameters.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case the value
     * of the subtraction of the parameters.
     */
    @Override
    public int compute(int data1, int data2) {
        setString(Integer.toString(data1 - data2));
        return (int) (data1 - data2);
    }

}
