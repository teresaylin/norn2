# norn2
Copyright (c) 2007-2017 MIT 6.005/6.031 course staff, all rights reserved. Redistribution of original or derived work requires permission of course staff.

#### About

This is a mailing list application for processing and analyzing email list expressions. It supports email list expressions inputted from the console as well as from the web. 

#### Valid List Expressions

List expressions are sets of recipients who should receive email messages. A list expression may be an expression, a sequence of list expressions, a list name (set of recipients of a mailing list), an email address (single recipient), or an empty string.
 
Expressions use the following operators: 
- , (union)
- ! (difference)
- \* (intersection)
- = (definition)
- ; (sequencing)
 
The order of operations in descending order is: '*', '!', ',', '=', and ';'. The user may also nest subexpressions in parentheses. 

A list expression may define a list name: ```listname = e``` defines listname as the expression ```e``` and returns the set of recipients of ```e```. List names are nonempty case-insensitive strings of letters, digits, underscores, dashes, and periods. 

Sequencing of list expressions or list definitions:
- Ex: ```x = a@mit.edu,b@mit.edu ; x * b@mit.edu```. After substituting for x in the second part of the expression, this expression is equivalent to ```(a@mit.edu,b@mit.edu) * b@mit.edu```, which represents the single recipient b@mit.edu.

The output of this console is an order-independent view of the emails specified in an email list expression.
 
Lastly, the user can use the commands ```!save``` or ```!load``` to save currently defined named lists to a file or load a saved file. Saved files contain a single valid list expression (a sequence of list definitions). A user can specify multiple files in the same ```!load``` command but must separate the files by a comma.
 
If a filename does not contain a valid list expression (during loading), or the file is unreadable (during saving), then a readable error is returned.

If given no input, this console proceeds to wait until valid input is given.