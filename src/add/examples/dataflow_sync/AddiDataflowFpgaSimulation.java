
package add.examples.dataflow_sync;

import add.dataflow.DataflowSyncSimulBase;

/**
 * ADDI data flow example in the FPGA Board.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version * 1.0
 */
public class AddiDataflowFpgaSimulation {

    public static void main(String argv[]) {

        DataflowSyncSimulBase base = new DataflowSyncSimulBase();
        int qtdeOut = 50;
        base.startFpgaJtag("DEMO/FIR_4_DATAFLOW_CONF.txt", "/home/jeronimo/altera/13.0sp1/quartus/bin/quartus_stp", "\nDesired Return: 4 5 6 ... 53\n",qtdeOut);
    }
}
