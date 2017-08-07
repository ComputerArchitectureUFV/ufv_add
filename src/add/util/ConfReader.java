package add.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class responsible for providing useful routines for the project.<br>
 * Universidade Federal de Viçosa - MG - Brasil.
 *
 * @author Jeronimo Costa Penha - jeronimopenha@gmail.com
 * @author Ricardo Santos Ferreira - cacauvicosa@gmail.com
 * @version 1.0
 */
public class ConfReader {

    /**
     * Method responsible for reading a configuration file and returning a
     * vector with the values read.
     *
     * @param file - File to read
     * @return - Returns a vector containing the values read in the file.
     */
    public int[] ReadConfig(File file) {

        String linha;
        int tmp;
        int[] rawData = null;
        boolean firstTime = true;

        FileReader reader;
        try {
            reader = new FileReader(file);
            BufferedReader leitor = new BufferedReader(reader);
            int tamArquivo = 0;
            linha = leitor.readLine();
            while (linha != null) {
                linha = leitor.readLine();
                tamArquivo++;
            }

            rawData = new int[tamArquivo];

            reader = new FileReader(file);
            leitor = new BufferedReader(reader);
            linha = leitor.readLine();
            tmp = 0;
            int idx = 0;
            while (linha != null) {
                for (int character : linha.toCharArray()) {
                    if (character == '/') {
                        break;
                    } else if (character - 87 > 9 && character - 87 < 16) {
                        character -= 87;
                    } else if (character - 55 > 9 && character - 55 < 16) {
                        character -= 55;
                    } else if (character - 48 >= 0 && character - 48 <= 9) {
                        character -= 48;
                    }

                    if (firstTime) {
                        tmp = character;
                        firstTime = false;
                    } else {
                        tmp = (tmp << 4) | character;
                    }
                }

                //System.out.println(Integer.toHexString(tmp));
                rawData[idx] = tmp;
                linha = leitor.readLine();
                tmp = 0;
                idx++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rawData;
    }
}
