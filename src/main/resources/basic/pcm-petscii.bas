1 S$="   ":T$="     ":P$="":FOR I=1 TO 255
2 IF I>64 THEN P$=P$+"@":GOTO 5
3 IF I>47 AND I<58 THEN P$=P$+"*":GOTO 5
4 P$=P$+" "
5 NEXT
6 DIM A$(21):DIM F$(21):DIM BK$(21):DIM V$(3,21):DIM MEM(6)
7 RANDOMIZE TIMER
10 CLS
12 GOSUB 7000
15 INPUT"Game type 1, 2 or 3";Z$:GOSUB 10000:IF Z$="." THEN 15
16 G=VAL(Z$)-1:PRINT
20 INPUT"Fast learning (Y/N)";Z$:GOSUB 10000:IF Z$="." THEN 20
21 Q$=Z$:PRINT
30 INPUT"What's your name";Z$:GOSUB 10000:N$=Z$
50 GOSUB 500: REM state loader
60 GOSUB 600: REM winnings loader
90 S=0:V=0:A=0:SK=0:SC$=A$(0)
93 GOSUB 5000
95 PRINT
96 PRINT"PCM plays the first move - press ENTER";:LINE INPUT Z$:GOSUB 10000
97 PRINT
100 GOSUB 700: REM PCM plays
105 GOSUB 900: REM update status
110 GOSUB 5000
140 A=1:PRINT:PRINT"Now ";N$;" plays the first move"
150 GOSUB 760
155 GOSUB 900: REM update current status
160 FOR Y=0 TO 20: REM update status pointer
170 IF SC$=A$(Y) THEN S=Y:GOTO 200
180 NEXT Y
200 GOSUB 5000
230 A=0:PRINT
231 PRINT"PCM plays the second move - press ENTER";:LINE INPUT Z$:GOSUB 10000
232 PRINT
235 SK=2: REM update stack pointer
240 GOSUB 700
245 GOSUB 900: REM update state machine
250 GOSUB 5000
300 A=1:PRINT:PRINT "Now ";N$;" plays the second move"
310 GOSUB 760
315 GOSUB 900: REM aggiorna stato corrente
320 GOSUB 5000
380 REM controlla pok
385 SK=0: REM reset stack pointer
390 IF G=0 THEN N=2:GOTO 420
400 IF G=1 THEN N=8:GOTO 420
410 N=17
420 FOR I=0 TO N
425 IF SC$=V$(G,I) THEN V=1:GOTO 440
430 NEXT I
440 IF V=1 THEN 480
450 PRINT:PRINT"PCM wins!": REM add winning transitions
455 PRINT:PRINT "Press ENTER ";
460 S=MEM(SK):C=MEM(SK+1):C$=RIGHT$(STR$(C),1):F$(S)=F$(S)+C$
470 S=MEM(SK+2):C=MEM(SK+3):C$=RIGHT$(STR$(C),1):F$(S)=F$(S)+C$
475 LINE INPUT Z$:GOSUB 10000:IF Z$="." THEN 475
476 GOTO 90
480 PRINT:PRINT N$;" wins!":GOSUB 945: REM remove losing transitions
485 PRINT:PRINT "Press ENTER ";
490 LINE INPUT Z$:GOSUB 10000:IF Z$="." THEN 490
491 GOTO 90
500 REM state loader
510 FOR Y=0 TO 20
520 FOR I=1 TO 6:READ B$:A$(Y)=A$(Y)+B$
530 IF ASC(B$)=45 THEN F$(Y)=F$(Y)+RIGHT$(STR$(I),1):BK$(Y)=F$(Y)
540 NEXT I:NEXT Y
550 RETURN
600 REM winnings loader
605 N=2
610 FOR Y=0 TO 2
620 FOR I=0 TO N:FOR A=0 TO 5:READ B$:V$(Y,I)=V$(Y,I)+B$:NEXT A
625 NEXT I
630 IF I=3 THEN N=8:GOTO 660
640 IF I=9 THEN N=17:GOTO 660
650 IF I=18 THEN RETURN
660 NEXT Y
700 REM PCM plays
720 N=INT(RND(1)*LEN(F$(S)))+1:C$=MID$(F$(S),N,1)
735 GOSUB 800:REM check if the same number exists in the same position
740 IF F=1 THEN 720: REM pick another one
745 MEM(SK)=S:MEM(SK+1)=VAL(C$):REM save state/char in the stack
750 RETURN
760 REM human plays
765 PRINT
766 LINE INPUT"Type letter of the desired position: ";Z$:GOSUB 10000:IF Z$="." THEN 766
767 C$=Z$+".":Z=ASC(C$):IF Z>=97 AND Z<=122 THEN C$=CHR$(Z-32)
770 GOSUB 850
775 IF F=1 THEN PRINT:PRINT"Invalid input... Try again":GOTO 765
780 GOSUB 800:REM check if the same number exists in the same position
785 IF F=1 THEN PRINT:PRINT"Invalid input... Try again":GOTO 765
790 RETURN
800 REM check if the same number exists in the same position
805 IF A=1 THEN C=ASC(C$)-64:GOTO 810:REM alice's number position
807 IF A=0 THEN C=VAL(C$):GOTO 810:REM bob's number position
810 B$=MID$(SC$,C,1)
820 IF ASC(B$)<>45 THEN F=1:RETURN
830 F=0:RETURN
850 REM check character insertion
860 IF (ASC(C$)<66 OR ASC(C$)>71) THEN F=1:RETURN
870 F=0:RETURN
900 REM update current status
905 IF A=1 THEN C=ASC(C$)-64:GOTO 910:REM alice's number position
907 IF A=0 THEN C=VAL(C$):GOTO 910:REM bob's number position
910 L$=LEFT$(SC$,C-1)
920 R$=RIGHT$(SC$,6-C)
930 SC$=L$+C$+R$
940 RETURN
945 REM delete states
947 IF Q$="n" OR Q$="N" THEN 996
950 SK=0
955 S=MEM(SK):B=MEM(SK+1):B$=RIGHT$(STR$(B),1)
960 FOR I=1 TO LEN(F$(S))
965 C$=MID$(F$(S),I,1)
970 IF C$=B$ THEN 990
975 NEXT I
980 GOTO 995
990 L$=LEFT$(F$(S),I-1):R$=RIGHT$(F$(S),LEN(F$(S))-I):F$(S)=L$+R$
992 IF LEN(F$(S))=0 THEN F$(S)=BK$(S)
995 IF SK=2 THEN RETURN
996 SK=2:GOTO 955
999 REM states
1000 DATA A,-,-,-,-,-
1010 DATA A,-,3,D,-,-
1020 DATA A,-,-,D,5,-
1030 DATA A,2,C,-,-,-
1040 DATA A,B,3,-,-,-
1050 DATA A,-,C,-,5,-
1060 DATA A,2,-,D,-,-
1070 DATA A,B,-,4,-,-
1080 DATA A,B,-,-,5,-
1090 DATA A,2,-,-,E,-
2000 DATA A,-,C,4,-,-
2010 DATA A,B,-,-,-,6
2020 DATA A,2,-,-,-,F
2030 DATA A,-,-,4,E,-
2040 DATA A,-,C,-,-,6
2050 DATA A,-,3,-,-,F
2060 DATA A,-,-,4,-,F
2070 DATA A,-,-,D,-,6
2080 DATA A,-,3,-,E,-
2090 DATA A,-,-,-,5,F
3000 DATA A,-,-,-,E,6
3001 REM winnings game1
3100 DATA A,2,C,4,E,-
3110 DATA A,2,C,-,E,6
3120 DATA A,-,C,4,E,6
3199 REM winnings game2
3200 DATA A,B,C,4,5,-
3210 DATA A,B,C,4,-,6
3220 DATA A,B,C,-,5,6
3230 DATA A,B,3,4,-,F
3240 DATA A,B,3,-,5,F
3250 DATA A,B,-,4,5,F
3260 DATA A,2,3,-,E,F
3270 DATA A,2,-,4,E,F
3280 DATA A,-,3,4,E,F
3299 REM winnings game3
3300 DATA A,B,3,D,5,-
3310 DATA A,B,3,D,-,6
3320 DATA A,B,-,D,5,6
3330 DATA A,B,3,4,E,-
3340 DATA A,B,3,-,E,6
3350 DATA A,B,-,4,E,6
3360 DATA A,2,C,4,-,F
3370 DATA A,2,C,-,5,F
3380 DATA A,-,C,4,5,F
3390 DATA A,2,3,D,-,F
3400 DATA A,2,-,D,5,F
3410 DATA A,-,3,D,5,F
3420 DATA A,2,C,D,5,-
3430 DATA A,2,C,D,-,6
3440 DATA A,-,C,D,5,6
3450 DATA A,2,3,D,E,-
3460 DATA A,2,-,D,E,6
3470 DATA A,-,3,D,E,6
5000 CLS
5010 PRINT "      A"
5020 PRINT "      "+MID$(P$,ASC(MID$(SC$,1,1)),1)
5030 PRINT "     : :            ";MID$(P$,49,1);"=PCM"
5040 PRINT "  F :   : B         ";MID$(P$,65,1);"=";N$
5050 PRINT S$+MID$(P$,ASC(MID$(SC$,6,1)),1)+T$+MID$(P$,ASC(MID$(SC$,2,1)),1)
5060 PRINT "   :     :"
5070 PRINT "   :     :"
5080 PRINT S$+MID$(P$,ASC(MID$(SC$,5,1)),1)+T$+MID$(P$,ASC(MID$(SC$,3,1)),1)
5090 PRINT "  E :   : C"
5100 PRINT "     : :"
5110 PRINT "      "+MID$(P$,ASC(MID$(SC$,4,1)),1)
5120 PRINT "      D"
5200 RETURN
7000 CLS
7010 PRINT "       The Paper Cup Machine (PCM)"
7020 PRINT "      devised by prof. Ferri of the"
7030 PRINT "      University of Bologna (by JM)"
7040 PRINT : PRINT "Purpose of this game is to understand"
7050 PRINT "how ai self-learning works."
7060 PRINT "Three games can be played:"
7070 PRINT : PRINT "- GAME 1"
7080 PRINT "You must place three tokens in"
7090 PRINT "NON-adjacent positions."
7091 PRINT
7100 PRINT "- GAME 2"
7110 PRINT "You must place three tokens in"
7120 PRINT "adjacent positions."
7125 PRINT
7130 PRINT "- GAME 3"
7140 PRINT "You must place TWO tokens in"
7150 PRINT "adjacent positions and the third"
7160 PRINT "NON-adjacent to the first two."
7170 PRINT : PRINT "Your first move always starts from 'A'."
7180 PRINT "PCM will improve after every match to"
7190 PRINT "the point of defeating you every time.";
7200 LINE INPUT Z$:GOSUB 10000:CLS:RETURN
10000 IF Z$<>"." THEN RETURN
10010 INPUT "QUIT: ARE YOU SURE";KK$
10020 IF KK$<>"Y" AND KK$<>"S" AND KK$<>"." THEN RETURN
10030 END
