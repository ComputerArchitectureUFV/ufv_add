package add.examples.dataflow_sync;

import add.dataflow.DataflowSyncSimulBase;

/**
 * ADD_AB data flow example in simulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version * 1.0
 */
public class AddAbHelloWorldDataflowHadesSimulation {

    public static void main(String argv[]) {
        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        dataflowBase.startSimulation("DEMO/ADD_AB_HELLOWORLD_DATAFLOW_CONF.txt", "/DESIGN/ADD_AB_HELLOWORLD_DATAFLOW.hds", "\nDesired Return: 0 5 10 15 20 25 30 35 40 45...\n", 10);
    }
}
