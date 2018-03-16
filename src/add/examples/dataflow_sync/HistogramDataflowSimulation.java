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
public class HistogramDataflowSimulation {

    public static void main(String argv[]) {
        final int QTDEDATA = (int)(Math.pow(2, 21));
        final int QTDECONF = 1;
        final int QTDEIN = 32;
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
        vector[4] = 0x1000001;

        for (int i = idxData; i < QTDEDATA + idxData; i++) {
            vector[i] = (int) (Math.random() * 255);
        }

        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        out = dataflowBase.startSimulation(vector, "/DESIGN/HISTOGRAM_DATAFLOW.hds", (int) Math.pow(2, 16));

        for (int i = 0; i < Math.pow(2, 16); i++) {
            if (out[i] > 0) {
                System.out.println("ID=" + i + ", " + out[i] + "x");
            }
        }
    }
}
