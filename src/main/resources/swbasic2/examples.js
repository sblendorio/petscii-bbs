const SOURCE_WELCOME =
`10 REM Example
20 LOCATE 5,10
30 END`;

const SOURCE_BAGELS =
`5 PRINT TAB(33);"BAGELS"
10 PRINT TAB(15);"CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY":PRINT:PRINT
15 REM *** BAGLES NUMBER GUESSING GAME
20 REM *** ORIGINAL SOURCE UNKNOWN BUT SUSPECTED TO BE
25 REM *** LAWRENCE HALL OF SCIENCE, U.C. BERKELY
30 DIM A1(6),A(3),B(3)
40 Y=0:T=255
50 PRINT:PRINT:PRINT
70 INPUT "WOULD YOU LIKE THE RULES (YES OR NO)";A$
90 IF LEFT$(A$,1)="N" THEN 150
100 PRINT:PRINT "I AM THINKING OF A THREE-DIGIT NUMBER.  TRY TO GUESS"
110 PRINT "MY NUMBER AND I WILL GIVE YOU CLUES AS FOLLOWS:"
120 PRINT "   PICO   - ONE DIGIT CORRECT BUT IN THE WRONG POSITION"
130 PRINT "   FERMI  - ONE DIGIT CORRECT AND IN THE RIGHT POSITION"
140 PRINT "   BAGELS - NO DIGITS CORRECT"
150 FOR I=1 TO 3
160 A(I)=INT(9*RND(1))+1
165 IF I-1=0 THEN 200
170 FOR J=1 TO I-1
180 IF A(I)=A(J) THEN 160
190 NEXT J
200 NEXT I
210 PRINT:PRINT "O.K.  I HAVE A NUMBER IN MIND."
220 FOR I=1 TO 20
230 PRINT "GUESS #";I,
240 INPUT A$
245 IF LEN(A$)<>3 THEN 630
250 FOR Z=1 TO 3:A1(Z)=ASC(MID$(A$,Z,1)):NEXT Z
260 FOR J=1 TO 3
270 IF A1(J)<48 THEN 300
280 IF A1(J)>57 THEN 300
285 B(J)=A1(J)-48
290 NEXT J
295 GOTO 320
300 PRINT "WHAT?"
310 GOTO 230
320 IF B(1)=B(2) THEN 650
330 IF B(2)=B(3) THEN 650
340 IF B(3)=B(1) THEN 650
350 C=0:D=0
360 FOR J=1 TO 2
370 IF A(J)<>B(J+1) THEN 390
380 C=C+1
390 IF A(J+1)<>B(J) THEN 410
400 C=C+1
410 NEXT J
420 IF A(1)<>B(3) THEN 440
430 C=C+1
440 IF A(3)<>B(1) THEN 460
450 C=C+1
460 FOR J=1 TO 3
470 IF A(J)<>B(J) THEN 490
480 D=D+1
490 NEXT J
500 IF D=3 THEN 680
505 IF C=0 THEN 545
520 FOR J=1 TO C
530 PRINT "PICO ";
540 NEXT J
545 IF D=0 THEN 580
550 FOR J=1 TO D
560 PRINT "FERMI ";
570 NEXT J
580 IF C+D<>0 THEN 600
590 PRINT "BAGELS";
600 PRINT
605 NEXT I
610 PRINT "OH WELL."
615 PRINT "THAT'S TWNETY GUESSES.  MY NUMBER WAS";100*A(1)+10*A(2)+A(3)
620 GOTO 700
630 PRINT "TRY GUESSING A THREE-DIGIT NUMBER.":GOTO 230
650 PRINT "OH, I FORGOT TO TELL YOU THAT THE NUMBER I HAVE IN MIND"
660 PRINT "HAS NO TWO DIGITS THE SAME.":GOTO 230
680 PRINT "YOU GOT IT!!!":PRINT
690 Y=Y+1
700 INPUT "PLAY AGAIN (YES OR NO)";A$
720 IF LEFT$(A$,1)="YES" THEN 150
730 IF Y=0 THEN 750
740 PRINT:PRINT "A";Y;"POINT BAGELS BUFF!!"
750 PRINT "HOPE YOU HAD FUN.  BYE."
999 END`;

