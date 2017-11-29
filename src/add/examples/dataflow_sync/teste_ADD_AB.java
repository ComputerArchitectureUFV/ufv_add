/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package add.examples.dataflow_sync;

import add.dataflow.DataflowSyncSimulBase;

/**
 *
 * @author jeronimo
 */
public class teste_ADD_AB {

    public static void main(String argv[]) {
        final int QTDEDATA = (int) 1024;
        final int QTDEDATAOUT = (int) 512;
        final int QTDECONF = 1; // Configurações de constantes, Quantidade de constantes diferentes do dataflow
        final int QTDEIN = 2;// Quantidade de saídas do componente de entrada
        final int QTDEOUT = 1;// Quantidade de entradas do componente de saída
        final int TAMVECTOR = 4 + QTDEDATA + QTDECONF;
        int idxConf = 4;
        int idxData = 4 + QTDECONF;
        int[] vector, out;

        vector = new int[TAMVECTOR];

        //Dados para o funcionamento dos componentes
        vector[0] = QTDEDATA + QTDECONF + 1;
        vector[1] = QTDEOUT;
        vector[2] = QTDEIN;
        vector[3] = QTDECONF;
        //Inseridas as constantes
        vector[4] = ((32 << 8) | 1);//24bits para a constante, 8bits para ID - Concatenados. Ex: 0x2001 - const 1 para ID1
        

        for (int i = idxData; i < QTDEDATA + idxData; i++) {
            vector[i] = (int) i - idxData;
        }

        DataflowSyncSimulBase dataflowBase = new DataflowSyncSimulBase();
        //out = dataflowBase.startSimulation(vector, "/home/jeronimo/Área de Trabalho/add_ab.hds", QTDEDATAOUT);
        
        out = dataflowBase.startFpgaJtag(vector, "/home/jeronimo/altera/13.0sp1/quartus/bin/quartus_stp", QTDEDATAOUT);

        for (int i = 0; i < out.length; i++) {
            System.out.println("Soma" + i + " =, " + out[i]);
        }
    }
}
