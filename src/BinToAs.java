
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//Essa classe vai converter de binário para Assembly
public class BinToAs {
	public String bin;
	public String op;
	public String rs;
	public String rt;
	public String rd;
	public String sh;
	public String fn;
	public String operand;
	public String jta;

	//variaveis hi e lo, usadas na divisão e multiplicação 
	protected int hi = 0;
	protected int lo = 0;

	// array de reg, onde Regitradores[0] = $0 ...
	public int[] reg = new int[32];
	

	public BinToAs(int[] reg) {
		this.reg = reg;
	}

	//Recebe como parametro o argumento e o número de argumentos (1, 2 ou 3)
	//retorna um array de inteiros com o número dos reg passados como argumento em ordem.
	//Ex: Se os argumentos foram $12, $2, $28, retorna um array com de inteiros onde [0] = 12, [1] = 2 e [2] = 28
	public int[] trataString(String argumento, int numeroDeArgumentos) {
		int[] argumentosInt = new int[numeroDeArgumentos];
		String argumentoSeparado;
		System.out.println(argumento);
		for (int i = 0; i < numeroDeArgumentos; i++) {
			argumentoSeparado = separaArgumento(argumento)[0];
			argumento = separaArgumento(argumento)[1];
			argumentosInt[i] = Integer.parseInt(argumentoSeparado);

		}
		return argumentosInt;

	}

	// separa o primeiro argumento da String do resto, a posição [0] tem o array
	// separado e a posição [1] o que sobrou
	public String[] separaArgumento(String argumento) {
		String[] argumentos = new String[2];
		argumentos[0] = argumento;
		argumentos[1] = "";
		for (int i = 0; i < argumento.length(); i++) {

			if (argumento.charAt(i) == '$') // se for igual a $, ele é excluido
			{
				argumentos[0] = argumento.substring(i + 1, argumento.length());
				argumentos[1] = "";
			}
			if (argumento.charAt(i) == ',') { // se nunca entrar nesse, significa que tem apenas um argumento,
														// se entrar, separo a parte do primeiro argumento do resto
				argumentos[1] = argumento.substring(i+2, argumento.length());  //resto do argumento (+2 porque a vírgula e o espaço não estão incluidos)
				argumentos[0] = argumento.substring(1, i); // primeiro argumento sem o $
				

				return argumentos; // se entrar aqui já retorna o argumento
			}
		}
		return argumentos; // só chega aqui se tiver apenas um argumento
	}

	// Separa a parte do OPcode da String
	public String separaOP(String bin) {
		String bin_op = bin.substring(0, 6);
		return bin_op;

	}

	// R e I - separa rs da String
	public String separaRS(String bin) {
		String bin_rs;
		bin_rs = bin.substring(6, 11);
		return bin_rs;
	}

	// R e I - separa rt da String
	public String separaRT(String bin) {
		String bin_rt = bin.substring(11, 16);
		return bin_rt;
	}

	// R - separa rd da String
	public String separaRD(String bin) {
		String bin_rd = bin.substring(16, 21);
		return bin_rd;
	}

	// R - separa sh da String
	public String separaSH(String bin) {
		String bin_sh = bin.substring(21, 26);
		return bin_sh;
	}

	// Separa a parte fn da String
	public String separaFN(String bin) {
		String bin_fn = bin.substring(26, 32);
		return bin_fn;
	}

	// Separa a parte do operando da String
	public String separaOperand(String bin) {
		String bin_operand = bin.substring(16, 32);
		return bin_operand;
	}

	// Tipo J - separa o jump target adress
	public String separaJTA(String bin) {
		String bin_JTA = bin.substring(6, 32);
		return bin_JTA;
	}

