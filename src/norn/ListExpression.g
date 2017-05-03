/* Copyright (c) 2015-2017 MIT 6.005/6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

@skip whitespace {
    root ::= sequence;
    sequence ::= definition (';' definition)*;
    definition ::= (listname '=')? union;
    union ::= difference (',' difference)*;
    difference ::= intersection ('!' intersection)*;
    intersection ::= primary ('*' primary)*;
    primary ::= listname | address | '(' sequence ')';
}
address ::= ([A-Za-z0-9_\-\.]+[@][A-Za-z0-9_\-\.]+)?;
whitespace ::= [ \t\r\n]+; 
listname ::= [A-Za-z0-9_\-\.]+;
