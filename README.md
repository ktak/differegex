# differegex
differegex is a regular expression engine and lexer based on the paper
*regular expression derivatives reexamined* by Owens, Reppy, and Turon.
the central idea is the definition of a derivative operation on a regular
expression with respect to the next character of the input, which results in
another regular expression that matches the remainder of the strings of the
original regular expression that began with that character. there is also a
similarity relation defined on regular expressions under which each regular
expression has only a finite number of dissimilar derivatives. this allows the
construction of a finite state machine by finding the fixed point of the set of
derivatives of a regular expression then mapping each regular expression in
this set to a state. for an example of how to construct a finite state machine
and use it for lexing see
[test/ktak/differegex/LexerTest.java](https://github.com/ktak/differegex/blob/master/test/ktak/differegex/LexerTest.java).
the only library that this project depends on is
[immutable-java](https://github.com/ktak/immutable-java)
