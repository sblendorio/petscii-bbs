5 RANDOMIZE TIMER
10 FOR I=1 TO 39: PRINT "*";:NEXT I: PRINT
20 PRINT "*";TAB(39);"*"
30 PRINT "*";TAB(39);"*"
40 PRINT "*";TAB(39);"*"
50 PRINT "*";TAB(15);"STAR TREK";TAB(39);"*"
60 PRINT "*";TAB(39);"*"
70 PRINT "*";TAB(39);"*"
80 PRINT "*";TAB(13);"BY VINCE BRIEL";TAB(39);"*"
90 PRINT "*";TAB(39);"*"
100 PRINT "*";TAB(39);"*"
110 PRINT "*";TAB(39);"*"
120 PRINT "*";TAB(39);"*"
130 FOR I=1 TO 39: PRINT "*";:NEXT I: PRINT
140 FOR I=1 TO 9:PRINT:NEXT I
150 DIM G(100),S(100)
160 A$="-\= * >!</=\-=-"
170 INPUT "WHAT IS YOUR NAME CAPTAIN",N$
250 INPUT "SKILL LEVEL (1-5) 5 IS HARDEST",S
260 IF S>0 AND S<6 THEN 280
270 PRINT "CHOOSE A NUMBER BETWEEN 1 AND 5 PLEASE.":GOTO 250
280 FOR I=1 TO 15:PRINT:NEXT I:PRINT "SETTING UP THE GALAXY"
290 FOR I=1 TO 100:G(I)=0:NEXT I
300 E1=5*S+INT(RND(1)*10):E7=2000:S1=0:T=10
310 FOR I=1 TO E1
320 X=INT(RND(1)*10)+1:Y=INT(RND(1)*10)+1:C=(10*Y+X)-10:IF G(C)>=100 THEN 320
330 G(C)=G(C)+100
340 NEXT I
350 FOR I=1 TO 7-S
360 X=INT(RND(1)*10)+1:Y=INT(RND(1)*10)+1:C=(10*Y+X)-10:G=G(C)
370 IF G<100 THEN 390
380 G=G-100:GOTO 370
390 IF G>=10 THEN 360
400 G(C)=G(C)+10
410 NEXT I
420 G=INT(RND(1)*300)+1:FOR I=1 TO G
430 X=INT(RND(1)*10)+1:Y=INT(RND(1)*10)+1:C=(10*Y+X)-10
440 G(C)=G(C)+1:NEXT I
450 REM SET UP RANDOM X,Y CORDINATES FOR WHOLE GALAXY
451 PRINT:PRINT:PRINT
452 PRINT "SPACE, THE FINAL FRONTIER.":FOR G=1 TO 500:NEXT G
453 PRINT "THESE ARE THE VOYAGES OF THE STARSHIP"
454 PRINT "ENTERPRISE. ITS FIVE YEAR MISSION:"
455 PRINT "TO EXPLORE STRANGE NEW WORLDS,"
456 PRINT "TO SEEK OUT NEW LIFE AND "
457 PRINT "NEW CIVILIZATIONS, TO BOLDLY GO "
458 PRINT "WHERE NO MAN HAS GONE BEFORE!"
459 PRINT:PRINT TAB(10); "S T A R   T R E K":PRINT:INPUT "PRESS ENTER",ZZ$
460 X1=INT(RND(1)*10)+1:Y1=INT(RND(1)*10)+1:GOTO 2000
470 PRINT:PRINT "LONG RANGE SENSOR SCAN:":PRINT:PRINT E1;" ENEMIES REMAINING.":PRINT
480 IF X1>1 THEN 500
490 PRINT "      10    1     2":GOTO 530
500 IF X1<10 THEN 520
510 PRINT "      9     10    1":GOTO 530
520 PRINT TAB(7); MID$(STR$(X1-1),2);"     ";MID$(STR$(X1),2);"     ";MID$(STR$(X1+1),2)
530 FOR Y=Y1-1 TO Y1+1:Y2=Y:IF Y2<1 THEN Y2=10
540 IF Y2>10 THEN Y2=1
550 FOR I=1 TO 22:PRINT "-";:NEXT I:PRINT:PRINT MID$(STR$(Y2),2);:IF Y2<10 THEN PRINT " ";
560 PRINT " :";:N=1:FOR X=X1-1 TO X1+1:X2=X:IF X2<1 THEN X2=10
570 IF X2>10 THEN X2=1
580 IF X2=1 THEN 600
590 PRINT TAB(N*6);
600 C=(10*Y2+X2)-10:IF G(C)<100 THEN PRINT"0";
610 IF G(C)<10 THEN PRINT "0";
620 PRINT MID$(STR$(G(C)),2);" : ";:N=N+1:NEXT X:PRINT:N=1:NEXT Y
630 FOR I=1 TO 22:PRINT "-";:NEXT I:PRINT:RETURN
650 REM SET UP OBJECTS IN QUADRANT PLACE ENTERPRISE FIRST
660 X4=INT(RND(1)*10)+1:Y4=INT(RND(1)*10)+1:C=(10*Y4+X4)-10:E6=0:E5=0
670 FOR I=1 TO 10:PRINT:NEXT I
680 PRINT "ENTERING QUADRANT ";MID$(STR$(X1),2);",";MID$(STR$(Y1),2)
690 FOR I=1 TO 100:S(I)=0:NEXT I:S(C)=1:C=(10*Y1+X1)-10:G=G(C)
700 IF G<100 THEN 760
710 X=INT(RND(1)*10)+1:Y=INT(RND(1)*10)+1:C=(10*Y+X)-10:IF S(C)<>0 THEN 710
720 D=INT(RND(1)*10)+1:IF D=1 THEN 740
730 S(C)=4:E6=100:E5=4:GOTO 750
740 S(C)=5:E6=200:E5=5
750 G=G-100
760 IF G<10 THEN 790
770 X=INT(RND(1)*10)+1:Y=INT(RND(1)*10)+1:C=(10*Y+X)-10:IF S(C)<>0 THEN 770
780 S(C)=3:G=G-10:IF G>=10 THEN 770
790 IF G=0 THEN 840
800 FOR I=1 TO G
810 X=INT(RND(1)*10)+1:Y=INT(RND(1)*10)+1:C=(10*Y+X)-10:IF S(C)<>0 THEN 810
820 S(C)=2:NEXT I
840 RETURN
850 REM PRINT QUADRANT
860 PRINT:PRINT "QUAD ";MID$(STR$(X1),2);",";MID$(STR$(Y1),2);" SEC ";MID$(STR$(X4),2);",";MID$(STR$(Y4),2);" COND: ";:C1=3:C=(10*Y1+X1)-10:IF G(C) < 10 THEN 880
870 C1=2
880 IF G(C) < 100 THEN 900
890 C1=1
900 IF C1=1 THEN PRINT "RED"
910 IF C1=2 THEN PRINT "YELLOW"
920 IF C1=3 THEN PRINT "GREEN"
930 FOR I=1 TO 30:PRINT "-";:NEXT I:PRINT:FOR Y=1 TO 10:FOR X=1 TO 10:C=(10*Y+X)-10:IF S(C)<>0 THEN 950
940 PRINT "   ";:GOTO 960
950 PRINT MID$(A$,(S(C)*3)-2,3);
960 NEXT X:PRINT:NEXT Y:FOR I=1 TO 30:PRINT "-";:NEXT I:PRINT:PRINT
970 PRINT "ENERGY=";MID$(STR$(E7),2);TAB(13);"SHIELDS=";MID$(STR$(S1),2);TAB(24);"TORP=";MID$(STR$(T),2):PRINT:PRINT:PRINT:PRINT:PRINT:RETURN

