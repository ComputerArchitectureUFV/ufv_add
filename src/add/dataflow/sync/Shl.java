package add.dataflow.sync;

/**
 * Shl component for the ADD Accelerator Design and Deploy.<br>
 * The component is responsible for moving all the bits of the first input to
 * the left N times, where N is equal to the value of the second input.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Shl extends GenericBin {

    /**
     * Object Constructor.
     */
    public Shl() {
        super();
        setCompName("SHL");
    }

    /**
     * Method responsible for the component computation: in this case, it moves
     * all the bits of the first parameter to the left N times, where N is equal
     * to the value of the second parameter.
     *
     * @param data1 - Value 1 to be used for computing.
     * @param data2 - Value 2 to be used for computing.
     * @return - Returns the result of the computation. In this case, it moves
     * all the bits of the first parameter to the left N times, where N is equal
     * to the value of second parameter.
     */
    @Override
    public int compute(int data1, int data2) {
        setString(Integer.toString(data1 << data2));
        return (int) (data1 << data2);
    }
}
