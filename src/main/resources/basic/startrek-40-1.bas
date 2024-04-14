5 DIM MEM(1024)
6 GOSUB 2300
7 CLS
10 C$="      "
20 Q=640:S=704:D$="    * >!<+++<*>"
30 INPUT "TYPE A NUMBER ",RR$ : R = VAL(RR$)
35 IF RR$="." THEN GOSUB 2200 : GOTO 30
40 IF R=0 THEN RANDOMIZE TIMER ELSE RANDOMIZE R
50 B=Q:GOSUB 1100
60 S1=75:N=S1:V=1:L=10:GOSUB 1200
70 B1=2:N=B1:V=10:L=20:GOSUB 1200
80 K1=7:N=K1:V=20:L=40:GOSUB 1200
90 T=15:GOSUB 1300:E0=INT(RND(1)*4096):GOSUB 1400:GOSUB 1000
110 PRINT
111 INPUT "COMMAND ",CC$: C=VAL(CC$)
113 IF CC$="." THEN GOSUB 2200 : GOTO 111
114 IF CC$="" OR C<0 OR C>5 THEN 110
115 C=C+1: ON C GOTO 200,300,400,500,600,700
200 INPUT "VECTOR ",XY$ : IF XY$ = "." THEN GOSUB 2200 : GOTO 200
201 IF XY$="" THEN X=0:Y=0:GOTO 206
202 IF INSTR(XY$,",")=0 OR INSTR(XY$,",")=1 OR INSTR(XY$,",")=LEN(XY$) THEN 200
205 GOSUB 2100
206 X0=X0+X:Y0=Y0+Y:E1=E1-ABS (X)-ABS (Y)
210 IF X0<0 OR X0>63 OR Y0<0 OR Y0>63 THEN 250
220 E0=E0+X+64*Y:Q1=Q0:S1=S0:GOSUB 1400:IF Q0=Q1 THEN 255
230 E1=E1-25:T=T-1:GOSUB 1000:GOSUB 1400:IF T>=0 THEN 260
240 C$="TIME":GOTO 980
250 C$="GALAXY":GOTO 980
255 MEM(S+S1)=0:GOSUB 1050
260 GOSUB 1800:IF B2=0 THEN 290
265 IF ABS (X6-X2)+ABS (Y6-Y2)<>1 THEN 290
270 PRINT "- DOCKED -":GOSUB 1300:B1=B1-1:B2=0
280 MEM(Q+Q0)=MEM(Q+Q0)-10:MEM(S+X6+8*Y6)=0:GOTO 110
290 IF K2<>0 THEN GOSUB 550
295 GOTO 110
300 C$="SHORT":GOSUB 350:GOSUB 2000:GOTO 110
350 PRINT C$;" RANGE SENSOR SCAN":GOSUB 360:RETURN
360 PRINT "FOR QUADRANT ";MID$(STR$(X1),2);",";MID$(STR$(Y1),2):RETURN
400 C$="LONG":GOSUB 350:N=3:GOSUB 880
410 FOR Y=Y1+1 TO Y1-1 STEP -1:F2=0:IF Y<0 OR Y>7 THEN F2=1
415 GOSUB 470:GOSUB 480
420 FOR X=X1-1 TO X1+1:F1=0:IF X<0 OR X>7 THEN F1=1
430 IF F1=0 AND F2=0 THEN 440
435 PRINT C$;:GOTO 460
440 Q9=X+8*Y:GOSUB 1500:MEM(Q+Q9)=C9+40:PRINT "! ";MID$(STR$(K2),2);MID$(STR$(B2),2);MID$(STR$(S2),2);" ";
460 NEXT X:PRINT "!":GOSUB 480:NEXT Y:GOSUB 470:GOSUB 890:GOTO 110
470 C$="+-----":GOSUB 1600:RETURN
480 C$="!     ":GOSUB 1600:RETURN
500 IF K2=0 THEN 690
510 GOSUB 790
515 PRINT "ENERGY: ";E1:INPUT "FIRE ",CC$ : C = VAL(CC$)
516 IF CC$="." THEN GOSUB 2200 : GOTO 515
517 IF C<1 THEN 110
520 E1=E1-C:GOSUB 1800:K5=K5-C-(R*9):IF K5>0 THEN 530
525 GOSUB 1900:GOTO 110
530 GOSUB 550:GOTO 500
550 J5=K5\5:K5=K5-J5:E1=E1-J5\R:PRINT MID$(STR$(J5\R),2);" UNITS OF PHASER DAMAGE":GOSUB 1800:RETURN
600 IF K2=0 THEN 690
605 IF E2<>0 THEN 610
606 PRINT "NO MORE TORPEDOES":GOTO 110
610 E2=E2-1:IF R>INT(RND(1)*15) THEN 640
615 GOSUB 1900:GOTO 110
640 PRINT "YOU MISSED!":GOSUB 550:GOTO 110
690 PRINT "NOTHING TO SHOOT AT!":GOTO 110
700 INPUT "COMPUTER REQUEST ",CC$ : C=VAL(CC$)
704 IF CC$="." THEN GOSUB 2200 : GOTO 700
706 IF C=0 THEN 800
710 PRINT "STATUS REPORT:":PRINT
720 GOSUB 790:PRINT "KLINGONS  =";K1
730 GOSUB 790:PRINT "STARDATES =";T
740 GOSUB 790:PRINT "STARBASES =";B1
750 GOSUB 790:PRINT "TORPEDOES =";E2
760 GOSUB 790:PRINT "ENERGY    =";E1
770 GOTO 110
790 PRINT "REMAINING ";:RETURN
800 PRINT " GALACTIC MAP":PRINT " ";:GOSUB 360:C$=" ---":N=8
810 GOSUB 880:FOR Y=7 TO 0 STEP -1:GOSUB 1600
820 FOR X=0 TO 7:Q9=X+8*Y:GOSUB 1500:P=MEM(Q+Q9)
830 IF P>39 THEN 840
835 PRINT "    ";:GOTO 850
840 PRINT " ";MID$(STR$(K2),2);MID$(STR$(B2),2);MID$(STR$(S2),2);
850 NEXT X:PRINT
860 NEXT Y:GOSUB 1600:GOSUB 890:GOTO 110
880 B8=B2:K8=K2:RETURN
890 B2=B8:K2=K8:RETURN
980 PRINT:PRINT "-- OUT OF ";C$;" --"
990 PRINT "YOU LEFT ";K1;" KLINGONS"
991 PRINT : INPUT "PRESS ENTER TO EXIT"; ZZ$
999 END
1000 B=S:GOSUB 1100:Q9=Q0:GOSUB 1500:MEM(Q+Q9)=C9+40
1010 N=S2:V=1:L=2:GOSUB 1200
1020 N=K2:V=3:L=4:GOSUB 1200:K5=100:Y5=I\8:X5=I-8*Y5
1030 N=B2:V=2:L=3:GOSUB 1200:Y6=I\8:X6=I-8*Y6
1035 C$="GREEN":IF K2<>0 THEN C$="RED!"
1036 PRINT:PRINT "STARDATE: ";MID$(STR$(3278-T),2);",  CONDITION: ";C$
1040 PRINT "QUADRANT ";MID$(STR$(X1),2);",";MID$(STR$(Y1),2);"  -  SECTOR ";MID$(STR$(X2),2);",";MID$(STR$(Y2),2)
1050 P=MEM(S+S0):MEM(S+S0)=4:IF P=0 THEN RETURN
1060 PRINT:PRINT "*** COLLISION WITH A ";
1070 IF P=1 THEN PRINT "STAR";
1075 IF P=2 THEN PRINT "STARBASE";
1076 IF P=3 THEN PRINT "KLINGON";
1080 PRINT " ***":IF P=3 THEN K1=K1-1
1085 GOTO 990
1100 FOR K=0 TO 63:MEM(B+K)=0:NEXT K:RETURN
1200 IF N=0 THEN RETURN
1205 FOR K=1 TO N
1210 I=INT(RND(1)*64):P=MEM(B+I):IF P+V>=L THEN 1210
1215 MEM(B+I)=P+V:NEXT K:RETURN
1300 E1=500:E2=3:RETURN
1400 Y0=E0\64:X0=E0-64*Y0:Y1=Y0\8:X1=X0\8:Y2=Y0-8*Y1:X2=X0-8*X1
1410 Q0=X1+8*Y1:S0=X2+8*Y2:R=ABS (X5-X2)+ABS (Y5-Y2):RETURN
1500 C9=MEM(Q+Q9):IF C9>=40 THEN C9=C9-40
1510 S2=C9-10*(C9\10):K2=C9\20:B2=(C9-S2-20*K2)\10:RETURN
1600 FOR J=1 TO N:PRINT C$;:NEXT J:PRINT MID$(C$,1,1):RETURN
1800 IF E1>0 THEN RETURN
1810 C$="ENERGY":GOTO 980
1900 PRINT "*** BOOM ***":K1=K1-1:K2=0:MEM(Q+Q0)=MEM(Q+Q0)-20:MEM(S+X5+8*Y5)=0
1910 IF K1 THEN RETURN
1915 PRINT:PRINT "MISSION ACCOMPLISHED!":END
2000 C$="+---":N=8:GOSUB 1600
2010 FOR Y=7 TO 0 STEP -1:FOR X=0 TO 7
2020 P=3*MEM(S+X+8*Y)+1:IF X=0 THEN PRINT "!";
2025 IF X<>0 THEN PRINT " ";
2026 PRINT MID$(D$,P,3);
2030 NEXT X:PRINT "!":IF Y=0 THEN 2040
2035 PRINT "+";TAB(33);"+"
2040 NEXT Y:GOSUB 1600:RETURN
2100 IF XY$="" THEN X=0:Y=0:RETURN
2110 CP=INSTR(XY$,",")
2120 IF CP=0 THEN X=VAL(XY$) : GOTO 2140
2130 IF CP=1 THEN X = 0 ELSE X=VAL(MID$(XY$,1,CP-1))
2140 IF CP=0 THEN Y = 0 ELSE Y=VAL(MID$(XY$,CP+1))
2150 RETURN
2200 INPUT "QUIT: ARE YOU SURE";KK$
2210 IF KK$<>"Y" AND KK$<>"S" AND KK$<>"." THEN RETURN
2220 END
2300 CLS : WT = 6 : PRINT : PRINT
2301 PRINT TAB(WT);"                ,------*------,"
2302 PRINT TAB(WT);",-------------   '---  ------'"
2303 PRINT TAB(WT);" '-------- --'      / /"
2304 PRINT TAB(WT);"     ,---' '-------/ /--,"
2305 PRINT TAB(WT);"      '----------------'"
2306 PRINT
2307 PRINT "  APPLE STAR-TREK BY ROBERT J. BISHOP"
2308 PRINT "  RE-TYPED MARCH 2005 BY PETE TURNBULL"
2309 PRINT "  FROM A SCANNED LISTING PROVIDED BY"
2310 PRINT "  BOB BISHOP." : PRINT : PRINT "  INSTRUCTIONS BY COREY COHEN." : PRINT
2311 PRINT "  PORTING TO MICROSOFT-LIKE BASIC BY"
2312 PRINT "  FRANCESCO SBLENDORIO (2024)"
2313 PRINT : PRINT
2314 INPUT "DO YOU WANT INSTRUCTIONS (Y/N)"; CC$
2315 IF CC$="." THEN GOSUB 2200 : GOTO 2314
2316 CLS : IF CC$="N" THEN RETURN
2317 PRINT "Mission:find and shoot down the Klingon"
2318 PRINT "The galaxy is divided into 64 quadrants"
2319 PRINT "arranged in an 8x8 grid; each quadrant"
2320 PRINT "is further subdivided into 8x8 sectors."
2321 PRINT "You are allotted 15 stardates and have"
2322 PRINT "two starbases at which you can refuel."
2323 PRINT "You are initially supplied with three"
2324 PRINT "photon torpedoes and 500 units of"
2325 PRINT "energy. Your energy supply is used to"
2326 PRINT "move you around the galaxy, (b) fire"
2327 PRINT "your phasers, and (c) protect the"
2328 PRINT "Enterprise via its deflection shields"
2329 PRINT "which are automatically activated by"
2330 PRINT "the on-board computer every time a"
2331 PRINT "Klingon fires at you."
2332 PRINT "Each time you enter or maneuver within"
2333 PRINT "a quadrant containing a Klingon, he"
2334 PRINT "will shoot at you, and the amount of"
2335 PRINT "damage his phasers did to your shields"
2336 PRINT "will be indicated. Each time you shoot"
2337 PRINT "at him with either phasers or photon"
2338 PRINT "torpedoes and fail to destroy him, he"
2339 PRINT "will also return fire upon you."
2340 INPUT "- 'P' TO PLAY, ENTER TO CONTINUE  ";ZZ$
2341 IF ZZ$="." THEN GOSUB 2200: GOTO 2340
2342 IF ZZ$="P" THEN RETURN
2343 CLS:PRINT "The commands are numbered from 0 to 5:"
2344 PRINT " 0: Moves the Enterprise. Computer"
2345 PRINT "asks ";CHR$(34);"VECTOR ?";CHR$(34);", to which you specify"
2346 PRINT "the number of sectors you want to move,"
2347 PRINT "both horizontally and vertically."
2348 PRINT "Both positive and negative values are"
2349 PRINT "allowed. These two vector commands must"
2350 PRINT "be separated by a comma; for example:"
2351 PRINT "-21,35 would move the Enterprise 21"
2352 PRINT "sectors to the left of its current"
2354 PRINT "position, and 35 sectors up."
2355 PRINT " 1: Short Range Sensor Scan. Prints the"
2356 PRINT "quadrant you are currently in, with the"
2357 PRINT "Enterprise represented by: <*>"
2358 PRINT "Klingons represented by:   +++"
2359 PRINT "starbases by: >!<  and stars by *"
2360 PRINT " 2: Long Range Sensor Scan. Displays a"
2361 PRINT "3x3 array of nearest-neighbor quadrants"
2362 PRINT "with the Enterprise's quadrant in the"
2363 PRINT "center. The scan is coded in the form:"
2364 PRINT "KBS, where K is the number of Klingons,"
2365 PRINT "B is the number of starbases, and S is"
2366 PRINT "the number of stars in the quadrant."
2367 INPUT "- 'P' TO PLAY, ENTER TO CONTINUE  ";ZZ$
2368 IF ZZ$="." THEN GOSUB 2200: GOTO 2367
2369 IF ZZ$="P" THEN RETURN
2370 CLS:PRINT " 3: Fire Phasers. The computer informs"
2371 PRINT "you as to how much total energy you"
2372 PRINT "have left, and then waits for you to"
2373 PRINT "indicate how much of that energy you"
2374 PRINT "want to fire at the enemy. (Note: the"
2375 PRINT "closer you are the more effect your"
2376 PRINT "phasers will have, and conversely!)" : PRINT
2377 PRINT " 4: Fire Photon Torpedo. You have no"
2388 PRINT "control over the course of the torpedo;"
2389 PRINT "the on-board computer automatically"
2390 PRINT "aims at the enemy, taking care to avoid"
2391 PRINT "hitting any intervening stars or"
2392 PRINT "starbases. (Again, the closer you are,"
2393 PRINT "the better your chance of hitting)" : PRINT
2394 PRINT " 5: Library Computer. The library"
2395 PRINT "computer allows for the following two"
2396 PRINT "requests:"
2397 PRINT " REQUEST = Zero: Cumulative record of"
2398 PRINT "  the results of all previous long"
2399 PRINT "  range sensor scans of the galaxy."
2400 PRINT " REQUEST = Non-zero: Status Report"
2401 INPUT "- 'P' TO PLAY, ENTER TO CONTINUE  ";ZZ$
2402 IF ZZ$="." THEN GOSUB 2200: GOTO 2401
2403 IF ZZ$="P" THEN RETURN
2404 CLS : PRINT "EXPENDITURE OF SUPPLIES"
2405 PRINT
2406 PRINT "Moving from one quadrant to another"
2407 PRINT "uses up energy and one stardate."
2408 PRINT "However, moving within a given quadrant"
2409 PRINT "uses up only energy."
2410 PRINT ""
2411 PRINT "RELATIVE POSITIONS CHANGE WITH TIME"
2412 PRINT ""
2413 PRINT "Much can happen in a few stardates! So"
2414 PRINT "if you leave a quadrant and then later"
2415 PRINT "return, don't expect the Klingons,"
2416 PRINT "stars, etc to still be in the same"
2417 PRINT "relative positions that they were in"
2418 PRINT "when you left! The number of each will"
2419 PRINT "still be the same, but their positions"
2420 PRINT "will be different. This means that"
2421 PRINT "whenever you enter a new quadrant, you"
2422 PRINT "don't know just"
2423 PRINT "where the various objects will be; in"
2424 PRINT "fact don't be surprised if once in a"
2425 PRINT "while you collide with things!!!"
2426 PRINT
2427 INPUT "- 'P' TO PLAY, ENTER TO CONTINUE  ";ZZ$
2428 IF ZZ$="." THEN GOSUB 2200: GOTO 2427
2429 IF ZZ$="P" THEN RETURN
2430 CLS : PRINT "REPLENISHMENT OF SUPPLIES"
2431 PRINT
2432 PRINT "Docking at a starbase re-initializes"
2433 PRINT "your supply of photon torpedoes to 3,"
2434 PRINT "and your energy supply 500. Docking is"
2435 PRINT "accomplished by moving the Enterprise"
2436 PRINT "to any one of the four sectors"
2437 PRINT "immediately adjacent a starbase, above,"
2438 PRINT "below, left, or right."
2439 PRINT
2440 PRINT "BATTLE RETREAT"
2441 PRINT "Firing zero units of phaser energy will"
2442 PRINT "return you to command mode. This allows"
2443 PRINT "you to retreat from battle."
2444 PRINT
2445 PRINT "GALAXY CO-ORDINATE SYSTEM"
2446 PRINT "Quadrant 0,0 is the lower left hand"
2447 PRINT "quadrant of the galaxy and quadrant 7,7"
2448 PRINT "is the upper right.Likewise, sector 0,0"
2449 PRINT "is in the lower left hand corner of the"
2450 PRINT "quadrant and 7,7 in the upper right."
2452 PRINT: PRINT "Enter '.' to quit game."
2451 INPUT "- END OF INSTRUCTIONS. PRESS ENTER";ZZ$
2454 IF ZZ$="." THEN GOSUB 2200: GOTO 2453
2499 RETURN
