import java.util.IllegalFormatException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Parser{
    private Scanner commands;
    public static final int ARITHMETIC =0, PUSH =1, POP =2;
    public static final int LABEL =3, GOTO =4, IF =5;
    public static final int FUNCTION =6, RETURN =7, CALL =8;
    private String argt1, currentCmd;
    private int argType, argt2;
    public static final ArrayList<String> arithmeticCmds =new ArrayList<String>();
    static {
	arithmeticCmds.add("add");arithmeticCmds.add("sub");arithmeticCmds.add("neg");
	arithmeticCmds.add("eq");arithmeticCmds.add("gt");arithmeticCmds.add("lt");
	arithmeticCmds.add("and");arithmeticCmds.add("or");arithmeticCmds.add("not");
    }
    public Parser(File inputFile){
	//argType = -1;	argt1 = "";	argt2 = -1;
	try{
	    commands = new Scanner(inputFile);
	    String lineCode ="", codes ="";	    
	    while(commands.hasNext()){
		lineCode = comment(commands.nextLine()).trim();
		if(lineCode.length() > 0){
		    codes +=lineCode +"\n";
		}
	    }
	    commands = new Scanner(codes.trim());
	}catch(FileNotFoundException e){
	    System.out.println("File not found");
	}
    }
    public String comment(String line){
	int pos = line.indexOf("//");
	if (pos != -1){
	    line = line.substring(0, pos);
	}
	return line;
    }
    public boolean moreCommands(){
	return commands.hasNextLine();
    }
    public void advance(){
	currentCmd = commands.nextLine();
	argt1 ="";
	argt2 =-1;
	String[] lineParts =currentCmd.split(" ");
	if(lineParts.length >3){
	    throw new IllegalArgumentException("Too much arguments");
	}
	if(arithmeticCmds.contains(lineParts[0])){
	    argType =ARITHMETIC;
	    argt1 =lineParts[0];
	}else if(lineParts[0].equals("return")){
	    argType = RETURN;
	    argt1 =lineParts[0];
	}else{
	    argt1 =lineParts[1];
	    if(lineParts[0].equals("push")){
		argType =PUSH;
	    }else if(lineParts[0].equals("pop")){
		argType =POP;
	    }else if(lineParts[0].equals("label")){
		argType =LABEL;
	    }else if(lineParts[0].equals("if-goto")){
		argType =IF;
	    }else if(lineParts[0].equals("goto")){
		argType =GOTO;
	    }else if(lineParts[0].equals("function")){
		argType =FUNCTION;
	    }else if(lineParts[0].equals("call")){
		argType =CALL;
	    }else{
		throw new IllegalArgumentException("Unknown Command Type");
	    }
	    if(argType ==PUSH || argType ==POP || argType ==FUNCTION || argType ==CALL){
		try {
		    argt2 = Integer.parseInt(lineParts[2]);		 
		}catch (Exception e){
		    throw new IllegalArgumentException("Argument2 is not an integer");
		}
	    }
	}
    }
    public int commandType(){
	return argType;
    }
    public String arg1(){
	return argt1;	
    }
    public int arg2(){
	return argt2;
    }
}