	// Identifica que tipo de operação é pelo OPcode
	public String IdentifyOpcode(String bin) {
		String bin_op = separaOP(bin); // chama o método separaOP para separar o OP code do resto da String
		if (bin_op.equals("000000")) // Se o Opcode "000000 então ou é syscall, ou uma instrução do tipo R"
		{
			// verifica se é syscall
			if (bin.substring(6, 32).equals("00000000000000000000001100")) {
				return DictSyscall("00000000000000000000001100") + ""; // DictSyscall retornará o código em Assembly
			} else
				// se não é syscall, então é R
				// separa todas as partes da instrução R
				op = bin_op;
			rs = separaRS(bin);
			rt = separaRT(bin);
			rd = separaRD(bin);
			sh = separaSH(bin);
			fn = separaFN(bin);
			return toAssemblyR(rs, rt, rd, sh, fn); // Envia cada parte para o método toAssemblyR, que irá retorna o
													// código em Assembly
		} else if (bin_op.equals("000010") | bin_op.equals("000011")) {
			// verifica se é uma instrução do tipo J
			// separa o opcode do jump target adress
			op = bin_op;
			jta = separaJTA(bin);
			return toAssemblyJ(op, jta); // Envia cada parte para o método toAssemblyJ, que irá retorna o código em
											// Assembly
		} else {
			// Se não é syscall, R ou J, é instrução do tipo I.
			// separa as partes de acordo com a instrução tipo I.
			op = bin_op;
			rs = separaRS(bin);
			rt = separaRT(bin);
			operand = separaOperand(bin);
			return toAssemblyI(op, rs, rt, operand); // Envia as partes para o método toAssemblyI, que irá retorna o
														// código em Assembly
		}
	}

	// recebe os valores separados em binário, consulta o dicionario de comandos R e
	// retorna os devidos comandos
	public String toAssemblyR(String rs, String rt, String rd, String sh, String fn) {
		String assembly = "";
		// se o fn da instrução é igual a x
		// então a instrução terá esse modelo
		if (fn.equals("001000")) {
			assembly = DictR(fn) + " " + DictR(rs) + "";
		} else if (fn.equals("010000") || fn.equals("010010") || fn.equals("001000")) {
			assembly = DictR(fn) + " " + DictR(rd) + "";
		} else if (fn.equals("011000") || fn.equals("011010") || fn.equals("011011") || fn.equals("011001")) {
			assembly = DictR(fn) + " " + DictR(rs) + ", " + DictR(rt) + "";
		} else if (fn.equals("000010") || fn.equals("000011") || fn.equals("000000")) {
			assembly = DictR(fn) + " " + DictR(rd) + ", " + DictR(rt) + ", " + DictR(sh) + "";
		} else
			assembly = DictR(fn) + " " + DictR(rd) + ", " + DictR(rs) + ", " + DictR(rt) + DictR(sh) + "";

		return assembly + exeIntrucaoR(this.fn, this.rs, this.rd, this.rt); // retorna o comando em Assembly
	}

	// recebe os valores separados em binário, consulta o dicionario de comandos I e
	// retorna os devidos comandos
	public String toAssemblyI(String op, String rs, String rt, String operand) {
		int numero = Integer.parseInt(operand, 2); // Converte o número do operando de binário para decimal
		String assembly = "";
		
		// Se o opcode foi igual a x
		// Então a instrução terá esse modelo
		if (op.equals("001111")) {
			assembly = DictI(op) + " " + DictI(rt) + ", " + numero + "";
		} else if (op.equals("000001")) {
			assembly = DictI(op) + " " + DictI(rs) + ", " + "start";
		} else if (Integer.parseInt(op, 2) >= 32 && Integer.parseInt(op, 2) <= 43) {
			assembly = DictI(op) + " " + DictI(rt) + ", " + numero + "(" + DictI(rs) + ")";
		} else if (op.equals("000101") || op.equals("000100")) {
			assembly = DictI(op) + " " + DictI(rs) + ", " + DictI(rt) + ", start";
		} else
			assembly = DictI(op) + " " + DictR(rt) + ", " + DictR(rs) + ", " + numero + "";

		return assembly + exeIntrucaoI(this.op, this.rs, this.rt, this.operand); // Retora a instrução em Assembly
	}

	// recebe os valores separados em binário, consulta o dicionario de comandos J e
	// retorna os devidos comandos
	public String toAssemblyJ(String op, String jta) {
		// As instruções do tipo J seguem o seguinte modelo:
		String assembly = DictJ(op) + " " + "start";

		return assembly;
	}

