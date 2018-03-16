package hebe.examples.dataflow_sync;

import hebe.dataflow.DataflowSyncSimulBase;

/**
 * HistogramDataflowSimulation example in FPGA Board.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version * 1.0
 */
public class HistogramDataflowFpgaSimulation {

    public static void main(String argv[]) {
        final int QTDEDATA = (int) 1024;
        final int QTDECONF = 1;
        final int ID = 1;
        final int QTDEIN = 2;
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
        vector[4] = ((QTDEDATA / QTDEIN) << 8) | ID;//0x40001;//configuração  - alterar se quiser

        for (int i = idxData; i < QTDEDATA + idxData; i++) {
            vector[i] = (int) (Math.random() * 10);
        }

        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        //out = dataflowBase.startSimulation(vector, "/DESIGN/HISTOGRAM_DATAFLOW.hds", (int) Math.pow(2, 16));
        out = dataflowBase.startFpgaJtag(vector, "/home/jeronimo/altera/13.0sp1/quartus/bin/quartus_stp", 256);

        int sum = 0;
        for (int i = 0; i < out.length; i++) {
            if (out[i] > 0) {
                System.out.println("Indice=" + i + ", " + out[i] + " vezes");
                sum+=out[i];
            }
        }
        System.out.println("Total=" +sum);
    }
}
