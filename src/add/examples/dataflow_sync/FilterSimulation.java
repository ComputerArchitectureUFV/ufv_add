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
public class FilterSimulation {

    public static void main(String argv[]) {
        final int QTDEDATA = (int)64;
        final int QTDECONF = 3;
        final int QTDEIN = 32;
        final int QTDEOUT = 32;
        final int TAMVECTOR = 4 + QTDEDATA + QTDECONF;
        int idxConf = 4;
        int idxData = 4 + QTDECONF;
        int[] vector, out;

        vector = new int[TAMVECTOR];

        vector[0] = QTDEDATA + QTDECONF + 1;
        vector[1] = QTDEOUT;
        vector[2] = QTDEIN;
        vector[3] = QTDECONF;
        vector[4] = 0xa01;//CONFIGURAÇÕES
        vector[5] = 0x102;
        vector[6] = 0x103;

        for (int i = idxData; i < QTDEDATA + idxData; i++) {
            vector[i] = (int) i - idxData;
        }

        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        out = dataflowBase.startSimulation(vector, "/DESIGN/FILTER32_GENERATED.hds", 64);

        for (int i = 0; i < out.length; i++) {
            System.out.println("ID=" + i + ", " + out[i]);
        }
    }
}