	// dicionário de comandos R a key é o codigo em binário, o value é o comando em
	// Assembly
	public String DictR(String bin) {
		// Dicionário do tipo R
		// Recebe uma String em binário e retorna seu correspondente em Assembly, de
		// acordo com o tipo R
		Map<String, String> dicr = new HashMap<String, String>();
		dicr.put("00001", "$1");
		dicr.put("00011", "$3");
		dicr.put("00010", "$2");
		dicr.put("00000", "");
		dicr.put("100000", "add");
		dicr.put("100001", "addu");
		dicr.put("100100", "and");
		dicr.put("011010", "div");
		dicr.put("011011", "divu");
		dicr.put("001000", "jr");
		dicr.put("010000", "mfhi");
		dicr.put("010010", "mflo");
		dicr.put("011000", "mult");
		dicr.put("011001", "multu");
		dicr.put("100111", "nor");
		dicr.put("100101", "or");
		dicr.put("000000", "sll");
		dicr.put("000100", "sllv");
		dicr.put("101010", "slt");
		dicr.put("000011", "sra");
		dicr.put("000111", "srav");
		dicr.put("000010", "srl");
		dicr.put("000110", "srlv");
		dicr.put("100010", "sub");
		dicr.put("100011", "subu");
		dicr.put("100110", "xor");
		dicr.put("01010", "10");

		return dicr.get(bin); // Retorna o comando em Assembly
	}

	// dicionário de comandos I a key é o codigo em binário, o value é o comando em
	// Assembly
	public String DictI(String bin) {
		// Dicionário do tipo I
		// Recebe uma String em binário e retorna seu correspondente em Assembly, de
		// acordo com o tipo I
		Map<String, String> dici = new HashMap<String, String>();
		/*
		 * Falta entender o start, já que varia de entrada para entrada dici.put("",
		 * "start");
		 */
		dici.put("00001", "$1");
		dici.put("00010", "$2");
		dici.put("001000", "addi");
		dici.put("001001", "addiu");
		dici.put("001100", "andi");
		dici.put("000111", "bgtz");
		dici.put("000100", "beq");
		dici.put("000001", "bltz");
		dici.put("000110", "blez");
		dici.put("000101", "bne");
		dici.put("100000", "lb");
		dici.put("100100", "lbu");
		dici.put("001111", "lui");
		dici.put("100011", "lw");
		dici.put("001101", "ori");
		dici.put("101000", "sb");
		dici.put("001010", "slti");
		dici.put("101011", "sw");
		dici.put("001110", "xori");

		return dici.get(bin);
	}

	// dicionário de comandos J a key é o codigo em binário, o value é o comando em
	// Assembly
	public String DictJ(String bin) {
		// Dicionário do tipo J
		// Recebe uma String em binário e retorna seu correspondente em Assembly, de
		// acordo com o tipo J
		Map<String, String> dicj = new HashMap<String, String>();
		/*
		 * Falta entender o start, já que varia de entrada para entrada dicj.put("",
		 * "start");
		 */
		dicj.put("000010", "j");
		dicj.put("000011", "jal");

		return dicj.get(bin);
	}

	// dicionario com comando Syscall
	public String DictSyscall(String bin) {
		// Dicionário do tipo syscall
		// Recebe uma String em binário e retorna seu correspondente em Assembly
		Map<String, String> dicsyscall = new HashMap<String, String>();
		dicsyscall.put("00000", "");
		dicsyscall.put("00000000000000000000001100", "syscall");

		return dicsyscall.get(bin);
	}


	//identificador de instruções tipo R
	public String exeIntrucaoR(String avalia, String rs, String rd, String rt){
		int reg1 = Integer.parseInt(rs, 2);
		int regD = Integer.parseInt(rd, 2);
		int reg2 = Integer.parseInt(rt, 2);
		int imediato = Integer.parseInt(operand, 2);

		
		//valores para teste, excluir dps 
		this.reg[2] = 5;
		this.reg[3] = 6;
		
		String retorno = "";

		switch (avalia){
			case "100000": //add
				this.add(regD, reg1, reg2);
				break;

			case "100010" : //sub
				this.sub(regD, reg1, reg2);
				break;

			case "101010": //slt
				this.slt(regD, reg1, reg2);
				break;

			case "100100": //and
				this.add(regD, reg1, reg2);
				break;

			case "010000": //mfhi
				this.mfhi(regD);
				break;

			case "010010": //mflo
				this.mflo(regD);
				break;

			case "011010": //div
				this.div(reg1, reg2);
				break;
		}

	 return retorno;
	}

