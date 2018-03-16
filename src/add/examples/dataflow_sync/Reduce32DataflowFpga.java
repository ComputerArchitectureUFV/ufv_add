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
public class Reduce32DataflowFpga {

    public static void main(String argv[]) {
        final int QTDEDATA = 32;
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
        out = dataflowBase.startFpgaJtag(vector, "/home/jeronimo/altera/13.0sp1/quartus/bin/quartus_stp", 1);

        System.out.println( "Retorno:");

        for (int i = 0; i < out.length; i++) {
            System.out.println( out[i]);
        }
    }
}