const SOURCE_HAMURABI =
`10 PRINT TAB(32);"HAMURABI"
20 PRINT TAB(15);"CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY"
30 PRINT:PRINT:PRINT
80 PRINT "TRY YOUR HAND AT GOVERNING ANCIENT SUMERIA"
90 PRINT "FOR A TEN-YEAR TERM OF OFFICE.":PRINT
95 D1=0: P1=0
100 Z=0: P=95:S=2800: H=3000: E=H-S
110 Y=3: A=H/Y: I=5: Q=1
210 D=0
215 PRINT:PRINT:PRINT "HAMURABI:  I BEG TO REPORT TO YOU,": Z=Z+1
217 PRINT "IN YEAR";Z;",";D;"PEOPLE STARVED,";I;"CAME TO THE CITY,"
218 P=P+I
227 IF Q>0 THEN 230
228 P=INT(P/2)
229 PRINT "A HORRIBLE PLAGUE STRUCK!  HALF THE PEOPLE DIED."
230 PRINT "POPULATION IS NOW";P
232 PRINT "THE CITY NOW OWNS ";A;"ACRES."
235 PRINT "YOU HARVESTED";Y;"BUSHELS PER ACRE."
250 PRINT "THE RATS ATE";E;"BUSHELS."
260 PRINT "YOU NOW HAVE ";S;"BUSHELS IN STORE.": PRINT
270 IF Z=11 THEN 860
310 C=INT(10*RND(1)): Y=C+17
312 PRINT "LAND IS TRADING AT";Y;"BUSHELS PER ACRE."
320 PRINT "HOW MANY ACRES DO YOU WISH TO BUY";
321 INPUT Q: IF Q<0 THEN 850
322 IF Y*Q<=S THEN 330
323 GOSUB 710
324 GOTO 320
330 IF Q=0 THEN 340
331 A=A+Q: S=S-Y*Q: C=0
334 GOTO 400
340 PRINT "HOW MANY ACRES DO YOU WISH TO SELL";
341 INPUT Q: IF Q<0 THEN 850
342 IF Q<A THEN 350
343 GOSUB 720
344 GOTO 340
350 A=A-Q: S=S+Y*Q: C=0
400 PRINT
410 PRINT "HOW MANY BUSHELS DO YOU WISH TO FEED YOUR PEOPLE";
411 INPUT Q
412 IF Q<0 THEN 850
418 REM *** TRYING TO USE MORE GRAIN THAN IS IN SILOS?
420 IF Q<=S THEN 430
421 GOSUB 710
422 GOTO 410
430 S=S-Q: C=1: PRINT
440 PRINT "HOW MANY ACRES DO YOU WISH TO PLANT WITH SEED";
441 INPUT D: IF D=0 THEN 511
442 IF D<0 THEN 850
444 REM *** TRYING TO PLANT MORE ACRES THAN YOU OWN?
445 IF D<=A THEN 450
446 GOSUB 720
447 GOTO 440
449 REM *** ENOUGH GRAIN FOR SEED?
450 IF INT(D/2)<=S THEN 455
452 GOSUB 710
453 GOTO 440
454 REM *** ENOUGH PEOPLE TO TEND THE CROPS?
455 IF D<10*P THEN 510
460 PRINT "BUT YOU HAVE ONLY";P;"PEOPLE TO TEND THE FIELDS!  NOW THEN,"
470 GOTO 440
510 S=S-INT(D/2)
511 GOSUB 800
512 REM *** A BOUNTIFUL HARVEST!
515 Y=C: H=D*Y: E=0
521 GOSUB 800
522 IF INT(C/2)<>C/2 THEN 530
523 REM *** RATS ARE RUNNING WILD!!
525 E=INT(S/C)
530 S=S-E+H
531 GOSUB 800
532 REM *** LET'S HAVE SOME BABIES
533 I=INT(C*(20*A+S)/P/100+1)
539 REM *** HOW MANY PEOPLE HAD FULL TUMMIES?
540 C=INT(Q/20)
541 REM *** HORROS, A 15% CHANCE OF PLAGUE
542 Q=INT(10*(2*RND(1)-.3))
550 IF P<C THEN 210
551 REM *** STARVE ENOUGH FOR IMPEACHMENT?
552 D=P-C: IF D>.45*P THEN 560
553 P1=((Z-1)*P1+D*100/P)/Z
555 P=C: D1=D1+D: GOTO 215
560 PRINT: PRINT "YOU STARVED";D;"PEOPLE IN ONE YEAR!!!"
565 PRINT "DUE TO THIS EXTREME MISMANAGEMENT YOU HAVE NOT ONLY"
566 PRINT "BEEN IMPEACHED AND THROWN OUT OF OFFICE BUT YOU HAVE"
567 PRINT "ALSO BEEN DECLARED NATIONAL FINK!!!!": GOTO 990
710 PRINT "HAMURABI:  THINK AGAIN.  YOU HAVE ONLY"
711 PRINT S;"BUSHELS OF GRAIN.  NOW THEN,"
712 RETURN
720 PRINT "HAMURABI:  THINK AGAIN.  YOU OWN ONLY";A;"ACRES.  NOW THEN,"
730 RETURN
800 C=INT(RND(1)*5)+1
801 RETURN
850 PRINT: PRINT "HAMURABI:  I CANNOT DO WHAT YOU WISH."
855 PRINT "GET YOURSELF ANOTHER STEWARD!!!!!"
857 GOTO 990
860 PRINT "IN YOUR 10-YEAR TERM OF OFFICE,";P1;"PERCENT OF THE"
862 PRINT "POPULATION STARVED PER YEAR ON THE AVERAGE, I.E. A TOTAL OF"
865 PRINT D1;"PEOPLE DIED!!": L=A/P
870 PRINT "YOU STARTED WITH 10 ACRES PER PERSON AND ENDED WITH"
875 PRINT L;"ACRES PER PERSON.": PRINT
880 IF P1>33 THEN 565
885 IF L<7 THEN 565
890 IF P1>10 THEN 940
892 IF L<9 THEN 940
895 IF P1>3 THEN 960
896 IF L<10 THEN 960
900 PRINT "A FANTASTIC PERFORMANCE!!!  CHARLEMANGE, DISRAELI, AND"
905 PRINT "JEFFERSON COMBINED COULD NOT HAVE DONE BETTER!":GOTO 990
940 PRINT "YOUR HEAVY-HANDED PERFORMANCE SMACKS OF NERO AND IVAN IV."
945 PRINT "THE PEOPLE (REMIANING) FIND YOU AN UNPLEASANT RULER, AND,"
950 PRINT "FRANKLY, HATE YOUR GUTS!!":GOTO 990
960 PRINT "YOUR PERFORMANCE COULD HAVE BEEN SOMEWHAT BETTER, BUT"
965 PRINT "REALLY WASN'T TOO BAD AT ALL. ";INT(P*.8*RND(1));"PEOPLE"
970 PRINT "WOULD DEARLY LIKE TO SEE YOU ASSASSINATED BUT WE ALL HAVE OUR"
975 PRINT "TRIVIAL PROBLEMS."
990 PRINT: FOR N=1 TO 10: PRINT CHR$(7);: NEXT N
995 PRINT "SO LONG FOR NOW.": PRINT
999 END`;

