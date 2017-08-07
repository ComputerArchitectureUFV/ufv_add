package add.dataflow;

import hades.gui.Editor;
import jfig.utils.SetupManager;
import hades.models.Design;
import hades.models.io.ClockGen;
import hades.models.io.Ipin;
import add.util.ConfReader;
import add.dataflow.sync.In1;
import add.dataflow.sync.In2;
import add.dataflow.sync.In4;
import add.dataflow.sync.In8;
import add.dataflow.sync.In16;
import add.dataflow.sync.In32;
import add.dataflow.sync.Out1;
import add.dataflow.sync.Out16;
import add.dataflow.sync.Out2;
import add.dataflow.sync.Out32;
import add.dataflow.sync.Out4;
import add.dataflow.sync.Out8;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for executing algorithms in the simulator or in the bundle with
 * FPGA.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class DataflowSyncSimulBase {

    /**
     * Method responsible for executing the algorithm in the simulator.
     *
     * @param conf - Configuration vector and data to be executed.
     * @param designPath - Design to be used to run the simulator.
     * @param outSize - Output vector size.
     * @return - Returns a vector with the processing results.
     */
    public int[] startSimulation(int[] conf, String designPath, int outSize) {
        return execHades(conf, designPath, outSize);
    }

    /**
     * Method responsible for executing the algorithm in the simulator and
     * display the output in the system default output.
     *
     * @param confPath - File containing the configuration and data to be
     * processed.
     * @param designPath - Design to be used to run the simulator.
     * @param desiredReturn - Expected outcome.
     * @param outSize - Output vector size.
     * @return - Returns a vector with the processing results.
     */
    public int[] startSimulation(String confPath, String designPath, String desiredReturn, int outSize) {

        ConfReader confReader = new ConfReader();
        File file;
        file = new File(confPath);
        System.out.printf(desiredReturn);
        return execHades(confReader.ReadConfig(file), designPath, outSize);

    }

    /**
     * Method responsible for running the algorithm on the FPGA board.
     *
     * @param conf - Configuration vector and data to be executed.
     * @param quartusStpPath - Path to the quartus_stp application.
     * @param outSize - Output vector size.
     * @return - Returns a vector with the processing results.
     */
    public int[] startFpgaJtag(int[] conf, String quartusStpPath, int outSize) {
        return execFpga(conf, quartusStpPath, outSize);
    }

    /**
     * Method responsible for running the algorithm on the FPGA boardand display
     * the output in the system default output.
     *
     * @param confPath - File containing the configuration and data to be
     * processed.
     * @param quartusStpPath - Path to the quartus_stp application.
     * @param desiredReturn - Expected outcome.
     * @param outSize - Output vector size.
     * @return - Returns a vector with the processing results.
     */
    public int[] startFpgaJtag(String confPath, String quartusStpPath, String desiredReturn, int outSize) {

        int[] vector = null;
        ConfReader confReader = new ConfReader();
        File file;
        file = new File(confPath);//getClass().getResource(confPath).toURI());//imprime os dados esperados
        System.out.printf(desiredReturn);
        vector = execFpga(confReader.ReadConfig(file), quartusStpPath, outSize);
        return vector;
    }

   
    /**
     * Execution in HADES Simulator
     *
     * @param rawData - Vector of data to be processed
     * @param designPath - Design to be used to run the simulator.
     * @param outSize - Output vector size.
     * @return - Returns a vector with the processing results.
     */
    public int[] execHades(int[] rawData, String designPath, int outSize) {

        int[] dataOut = null;

        int vectorSize = rawData[0];
        int qtdeIn = rawData[2];
        int qtdeOut = rawData[1];

        int[] dataIn = new int[vectorSize];

        for (int j = 0; j < vectorSize; j++) {
            dataIn[j] = (int) rawData[j + 3];
        }

        //Setup Hades
        SetupManager.loadGlobalProperties("hades/.hadesrc");
        SetupManager.loadUserProperties(".hadesrc");
        SetupManager.loadLocalProperties(".hadesrc");
        //*********

        //Impede que o hades já abra e inicie a simulação
        SetupManager.setProperty("Hades.Editor.AutoStartSimulation", "false");
        //******

        // create an editor
        //false como parâmetro = não mostra a janela
        Editor editor = new Editor();

        // specify window size and position
        //No manual está editor.getEditFrame().setBounds( 100, 100, 700, 500 );
        //Porém vi que o método agora possui o nome que se segue abaixo:
        editor.getFrame().setBounds(0, 0, 1024, 768);
        //*************

        // load a designPath
        editor.doOpenDesign(designPath, true);
        Design designHades = editor.getDesign();
        //******************

        // set some stimuli (or simply use the interactive switches)
        //Aqui são setadas as variáveis que comandarão o RESET e o CLOCK
        //bem como os componentes de entrada e saída.
        Ipin res = (Ipin) designHades.getComponent("reset");
        ClockGen clk = (ClockGen) designHades.getComponent("clock");

        //*******
        //Inicialização do Componente IN_X
        In1 in1;
        In2 in2;
        In4 in4;
        In8 in8;
        In16 in16;
        In32 in32;

        switch (qtdeIn) {
            case 1:
                in1 = (In1) designHades.getComponent("in");
                in1.setVectorIn(dataIn);
                break;
            case 2:
                in2 = (In2) designHades.getComponent("in");
                in2.setVectorIn(dataIn);
                break;
            case 4:
                in4 = (In4) designHades.getComponent("in");
                in4.setVectorIn(dataIn);
                break;
            case 8:
                in8 = (In8) designHades.getComponent("in");
                in8.setVectorIn(dataIn);
                break;
            case 16:
                in16 = (In16) designHades.getComponent("in");
                in16.setVectorIn(dataIn);
                break;
            case 32:
                in32 = (In32) designHades.getComponent("in");
                in32.setVectorIn(dataIn);
                break;
        }
        //*******************

        // now start the simulator
        editor.getSimulator().runForever();
        //comandos para a simulação. É importante que coloque-se delays para a alteração do valor da constante da entrada
        //pois senão apenas o último será utilizado. Os método abaixo não são bloqueadores.
        res.assign("1", 0.00); // value, time
        res.assign("0", 0.02); // value, time

        //Inicialização do Componente OUT_X
        Out1 out1;
        Out2 out2;
        Out4 out4;
        Out8 out8;
        Out16 out16;
        Out32 out32;

        switch (qtdeOut) {
            case 1:
                out1 = (Out1) designHades.getComponent("out");
                out1.setQtdeSave(outSize);
                while (!out1.getDoneSignal()) {

                    try {
                        Thread.currentThread().sleep(30);
                    } // Aguarda 30 ms para verificar novamente o término da simulação
                    catch (Exception e) {
                    }
                }

                dataOut = out1.getVectorOut();
                break;
            case 2:
                out2 = (Out2) designHades.getComponent("out");
                out2.setQtdeSave(outSize);
                while (!out2.getDoneSignal()) {

                    try {
                        Thread.currentThread().sleep(30);
                    } // Aguarda 30 ms para verificar novamente o término da simulação
                    catch (Exception e) {
                    }
                }

                dataOut = out2.getVectorOut();
                break;
            case 4:
                out4 = (Out4) designHades.getComponent("out");
                out4.setQtdeSave(outSize);
                while (!out4.getDoneSignal()) {

                    try {
                        Thread.currentThread().sleep(30);
                    } // Aguarda 30 ms para verificar novamente o término da simulação
                    catch (Exception e) {
                    }
                }

                dataOut = out4.getVectorOut();
                break;
            case 8:
                out8 = (Out8) designHades.getComponent("out");
                out8.setQtdeSave(outSize);
                while (!out8.getDoneSignal()) {

                    try {
                        Thread.currentThread().sleep(30);
                    } // Aguarda 30 ms para verificar novamente o término da simulação
                    catch (Exception e) {
                    }
                }

                dataOut = out8.getVectorOut();
                break;
            case 16:
                out16 = (Out16) designHades.getComponent("out");
                out16.setQtdeSave(outSize);
                while (!out16.getDoneSignal()) {

                    try {
                        Thread.currentThread().sleep(30);
                    } // Aguarda 30 ms para verificar novamente o término da simulação
                    catch (Exception e) {
                    }
                }

                dataOut = out16.getVectorOut();
                break;
            case 32:
                out32 = (Out32) designHades.getComponent("out");
                out32.setQtdeSave(outSize);
                while (!out32.getDoneSignal()) {

                    try {
                        Thread.currentThread().sleep(30);
                    } // Aguarda 30 ms para verificar novamente o término da simulação
                    catch (Exception e) {
                    }
                }

                dataOut = out32.getVectorOut();
                break;
        }
        //*******************

        System.out.printf("\nReturned Data\n");
        //recupera os dados de saída do componente OUT
        int size = dataOut.length;
        //imprime os dados em termianl para conferência
        for (int j = 0; j < size; j++) {
            System.out.printf("%d ", dataOut[j]);
        }
        System.out.printf("\n");
        //editor.doClose(); //This line exits everything
        return dataOut;
    }

    /**
     * Execution in FPGA Boards
     *
     * @param rawData - Vector of data to be processed
     * @param quartusStpPath - Path to the quartus_stp application.
     * @param outSize - Output vector size.
     * @return - Returns a vector with the processing results.
     */
    public int[] execFpga(int[] rawData, String quartusStpPath, int outSize) {

        int[] dataOut = null;

        try {
            //arquivo de lock
            FileWriter arq;

            arq = new FileWriter("/tmp/lock.dat");

            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.print("0");
            arq.close();

            //arquivo tcl
            arq = new FileWriter("/tmp/send.tcl");
            gravarArq = new PrintWriter(arq);
            gravarArq.printf("%s", new Tcl().getTclIn1());
            arq.close();

            //arquivo de dados
            arq = new FileWriter("/tmp/data.dat");
            gravarArq = new PrintWriter(arq);

            //qtde configurações
            int qtde_conf;
            if (rawData[3] % 16 == 0) {
                qtde_conf = rawData[3];
                gravarArq.printf("%d\n", rawData[3]);
            } else {
                qtde_conf = rawData[3] - (rawData[3] % 16) + 16;
                gravarArq.printf("%d\n", qtde_conf);
            }
            //qtde de entradas
            int qtde_data_Lines;
            int qtde_data;
            if ((rawData[0] - rawData[3] - 1) % 32 == 0) {
                qtde_data_Lines = (rawData[0] - rawData[3] - 1) / 32;
                qtde_data = (rawData[0] - rawData[3] - 1);
                gravarArq.printf("%d\n", qtde_data_Lines);
            } else {
                qtde_data_Lines = ((rawData[0] - rawData[3] - 1) - ((rawData[0] - rawData[3] - 1) % 32) + 32) / 32;
                qtde_data = (rawData[0] - rawData[3] - 1) - ((rawData[0] - rawData[3] - 1) % 32) + 32;
                gravarArq.printf("%d\n", qtde_data_Lines);
            }

            //qtde de saidas
            int qtde_out_lines;
            int qtde_out;
            if (outSize % 32 == 0) {
                qtde_out_lines = outSize / 32;
                qtde_out = outSize;
                gravarArq.printf("%d\n", qtde_out_lines);
            } else {
                qtde_out_lines = (outSize - (outSize % 32) + 32) / 32;
                qtde_out = outSize - (outSize % 32) + 32;
                gravarArq.printf("%d\n", qtde_out_lines);
            }

            //aqui começa a criação das configurações
            //cria o vetor de configurações
            int[] conf_vector = new int[qtde_conf];
            for (int i = 0; i < qtde_conf; i++) {
                if (i < rawData[3]) {
                    conf_vector[i] = rawData[i + 4];
                } else {
                    conf_vector[i] = 0;
                }
            }

            String confword = "";
            String conftmp;
            //cria uma string com os dados concatenados em binário
            for (int i = 0; i < qtde_conf; i++) {
                conftmp = Integer.toBinaryString(conf_vector[i]);
                if (conftmp.length() < 32) {
                    for (int j = conftmp.length(); j < 32; j++) {
                        conftmp = "0" + conftmp;
                    }
                }
                confword = conftmp + confword;
                if ((i + 1) % 16 == 0) {
                    gravarArq.println(confword);
                    confword = "";
                }
            }

            //aqui começa a criação dos dados
            //cria o vetor de dados
            int[] data_vector = new int[qtde_data];

            for (int i = 0; i < qtde_data; i++) {
                if (i < (rawData[0] - rawData[3] - 1)) {
                    data_vector[i] = rawData[i + 4 + rawData[3]];
                } else {
                    data_vector[i] = 0;
                }
            }

            //cria uma string com os dados concatenados em binário
            String dataword = "";
            String datatmp;
            for (int i = 0; i < qtde_data; i++) {
                datatmp = Integer.toBinaryString(data_vector[i]);
                if (datatmp.length() < 16) {
                    for (int j = datatmp.length(); j < 16; j++) {
                        datatmp = "0" + datatmp;
                    }
                }
                dataword = datatmp + dataword;
                if ((i + 1) % 32 == 0) {
                    gravarArq.println(dataword);
                    dataword = "";
                }
            }
            arq.close();

            //Aguarda o término do processamento pela placa de FPGA
            File file = new File("/tmp/lock.dat");
            Runtime.getRuntime().exec(quartusStpPath + " -t /tmp/send.tcl ");
            while (file.exists()) {

                try {
                    Thread.currentThread().sleep(1);
                } // Aguarda 30 ms para verificar novamente o término da simulação
                catch (Exception e) {
                }
                file = new File("/tmp/lock.dat");

            }

            //leitura do arquivo de saída
            file = new File("/tmp/out.dat");
            FileReader reader = new FileReader(file);
            BufferedReader leitor = new BufferedReader(reader);

            dataOut = new int[outSize];

            String linha;
            reader = new FileReader(file);
            leitor = new BufferedReader(reader);
            linha = leitor.readLine();

            int idx = 0;
            while (linha != null) {
                if (linha.length() < 512) {
                    for (int j = linha.length(); j < 512; j++) {
                        linha = "0" + linha;
                    }
                }
                for (int i = 0; i < 32; i++) {
                    int idxf = linha.length() - (i * 16);
                    int idxi = idxf - 16;
                    //System.out.print(Integer.parseInt(linha.substring(idxi, idxf), 2));
                    //System.out.print(" ");
                    if (idx < dataOut.length) {
                        dataOut[idx] = Integer.parseInt(linha.substring(idxi, idxf), 2);
                    }
                    idx++;
                }
                linha = leitor.readLine();
            }

            //correção necessária para que a placa retorne números negativos de 16 bits
            for (int i = 0; i < dataOut.length; i++) {
                if ((dataOut[i] >> 15) == 1) {
                    dataOut[i] = (int) (((~dataOut[i] + 1) & 0x0000ffff) * (-1));
                    //System.out.println(Integer.toBinaryString(rawData[i]));
                }

            }

            System.out.printf("\nReturned Data\n");
            for (int i = 0; i < dataOut.length; i++) {
                System.out.print(dataOut[i] + " ");
            }
            System.out.printf("\n");

            file.delete();
            file = new File("/tmp/data.dat");
            file.delete();
            file = new File("/tmp/send.tcl");
            file.delete();

        } catch (IOException ex) {
            Logger.getLogger(DataflowSyncSimulBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataOut;
    }
}

class Tcl {

    private String tcl;

    public Tcl() {
        tcl = "#Início da configuração para a execução do Script do Simulador em FPGA\n"
                + "\n"
                + "set usb [lindex [get_hardware_names] 0]\n"
                + "set device_name [lindex [get_device_names -hardware_name $usb] 0]\n"
                + "#puts \"*************************\"\n"
                + "puts \"Programming cable:\"\n"
                + "puts $usb\n"
                + "\n"
                + "proc int2bits {i} {    \n"
                + "    set res \"\"\n"
                + "    while {$i>0} {\n"
                + "        set res [expr {$i%2}]$res\n"
                + "        set i [expr {$i/2}]\n"
                + "    }\n"
                + "    if {$res==\"\"} {set res 0}\n"
                + "    return $res\n"
                + "}\n"
                + "\n"
                + "proc bin2dec {bin} {\n"
                + "    #returns integer equivalent of $bin \n"
                + "    set res 0\n"
                + "    if {$bin == 0} {\n"
                + "        return 0\n"
                + "    } elseif {[string match -* $bin]} {\n"
                + "        set sign -\n"
                + "        set bin [string range $bin[set bin {}] 1 end]\n"
                + "    } else {\n"
                + "        set sign {}\n"
                + "    }\n"
                + "    foreach i [split $bin {}] {\n"
                + "        set res [expr {$res*2+$i}]\n"
                + "    }\n"
                + "    return $sign$res\n"
                + "}\n"
                + "\n"
                + "proc push_reset {index value} {\n"
                + "    global device_name usb\n"
                + "\n"
                + "    if {$value > 3} {\n"
                + "        return \"value entered exceeds 2 bits\" \n"
                + "       \n"
                + "    }\n"
                + "\n"
                + "    set push_value [int2bits $value]\n"
                + "    set diff [expr {2 - [string length $push_value]%2}]\n"
                + "\n"
                + "    if {$diff != 2} {\n"
                + "        set push_value [format %0${diff}d$push_value 0] \n"
                + "        \n"
                + "    }\n"
                + "\n"
                + "    set resp [bin2dec $push_value]\n"
                + "    #puts $resp\n"
                + "    device_virtual_ir_shift -instance_index $index -ir_value 1 -no_captured_ir_value\n"
                + "    device_virtual_dr_shift -instance_index $index -dr_value $push_value -length 2 -no_captured_dr_value\n"
                + "}\n"
                + "\n"
                + "\n"
                + "proc push_conf {index value} {\n"
                + "    global device_name usb\n"
                + "\n"
                + "    set push_value [int2bits $value]\n"
                + "    set diff [expr {32 - [string length $push_value]%32}]\n"
                + "\n"
                + "    if {$diff != 32} {\n"
                + "        set push_value [format %0${diff}d$push_value 0] \n"
                + "        \n"
                + "    }\n"
                + "\n"
                + "    set resp [bin2dec $push_value]\n"
                + "    #puts $resp\n"
                + "    device_virtual_ir_shift -instance_index $index -ir_value 1 -no_captured_ir_value\n"
                + "    device_virtual_dr_shift -instance_index $index -dr_value $push_value -length 32 -no_captured_dr_value\n"
                + "}\n"
                + "\n"
                + "proc push_in {index value} {\n"
                + "    global device_name usb\n"
                + "\n"
                + "    set push_value [int2bits $value]\n"
                + "    set diff [expr {514 - [string length $push_value]%514}]\n"
                + "\n"
                + "    if {$diff != 514} {\n"
                + "        set push_value [format %0${diff}d$push_value 0] \n"
                + "        \n"
                + "    }\n"
                + "\n"
                + "    set resp [bin2dec $push_value]\n"
                + "    #puts $resp\n"
                + "    device_virtual_ir_shift -instance_index $index -ir_value 1 -no_captured_ir_value\n"
                + "    device_virtual_dr_shift -instance_index $index -dr_value $push_value -length 514 -no_captured_dr_value\n"
                + "}\n"
                + "\n"
                + "proc pop {index} {\n"
                + "    global device_name usb filename fileId\n"
                + "    variable x\n"
                + "    variable y\n"
                + "    \n"
                + "    device_virtual_ir_shift -instance_index $index -ir_value 2 -no_captured_ir_value\n"
                + "    set x [device_virtual_dr_shift -instance_index 0 -length 514]\n"
                + "    set resp [bin2dec $x]\n"
                + "    #puts $resp\n"
                + "    return $resp\n"
                + "}\n"
                + "\n"
                + "\n"
                + "#*******************Início do Script de simulação\n"
                + "puts \"Executing a Dataflow Graph\"\n"
                + "\n"
                + "#Abre o dispositivo para execução do dataflow\n"
                + "open_device -device_name $device_name -hardware_name $usb\n"
                + "device_lock -timeout 10000\n"
                + "\n"
                + "#handle para salvar as saídas\n"
                + "set handle [open \"/tmp/out.dat\" w+]\n"
                + "\n"
                + "#Leitura das configurações e dado no arquivo data.dat\n"
                + "set data_file [open /tmp/data.dat]\n"
                + "set data_data [read $data_file]\n"
                + "close $data_file\n"
                + "\n"
                + "set data_list [split $data_data \"\\n\"]\n"
                + "\n"
                + "#***************************************************\n"
                + "\n"
                + "\n"
                + "#Reset para a placa através do componente JTAG 1\n"
                + "push_reset 1 0x0\n"
                + "#reset\n"
                + "push_reset 1 0x1\n"
                + "#start - liga um led para informar que o processamento está ocorrendo\n"
                + "push_reset 1 0x2\n"
                + "\n"
                + "\n"
                + "#configurações - seta a quantidade de configurações que  circuito deverá ler\n"
                + "set numconf [lindex $data_list 0]\n"
                + "#seta a quantidade de configurações para o circuito através do componente JTAG 2\n"
                + "push_conf 2 $numconf\n"
                + "\n"
                + "\n"
                + "#variáveis para a comunicação assíncrona JTAG\n"
                + "# e controle do envio e recebimento dos dados\n"
                + "set rdyo 0x0\n"
                + "set acko 0x0\n"
                + "set counterin 0\n"
                + "set counterout 0\n"
                + "set counterconf 0\n"
                + "set flag1 0\n"
                + "set flag2 0\n"
                + "set dat 0\n"
                + "set conf 0\n"
                + "set idxlist 2\n"
                + "set numin [lindex $data_list 1]\n"
                + "set numout [lindex $data_list 2]\n"
                + "\n"
                + "#transmissão de configurações e dados, e recepção dos dados processados\n"
                + "\n"
                + "while { $counterin < $numin || $counterout < $numout  } {\n"
                + "  \n"
                + "  if { $counterconf < [expr $numconf / 16] } {\n"
                + "    if { $flag1 == 0 } {\n"
                + "        set idxlist [expr $idxlist + 1]\n"
                + "        set t [lindex $data_list $idxlist]\n"
                + "        set conf [bin2dec [string tolower $t]]\n"
                + "        set rdyo 1\n"
                + "        push_in 0 [expr [expr $conf << 2] | [expr $acko  | $rdyo]]\n"
                + "        puts \"conf - foi\"\n"
                + "        set flag1 1\n"
                + "      } elseif { $flag1 == 1 } {\n"
                + "        if { [expr [pop 0] >> 1 & 0x1] == 1 } {\n"
                + "          set rdyo 0\n"
                + "          push_in 0 [expr [expr $conf << 2] | [expr $acko | $rdyo]]\n"
                + "          set flag1 2\n"
                + "        }\n"
                + "      } elseif { $flag1 == 2 } {\n"
                + "        if { [expr [pop 0] >> 1 & 0x1] == 0 } {\n"
                + "          set flag1 0\n"
                + "          set counterconf [expr $counterconf + 1]\n"
                + "        }\n"
                + "      }\n"
                + "  } else {\n"
                + "    #escrita dados na fifo_in\n"
                + "    if { $counterin < $numin } {  \n"
                + "      if { $flag1 == 0 } {\n"
                + "        set idxlist [expr $idxlist + 1]\n"
                + "        set t [lindex $data_list $idxlist]\n"
                + "        set dat [bin2dec [string tolower $t]]\n"
                + "        set rdyo 1\n"
                + "        push_in 0 [expr [expr $dat << 2] | [expr $acko  | $rdyo]]\n"
                + "        puts \"data - foi\"\n"
                + "        set flag1 1\n"
                + "      } elseif { $flag1 == 1 } {\n"
                + "        if { [expr [pop 0] >> 1 & 0x1] == 1 } {\n"
                + "          set rdyo 0\n"
                + "          push_in 0 [expr [expr $dat << 2] | [expr $acko | $rdyo]]\n"
                + "          set flag1 2\n"
                + "        }\n"
                + "      } elseif { $flag1 == 2 } {\n"
                + "        if { [expr [pop 0] >> 1 & 0x1] == 0 } {\n"
                + "          set flag1 0\n"
                + "          set counterin [expr $counterin + 1]\n"
                + "        }\n"
                + "      }\n"
                + "    } else {\n"
                + "      if { $flag1 == 0 } {\n"
                + "        set dat 0\n"
                + "        set rdyo 1\n"
                + "        push_in 0 [expr [expr $dat << 2] | [expr $acko  | $rdyo]]\n"
                + "        puts \"foi\"\n"
                + "        set flag1 1\n"
                + "      } elseif { $flag1 == 1 } {\n"
                + "        if { [expr [pop 0] >> 1 & 0x1] == 1 } {\n"
                + "          set rdyo 0\n"
                + "          push_in 0 [expr [expr $dat << 2] | [expr $acko | $rdyo]]\n"
                + "          set flag1 2\n"
                + "        }\n"
                + "      } elseif { $flag1 == 2 } {\n"
                + "        if { [expr [pop 0] >> 1 & 0x1] == 0 } {\n"
                + "          set flag1 0\n"
                + "          set counterin [expr $counterin + 1]\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  \n"
                + "    #leitura dados da fifo_out\n"
                + "    if { $counterout < $numout } { \n"
                + "      if { $flag2 == 0 } {\n"
                + "        if { [expr [pop 0] >> 0 & 0x1] == 1 } {\n"
                + "          #puts \"chegou\"\n"
                + "          puts $handle  [int2bits [expr [pop 0] >> 2]]\n"
                + "          set acko 2\n"
                + "          push_in 0 [expr [expr $dat << 2] | [expr $acko | $rdyo]]\n"
                + "          set flag2 1\n"
                + "        }\n"
                + "      } elseif { $flag2 == 1 } {\n"
                + "        if { [expr [pop 0] >> 0 & 0x1] == 0 } {\n"
                + "          set acko 0\n"
                + "          push_in 0 [expr [expr $dat << 2] | [expr $acko | $rdyo]]\n"
                + "          set flag2 0\n"
                + "          set counterout [expr $counterout + 1]\n"
                + "        }\n"
                + "      } \n"
                + "    }\n"
                + "  }\n"
                + "}\n"
                + "\n"
                + "\n"
                + "#reset - stop - desliga o led que indica o processamento\n"
                + "push_reset 1 0x1\n"
                + "#push_reset 1 0x0\n"
                + "\n"
                + "close $handle\n"
                + "file delete /tmp/lock.dat\n"
                + "\n"
                + "device_unlock\n"
                + "close_device";
    }

    /**
     * @return the tcl
     */
    public String getTclIn1() {
        return tcl;
    }

}
