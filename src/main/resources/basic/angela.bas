10 REM ANGELA GAME
20 p = 0
30 a1 = 0
40 m1 = 0
50 r1 = 0
60 b1 = 0
70 b2 = 0
80 c1 = 0
90 c2 = 0
100 d1 = 0
110 r = 0
120 t = 0
130 a = 0
140 p1 = 0
150 u = 0
160 v = -1
170 f = 0
180 c = 6
185 a$=""
190 PRINT "Angela Game"
200 PRINT "-----------":print
210 PRINT "I for instructions":print "C for credits"
211 PRINT "ENTER to play":PRINT
212 INPUT "Your choice";a$: IF a$="." THEN GOSUB 10000:GOTO 212
213 if a$="i" then gosub 2500
214 if a$="c" then GOSUB 2300
215 PRINT:PRINT "Do you want to see the P101 registers"
216 INPUT "during the game";r$: IF r$="." THEN GOSUB 10000:GOTO 216
220 REM
230 PRINT:INPUT "Goal";zz$: m1=val(zz$) : IF zz$="." THEN GOSUB 10000:GOTO 230
240 IF m1 >= 30 AND m1 <= 100 THEN 280
250 PRINT "Please type a number between 30 and 100"
260 GOTO 220
280 v$ = "B1": GOSUB 1860
300 v$ = "B2": GOSUB 2080
310 v$ = "C1": GOSUB 1860
320 p = 1
330 INPUT "Your number";zz$: m1=val(zz$) : IF zz$="." THEN GOSUB 10000: GOSUB 1570: GOTO 330
340 rem sblend print:print "Computing..";
350 m1 = INT(m1)

360 IF not((m1 < 0 OR m1 > 6) AND p = 1) THEN 362
361 PRINT:PRINT "Value not allowed": GOSUB 1572: GOTO 330
362 REM

370 IF not((m1 < 1 OR m1 > 6) AND p = 0) THEN 373
371 PRINT:PRINT "Value not allowed": GOSUB 1572:GOTO 330
373 REM
380 p = 0
390 GOSUB 440
400 GOSUB 1570
410 IF f = 0 THEN GOSUB 1510
420 IF f = 1 THEN 5000
430 GOTO 330
440 v$ = "D1": GOSUB 1860
450 a = d1
460 v$ = "C2": GOSUB 2080
470 GOSUB 1020
480 RETURN
490 v$ = "B2": GOSUB 1900
500 v$ = "D1": GOSUB 1590
510 v$ = "B2": GOSUB 1940
520 u = b2
530 GOSUB 1450: IF f = 1 THEN RETURN
540 v$ = "B1": GOSUB 1900
550 v$ = "B2": GOSUB 1650
560 v = 9: GOSUB 2050
570 v$ = "M": GOSUB 1770
580 v$ = "R": GOSUB 1940

590 IF a1 > 0 THEN 592
591 v$ = "M": GOSUB 1590
592 REM

600 GOSUB 620
610 RETURN
620 v$ = "A": GOSUB 1770
630 v$ = "C1": GOSUB 1860
640 v$ = "M": GOSUB 1590
650 v$ = "C2": GOSUB 1940
660 v$ = "C1": GOSUB 1900
670 v = 3: GOSUB 2050
680 v$ = "M": GOSUB 1650
690 GOSUB 2020
700 IF a1 > 0 THEN 702
701 v = 5: GOSUB 2050: v$ = "C2": GOSUB 1860: GOSUB 1020: RETURN
702 REM

710 GOSUB 730
720 RETURN
730 v$ = "M": GOSUB 1650
740 IF a1 <= 0 THEN 742
741 GOSUB 800: RETURN
742 REM

750 GOSUB 2020
760 IF a1 <= 0 THEN 762
761 GOSUB 1020: RETURN
762 REM

770 v$ = "C2": GOSUB 1860
780 GOSUB 1020
790 RETURN
800 v = 1: GOSUB 2050
810 v$ = "M": GOSUB 1650
820 IF a1 <= 0 THEN 822
821 GOSUB 880: RETURN
822 REM
830 v$ = "C2": GOSUB 1860
840 v = 3: GOSUB 2050
850 v$ = "C1": GOSUB 1860
860 GOSUB 1020
870 RETURN
880 v$ = "M": GOSUB 1650
890 IF a1 <= 0 THEN 892
891 GOSUB 950: RETURN
892 REM