const SOURCE_CHOMP =
`10 PRINT TAB(33);"CHOMP"
20 PRINT TAB(15);"CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY"
30 PRINT:PRINT:PRINT
40 DIM A(10,10)
100 REM *** THE GAME OF CHOMP *** COPYRIGHT PCC 1973 ***
110 PRINT
120 PRINT "THIS IS THE GAME OF CHOMP (SCIENTIFIC AMERICAN, JAN 1973)"
130 PRINT "DO YOU WANT THE RULES (1=YES, 0=NO!)";
140 INPUT R
150 IF R=0 THEN 340
160 F=1
170 R=5
180 C=7
190 PRINT "CHOMP IS FOR 1 OR MORE PLAYERS (HUMANS ONLY)."
200 PRINT
210 PRINT "HERE'S HOW A BOARD LOOKS (THIS ONE IS 5 BY 7):"
220 GOSUB 540
230 PRINT
240 PRINT "THE BOARD IS A BIG COOKIE - R ROWS HIGH AND C COLUMNS"
250 PRINT "WIDE. YOU INPUT R AND C AT THE START. IN THE UPPER LEFT"
260 PRINT "CORNER OF THE COOKIE IS A POISON SQUARE (P). THE ONE WHO"
270 PRINT "CHOMPS THE POISON SQUARE LOSES. TO TAKE A CHOMP, TYPE THE"
280 PRINT "ROW AND COLUMN OF ONE OF THE SQUARES ON THE COOKIE."
290 PRINT "ALL OF THE SQUARES BELOW AND TO THE RIGHT OF THAT SQUARE"
300 PRINT "(INCLUDING THAT SQUARE, TOO) DISAPPEAR -- CHOMP!!"
310 PRINT "NO FAIR CHOMPING SQUARES THAT HAVE ALREADY BEEN CHOMPED,"
320 PRINT "OR THAT ARE OUTSIDE THE ORIGINAL DIMENSIONS OF THE COOKIE."
330 PRINT
340 PRINT "HERE WE GO..."
350 REM
360 F=0
370 FOR I=1 TO 10
372 FOR J=1 TO 10
375 A(I,J)=0
377 NEXT J
379 NEXT I
380 PRINT
390 PRINT "HOW MANY PLAYERS";
400 INPUT P
410 I1=0
420 PRINT "HOW MANY ROWS";
430 INPUT R
440 IF R <= 9 THEN 470
450 PRINT "TOO MANY ROWS (9 IS MAXIMUM). NOW, ";
460 GOTO 420
470 PRINT "HOW MANY COLUMNS";
480 INPUT C
490 IF C <= 9 THEN 530
500 PRINT "TOO MANY COLUMNS (9 IS MAXIMUM). NOW, ";
510 GOTO 470
530 PRINT
540 FOR I=1 TO R
550 FOR J=1 TO C
560 A(I,J)=1
570 NEXT J
580 NEXT I
590 A(1,1)=-1
600 REM PRINT THE BOARD
610 PRINT
620 PRINT TAB(7);"1 2 3 4 5 6 7 8 9"
630 FOR I=1 TO R
640 PRINT I;TAB(7);
650 FOR J=1 TO C
660 IF A(I,J)=-1 THEN 700
670 IF A(I,J)=0 THEN 720
680 PRINT "* ";
690 GOTO 710
700 PRINT "P ";
710 NEXT J
720 PRINT
730 NEXT I
740 PRINT
750 IF F=0 THEN 770
760 RETURN
770 REM GET CHOMPS FOR EACH PLAYER IN TURN
780 LET I1=I1+1
790 LET P1=I1-INT(I1/P)*P
800 IF P1 <> 0 THEN 820
810 P1=P
820 PRINT "PLAYER";P1
830 PRINT "COORDINATES OF CHOMP (ROW,COLUMN)";
840 INPUT R1,C1
850 IF R1<1 THEN 920
860 IF R1>R THEN 920
870 IF C1<1 THEN 920
880 IF C1>C THEN 920
890 IF A(R1,C1)=0 THEN 920
900 IF A(R1,C1)=-1 THEN 1010
910 GOTO 940
920 PRINT "NO FAIR. YOU'RE TRYING TO CHOMP ON EMPTY SPACE!"
930 GOTO 820
940 FOR I=R1 TO R
950 FOR J=C1 TO C
960 A(I,J)=0
970 NEXT J
980 NEXT I
990 GOTO 610
1000 REM END OF GAME DETECTED IN LINE 900
1010 PRINT "YOU LOSE, PLAYER";P1
1020 PRINT
1030 PRINT "AGAIN (1=YES, 0=NO!)";
1040 INPUT R$
1050 IF R=1 THEN 340
1060 END`;

