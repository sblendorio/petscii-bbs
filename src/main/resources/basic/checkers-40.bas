1 REM  CONVERSION BY VINCE BRIEL 1/28/2007
2 PRINT "CHECKERS- CREATIVE COMPUTING MORRISTOWN"
3 PRINT "COMPUTER IS X, AND YOU ARE O."
4 PRINT "(1,1) IS THE LOWER LEFT CORNER"
5 PRINT "+TO = ANOTHER? TYPE 0 FOR NO. '.'=QUIT"
6 DIM R(5),S(64):G=-1:R(1)=-99: FOR X=1 TO 64:S(X)=0: NEXT X
7 S(1)=1:S(3)=1:S(5)=1:S(7)=1:S(10)=1:S(12)=1:S(14)=1:S(16)=1:S(17)=1:S(19)=1:S(21)=1:S(23)=1
8 S(42)=-1:S(44)=-1:S(46)=-1:S(48)=-1:S(49)=-1:S(51)=-1
9 S(53)=-1:S(55)=-1:S(58)=-1:S(60)=-1:S(62)=-1:S(64)=-1
10 FOR X=1 TO 8: FOR Y=1 TO 8 : IF S((8*Y+X)-8)>-1 THEN 350
310 IF S((8*Y+X)-8)<>-1 THEN 330
320 FOR A=-1 TO 1 STEP 2:B=G: GOSUB 650: NEXT A
330 IF S((8*Y+X)-8)<>-2 THEN 350
340 FOR A=-1 TO 1 STEP 2: FOR B=-1 TO 1 STEP 2: GOSUB 650 : NEXT B,A
350 NEXT Y,X: GOTO 1140
650 U=X+A:V=Y+B: IF U<1 OR U>8 OR V<1 OR V>8 THEN 870
740 IF S((8*V+U)-8)<>0 THEN 770
750 GOSUB 910: GOTO 870
770 IF S((8*V+U)-8)<0 THEN 870
790 U=U+A:V=V+B: IF U<1 OR V<1 OR U>8 OR V>8 THEN 870
850 IF S((8*V+U)-8)=0 THEN GOSUB 910
870 RETURN
910 IF V=0 AND S((8*Y+X)-8)=-1 THEN Q=Q+2
920 IF ABS (Y-V)=2 THEN Q=Q+5
960 IF Y=8 THEN Q=Q-2
980 IF U=1 OR U=8 THEN Q=Q+1
1030 FOR C=-1 TO 1 STEP 2: IF U+C<1 OR U+C>8 OR V+G<1 THEN 1080
1032 U1=U+C:V1=V+G:X1=U-C:Y1=V-G
1035 IF S((8*V1+U1)-8)>=0 THEN 1040
1037 Q=Q+1: GOTO 1080
1040 IF U-C<1 OR U-C>8 OR V-G>8 OR S((8*V1+U1)-8)<=0 OR S((8*Y1+X1)-8)<>0 THEN 1080
1060 IF U-C=X AND V-G=Y THEN Q=Q-2
1080 NEXT C: IF Q<=R(1) THEN 1100
1090 R(1)=Q:R(2)=X:R(3)=Y:R(4)=U:R(5)=V
1100 Q=0: RETURN
1140 IF R(1)=-99 THEN 1880
1230 PRINT "FROM ";MID$(STR$(R(2)),2);",";MID$(STR$(R(3)),2);" TO ";MID$(STR$(R(4)),2);",";R(5);:R(1)=-99
1240 IF R(5)<>1 THEN 1250
1245 S((8*R(5)+R(4))-8)=-2:S((8*R(3)+R(2))-8)=0: GOTO 1420
1250 S((8*R(5)+R(4))-8)=S((8*R(3)+R(2))-8):S((8*R(3)+R(2))-8)=0: IF ABS (R(2)-R(4))<>2 THEN 1420
1330 U1=(R(2)+R(4))/2:V1=(R(3)+R(5))/2:S((8*V1+U1)-8)=0
1340 X=R(4):Y=R(5): IF S((8*Y+X)-8)<>-1 THEN 1350
1345 B=-2: FOR A=-2 TO 2 STEP 4 : GOSUB 1370
1350 IF S((8*Y+X)-8)<>-2 THEN 1360
1355 FOR A=-2 TO 2 STEP 4: FOR B=-2 TO 2 STEP 4: GOSUB 1370 : NEXT B
1360 NEXT A: IF R(1)=-99 THEN 1420
1365 PRINT " TO ";MID$(STR$(R(4)),2);",";MID$(STR$(R(5)),2);:R(1)=-99: GOTO 1240
1370 U=X+A:V=Y+B: IF U<1 OR U>8 OR V<1 OR V>8 THEN 1400
1380 X1=X+A/2:Y1=Y+B/2: IF S((8*V+U)-8)=0 AND S((8*Y1+X1)-8)>0 THEN GOSUB 910
1400 RETURN
1420 PRINT : PRINT : FOR Y=8 TO 1 STEP -1: FOR X=1 TO 8: PRINT TAB(X*4);: IF S((8*Y+X)-8)=0 THEN PRINT ".";
1470 IF S((8*Y+X)-8)=1 THEN PRINT "O";
1490 IF S((8*Y+X)-8)=-1 THEN PRINT "X";
1510 IF S((8*Y+X)-8)=-2 THEN PRINT "X*";
1530 IF S((8*Y+X)-8)=2 THEN PRINT "O*";
1550 NEXT X: PRINT : PRINT : NEXT Y: PRINT
1552 FOR L=1 TO 8: FOR M=1 TO 8 : IF S((8*M+L)-8)=1 OR S((8*M+L)-8)=2 THEN U1=1
1558 IF S((8*M+L)-8)=-1 OR S((8*M+L)-8)=-2 THEN V1=1
1560 NEXT M,L: IF U1<>1 THEN 1885
1566 IF V1<>1 THEN 1880
1590 U1=0:V1=0: PROMPT$="FROM":GOSUB 2000:E=X9:H=Y9:X=E:Y=H: IF S((8*Y+X)-8)<=0 THEN 1590
1670 PROMPT$="TO":GOSUB 2000:A=X9:B=Y9:X=A:Y=B: IF S((8*Y+X)-8)<>0 OR ABS (A-E)>2 OR ABS (A-E)<> ABS (B-H) THEN 1670
1750 S((8*B+A)-8)=S((8*H+E)-8):S((8*H+E)-8)=0: IF ABS (E-A)<>2 THEN 1810
1800 X1=(E+A)/2:Y1=(H+B)/2:S((8*Y1+X1)-8)=0
1802 PROMPT$="+TO":GOSUB 2100: X1=X9:Y1=Y9: IF X1<1 THEN 1810
1804 IF S((8*Y1+X1)-8)<>0 OR ABS(X1-A)<>2 OR ABS (Y1-B)<>2 THEN 1802
1806 E=A:H=B:A=X1:B=Y1: GOTO 1750
1810 IF B=8 THEN S((8*B+A)-8)=2
1830 GOTO 10
1880 PRINT : PRINT "YOU WIN.": END
1885 PRINT : PRINT "I WIN.": END
1999 END
2000 PRINT PROMPT$;:INPUT XY$:IF XY$="" THEN 2000
2010 IF XY$="." THEN GOSUB 2200
2020 CP=INSTR(XY$,",")
2030 IF CP=0 OR CP=1 THEN 2000
2040 X9=VAL(MID$(XY$,1,CP-1))
2050 Y9=VAL(MID$(XY$,CP+1))
2060 IF (X9>=1 AND X9<=8) AND (Y9>=1 AND Y9<=8) THEN RETURN
2070 GOTO 2000
2100 PRINT PROMPT$;:INPUT XY$:IF XY$="." THEN GOSUB 2200
2110 IF XY$="" THEN X9=0:Y9=1:RETURN
2120 CP=INSTR(XY$,",")
2130 IF CP=0 OR CP=1 THEN X9=0:Y9=1:RETURN
2140 X9=VAL(MID$(XY$,1,CP-1))
2150 Y9=VAL(MID$(XY$,CP+1))
2160 IF (X9>=0 AND X9<=8) AND (Y9>=1 AND Y9<=8) THEN RETURN
2170 GOTO 2100
2200 INPUT "QUIT: ARE YOU SURE";KK$
2210 IF KK$<>"Y" AND KK$<>"S" AND KK$<>"." THEN RETURN
2220 END
