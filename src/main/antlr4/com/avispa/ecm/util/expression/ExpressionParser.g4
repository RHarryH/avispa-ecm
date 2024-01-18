parser grammar ExpressionParser;

options {
  tokenVocab=ExpressionLexer;
}

start
    : subString*
    ;

subString
    : function
    | PLAIN_TEXT+
    | ESCAPE;

function
    : FUNCTION_NAME LEFT_PARENTHESIS params? RIGHT_PARENTHESIS
    ;

params
    : expression (COMMA expression)*
    ;

expression
    : TEXT  # TextExpr
    | function # FunctionExpr
    | left=expression CONCAT right=expression # ConcatExpr
    ;