package add.dataflow.sync;

/**
 * Min component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for passing the output to the lowest value
 * input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Min extends GenericBin {

    /**
     * Object Constructor.
     */
    public Min() {
        super();
        setCompName("MIN");
    }

    /**
     * Method responsible for the computation of components: in this case, it
     * performs a comparison between the parameters and returns the smaller
     * between the two.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case, the
     * smallest of the parameters.
     */
    @Override
    public int compute(int data1, int data2) {
        if (data1 < data2) {
            setString(Integer.toString(data1));
            return data1;
        } else {
            setString(Integer.toString(data2));
            return data2;
        }
    }
}
