grammar RateGrammar;

// PARSER
program : expression;


expression
    : '(' expression ')'                   # parenExpression
    | expression op=('*'|'/') expression   # multOrDiv
    | expression op=('+'|'-') expression   # addOrSubtract
    | 'ceil(' expression ')'               # ceil
    | 'floor(' expression ')'              # floor
    | capacity                             # placeCapacity
    | token_number                         # placeTokens
    | token_color_number                   # placeColorTokens
    | INT                                  # integer
    | DOUBLE                               # double;

// Probably dont need this
capacity: 'cap(' ID ')';

token_number: '#(' ID ')';

token_color_number: '#(' ID ',' ID ')';

// LEXER
ID     : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*;
INT    : '0'..'9'+;
DOUBLE  : '0'..'9'+ '.' '0'..'9'+;
WS     : [ \t\n\r]+ -> skip ;

MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';