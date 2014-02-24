grammar RateGrammar;

// PARSER
program : expression;


expression
    : '(' expression ')'                # parenExpression
    | expression ('*'|'/') expression   # multOrDiv
    | expression ('+'|'-') expression   # addOrSubtractaa
    | capacity                          # placeCapacity
    | token_number                      # placeTokens
    | INT                               # integer;

capacity: 'cap(' ID ')';

token_number: '#(' ID ')';

// LEXER
ID     : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9')*;
INT    : '0'..'9'+;
WS     : [ \t\n\r]+ -> skip ;