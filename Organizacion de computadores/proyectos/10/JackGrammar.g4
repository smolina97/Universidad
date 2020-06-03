//Define a grammar for Jack Language
grammar JackGrammar;
//Lexical elements:
//The Jack language include five types of terminal elements(tokens)
KEYWORD :   'class'|'constructor'|'function'|'method'|'field'|'static'
    |'var'|'int'|'char'|'boolean'|'void'|'true'|'false'|'null'|'this'
    |'let'|'do'|'if'|'else'|'while'|'return' ;
SYMBOL  :   '{'|'}'|'('|')'|'['|']'|'.'|','|';'|'+'|'-'|'*'|'/'|'&'|'|'
    |'<'|'>'|'='|'~' ;
INTEGER : '0' | ('1'..'9')('0'..'9')* ; //Lexical Elements In Uppercase
STRING  :   '"'(~('"'|'\n'|'\r'))*'"'|'"' ~'"' '"' ;
ID  :   ([a-zA-Z][a-zA-Z0-9_]*) ;
//Program structure:
//A Jack Program is a collection of classes, each appearing in a separate file.
//The compilation unit is a class. A class is a sequence of tokens strucured
//according to the following context free syntax:
clazz   :   'class' className '{' classVarDec* subroutineDec* '}' ;
classVarDec :   ('static'|'field') type varName (','varName)* ';' ;
type    :   'int'|'char'|'boolean'|className ;
subroutineDec   :   ('constructor'|'function'|'method') ('void'|type) subroutineName
        '('parameterList')'subroutineBody ;
parameterList   :   ((type varName)(','type varName)*)? ;
subroutineBody  :   '{'varDec* statements'}' ;
varDec  :   'var' type varName (',' varName)* ';' ;
className   : ID ;
subroutineName  : ID ;
varName :ID ;
//Statements:
statements  :   statement* ;
statement  :   letStatement|ifStatement|whileStatement|doStatement|returnStatement ;
letStatement    :   'let' varName ('['expression']')?'='expression';' ;
ifStatement :   'if' '(' expression ')' '{' statements '}' ('else''{'statements'}')? ;
whileStatement  :   'while' '(' expression ')' '{' statements '}' ;
doStatement :   'do' subroutineCall ';' ;
returnStatement :   'return' expression?';' ;
//Expressions:
expression  :   term (op term)* ;
term    :   INTEGER|STRING|keywordConstant|varName
    |varName '[' expression ']'|subroutineCall|'('expression')'|unaryOp term ;
subroutineCall  :   subroutineName '(' expressionList ')'|(className|varName)'.'subroutineName'('expressionList')' ;
expressionList  :   (expression(','expression)*)? ;
op  :   '+'|'-'|'*'|'/'|'&'|'|'|'<'|'>'|'=' ;
unaryOp :   '-'|'~' ;
keywordConstant : 'true'|'false'|'null'|'this' ;
WS  :   (' '|'\t'|'\r'|'\n'|'\f') -> skip ;
LINE_COMMENT    :   ('//' ~( '\r' | '\n' )*) -> skip ;
COMMENT    :   ('/*' .*? '*/') -> skip ;
PARAGRAPH_COMMENTS    :   ('/**' .*? '*/') -> skip ;
