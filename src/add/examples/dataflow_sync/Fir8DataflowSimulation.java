package hebe.examples.dataflow_sync;

import hebe.dataflow.DataflowSyncSimulBase;

/**
 * HistogramDataflowSimulation example in simulator.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version * 1.0
 */
public class Fir8DataflowSimulation {

    public static void main(String argv[]) {
        final int QTDEDATA = (int)10;
        final int QTDECONF = 8;
        final int QTDEIN = 1;
        final int QTDEOUT = 1;
        final int TAMVECTOR = 4 + QTDEDATA + QTDECONF;
        int idxConf = 4;
        int idxData = 4 + QTDECONF;
        int[] vector, out;

        vector = new int[TAMVECTOR];

        vector[0] = QTDEDATA + QTDECONF + 1;
        vector[1] = QTDEOUT;
        vector[2] = QTDEIN;
        vector[3] = QTDECONF;
        vector[4] = 0x808;
        vector[5] = 0x707;
        vector[6] = 0x606;
        vector[7] = 0x505;
        vector[8] = 0x404;
        vector[9] = 0x303;
        vector[10] = 0x202;
        vector[11] = 0x101;

        for (int i = idxData; i < QTDEDATA + idxData; i++) {
            vector[i] = (int) 2;
        }

        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        out = dataflowBase.startSimulation(vector, "/DESIGN/FIR8_DATAFLOW.hds", 3);

        for (int i = 0; i < out.length; i++) {
            System.out.println("ID=" + i + ", " + out[i]);
        }
    }
}