const SOURCE_RSP =
`10 PRINT TAB(21);"GAME OF ROCK, SCISSORS, PAPER"
20 PRINT TAB(15);"CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY"
25 PRINT:PRINT:PRINT
30 INPUT "HOW MANY GAMES";Q
40 IF Q<11 THEN 60
50 PRINT "SORRY, BUT WE AREN'T ALLOWED TO PLAY THAT MANY.": GOTO 30
60 FOR G=1 TO Q
70 PRINT: PRINT "GAME NUMBER";G
80 X=INT(RND(1)*3+1)
90 PRINT "3=ROCK...2=SCISSORS...1=PAPER"
100 INPUT "1...2...3...WHAT'S YOUR CHOICE";K
110 IF (K-1)*(K-2)*(K-3)<>0 THEN PRINT "INVALID.": GOTO 90
120 PRINT "THIS IS MY CHOICE..."
130 ON X GOTO 140,150,160
140 PRINT "...PAPER": GOTO 170
150 PRINT "...SCISSORS": GOTO 170
160 PRINT "...ROCK"
170 IF X=K THEN 250
180 IF X>K THEN 230
190 IF X=1 THEN 210
200 PRINT "YOU WIN!!!":H=H+1: GOTO 260
210 IF K<>3 THEN 200
220 PRINT "WOW!  I WIN!!!":C=C+1:GOTO 260
230 IF K<>1 OR X<>3 THEN 220
240 GOTO 200
250 PRINT "TIE GAME.  NO WINNER."
260 NEXT G
270 PRINT: PRINT "HERE IS THE FINAL GAME SCORE:"
280 PRINT "I HAVE WON";C;"GAME(S)."
290 PRINT "YOU HAVE WON";H;"GAME(S)."
300 PRINT "AND";Q-(C+H);"GAME(S) ENDED IN A TIE."
310 PRINT: PRINT "THANKS FOR PLAYING!!"
320 END`;

