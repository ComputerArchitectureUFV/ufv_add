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
public class Reduce32DataflowSimulation {

    public static void main(String argv[]) {
        final int QTDEDATA = 1024;
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
        vector[4] = 0x2001;

        for (int i = idxData; i < QTDEDATA + idxData; i++) {
            vector[i] = (int) 1;
        }

        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        out = dataflowBase.startSimulation(vector, "/DESIGN/REDUCE32_DATAFLOW.hds", 1);

        System.out.println( "Retorno:");

        for (int i = 0; i < out.length; i++) {
            System.out.println( out[i]);
        }
    }
}
