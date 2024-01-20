lexer grammar ExpressionLexer;

FUNCTION_HEADER : '$' LETTER (LETTER | DIGIT)* '(' -> pushMode(FUNCTION_CONTEXT);

ESCAPE: '\\' [$];
PLAIN_TEXT : .;

mode FUNCTION_CONTEXT;

TEXT : '\'' PARAM_VALUE* '\'';

COMMA : ',';
CONCAT : '+';
RIGHT_PARENTHESIS : ')' -> popMode;

INNER_FUNCTION_HEADER : '$' LETTER (LETTER | DIGIT)* '(' -> type(FUNCTION_HEADER), pushMode(FUNCTION_CONTEXT);
INNER_PLAIN_TEXT : . -> skip;

fragment DIGIT : [0-9];
fragment LETTER : [A-Za-z];

fragment APOSTROPHE_ESCAPE : '\\' '\'';
fragment STRING : ~['];
fragment PARAM_VALUE: APOSTROPHE_ESCAPE | STRING;