1000 REM PHASORS
1010 INPUT "ENTER SHIELD LEVEL",G:IF G<E7 THEN 1030
1020 PRINT "NOT ENOUGH ENERGY.":GOTO 1000
1030 S1=G:E7=E7-G:IF S1>0 THEN 1050
1040 S1=0
1050 RETURN
1100 REM ENEMY FIRES
1110 IF E6>50 THEN 1130
1120 RETURN
1130 G=INT(RND(1)*E6/2)+1:IF E5=5 THEN 1220
1140 PRINT "KLINGON FIRES PHASOR BLAST ";MID$(STR$(G),2);" STRONG."
1150 S1=S1-G:E6=E6-(G/2):IF S1>0 THEN 1210
1160 E7=E7-G*2:S1=0:PRINT "SHIELDS DOWN! ";MID$(STR$(G*2),2);" DAMAGE TO SHIPS ENERGY"
1170 IF E7>0 THEN 1210
1180 PRINT "THE ENTERPRISE WAS DESTROYED!"
1190 PRINT "YOUR NEXT OF KIN WILL BE NOTIFIED."
1200 END
1210 RETURN
1220 G=G*(INT(RND(1)*3)+1):PRINT "ROMULAN FIRES STRANGE WEAPON...":PRINT "CAUSES ";MID$(STR$(G),2);" IN DAMAGE"
1230 GOTO 1150
1250 REM WARP SPEED
1260 IF S1<=0 THEN 1290
1270 PRINT "CAN'T WARP WITH SHIELDS UP.":GOTO 1900
1290 INPUT "DIRECTION ",T1:IF T1<1 OR T1>8 THEN 1290
1300 INPUT "WARP SPEED",G:IF G*10 < E7 THEN 1360
1320 PRINT "NOT ENOUGH ENERGY.":GOTO 1900
1360 FOR I=1 TO G:IF T1<>1 THEN 1450
1380 X1=X1+1:IF X1>10 THEN X1=1
1420 Y1=Y1-1:IF Y1<1 THEN Y1=10
1450 IF T1<>2 THEN 1500
1480 X1=X1+1:IF X1>10 THEN X1=1
1500 IF T1<>3 THEN 1580
1520 X1=X1+1:IF X1>10 THEN X1=1
1540 Y1=Y1+1:IF Y1>10 THEN Y1=1
1580 IF T1<>4 THEN 1640
1600 Y1=Y1+1:IF Y1>10 THEN Y1=1
1640 IF T1<>5 THEN 1700
1660 X1=X1-1:IF X1<1 THEN X1=10
1680 Y1=Y1+1:IF Y1>10 THEN Y1=1
1700 IF T1<>6 THEN 1760
1740 X1=X1-1:IF X1<1 THEN X1=10
1760 IF T1<>7 THEN 1820
1780 X1=X1-1:IF X1<1 THEN X1=10
1800 Y1=Y1-1:IF Y1<1 THEN Y1=10
1820 IF T1<>8 THEN 1850
1840 Y1=Y1-1:IF Y1<1 THEN Y1=10
1850 E7=E7-10:NEXT I:GOSUB 650
1870 GOSUB 850
1900 RETURN
2000 GOSUB 650
2010 GOSUB 850
2020 PRINT:PRINT "COMMAND CAPTAIN ";N$;" (0=HELP) ";
2030 INPUT C2
2040 IF G((10*Y1+X1)-10)<100 THEN 2070
2050 G=INT(RND(1)*5)+1:IF G>3 THEN 2070
2060 GOSUB 1100
2070 IF C2<>0 THEN 2100
2080 GOSUB 2360
2090 GOTO 2020
2100 IF C2<>1 THEN 2130
2110 GOSUB 470
2120 GOTO 2020
2130 IF C2<>2 THEN 2160
2140 GOSUB 850
2150 GOTO 2020
2160 IF C2<>3 THEN 2190
2170 GOSUB 1250
2180 GOTO 2020
2190 IF C2<>4 THEN 2220
2200 GOSUB 1000
2210 GOTO 2020
2220 IF C2<>5 THEN 2250
2230 GOSUB 2400
2240 GOTO 2020
2250 IF C2<>6 THEN 2280
2260 GOSUB 3000
2270 GOTO 2020
2280 IF C2<>7 THEN 2310
2290 GOSUB 4200
2300 GOTO 2020
2310 IF C2<>8 THEN 2350
2320 GOTO 7000
2350 PRINT "ENTER A VALID COMMAND.":GOTO 2020
2360 PRINT:PRINT "COMMAND LIST:":PRINT:PRINT "0 = HELP, THIS LIST":PRINT "1 = LONG RANGE SENSOR SCAN"
2370 PRINT "2 = SHORT RANGE SENSOR SCAN":PRINT "3 = WARP ENGINES":PRINT "4 = SHIELD CONTROL":PRINT "5 = IMPULSE ENGINES"
2380 PRINT "6 = PHASOR CONTROL":PRINT "7 = PHOTON TORPEDO CONTROL"
2390 PRINT "8 =  SELF DESTRUCT"
2391 PRINT "   DIRECTIONS:"
2392 PRINT "      8"
2393 PRINT "   7  :  1"
2394 PRINT "    \ : /"
2395 PRINT "  6---0---2"
2396 PRINT "    / : \"
2397 PRINT "   5  :  3"
2398 PRINT "      4"
2399 RETURN
2410 IF D>0 AND D<9 THEN 2440
2420 PRINT "ANSWER BETWEEN 1 AND 8."
2430 GOTO 2400
2440 INPUT "IMPULSE SPEED",J
2450 IF E7>J THEN 2480
2460 PRINT "YOU DON'T HAVE ENOUGH ENERGY!"
2470 RETURN
2480 S((10*Y4+X4)-10)=0:FOR I=1 TO J:IF D<>1 THEN 2510
2490 X4=X4+1:IF X4>10 THEN X4=1
2500 Y4=Y4-1:IF Y4<1 THEN Y4=10
2510 IF D<>2 THEN 2530
2520 X4=X4+1:IF X4>10 THEN X4=1
2530 IF D<>3 THEN 2560
2540 X4=X4+1:IF X4>10 THEN X4=1
2550 Y4=Y4+1:IF Y4>10 THEN Y4=1
2560 IF D<>4 THEN 2580
2570 Y4=Y4+1:IF Y4>10 THEN Y4=1
2580 IF D<>5 THEN 2610
2590 X4=X4-1:IF X4<1 THEN X4=10
2600 Y4=Y4+1:IF Y4>10 THEN Y4=1
2610 IF D<>6 THEN 2630
2620 X4=X4-1:IF X4<1 THEN X4=10
2630 IF D<>7 THEN 2660
2640 X4=X4-1:IF X4<1 THEN X4=10
2650 Y4=Y4-1:IF Y4<1 THEN Y4=10
2660 IF D<>8 THEN 2680
2670 Y4=Y4-1:IF Y4<1 THEN Y4=10
2680 E7=E7-1:G=S((10*Y4+X4)-10)
2690 IF G=0 THEN 2900
2700 IF G<>2 THEN 2750
2720 PRINT "YOU RAN INTO A STAR AND BURNED!"
2730 PRINT "YOUR NEXT OF KIN WILL BE NOTIFIED"
2740 END
2750 IF G<>3 THEN 2800
2770 PRINT "YOU RAMMED THE STARBASE!"
2780 PRINT:PRINT "THOUSANDS ARE DEAD."
2790 PRINT N$;" WILL BE KNOWN AS A KILLER.":END
2800 PRINT "YOU RAMMED THE ENEMY SHIP"
2810 PRINT "IT WAS DESTROYED BUT SO WAS"
2820 PRINT "THE ENTERPRISE!":END
2900 NEXT I
2920 S((10*Y4+X4)-10)=1
2930 GOSUB 850
2940 GOSUB 4050
2950 RETURN
3000 REM PHASOR CONTROL
3010 IF E5<>0 THEN 3100
3020 PRINT "NO ENEMY TO LOCK PHASORS ONTO!":RETURN
3100 INPUT "AMOUNT OF ENERGY TO PHASORS",G
3110 IF G<E7 THEN 3140
3120 PRINT "YOU DON'T HAVE THAT MUCH ENERGY.":GOTO 3100
3130 E7=E7-G
3140 IF E5=5 THEN 3390
3150 R=INT(RND(1)*10)+1:IF R<>1 THEN 3300
3170 PRINT "PHASORS MISSED!":RETURN
3300 PRINT "KLINGON HIT WITH ";MID$(STR$(G),2);" PHASOR UNITS."
3310 E6=E6-G:IF E6>0 THEN 3380
3320 PRINT "*** KLINGON DESTROYED ***"
3330 G((10*Y1+X1)-10)=G((10*Y1+X1)-10)-100
3340 E1=E1-1:IF E1=0 THEN 4000
3350 E6=0:E5=0:FOR I=1 TO 100:IF S(I)=4 THEN S(I)=0
3360 IF S(I)=5 THEN S(I)=0
3370 NEXT I:GOSUB 850
3380 RETURN
3390 R=INT(RND(1)*5)+1:IF R>3 THEN 3410
3400 PRINT "ROMULAN SHIP AVOIDED PHASOR FIRE!":RETURN
3410 PRINT "ROMULAN HIT WITH ";MID$(STR$(G),2);" PHASOR UNITS."
3420 E6=E6-G:IF E6>0 THEN 3380
3430 PRINT "*** ROMULAN SHIP DESTROYED ***"
3440 G((10*Y1+X1)-10)=G((10*Y1+X1)-10)-100:E1=E1-1:IF E1=0 THEN 4000
3450 GOTO 3350
4000 FOR I=1 TO 24:PRINT:NEXT I
4010 PRINT "YOU HAVE DEFENDED THE GALAXY AND DEFEATED"
4020 PRINT "ALL ENEMIES! YOU WILL BE PROMOTED TO"
4030 PRINT "ADMIRAL BEFORE BEING SUCKED OUT INTO"
4040 PRINT "SPACE IN STAR TREK GENERATIONS."
4050 REM CHECK IF DOCKED
4060 D9=0:X=X4-1:IF X<1 THEN X=10
4070 X9=X4+1:IF X9>10 THEN X9=1
4080 IF S((10*Y4+X)-10)=3 THEN 4120
4090 IF S((10*Y4+X9)-10)=3 THEN 4120
4100 RETURN
4120 IF S1=0 THEN 4140
4130 PRINT "YOU CAN'T DOCK WITH SHIELDS UP.":RETURN
4140 PRINT "YOU ARE DOCKED WITH THE STARBASE":D9=1
4150 PRINT "ENERGY RESTORED":E7=2000:T=10:RETURN
4200 REM PHOTON TORPEDO CONTROL
4300 IF T>0 THEN 4500
4400 PRINT "YOU DON'T HAVE ANY TORPEDOS.":RETURN
4500 INPUT "DIRECTION",D
4600 IF D>0 AND D<9 THEN 4800
4700 PRINT "PLEASE CHOOSE BETWEEN 1-8":GOTO 4500
4800 X5=X4:Y5=Y4:T=T-1:PRINT:PRINT "TRACKING TORPEDO PATH":PRINT
5000 PRINT TAB(10);MID$(STR$(X5),2);",";MID$(STR$(Y5),2): IF D<>1 THEN 5500
5100 X5=X5+1:IF X5>10 THEN 6000
5300 Y5=Y5-1:IF Y5<1 THEN 6000
5500 IF D<>2 THEN 5560
5520 X5=X5+1:IF X5>10 THEN 6000
5560 IF D<>3 THEN 5680
5600 X5=X5+1:IF X5>10 THEN 6000
5640 Y5=Y5+1:IF Y5>10 THEN 6000
5680 IF D<>4 THEN 5720
5700 Y5=Y5+1:IF Y5>10 THEN 6000
5720 IF D<>5 THEN 5780
5740 X5=X5-1:IF X5<1 THEN 6000
5760 Y5=Y5+1:IF Y5>10 THEN 6000
5780 IF D<>6 THEN 5800
5790 X5=X5-1:IF X5<1 THEN 6000
5800 IF D<>7 THEN 5830
5810 X5=X5-1:IF X5<1 THEN 6000
5820 Y5=Y5-1:IF Y5<1 THEN 6000
5830 IF D<>8 THEN 5850
5840 Y5=Y5-1:IF Y5<1 THEN 6000
5850 V=S((10*Y5+X5)-10):IF V=0 THEN 5000
5860 IF V=2 THEN 6200
5870 IF V=3 THEN 6400
5880 IF V=4 THEN 6450
5890 IF V=5 THEN 6600
5900 GOTO 5000:REM CONTINUE UNTIL HIT SOMETHING OR LEAVE QUAD
6000 REM OUT OF QUADRANT
6100 PRINT:PRINT "     *****  TORPEDO MISSED *****":PRINT:RETURN
6200 REM HIT STAR
6300 PRINT "TORPEDO EXPLODED IN THE STAR":PRINT:RETURN
6400 PRINT "     ***** STAR BASE DESTROYED *****":GOTO 2780
6450 PRINT "KLINGON HIT BY TORPEDO"
6460 IF INT(RND(1)*100)+1>98 THEN 6550
6470 PRINT:PRINT "KLINGON DESTROYED"
6480 S((10*Y5+X5)-10)=0:G((10*Y1+X1)-10)=G((10*Y1+X1)-10)-100:E1=E1-1:E5=0:E6=0:IF E1=0 THEN 4000
6490 PRINT:GOSUB 850
6500 RETURN
6550 PRINT "KLINGON SURVIVED IMPACT FROM TORPEDO":E6=E6/2:RETURN
6600 PRINT "ROMULAN HIT BY TORPEDO"
6610 IF INT(RND(1)*100)+1<50 THEN 6650
6620 PRINT:PRINT "ROMULAN SHIP DESTROYED!":GOTO 6480
6650 PRINT "ROMULAN SHIELDS HOLDING":E6=E6/2:RETURN
7000 PRINT:PRINT "SELF DESTRUCT SEQUENCE IS NOW ACTIVE"
7100 FOR X=10 TO 1 STEP -1
7200 PRINT TAB(19);X:FOR Y=1 TO 500:NEXT Y:NEXT X
7300 PRINT:PRINT:PRINT "      **** KA-BOOM!!! ****"
7400 PRINT "PIECES OF THE ENTERPRISE ARE NOW"
7500 PRINT "SPREAD ACROSS THE GALAXY"
7600 PRINT "YOUR NEXT OF KIN WILL BE NOTIFIED."
7700 END
