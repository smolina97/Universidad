import java.io.File;

public class VMtranslator{
    public static void main(String args[]){
	if(args.length != 1){
	    System.out.println("Not input file\n Usage: java VMTranslator *.vm");
	}else{
	    File file =new File(args[0]);
	    File outputFile =new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".asm");
	    Writer writer = new Writer(outputFile);
	    Parser parser =new Parser(file);
	    int type = -1;
	    while(parser.moreCommands()){
		parser.advance();
		type = parser.commandType();
		if(type == Parser.ARITHMETIC){
		    writer.arithmetic(parser.arg1());
		}else if(type ==Parser.POP || type ==Parser.PUSH){
		    writer.pushPop(type, parser.arg1(), parser.arg2());
		}
	    }
	    writer.close();
	    System.out.println("File Created");
	}
    }
}
