package add.dataflow.sync;

/**
 * BEQI component for the ADD Accelerator Design and Deploy. <br>
 * The component is responsible for comparing inequality between the input and a
 * constant (immediate). Depending on the result of the comparison, the "IF"
 * output or the "ELSE" output will receive the value "1" while the other will
 * receive the value "0". <br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class BneI extends GenericBranchI {

    /**
     * Object Constructor.
     */
    public BneI() {
        super();
        setCompName("BNEI");
    }

    /**
     * Method responsible for component computing: in this case performs a
     * comparison of inequality between the input and a constant. Depending on
     * the result of the comparison, the "IF" output or the "ELSE" output will
     * receive the value "1" while the other will receive the value "0".
     *
     * @param data - Value to be used for computing.
     * @return - Returns the result of the computation. In this case "0" if the
     * parameter is equal to the constraint or "1" if they are different.
     */
    @Override
    public int compute(int data) {
        setString(Integer.toString(getId()), Integer.toString(getImmediate()));
        return (int) ((data != getImmediate()) ? 1 : 0);
    }
}
