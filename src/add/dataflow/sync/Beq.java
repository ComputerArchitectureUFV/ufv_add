package add.dataflow.sync;

/**
 * Beq component for the ADD Accelerator Design and Deploy. <br>
 * The component is responsible for comparing equality between the input.
 * Depending on the result of the comparison, the "IF" output or the "ELSE"
 * output will receive the value "1" while the other will receive the value "0".
 * <br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class Beq extends GenericBranch {

    /**
     * Object Constructor.
     */
    public Beq() {
        super();
        setCompName("BEQ");
    }

    /**
     * Method responsible for component computing: in this case performs a
     * comparison of equality between the input. Depending on the result of the
     * comparison, the "IF" output or the "ELSE" output will receive the value
     * "1" while the other will receive the value "0".
     *
     * @param data1 - Value to be used for the computation related to input 1.
     * @param data2 - Value to be used for the computation related to input 2.
     * @return - Returns the result of the computation. In this case "1" if the
     * parameters are equal or "0" if they are different.
     */
    @Override
    public int compute(int data1, int data2) {
        setString(Integer.toString((data1 == data2) ? 1 : 0));
        return (int) ((data1 == data2) ? 1 : 0);
    }
}
