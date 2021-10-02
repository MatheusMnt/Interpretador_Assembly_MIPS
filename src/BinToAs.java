
import java.util.HashMap;
import java.util.Map;

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
	
	public BinToAs(String bin)
	{
		this.bin = bin;
	}
	//verifica se a String tem tamanho de 32 bits
	private Boolean isOK(String bin)
	{
		if(bin.length() == 32)
		{
			return true;
		}
		return false;
	}

	//Separa a parte do OPcode da String
	public String separaOP(String bin)
	{
		String bin_op = bin.substring(0, 6);
		return bin_op;
		
	}
	//R e I - separa rs da String
	public String separaRS(String bin)
	{
		String bin_rs;
		bin_rs = bin.substring(6, 11);
		return bin_rs;
	}
	//R e I - separa rt da String
	public String separaRT(String bin)
	{
		String bin_rt = bin.substring(11, 16);
		return bin_rt;
	}
	//R - separa rd da String
	public String separaRD(String bin)
	{
		String bin_rd = bin.substring(16, 21);
		return bin_rd;
	}
	//R - separa sh da String
	public String separaSH(String bin)
	{
		String bin_sh = bin.substring(21, 26);
		return bin_sh;
	}
	public String separaFN(String bin)
	{
		String bin_fn = bin.substring(26, 32);
		return bin_fn;
	}
	public String separaOperand(String bin)
	{
		String bin_operand = bin.substring(16, 32);
		return bin_operand;
	}
	
	//Tipo J, separa
	public String separaJTA(String bin)
	{
		String bin_JTA = bin.substring(6, 32);
		return bin_JTA;
	}
		
	//Identifica que tipo de opera��o � pelo OPcode
	public String IdentifyOpcode(String bin)
	{
		String bin_op = separaOP(bin);
		if(bin_op.equals("000000"))
		{
			// verifica se é syscall
			if (bin.substring(6, 32).equals("00000000000000000000001100")){
				return DictSyscall("00000000000000000000001100") + ";";
			} else 
			    //R
				op = bin_op;
				rs = separaRS(bin);
				rt = separaRT(bin);
				rd = separaRD(bin);
				sh = separaSH(bin);
				fn = separaFN(bin);
				return toAssemblyR(rs, rt, rd, sh, fn);
		}else if(bin_op.equals("000010") | bin_op.equals("000011"))
		{
			//J
			op = bin_op;
			jta = separaJTA(bin);
			return toAssemblyJ(op, jta);
		}else
		{
			//I
			
			op = bin_op;
			rs = separaRS(bin);
			rt = separaRT(bin);
			operand = separaOperand(bin);
			return toAssemblyI(op, rs, rt, operand);
		}
	}

    //recebe os valores separados em binário, consulta o dicionario de comandos R e retorna os devidos comandos 
	public String toAssemblyR (String rs, String rt, String rd, String sh, String fn){
		String assembly = "";
		if (fn.equals("001000")){
			assembly = DictR(fn) + " " + DictR(rs) + ";";
		}else if ( fn.equals("010000") || fn.equals("010010") || fn.equals("001000")){
			assembly = DictR(fn) + " " + DictR(rd)  + ";";
		} else if (fn.equals("011000") || fn.equals("011010") || fn.equals("011011") || fn.equals("011001")){
			assembly = DictR(fn) + " " + DictR(rs) + ", " + DictR(rt) + ";";
		} else if (fn.equals("000010") || fn.equals("000011")|| fn.equals("000000")){
			assembly = DictR(fn) + " " + DictR(rd) + ", " + DictR(rt) + ", " + DictR(sh) + ";";		
		}else
		    assembly = DictR(fn) + " " + DictR(rd) + ", " + DictR(rs) + ", " + DictR(rt) + DictR(sh) + ";";

		return assembly;
	}

	// recebe os valores separados em binário, consulta o dicionario de comandos I e retorna os devidos comandos 
	public String toAssemblyI (String op, String rs, String rt, String operand){
		int numero = Integer.parseInt(operand, 2);
		String assembly = "";
		
		if (op.equals("001111")){
			assembly = DictI(op) + " " + DictI(rt) + ", " + numero + ";";
		} else if (op.equals("000001")){
			assembly = DictI(op)+ " " + DictI(rs) + ", " + "start;";
		} else if (Integer.parseInt(op, 2) >= 32 && Integer.parseInt(op, 2) <= 43){
			assembly = DictI(op) + " " + DictI(rt) + " " +numero + "(" + DictI(rs) + ");";
		}else if ( op.equals("000101") || op.equals("000100")){
			assembly = DictI(op)+ " " + DictI(rs) + ", " + DictI(rt) + ", start;";
		}else 
		    assembly = DictI(op)+ " " + DictR(rt) + ", " + DictR(rs) + ", " + numero + ";";

		return assembly;
	}

	//recebe os valores separados em binário, consulta o dicionario de comandos J e retorna os devidos comandos 
	public String toAssemblyJ(String op, String  jta){
		String assembly = DictJ(op) + " " + "start;" ;

		return assembly;
	}

	//dicionário de comandos R a key é o codigo em binário, o value é o comando em Assembly
	public String DictR(String bin){
		//Dicionário do tipo R
		Map<String, String> dicr = new HashMap<String, String>();
		dicr.put("00001", "$1");
		dicr.put("00011","$3");
		dicr.put("00010","$2");
		dicr.put("00000", "");
		dicr.put("100000", "add");
		dicr.put("100001","addu");
		dicr.put("100100","and");
		dicr.put("011010","div");
		dicr.put("011011","divu");
		dicr.put("001000","jr");
		dicr.put("010000","mfhi");
		dicr.put("010010","mflo");
		dicr.put("011000","mult");
		dicr.put("011001","multu");
		dicr.put("100111","nor");
		dicr.put("100101","or");
		dicr.put("000000","sll");
		dicr.put("000100","sllv");
		dicr.put("101010","slt");
		dicr.put("000011","sra");
		dicr.put("000111","srav");
		dicr.put("000010","srl");
		dicr.put("000110","srlv");
		dicr.put("100010","sub");
		dicr.put("100011","subu");
		dicr.put("100110","xor");
		dicr.put("01010", "10");

		return dicr.get(bin);
	}

	//dicionário de comandos I a key é o codigo em binário, o value é o comando em Assembly
	public String DictI(String bin){
		//Dicionário do tipo I
		Map<String, String> dici = new HashMap<String, String>();
		/*Falta entender o start, já que varia de entrada para entrada
		dici.put("", "start");*/
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

	//dicionário de comandos J a key é o codigo em binário, o value é o comando em Assembly
	public String DictJ(String bin){
		//Dicionário do tipo J
		Map<String, String> dicj = new HashMap<String, String>();
		/*Falta entender o start, já que varia de entrada para entrada
		dicj.put("", "start");*/
		dicj.put("000010", "j");
		dicj.put("000011", "jal");


		return dicj.get(bin);
	}

	////dicionario com comando Syscall
	public String DictSyscall(String bin){
		//Dicionário do tipo syscall
		Map<String, String> dicsyscall = new HashMap<String, String>();
		dicsyscall.put("00000", "");
		dicsyscall.put("00000000000000000000001100", "syscall");

		return dicsyscall.get(bin);
	}

	

}