const SOURCE_BUNNY =
`10 PRINT TAB(33);"BUNNY"
20 PRINT TAB(15);"CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY"
30 PRINT: PRINT: PRINT
100 REM  "BUNNY" FROM AHL'S 'BASIC COMPUTER GAMES'
110 REM
120 FOR I=0 TO 4: READ B(I): NEXT I
130 GOSUB 260
140 L=64: REM  ASCII LETTER CODE...
150 REM
160 PRINT
170 READ X: IF X<0 THEN 160
175 IF X>128 THEN 240
180 PRINT TAB(X);: READ Y
190 FOR I=X TO Y: J=I-5*INT(I/5)
200 PRINT CHR$(L+B(J));
210 NEXT I
220 GOTO 170
230 REM
240 GOSUB 260: GOTO 450
250 REM
260 FOR I=1 TO 3: PRINT CHR$(10);: NEXT I
270 RETURN
280 REM
290 DATA 2,21,14,14,25
300 DATA 1,2,-1,0,2,45,50,-1,0,5,43,52,-1,0,7,41,52,-1
310 DATA 1,9,37,50,-1,2,11,36,50,-1,3,13,34,49,-1,4,14,32,48,-1
320 DATA 5,15,31,47,-1,6,16,30,45,-1,7,17,29,44,-1,8,19,28,43,-1
330 DATA 9,20,27,41,-1,10,21,26,40,-1,11,22,25,38,-1,12,22,24,36,-1
340 DATA 13,34,-1,14,33,-1,15,31,-1,17,29,-1,18,27,-1
350 DATA 19,26,-1,16,28,-1,13,30,-1,11,31,-1,10,32,-1
360 DATA 8,33,-1,7,34,-1,6,13,16,34,-1,5,12,16,35,-1
370 DATA 4,12,16,35,-1,3,12,15,35,-1,2,35,-1,1,35,-1
380 DATA 2,34,-1,3,34,-1,4,33,-1,6,33,-1,10,32,34,34,-1
390 DATA 14,17,19,25,28,31,35,35,-1,15,19,23,30,36,36,-1
400 DATA 14,18,21,21,24,30,37,37,-1,13,18,23,29,33,38,-1
410 DATA 12,29,31,33,-1,11,13,17,17,19,19,22,22,24,31,-1
420 DATA 10,11,17,18,22,22,24,24,29,29,-1
430 DATA 22,23,26,29,-1,27,29,-1,28,29,-1,4096
440 REM
450 END`;

