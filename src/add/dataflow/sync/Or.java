package add.dataflow.sync;

/**
 * Or component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for the logical operation "Or" between the
 * input<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Or extends GenericBin {

    /**
     * Object Constructor.
     */
    public Or() {
        super();
        setCompName("OR");
    }

    /**
     * Method responsible for the component computation: in this case it
     * performs the logical operation "Or" between the parameters.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case the result
     * of the logical operation "Or" between the parameters.
     */
    @Override
    public int compute(int data1, int data2) {
        setString(Integer.toString(data1 | data2));
        return (int) (data1 | data2);
    }
}
