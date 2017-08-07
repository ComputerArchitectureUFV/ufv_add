package add.dataflow.sync;

/**
 * AccMax component for the ADD Accelerator Design and Deploy.<br>
 * The component implements a store for the highest input value.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class AccMax extends GenericAcc {

    private boolean firstScan;

    /**
     * Object Constructor.
     */
    public AccMax() {
        super();
        setCompName("ACC_MAX");
        firstScan = true;
    }

    /**
     * Method responsible for actions required when "Reset" occurs.
     *
     */
    @Override
    public void reset() {
        immediate = id;
        counter = immediate;
        firstScan = true;
    }

    /**
     * Method that compares the parameter to the stored value. If the parameter
     * is larger, it will override the stored value.
     *
     * @param data - Value to be used for computing.
     */
    @Override
    protected void accumulate(int data) {
        if (firstScan) {
            acc = data;
            firstScan = false;
        } else if (data > acc) {
            acc = data;
        }
        setString(Integer.toString(id), Integer.toString(immediate));
    }
}