900 v$ = "C2": GOSUB 1860
910 v = 4: GOSUB 2050
920 v$ = "C1": GOSUB 1860
930 GOSUB 1020
940 RETURN
950 v$ = "M": GOSUB 1650
960 v$ = "C1": GOSUB 1860
970 v$ = "M": GOSUB 1590
980 v$ = "M": GOSUB 1590
990 v$ = "C2": GOSUB 1940
1000 GOSUB 1020
1010 RETURN
1020 v$ = "D1": GOSUB 1900
1030 v$ = "C1": GOSUB 1650
1040 GOSUB 2020
1050 IF a1 <= 0 THEN 1052
1051 GOSUB 1080: RETURN
1052 REM
1060 GOSUB 1150
1070 RETURN
1080 v = 7: GOSUB 2050
1090 v$ = "M": GOSUB 1900
1100 v$ = "D1": GOSUB 1650
1110 v$ = "C1": GOSUB 1650
1120 GOSUB 2020
1130 IF a1 > 0 THEN 1132
1131 GOSUB 1150: goto 1133
1132 GOSUB 1230
1133 REM

1140 RETURN
1150 v$ = "C2": GOSUB 1900
1160 IF a1 <= 0 THEN 1162
1161 GOSUB 1200: RETURN
1162 rem

1170 PRINT:PRINT "This value is not allowed": GOSUB 1572
1180 v = 0
1190 RETURN
1200 v$ = "C1": GOSUB 1940
1210 GOSUB 1230
1220 RETURN
1230 v$ = "C2": GOSUB 1900
1240 IF a1 > 0 THEN 1242
1241 GOSUB 490: goto 1243
1242 GOSUB 1260
1243 REM

1250 RETURN
1260 v$ = "B1": GOSUB 1900
1270 v$ = "B2": GOSUB 1650
1280 v = 2: GOSUB 2050
1290 v$ = "M": GOSUB 1650
1300 IF a1 <= 0 THEN 1302
1301 GOSUB 1390: RETURN
1302 REM

1310 v$ = "D1": GOSUB 1900
1320 v$ = "M": GOSUB 1650
1330 GOSUB 2020
1340 IF a1 <= 0 THEN 1342
1341 GOSUB 1390: RETURN
1342 rem

1350 v = 1: GOSUB 2050
1360 v$ = "C1": GOSUB 1860
1370 GOSUB 1390
1380 RETURN
1390 p1 = c1
1400 v$ = "B2": GOSUB 1900
1410 v$ = "C1": GOSUB 1590
1420 v$ = "B2": GOSUB 1940
1430 u = b2
1440 RETURN
1450 f = 0
1460 v$ = "B1": GOSUB 1900
1470 v$ = "B2": GOSUB 1650
1480 IF a1 <> 0 THEN 1483
1481 PRINT:PRINT "You Win! Congratulations!! :-D": f = 1
1482 PRINT
1483 REM

1490 IF a1 >= 0 THEN 1492
1491 PRINT:PRINT:PRINT "You lose... :'(": f = 1
1492 REM

1500 RETURN
1510 f = 0
1520 v$ = "B1": GOSUB 1900
1530 v$ = "B2": GOSUB 1650
1540 IF a1 <> 0 THEN 1542
1541 PRINT:PRINT:PRINT "You lose... :'(": f = 1:PRINT
1542 REM

1550 IF a1 >= 0 THEN 1552
1551 PRINT:PRINT "You Win! Congratulations!! :-D": f = 1:PRINT
1552 REM

1560 RETURN
1570 IF v THEN 1572
1571 v = -1 : a = 0: p1 = 0: goto 1574
1572 if r$="y" THEN PRINT
1573 PRINT "You:";MID$(STR$(a),2);" CPU:";mid$(str$(p1),2);" Sum:";mid$(str$(u),2);" ";: c = c + 1: a = 0: p1 = 0
1574 IF r$="y" THEN PRINT
1575 rem

1580 RETURN
1590 GOSUB 2120
1600 m1 = r
1620 a1 = a1 + m1
1640 RETURN
1650 GOSUB 2120
1660 m1 = r
1680 a1 = a1 - m1
1700 RETURN
1710 GOSUB 2120
1720 m1 = r
1740 a1 = a1 * m1
1760 RETURN
1770 t = a1
1780 GOSUB 2120
1790 m1 = r
1810 a1 = a1 / m1
1830 r1 = t-int(t/m1)*m1  :rem  t MOD m1
1850 RETURN
1860 r = m1
1870 GOSUB 2210
1890 RETURN
1900 GOSUB 2120
1910 a1 = r
1930 RETURN
1940 t = a1
1950 GOSUB 2120
1960 a1 = r
1980 r = t
1990 GOSUB 2210
2010 RETURN
2020 a1 = ABS(a1)
2040 RETURN
2050 m1 = v
2070 RETURN
2080 r = 0
2090 GOSUB 2210
2110 RETURN
2120 IF v$<>"A" THEN 2122
2121 r=a1:RETURN
2122 if r$<>"y" then 2125
2123 gosub 3000
2125 rem sblend if r$<>"y" then print ".";

