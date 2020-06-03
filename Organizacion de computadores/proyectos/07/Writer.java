import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;

public class Writer{
    private PrintWriter printerOutput;
    private int jumpFlags;
    public Writer(File outputFile){
	try{
	    printerOutput =new PrintWriter(outputFile);
	    jumpFlags =0;
	}catch(FileNotFoundException e){
	    e.printStackTrace();
	}
    }
    public void arithmetic(String command){
	if (command.equals("add")){
	    printerOutput.print(arithmeticTemplate1() + "M=M+D\n");
	}else if(command.equals("sub")){
	    printerOutput.print(arithmeticTemplate1() + "M=M-D\n");
	}else if(command.equals("and")){
	    printerOutput.print(arithmeticTemplate1() + "M=M&D\n");
	}else if(command.equals("or")){
	    printerOutput.print(arithmeticTemplate1() + "M=M|D\n");
	}else if(command.equals("gt")){
	    printerOutput.print(arithmeticTemplate2("JLE"));
	    jumpFlags++;
	}else if(command.equals("lt")){
	    printerOutput.print(arithmeticTemplate2("JGE"));
	    jumpFlags++;
	}else if(command.equals("eq")){
	    printerOutput.print(arithmeticTemplate2("JNE"));
	    jumpFlags++;
	}else if(command.equals("not")){
	    printerOutput.print("@SP\nA=M-1\nM=!M\n");
	}else if(command.equals("neg")){
	    printerOutput.print("D=0\n@SP\nA=M-1\nM=D-M\n");
	}else{
	    throw new IllegalArgumentException("It is not an arithmetic command");
	}
    }
    public void pushPop(int command, String segment, int index){
	if(command ==Parser.PUSH){
	    if(segment.equals("constant")){
		printerOutput.print("@"+index+"\n"+"D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
	    }else if(segment.equals("local")){
		printerOutput.print(pushTemplate1("LCL",index,false));
	    }else if(segment.equals("argument")){
		printerOutput.print(pushTemplate1("ARG",index,false));
	    }else if(segment.equals("this")){
		printerOutput.print(pushTemplate1("THIS",index,false));
	    }else if(segment.equals("that")){
		printerOutput.print(pushTemplate1("THAT",index,false));
	    }else if(segment.equals("temp")){
		printerOutput.print(pushTemplate1("R5", index + 5,false));
	    }else if(segment.equals("pointer") && index ==0){
		printerOutput.print(pushTemplate1("THIS",index,true));
	    }else if(segment.equals("pointer") && index ==1){
		printerOutput.print(pushTemplate1("THAT",index,true));
	    }else if(segment.equals("static")){
		printerOutput.print(pushTemplate1(String.valueOf(16 + index),index,true));
	    }
	}else if(command ==Parser.POP){
	    if(segment.equals("local")){
		printerOutput.print(popTemplate1("LCL",index,false));
	    }else if(segment.equals("argument")){
		printerOutput.print(popTemplate1("ARG",index,false));
	    }else if(segment.equals("this")){
		printerOutput.print(popTemplate1("THIS",index,false));
	    }else if(segment.equals("that")){
		printerOutput.print(popTemplate1("THAT",index,false));
	    }else if(segment.equals("temp")){
		printerOutput.print(popTemplate1("R5", index + 5,false));
	    }else if(segment.equals("pointer") && index ==0){
		printerOutput.print(popTemplate1("THIS",index,true));
	    }else if(segment.equals("pointer") && index ==1){
		printerOutput.print(popTemplate1("THAT",index,true));
	    }else if(segment.equals("static")){
		printerOutput.print(popTemplate1(String.valueOf(16 +index),index,true));
	    }
	}else {
	    throw new IllegalArgumentException("It is not a push or pop command");
	}
    }
    public void close(){
	printerOutput.close();
    }
    private String arithmeticTemplate1(){
	return "@SP\n"+"AM=M-1\n"+"D=M\n"+"A=A-1\n";
    }
    private String arithmeticTemplate2(String type){
	return "@SP\n"+"AM=M-1\n"+"D=M\n"+"A=A-1\n"+"D=M-D\n"+"@FALSE"+
	    jumpFlags+"\n"+"D;"+type+"\n"+"@SP\n"+"A=M-1\n"+"M=-1\n"+
	    "@CONTINUE"+jumpFlags+"\n"+"0;JMP\n"+"(FALSE"+jumpFlags+
	    ")\n"+"@SP\n"+"A=M-1\n"+"M=0\n"+"(CONTINUE"+jumpFlags+")\n";
    }
    private String pushTemplate1(String segment, int index, boolean isDirect){
	String noPointerCode =(isDirect)?"":"@"+index+"\n"+"A=D+A\nD=M\n";
	return "@"+segment+"\n"+"D=M\n"+noPointerCode +"@SP\n"+
	    "A=M\n"+"M=D\n"+"@SP\n"+"M=M+1\n";
    }
    private String popTemplate1(String segment, int index, boolean isDirect){
	String noPointerCode =(isDirect)?"D=A\n":"D=M\n@"+index+"\nD=D+A\n";
	return "@"+segment+"\n"+noPointerCode+"@R13\n"+"M=D\n"+"@SP\n" +
	    "AM=M-1\n"+"D=M\n"+"@R13\n"+"A=M\n"+"M=D\n";
    }
}
