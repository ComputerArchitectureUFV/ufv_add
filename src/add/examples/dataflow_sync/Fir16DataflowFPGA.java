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
public class Fir16DataflowFPGA {

    public static void main(String argv[]) {
        final int QTDEDATA = (int)2048;
        final int QTDECONF = 16;
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
        vector[4] = 0x1010;
        vector[5] = 0xf0f;
        vector[6] = 0xe0e;
        vector[7] = 0xd0d;
        vector[8] = 0xc0c;
        vector[9] = 0xb0b;
        vector[10] = 0xa0a;
        vector[11] = 0x909;
        vector[12] = 0x808;
        vector[13] = 0x707;
        vector[14] = 0x606;
        vector[15] = 0x505;
        vector[16] = 0x404;
        vector[17] = 0x303;
        vector[18] = 0x202;
        vector[19] = 0x101;

        for (int i = idxData; i < QTDEDATA + idxData; i++) {
            vector[i] = (int) 2;
        }

        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        out = dataflowBase.startFpgaJtag(vector, "/home/jeronimo/altera/13.0sp1/quartus/bin/quartus_stp", QTDEDATA-15);

        boolean flag = true;
        
        for (int i = 0; i < QTDEDATA-15; i++) {
            if (out[i] != 272) {
                System.out.println("Erro de retorno" );
                flag = false;
                break;
            }
        }
        
        if(flag) System.out.println("Execução correta" );
    }
}