const SOURCE_ELIZA =
`4 CLS
5 PRINT TAB(16)"**************************"
10 PRINT TAB(26)"ELIZA"
20 PRINT TAB(20)"CREATIVE COMPUTING"
30 PRINT TAB(18)"MORRISTOWN, NEW JERSEY":PRINT
40 PRINT TAB(19)"ADAPTED FOR IBM PC BY"
50 PRINT TAB(11)"PATRICIA DANIELSON AND PAUL HASHFIELD"
52 PRINT TAB(14)"BE SURE THAT THE CAPS LOCK IS ON"
53 PRINT:PRINT TAB(5)"PLEASE DON'T USE COMMAS OR PERIODS IN YOUR INPUTS":PRINT
55 PRINT TAB(16)"*************************"
60 PRINT :PRINT :PRINT
80 REM*****INITIALIZATION**********
100 DIM S(36),R(36),N(36)
105 DIM KW$(36),WI$(7),WO$(7),RP$(112)
110 N1=36:N2=14:N3=112
112 FOR X = 1 TO N1: READ KW$(X): NEXT X
114 FOR X = 1 TO N2/2: READ WI$(X):READ WO$(X): NEXT X
116 FOR X = 1 TO N3: READ RP$(X): NEXT X
130 FOR X=1 TO N1
140 READ S(X),L:R(X)=S(X):N(X)=S(X)+L-1
150 NEXT X
160 PRINT "HI! I'M ELIZA. WHAT'S YOUR PROBLEM?"
170 REM ***********************************
180 REM *******USER INPUT SECTION**********
190 REM ***********************************
200 INPUT I$
201 I$="  "+I$+"  "
210 REM GET RID OF APOSTROPHES
220 FOR L=1 TO LEN(I$)
230 REM IF MID$(I$,L,1)="'"THEN I$=LEFT$(I$,L-1)+RIGHT$(I$,LEN(I$)-L):GOTO 230
240 IF L+4>LEN(I$)THEN 250
241 IF MID$(I$,L,4) <> "SHUT" THEN 250
242 PRINT "O.K. IF YOU FEEL THAT WAY I'LL SHUT UP...."
243 END
250 NEXT L
255 IF I$=P$ THEN PRINT "PLEASE DON'T REPEAT YOURSELF!":GOTO 170
260 REM ***********************************
270 REM ********FIND KEYWORD IN I$*********
280 REM ***********************************
300 FOR K=1 TO N1
320 FOR L=1 TO LEN (I$)-LEN (KW$(K))+1
340 IF MID$(I$,L,LEN(KW$(K)))<>KW$(K) THEN 350
341 IF K <> 13 THEN 349
342 IF MID$(I$,L,LEN(KW$(29)))=KW$(29) THEN K = 29
349 F$ = KW$(K): GOTO 390
350 NEXT L
360 NEXT K
370 K=36: GOTO 570:REM WE DIDN'T FIND ANY KEYWORDS
380 REM ******************************************
390 REM **TAKE PART OF STRING AND CONJUGATE IT****
400 REM **USING THE LIST OF STRINGS TO BE SWAPPED*
410 REM ******************************************
430 C$=" "+RIGHT$(I$,LEN(I$)-LEN(F$)-L+1)+" "
440 FOR X=1 TO N2/2
460 FOR L=1 TO LEN(C$)
470 IF L+LEN(WI$(X))>LEN(C$) THEN 510
480 IF MID$(C$,L,LEN(WI$(X)))<>WI$(X) THEN 510
490 C$=LEFT$(C$,L-1)+WO$(X)+RIGHT$(C$,LEN(C$)-L-LEN(WI$(X))+1)
495 L = L+LEN(WO$(X))
500 GOTO 540
510 IF L+LEN(WO$(X))>LEN(C$)THEN 540
520 IF MID$(C$,L,LEN(WO$(X)))<>WO$(X) THEN 540
530 C$=LEFT$(C$,L-1)+WI$(X)+RIGHT$(C$,LEN(C$)-L-LEN(WO$(X))+1)
535 L=L+LEN(WI$(X))
540 NEXT L
550 NEXT X
555 IF MID$(C$,2,1)=" "THEN C$=RIGHT$(C$,LEN(C$)-1):REM ONLY 1 SPACE
556 FOR L=1 TO LEN(C$)
557 IF MID$(C$,L,1)="!" THEN C$=LEFT$(C$,L-1)+RIGHT$(C$,LEN(C$)-L):GOTO 557
558 NEXT L
560 REM **********************************************
570 REM **NOW USING THE KEYWORD NUMBER (K) GET REPLY**
580 REM **********************************************
600 F$ = RP$(R(K))
610 R(K)=R(K)+1:IF R(K)>N(K) THEN R(K)=S(K)
620 IF RIGHT$(F$,1)<>"*" THEN PRINT F$:P$=I$:GOTO 170
625 IF C$<>"   " THEN 630
626 PRINT "YOU WILL HAVE TO ELABORATE MORE FOR ME TO HELP YOU"
627 GOTO 170
630 PRINT LEFT$(F$,LEN(F$)-1);C$
640 P$=I$:GOTO 170
1000 REM *******************************
1010 REM *****PROGRAM DATA FOLLOWS******
1020 REM *******************************
1030 REM *********KEYWORDS**************
1049 REM *******************************
1050 DATA "CAN YOU ","CAN I ","YOU ARE ","YOU'RE ","I DON'T ","I FEEL "
1060 DATA "WHY DON'T YOU ","WHY CAN'T I ","ARE YOU ","I CAN'T ","I AM ","I'M "
1070 DATA "YOU ","I WANT ","WHAT ","HOW ","WHO ","WHERE ","WHEN ","WHY "
1080 DATA "NAME ","CAUSE ","SORRY ","DREAM ","HELLO ","HI ","MAYBE "
1090 DATA "NO","YOUR ","ALWAYS ","THINK ","ALIKE ","YES ","FRIEND "
1100 DATA "COMPUTER", "NOKEYFOUND"
1200 REM *********************************
1210 REM ***STRING DATA FOR CONJUGATIONS**
1220 REM *********************************
1230 DATA " ARE "," AM "," WERE "," WAS "," YOU "," I "," YOUR"," MY "
1235 DATA " I'VE "," YOU'VE "," I'M "," YOU'RE "
1240 DATA " ME "," YOU "
1300 REM ******************************
1310 REM *********REPLIES**************
1320 REM ******************************
1330 DATA "DON'T YOU BELIEVE THAT I CAN*"
1340 DATA "PERHAPS YOU WOULD LIKE TO BE LIKE ME*"
1350 DATA "YOU WANT ME TO BE ABLE TO*"
1360 DATA "PERHAPS YOU DON'T WANT TO*"
1365 DATA "DO YOU WANT TO BE ABLE TO*"
1370 DATA "WHAT MAKES YOU THINK I AM*"
1380 DATA "DOES IT PLEASE YOU TO BELIEVE I AM*"
1390 DATA "PERHAPS YOU WOULD LIKE TO BE*"
1400 DATA "DO YOU SOMETIMES WISH YOU WERE*"
1410 DATA "DON'T YOU REALLY*"
1420 DATA "WHY DON'T YOU*"
1430 DATA "DO YOU WISH TO BE ABLE TO*"
1440 DATA "DOES THAT TROUBLE YOU*"
1450 DATA "DO YOU OFTEN FEEL*"
1460 DATA "DO YOU OFTEN FEEL*"
1470 DATA "DO YOU ENJOY FEELING*"
1480 DATA "DO YOU REALLY BELIEVE I DON'T*"
1490 DATA "PERHAPS IN GOOD TIME I WILL*"
1500 DATA "DO YOU WANT ME TO*"
1510 DATA "DO YOU THINK YOU SHOULD BE ABLE TO*"
1520 DATA "WHY CAN'T YOU*"
1530 DATA "WHY ARE YOU INTERESTED IN WHETHER OR NOT I AM*"
1540 DATA "WOULD YOU PREFER IF I WERE NOT*"
1550 DATA "PERHAPS IN YOUR FANTASIES I AM*"
1560 DATA "HOW DO YOU KNOW YOU CAN'T*"
1570 DATA "HAVE YOU TRIED?"
1580 DATA "PERHAPS YOU CAN NOW*"
1590 DATA "DID YOU COME TO ME BECAUSE YOU ARE*"
1600 DATA "HOW LONG HAVE YOU BEEN*"
1610 DATA "DO YOU BELIEVE IT IS NORMAL TO BE*"
1620 DATA "DO YOU ENJOY BEING*"
1630 DATA "WE WERE DISCUSSING YOU--NOT ME."
1640 DATA "OH, I*"
1650 DATA "YOU'RE NOT REALLY TALKING ABOUT ME, ARE YOU?"
1660 DATA "WHAT WOULD IT MEAN TO YOU IF YOU GOT*"
1670 DATA "WHY DO YOU WANT*"
1680 DATA "SUPPOSE YOU SOON GOT*"
1690 DATA "WHAT IF YOU NEVER GOT*"
1700 DATA "I SOMETIMES ALSO WANT*"
1710 DATA "WHY DO YOU ASK?"
1720 DATA "DOES THAT QUESTION INTEREST YOU?"
1730 DATA "WHAT ANSWER WOULD PLEASE YOU THE MOST?"
1740 DATA "WHAT DO YOU THINK?"
1750 DATA "ARE SUCH QUESTIONS ON YOUR MIND OFTEN?"
1760 DATA "WHAT IS IT THAT YOU REALLY WANT TO KNOW?"
1770 DATA "HAVE YOU ASKED ANYONE ELSE?"
1780 DATA "HAVE YOU ASKED SUCH QUESTIONS BEFORE?"
1790 DATA "WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?"
1800 DATA "NAMES DON'T INTEREST ME."
1810 DATA "I DON'T CARE ABOUT NAMES --PLEASE GO ON."
1820 DATA "IS THAT THE REAL REASON?"
1830 DATA "DON'T ANY OTHER REASONS COME TO MIND?"
1840 DATA "DOES THAT REASON EXPLAIN ANYTHING ELSE?"
1850 DATA "WHAT OTHER REASONS MIGHT THERE BE?"
1860 DATA "PLEASE DON'T APOLOGIZE!"
1870 DATA "APOLOGIES ARE NOT NECESSARY."
1880 DATA "WHAT FEELINGS DO YOU HAVE WHEN YOU APOLOGIZE?"
1890 DATA "DON'T BE SO DEFENSIVE!"
1900 DATA "WHAT DOES THAT DREAM SUGGEST TO YOU?"
1910 DATA "DO YOU DREAM OFTEN?"
1920 DATA "WHAT PERSONS APPEAR IN YOUR DREAMS?"
1930 DATA "ARE YOU DISTURBED BY YOUR DREAMS?"
1940 DATA "HOW DO YOU DO ...PLEASE STATE YOUR PROBLEM."
1950 DATA "YOU DON'T SEEM QUITE CERTAIN."
1960 DATA "WHY THE UNCERTAIN TONE?"
1970 DATA "CAN'T YOU BE MORE POSITIVE?"
1980 DATA "YOU AREN'T SURE?"
1990 DATA "DON'T YOU KNOW?"
2000 DATA "ARE YOU SAYING NO JUST TO BE NEGATIVE?"
2010 DATA "YOU ARE BEING A BIT NEGATIVE."
2020 DATA "WHY NOT?"
2030 DATA "ARE YOU SURE?"
2040 DATA "WHY NO?"
2050 DATA "WHY ARE YOU CONCERNED ABOUT MY*"
2060 DATA "WHAT ABOUT YOUR OWN*"
2070 DATA "CAN YOU THINK OF A SPECIFIC EXAMPLE?"
2080 DATA "WHEN?"
2090 DATA "WHAT ARE YOU THINKING OF?"
2100 DATA "REALLY, ALWAYS?"
2110 DATA "DO YOU REALLY THINK SO?"
2120 DATA "BUT YOU ARE NOT SURE YOU*"
2130 DATA "DO YOU DOUBT YOU*"
2140 DATA "IN WHAT WAY?"
2150 DATA "WHAT RESEMBLANCE DO YOU SEE?"
2160 DATA "WHAT DOES THE SIMILARITY SUGGEST TO YOU?"
2170 DATA "WHAT OTHER CONNECTIONS DO YOU SEE?"
2180 DATA "COULD THERE REALLY BE SOME CONNECTION?"
2190 DATA "HOW?"
2200 DATA "YOU SEEM QUITE POSITIVE."
2210 DATA "ARE YOU SURE?"
2220 DATA "I SEE."
2230 DATA "I UNDERSTAND."
2240 DATA "WHY DO YOU BRING UP THE TOPIC OF FRIENDS?"
2250 DATA "DO YOUR FRIENDS WORRY YOU?"
2260 DATA "DO YOUR FRIENDS PICK ON YOU?"
2270 DATA "ARE YOU SURE YOU HAVE ANY FRIENDS?"
2280 DATA "DO YOU IMPOSE ON YOUR FRIENDS?"
2290 DATA "PERHAPS YOUR LOVE FOR FRIENDS WORRIES YOU."
2300 DATA "DO COMPUTERS WORRY YOU?"
2310 DATA "ARE YOU TALKING ABOUT ME IN PARTICULAR?"
2320 DATA "ARE YOU FRIGHTENED BY MACHINES?"
2330 DATA "WHY DO YOU MENTION COMPUTERS?"
2340 DATA "WHAT DO YOU THINK MACHINES HAVE TO DO WITH YOUR PROBLEM?"
2350 DATA "DON'T YOU THINK COMPUTERS CAN HELP PEOPLE?"
2360 DATA "WHAT IS IT ABOUT MACHINES THAT WORRIES YOU?"
2370 DATA "SAY, DO YOU HAVE ANY PSYCHOLOGICAL PROBLEMS?"
2380 DATA "WHAT DOES THAT SUGGEST TO YOU?"
2390 DATA "I SEE."
2400 DATA "I'M NOT SURE I UNDERSTAND YOU FULLY."
2410 DATA "COME COME ELUCIDATE YOUR THOUGHTS."
2420 DATA "CAN YOU ELABORATE ON THAT?"
2430 DATA "THAT IS QUITE INTERESTING."
2500  REM *************************
2510 REM *****DATA FOR FINDING RIGHT REPLIES
2520 REM *************************
2530 DATA 1,3,4,2,6,4,6,4,10,4,14,3,17,3,20,2,22,3,25,3
2540 DATA 28,4,28,4,32,3,35,5,40,9,40,9,40,9,40,9,40,9,40,9
2550 DATA 49,2,51,4,55,4,59,4,63,1,63,1,64,5,69,5,74,2,76,4
2560 DATA 80,3,83,7,90,3,93,6,99,7,106,6`;