	//identificador de instruções tipo I
    public String exeIntrucaoI(String op, String rs, String rt, String operand){
		int reg1 = Integer.parseInt(rs, 2);
		int regD = Integer.parseInt(rt, 2);
		int imediato = Integer.parseInt(operand, 2);

		String retorno = "";

		switch (op) {
			case "001000": //addi
				this.addi(regD, reg1, imediato);
				retorno = "\n" + Arrays.toString(reg);
			break;

			case "001010": // slti
				this.slti(regD, reg1, imediato);
			break;

			case "001100": // andi
				this.andi(regD, reg1, imediato);
			break;
		}
	    return retorno;
    }


	//Intruções Lógicas e Aritméticas 

		//soma com overflow
	public void add(int destino, int fonte1, int fonte2){
		reg[destino] = reg[fonte1] + reg[fonte2];
	}

		//subtração com overflow
	public void sub(int destino, int fonte1, int fonte2){
		reg[destino] = reg[fonte1] - reg[fonte2];
	}

		// menor que 
	public void slt(int destino, int fonte1, int fonte2){
		if (reg[fonte1] < reg[fonte2]){
			reg[destino] = 1;
		} else 
			reg[destino] = 0;
	}
		// AND lógico
	public void and(int destino, int fonte1, int fonte2){
		if (reg[fonte1] == reg[fonte2]){
			reg[destino] = 1;
		} else 
			reg[destino] = 0;
	}


	//public void or(int destino, int fonte1, int fonte2)

	//public void xor(int destino, int fonte1, int fonte2)

	//public void nor(int destino, int fonte1, int fonte2)


	public void mfhi(int destino){
		hi = reg[destino];
	}

	public void mflo(int destino){
		lo = reg[destino];
	}
		//adição sem overflow
	//public void addu(int destino, int )

		//subtração sem overflow
	//public void subu(int destino, int fonte1, int fonte 2)

		//multiplicação com overflow
	//public void mult(int destino, int fonte1, int fonte2)

		//multiplicação sem overflow
	//public void multu(int destino, int fonte1, int fonte2)

		//divisão com overflow
	public void div(int fonte1, int fonte2){
		 // hi recebe o resto 
		hi = reg[fonte1] % reg[fonte2];
		 //lo recebe o quociente
		lo = (reg[fonte1] / reg[fonte2]);
	}

		//divisão sem overflow
	//public void divu(int fonte1, int fonte2)

		//shift left logical
	//public void sll(int destino, int fonte1, int imediato)

		//shift right logical
	//public void srl(int destino, int fonte1, int imediato)

		//shift right aritmethic 
	//public void sra(int destino, int fonte1, int imediato)

	    //shift left logical variable 
	//public void sllv(int destino, int fonte1, int fonte2)

	   //shift right logical variable 
	//public void srlv(int destino, int fonte1, int fonte2)

	   //shift right aritmethic variable 
	//public void srav(int destino, int fonte1, int fonte2)

	   //adição imediata
	public void addi(int destino, int fonte1, int imediato){
		reg[destino] = reg[fonte1] + imediato;
	}

	   //menor que imediato
	public void slti(int destino, int fonte1, int imediato){
		if (reg[fonte1] < imediato){
			reg[destino] = 1;
		}else
		 	reg[destino] = 0;
	}

	  //AND imediato 
	public void andi(int destino, int fonte1, int imediato){
		if (reg[fonte1] == imediato){
			reg[destino] = 1;
		}else 
			reg[destino] = 0;
	}

	  //OR imediato 
	//public void ori(int destino, int fonte1, int imediato)

	  //XOR (OR exclusivo) imediato 
	//public void xori(int destino, int fonte1, int imediato)

	  //adição imediata sem overflow
	//public void addiu(int destino, int fonte1, int imediato)
	

}
