package add.examples.dataflow_sync;

import add.dataflow.DataflowSyncSimulBase;

/**
 * Branch_Test data flow example in simulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version * 1.0
 */
public class BranchTestDataflowHadesSimulation {

    public static void main(String argv[]) {
        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        dataflowBase.startSimulation("DEMO/BRANCH_TEST_DATAFLOW_CONF.txt", "/DESIGN/BRANCH_TEST_DATAFLOW.hds", "\nDesired Return: 0 3 4 9 8 15 12...\n", 7);

    }
}
