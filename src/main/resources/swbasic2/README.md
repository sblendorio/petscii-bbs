# SWBASIC2
**Simple BASIC interpreter in Javascript**

- https://gitlab.com/konyisoft/swbasic
- https://swbasic2.konyisoft.eu/

## About

SWBASIC2 is an updated/improved version of Yohanes Nugroho's [Simple Web Basic](https://yohan.es/swbasic/) from 2010. In version **2** many major changes have been made on the original interpreter. Now it is mostly compatible with the first Altair Basic released in 1975.

- Refactored to meet newer ECMAScript standards (ES6+) so it can be easily imported into modern web application projects (Angular, React, etc.)
- Implemented some missing statements and keywords: DEF FN, ON GOTO, ON GOSUB, NOT, RESTORE, STOP
- Implemented some missing functions: ATN, EXP, LOG, POS, SGN, SPC, TAB, SPACE$
- Implemented additional language elements:
  - comma separated input
  - jumping out of FOR loops
  - no white spaces reqired in program code (except for line numbers)
  - complex IF THEN ELSE statements
  - improved PRINT with tabulators, etc.
- Fixed some parser/interpreter bugs

At this time the primary goal for SWBASIC2 is to run all the programs of [Basic Computer Games](https://www.atariarchives.org/basicgames/) without errors.

## Demo

A demo application of SWBASIC2 with some example programs can be found here: [swbasic2.konyisoft.eu](https://swbasic2.konyisoft.eu/). JQuery is not a requirement for SWBASIC2 itself, it is only used by the demo framework.

## Keywords

?, AND, CLEAR, CLS, DATA, DEF FN, DIM, ELSE, END, FOR, WIDTH, GOSUB, GOTO, IF, INPUT, LET, MOD, NEXT, NOT, ON GOTO, ON GOSUB, OR, PRINT, RANDOMIZE, READ, REM, RESTORE, RETURN, STEP, STOP, SYSTEM, THEN, TO, WHILE, WEND

## Functions

ABS, ASC, ATN, CHR$, SPACE$, COS, EXP, INSTR, INT, LEFT$, LEN, LOG, MID$, POS, RIGHT$, RND, SGN, SIN, SQR, SPC, STRING$, STR$, TAB, TAN, TIMER, VAL

## License

GNU GPLv3
****
Krisztian Konya, Konyisoft, 2019-2020  
[konyisoft.eu](https://konyisoft.eu/)