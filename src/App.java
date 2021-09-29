public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        //teste
        System.out.println(hexToBin("0x3c010064"));
    }

    //Strign formatada, recebe uma string em hex, trasnforma em binário através da calsse integer 
    //Recebe uma String em Hexadecima e retorna uma String em binário 
    // ex: hexToBin("0x3c010064") returns "00111100000000010000000001100100"

    public static String hexToBin(String hex){

    
        hex = hex.replaceFirst("0x", "");
        String binario = Integer.toBinaryString(Integer.parseInt(hex, 16));
        while (binario.length() < 32) {

            binario = "0" + binario;
            
        }

        return binario;     
    }
}
