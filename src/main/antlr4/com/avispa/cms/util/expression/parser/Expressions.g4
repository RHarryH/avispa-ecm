grammar Expressions;

// LEXER (tokens/terminals)

fragment DIGIT : [0-9];
fragment LETTER : [A-Za-z];

FUNCTION_NAME : '$' LETTER (LETTER | DIGIT)+;

COMMA             : ',';
CONCAT            : '+';
LEFT_PARENTHESIS  : '(';
RIGHT_PARENTHESIS : ')';

TEXT  : '\'' ~[\r\n']* '\'';
SPACE : [ \t\r\n] -> skip;

start : expression;

expression
    : left=expression CONCAT right=expression # ConcatExpr
    | function # FunctionExpr
    | TEXT # TextExpr
    ;

function
    : FUNCTION_NAME LEFT_PARENTHESIS params? RIGHT_PARENTHESIS
    ;

params
    : expression (COMMA expression)*
    ;
