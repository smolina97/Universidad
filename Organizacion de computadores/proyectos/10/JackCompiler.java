import java.util.*;
import java.io.*;

import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.*;

public class JackCompiler{
    /** 
     * This class provides an empty implementation of JackGrammarListener,
     * which can be extended to create a listener which only needs to handle a subset
     * of the available methods.
     */ // Generated from JackGrammar.g4 by ANTLR 4.7
    public static class JackGrammarBaseListener implements JackGrammarListener {
	String output = "";
	String outputT = "";
	String indent = "";
	public String getOutput() { return output;}	
	public String getOutputT() { return outputT;}
	public void enterClazz(JackGrammarParser.ClazzContext ctx) { output += "<class>\n"; indent += "  ";}
	public void exitClazz(JackGrammarParser.ClazzContext ctx) { indent = indent.substring(0, indent.length()-2); output += "</class>\n";
	}
	public void enterClassVarDec(JackGrammarParser.ClassVarDecContext ctx) {
	    output += indent + "<classVarDec>\n"; indent += "  ";
	}
	public void exitClassVarDec(JackGrammarParser.ClassVarDecContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</classVarDec>\n";
	}
	public void enterType(JackGrammarParser.TypeContext ctx) { }//output += indent + "<type>\n";}
	public void exitType(JackGrammarParser.TypeContext ctx) { }//output += indent + "</type>\n";}
	public void enterSubroutineDec(JackGrammarParser.SubroutineDecContext ctx) {
	    output += indent + "<subroutineDec>\n"; indent += "  ";
	}
	public void exitSubroutineDec(JackGrammarParser.SubroutineDecContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</subroutineDec>\n"; 
	}
	public void enterParameterList(JackGrammarParser.ParameterListContext ctx) {
	    output += indent + "<parameterList>\n"; indent += "  ";
	}
	public void exitParameterList(JackGrammarParser.ParameterListContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</parameterList>\n";
	}
	public void enterSubroutineBody(JackGrammarParser.SubroutineBodyContext ctx) {
	    output += indent + "<subroutineBody>\n"; indent += "  ";
	}
	public void exitSubroutineBody(JackGrammarParser.SubroutineBodyContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</subroutineBody>\n"; 
	}
	public void enterVarDec(JackGrammarParser.VarDecContext ctx) {
	    output += indent + "<varDec>\n"; indent += "  ";
	}
	public void exitVarDec(JackGrammarParser.VarDecContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</varDec>\n";
	}
	public void enterClassName(JackGrammarParser.ClassNameContext ctx) { }//output += indent + "<className>\n";}
	public void exitClassName(JackGrammarParser.ClassNameContext ctx) { }//output += indent + "</className>\n";}
	public void enterSubroutineName(JackGrammarParser.SubroutineNameContext ctx) { }//output += indent + "<subroutineName>\n";}
	public void exitSubroutineName(JackGrammarParser.SubroutineNameContext ctx) { }//output += indent + "</subroutineName>\n";}
	public void enterVarName(JackGrammarParser.VarNameContext ctx) { }//output += indent + "<varName>\n";}
	public void exitVarName(JackGrammarParser.VarNameContext ctx) { }//output += indent + "</varName>\n";}
	public void enterStatements(JackGrammarParser.StatementsContext ctx) {
	    output += indent + "<statements>\n"; indent += "  ";
	}
	public void exitStatements(JackGrammarParser.StatementsContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</statements>\n"; 
	}
	public void enterStatement(JackGrammarParser.StatementContext ctx) { }//output += indent + "<statement>\n";}
	public void exitStatement(JackGrammarParser.StatementContext ctx) { }//output += indent + "</statement>\n";}
	public void enterLetStatement(JackGrammarParser.LetStatementContext ctx) {
	    output += indent + "<letStatement>\n"; indent += "  ";
	}
	public void exitLetStatement(JackGrammarParser.LetStatementContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</letStatement>\n"; 
	}
	public void enterIfStatement(JackGrammarParser.IfStatementContext ctx) {
	    output += indent + "<ifStatement>\n"; indent += "  ";
	}
	public void exitIfStatement(JackGrammarParser.IfStatementContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</ifStatement>\n"; 
	}
	public void enterWhileStatement(JackGrammarParser.WhileStatementContext ctx) {
	    output += indent + "<whileStatement>\n"; indent += "  ";
	}
	public void exitWhileStatement(JackGrammarParser.WhileStatementContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</whileStatement>\n"; 
	}
	public void enterDoStatement(JackGrammarParser.DoStatementContext ctx) {
	    output += indent + "<doStatement>\n"; indent += "  ";
	}
	public void exitDoStatement(JackGrammarParser.DoStatementContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</doStatement>\n"; 
	}
	public void enterReturnStatement(JackGrammarParser.ReturnStatementContext ctx) {
	     output += indent + "<returnStatement>\n"; indent += "  ";
	}
	public void exitReturnStatement(JackGrammarParser.ReturnStatementContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</returnStatement>\n"; 
	}
	public void enterExpression(JackGrammarParser.ExpressionContext ctx) {
	    output += indent + "<expression>\n"; indent += "  ";
	}
	public void exitExpression(JackGrammarParser.ExpressionContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</expression>\n"; 
	}
	public void enterTerm(JackGrammarParser.TermContext ctx) {
	    output += indent + "<term>\n"; indent += "  ";
	}
	public void exitTerm(JackGrammarParser.TermContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</term>\n"; 
	}
	public void enterSubroutineCall(JackGrammarParser.SubroutineCallContext ctx) { }//output += indent + "<subroutineCall>\n";}
	public void exitSubroutineCall(JackGrammarParser.SubroutineCallContext ctx) { }//output += indent + "</subroutineCall>\n";}
	public void enterExpressionList(JackGrammarParser.ExpressionListContext ctx) {
	    output += indent + "<expressionList>\n"; indent += "  ";
	}
	public void exitExpressionList(JackGrammarParser.ExpressionListContext ctx) {
	    indent = indent.substring(0, indent.length()-2); output += indent + "</expressionList>\n"; 
	}
	public void enterOp(JackGrammarParser.OpContext ctx) { }//output += indent + "<op>\n";}
	public void exitOp(JackGrammarParser.OpContext ctx) { }//output += indent + "</op>\n";}
	public void enterUnaryOp(JackGrammarParser.UnaryOpContext ctx) {
	    // output += indent + "<unaryOp>\n"; indent += "  ";
	}
	public void exitUnaryOp(JackGrammarParser.UnaryOpContext ctx) {
	    // indent = indent.substring(0, indent.length()-2); output += indent + "</unaryOp>\n"; 
	}
	public void enterKeywordConstant(JackGrammarParser.KeywordConstantContext ctx) {
	    //output += indent + "<keywordConstant>\n"; indent += "  ";
	}
	public void exitKeywordConstant(JackGrammarParser.KeywordConstantContext ctx) {
	    //indent = indent.substring(0, indent.length()-2); output += indent + "</keyworkConstant>\n"; 
	}
	public void enterEveryRule(ParserRuleContext ctx) { }//output += "enterEveryRule ::-=>  " + ctx.getText()+ "\n";}
	public void exitEveryRule(ParserRuleContext ctx) { }//output += "exitEveryRule ::-=>  " + ctx.getText()+ "\n";}
	public void visitTerminal(TerminalNode node) {	    
	    String child = node.getText();
	    if(child.equals("class")|| child.equals("constructor")|| child.equals("function")|| child.equals("method")|| child.equals("field")|| child.equals("static")
	       || child.equals("var")|| child.equals("int")|| child.equals("char")|| child.equals("boolean")|| child.equals("void")|| child.equals("true")
	       || child.equals("false")|| child.equals("null")|| child.equals("this")|| child.equals("let")|| child.equals("do")|| child.equals("if")
	       || child.equals("else")|| child.equals("while")|| child.equals("return")){
	    	output += indent + "<keyword> " + child + " </keyword>\n";
		outputT += "<keyword> " + child + " </keyword>\n";
	    }else if(child.equals("{")|| child.equals("}")|| child.equals("(")|| child.equals(")")|| child.equals("[")|| child.equals("]")|| child.equals(".")
	    	     || child.equals(",")|| child.equals(";")|| child.equals("+")|| child.equals("-")|| child.equals("*")|| child.equals("/")|| child.equals("&")
	    	     || child.equals("|")|| child.equals("|") || child.equals("<")|| child.equals(">")|| child.equals("~")|| child.equals("=")){
		if(child.equals("<")){
		    child = "&lt;";
		}else if(child.equals(">")){
		    child = "&gt";
		}else if(child.equals("&")){
		    child = "&amp";
		}
	    	output += indent + "<symbol> " + child + " </symbol>\n";
		outputT += "<symbol> " + child + " </symbol>\n";
	    }else if(isInteger(child)){
	    	output += indent + "<integerConstant> " + child + " </integerConstant>\n";
		outputT += "<integerConstant> " + child + " </integerConstant>\n";
	    }else if(child.charAt(0) == '"' && child.charAt(child.length()-1) == '"'){
	    	output += indent + "<stringConstant> " + child.substring(1, child.length()-1) + " </stringConstant>\n";
		outputT += "<stringConstant> " + child.substring(1, child.length()-1) + " </stringConstant>\n";
	    }else{
	    	output += indent + "<identifier> " + child + " </identifier>\n";
		outputT += "<identifier> " + child + " </identifier>\n";
	    }
	}
	public void visitErrorNode(ErrorNode node) { }//output += "visitErrorNode ::-=>  "+ node.getText()+ "\n";}
	public boolean isInteger(String numero){
	    try{
		Integer.parseInt(numero);
		return true;
	    }catch(NumberFormatException e){
		return false;
	    }
	}
    }
    public static void main(String[] args) throws Exception {
	for(int i =0; i< args.length; i++){
	    JackGrammarLexer lexer = new JackGrammarLexer( new ANTLRFileStream( args[i]));
	    CommonTokenStream tokens = new CommonTokenStream( lexer );
	    JackGrammarParser parser = new JackGrammarParser( tokens );
	    ParseTree tree = parser.clazz();
	    ParseTreeWalker walker = new ParseTreeWalker();
	    JackGrammarBaseListener Listener = new JackGrammarBaseListener();
	    walker.walk(Listener, tree);
	    String output = Listener.getOutput();
	    String outputT = Listener.getOutputT();
	    BufferedWriter writer = new BufferedWriter(new FileWriter(args[i].replaceAll(".jack", ".xml")));
	    BufferedWriter writerT = new BufferedWriter(new FileWriter(args[i].replaceAll(".jack", "T.xml")));
	    writer.write(output);
	    writerT.write("<tokens>\n" + outputT + "</tokens>");     
	    writer.close();
	    writerT.close();
	    
	}
    }
}
