package hebe.examples.dataflow_sync;

import hebe.dataflow.DataflowSyncSimulBase;

/**
 * AddAb data flow (Automatically generated) example in the simulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version * 1.0
 */
public class AddAbHelloWorldDataflowHadesSimulationWithGeneratedHds {

    public static void main(String argv[]) {
        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        dataflowBase.startSimulation("DEMO/ADD_AB_HELLOWORLD_DATAFLOW_CONF.txt", "/DESIGN/ADD_AB_HELLOWORLD_DATAFLOW_GENERATED.hds", "\nDesired Return: 5 10 15 20 25 30 35 40 45...\n", 10);
    }
}
