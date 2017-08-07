package add.dataflow.sync;

/**
 * Shr component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for moving all the bits of the first input to
 * the right N times, where N is equal to the value of the value from the second
 * input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Shr extends GenericBin {

    /**
     * Object Constructor.
     */
    public Shr() {
        super();
        setCompName("SHR");
    }

    /**
     * Method responsible for the component computation: in this case, it moves
     * all the bits of the first parameter to the right N times, where N is
     * equal to the value of the second parameter.
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case, it moves
     * all the bits of the first parameter to the right N times, where N is
     * equal to the value of the second parameter.
     */
    @Override
    public int compute(int data1, int data2) {
        setString(Integer.toString(data1 >> data2));
        return (int) (data1 >> data2);
    }
}
