import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.math.BigInteger;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        //teste
        //System.out.println(hexToBin("0x00430820"));

        BinToAs teste = new BinToAs(hexToBin("0xac410064"));

        System.out.println(teste.IdentifyOpcode(hexToBin("0xac410064")));


        Scanner ler = new Scanner(System.in);

    System.out.printf("Informe o caminho do aruivo entrada:\n");
    String nome = ler.nextLine();

    try {
      // carrega o arquivo de entrada para leitura 
      FileReader entrada = new FileReader(nome);
      BufferedReader lerArq = new BufferedReader(entrada);

      //cria um arquivo de saída 
      FileWriter saida = new FileWriter("C:/Users/mathe/ProjetoMips/src/saida.txt");
      PrintWriter gravarArq = new PrintWriter(saida);

      String linha = lerArq.readLine(); 
      // lê a primeira linha
      // a variável "linha" recebe o valor "null" quando o processo
      // de repetição atingir o final do arquivo texto

      while (linha != null) {
        //imprime no terminal a saída 
        System.out.printf("%s\n", teste.IdentifyOpcode(hexToBin(linha)));
        //escreve a saída no arquivo criado

        gravarArq.printf("%s\n", teste.IdentifyOpcode(hexToBin(linha)));
        linha = lerArq.readLine(); // lê da segunda até a última linha
      }

      //fecha os dois arquivos 
      entrada.close();
      saida.close();
    } catch (IOException e) {
        System.err.printf("Erro na abertura do arquivo: %s.\n",
          e.getMessage());
    }

    System.out.println();

}

    //Strign formatada, recebe uma string em hex, trasnforma em binário através da calsse BigInteger 
    //Recebe uma String em Hexadecima e retorna uma String em binário com 32bits 
    //ex: hexToBin("0x3c010064") returns "00111100000000010000000001100100"
    public static String hexToBin(String hex){

        
        hex = hex.replaceFirst("0x", "");
        String binario = new BigInteger(hex, 16).toString(2);
        while (binario.length() < 32) {

            binario = "0" + binario;
            
        }

        return binario;     
    }
}
