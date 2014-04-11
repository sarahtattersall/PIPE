---
layout: post
title: Functional Rates
post-id: functional
---

Supporting marking-dependent rates helps create more expressive Petri nets whilst keeping the design simple. Arc weights and transition rates can now be expressed in terms of other components. Functional rates have the following grammar:

```
   program            ::= expression;
   expression         ::= '(' expression ')' |
                           expression * expression | 
                           expression / expression | 
                           expression + expression | 
                           'ceil(' expression ')'  | 
                           'floor(' expression ')' |
                           capacity                |
                           token_number            |
                           token_color_number      |
                           INT                     |
                           DOUBLE;                 
   capacity           ::= 'cap(' ID ')';
   token_number       ::= '#(' ID ')';
   token_color_number ::= '#(' ID ',' ID ')';

``` 
Where an ID refers to a place name, or in the case of `token_color_number` a place name followed by a token name. 

#### Examples ####
Examples of functional weights using the grammar are given in the table below. 


| Expression        | Meaning                                       | 
| ------------------|-----------------------------------------------| 
| `#(P0)`           | the sum of all tokens in P0                   |
| `#(P0, Default)`  | the number of Default tokens in P0            | 
| `#(P1, Red)*10`   | the number of red tokens in P1 multiplied by 10 |
| `floor(10.5/3)`   | the floor of 10.5/3 i.e. 3                    |
| `ceil(cap(P5) * 2.5)` | the ceiling of the capacity (max number of tokens allowed) in P5 multiplied by 2.5 | 
| `#(P0) + #(P2)`   | the sum of the total number of tokens in P1 and P2 |