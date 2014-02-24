grammar RateGrammar;

// PARSER
program : expression;


expression
    : '(' expression ')'                   # parenExpression
    | expression op=('*'|'/') expression   # multOrDiv
    | expression op=('+'|'-') expression   # addOrSubtract
    | capacity                             # placeCapacity
    | token_number                         # placeTokens
    | INT                                  # integer
    | DOUBLE                               # double;

capacity: 'cap(' ID ')';

token_number: '#(' ID ')';

// LEXER
ID     : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*;
INT    : '0'..'9'+;
DOUBLE  : '0'..'9'+ '.' '0'..'9'+;
WS     : [ \t\n\r]+ -> skip ;

MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';