2130 IF v$<>"M" THEN 2132
2131 r=m1: RETURN
2132 REM

2140 IF v$<>"R" THEN 2142
2141 r=r1: RETURN
2142 REM

2150 IF v$<>"B1" THEN 2152
2151 r=b1: RETURN
2152 REM

2160 IF v$<>"B2" THEN 2162
2161 r=b2: RETURN
2162 REM

2170 IF v$<>"C1" THEN 2172
2171 r=c1: RETURN
2172 REM

2180 IF v$<>"C2" THEN 2182
2181 r=c2: RETURN
2182 REM

2190 IF v$<>"D1" THEN 2192
2191 r=d1: RETURN
2192 REM

2200 RETURN

2210 IF v$<>"A" THEN 2212
2211 a1=r: RETURN
2212 REM

2220 IF v$<>"M" THEN 2222
2221 m1=r: RETURN
2222 REM

2230 IF v$<>"R" THEN 2232
2231 r1=r: RETURN
2232 REM

2240 IF v$<>"B1" THEN 2242
2241 b1=r: RETURN
2242 REM

2250 IF v$<>"B2" THEN 2252
2251 b2=r: RETURN
2252 REM

2260 IF v$<>"C1" THEN 2262
2261 c1=r: RETURN
2262 REM

2270 IF v$<>"C2" THEN 2272
2271 c2=r: RETURN
2272 REM

2280 IF v$<>"D1" THEN 2282
2281 d1=r: RETURN
2282 REM

2290 RETURN

2300 PRINT:PRINT
2310 PRINT "Angela Game, originally developed for"
2320 PRINT "Olivetti Programma 101 Desktop Computer"
2330 PRINT "by:"
2340 PRINT "- Piergiorgio Perotto"
2350 PRINT "- Gastone Garziera"
2360 PRINT "- Giovanni De Sandre"
2370 PRINT "as a demo for BEMA in New York in 1965."
2380 PRINT
2390 PRINT "Conversion to QB 4.5 from JS in 2013:"
2400 PRINT "- Claudio Larini"
2410 PRINT
2420 PRINT "Conversion to Microsoft Basic in 2024:"
2430 PRINT "- Francesco Sblendorio"
2440 PRINT "- Claudio Parmigiani"
2450 PRINT: RETURN

2500 PRINT "INSTRUCTIONS:"
2510 PRINT "A numerical goal (between 30 and 100)"
2520 PRINT "is set to be reached."
2530 PRINT "The player chooses a value from 1 to 6"
2540 PRINT "and the program will then do the same."
2550 PRINT "The numbers will add up and, in doing"
2560 PRINT "so, you will get closer to the goal."
2570 PRINT
2580 PRINT "It is not allowed to play the previous"
2590 PRINT "number of the opponent, nor the value"
2600 PRINT "corresponding to its complement to 7:"
2610 PRINT "1/6, 2/5, 3/4."
2620 PRINT
2630 PRINT "On the first round only: enter 0 to"
2640 PRINT "make the computer start the game."
2650 PRINT "The player who reaches the goal first,"
2660 PRINT "or forces the opponent to exceed it,"
2670 PRINT "wins."
2680 PRINT
2690 RETURN

3000 PRINT "A=";MID$(STR$(A1),2);" M=";MID$(STR$(M1),2);" R=";MID$(STR$(R1),2);" B=";MID$(STR$(B1),2);
3010 PRINT " B'=";MID$(STR$(B2),2);" C=";MID$(STR$(C1),2);" C'=";MID$(STR$(C2),2);" D=";MID$(STR$(D1),2)
3020 RETURN

5000 PRINT:PRINT:INPUT "Another game";s$: IF s$="." THEN GOSUB 10000: GOTO 5000
5010 if s$="y" then 230
5020 end
10000 INPUT "QUIT: ARE YOU SURE";KK$
10010 IF KK$<>"Y" AND KK$<>"S" AND KK$<>"." THEN RETURN
10020 END
