1000 REM *** Based on Super Star Trek from "101 BASIC Computer Games" by David Ahl
1010 REM *** Modified and corrected by Lorenz Wiest 12-MAY-2014 (Final Version)
1020 REM *** (1) Adjusted coordinate system (X => left, Y => down)
1030 REM *** (2) Corrected direction vector calculation
1040 REM *** (3) Corrected direction calculation
1050 REM *** (4) Added HLP command
1060 REM *** (5) Accept lowercase commands
1070 DIM G(8,8),K(3,3),N(3),Z(8,8),D(8)
1080 PRINT
1090 PRINT
1100 PRINT
1110 PRINT
1120 PRINT
1130 PRINT
1140 PRINT
1150 PRINT
1160 PRINT
1170 PRINT
1180 PRINT
1190 PRINT TAB(21);"                ,------*------,"
1200 PRINT TAB(21);",-------------   '---  ------'"
1210 PRINT TAB(21);" '-------- --'      / /"
1220 PRINT TAB(21);"     ,---' '-------/ /--,"
1230 PRINT TAB(21);"      '----------------'"
1240 PRINT
1250 PRINT TAB(21);"The USS ENTERPRISE --- NCC-1701"
1260 PRINT
1270 PRINT
1280 PRINT
1290 PRINT
1300 PRINT
1310 T=INT(RND(1)*20+20)*100 : T0=T : T9=25+INT(RND(1)*10)
1320 D0=0
1330 E=3000 : E0=E
1340 P=10 : P0=P
1350 S=0 : S9=200
1360 B9=0 : K9=0
1370 X$="" : X0$="is"
1380 DEF FND(D)=SQR((K(I,1)-S1)^2+(K(I,2)-S2)^2)
1390 DEF FNR(R)=INT(RND(R)*7.98+1.01)
1400 REM *** Initialize ENTERPRISE's position ***
1410 Q1=FNR(1) : Q2=FNR(1)
1420 S1=FNR(1) : S2=FNR(1)
1430 FOR I=1 TO 8
1440 D(I)=0
1450 NEXT I
1460 A1$="NAVSRSLRSPHATORSHEDAMCOMXXXHLP"
1470 REM *** Set up what exists in Galaxy... ***
1480 FOR J=1 TO 8
1490 FOR I=1 TO 8
1500 Z(I,J)=0
1510 K3=0
1520 R1=RND(1)
1530 IF R1>0.98 THEN K3=3 : GOTO 1560
1540 IF R1>0.95 THEN K3=2 : GOTO 1560
1550 IF R1>0.80 THEN K3=1
1560 K9=K9+K3
1570 B3=0
1580 IF RND(1)>0.96 THEN B3=1 : B9=B9+1
1590 G(I,J)=K3*100+B3*10+FNR(1)
1600 NEXT I
1610 NEXT J
1620 IF K9>T9 THEN T9=K9+1
1630 IF B9<>0 THEN 1690
1640 IF G(Q1,Q2)<200 THEN G(Q1,Q2)=G(Q1,Q2)+100 : K9=K9+1
1650 B9=1
1660 G(Q1,Q2)=G(Q1,Q2)+10
1670 Q1=FNR(1)
1680 Q2=FNR(1)
1690 K7=K9
1700 IF B9<>1 THEN X$="s" : X0$="are"
1710 PRINT "Your orders are as follows:"
1720 PRINT "   Destroy the";K9;"Klingon warships which have invaded the Galaxy before"
1730 PRINT "   they can attack Federation Headquarters on Stardate";STR$(T0+T9);"."
1740 PRINT "   This gives you";T9;"days."
1750 PRINT "   There ";X0$;B9;"Starbase";X$;" in the Galaxy for resupplying your ship."
1760 PRINT
1770 PRINT "Hit RETURN when ready to accept command."
1780 INPUT "",A$
1790 REM *** Here anytime new quadrant entered ***
1800 Z4=Q1 : Z5=Q2 : K3=0 : B3=0 : S3=0 : G5=0 : D4=0.5*RND(1) : Z(Q1,Q2)=G(Q1,Q2)
1810 IF Q1<1 OR Q1>8 OR Q2<1 OR Q2>8 THEN 1970
1820 GOSUB 7740 : REM * Get quadrant name
1830 PRINT
1840 IF T0<>T THEN 1880
1850 PRINT "Your mission begins with your Starship located in the Galactic quadrant"
1860 PRINT G2$;"."
1870 GOTO 1890
1880 PRINT "Now entering ";G2$;" quadrant..."
1890 PRINT
1900 K3=INT(G(Q1,Q2)*0.01)
1910 B3=INT(G(Q1,Q2)*0.1)-10*K3
1920 S3=G(Q1,Q2)-100*K3-10*B3
1930 IF K3=0 THEN 1960
1940 PRINT "COMBAT AREA         CONDITION RED"
1950 IF S<=200 THEN PRINT "     SHIELDS DANGEROUSLY LOW"
1960 FOR I=1 TO 3 : K(I,1)=0 : K(I,2)=0 : NEXT I
1970 FOR I=1 TO 3 : K(I,3)=0 : NEXT I
1980 Q$=SPACE$(192) : REM 8 x 8 x 3 Characters 
1990 REM *** Position ENTERPRISE in quadrant, then place K3 Klingons, ***
2000 REM *** B3 starbases and S3 stars elsewhere                      ***
2010 A$="<*>" : Z1=S1 : Z2=S2 : GOSUB 7490 : REM * Place into quadrant
2020 IF K3<1 THEN 2080
2030 FOR I=1 TO K3
2040 GOSUB 7410 : REM * Get empty sector coordinates
2050 A$="+K+" : Z1=R1 : Z2=R2 : GOSUB 7490 : REM * Place into quadrant
2060 K(I,1)=R1 : K(I,2)=R2 : K(I,3)=S9*(0.5+RND(1))
2070 NEXT I
2080 IF B3<1 THEN 2110
2090 GOSUB 7410 : REM * Get empty sector coordinates
2100 A$=">!<" : Z1=R1 : B4=R1 : Z2=R2 : B5=R2 : GOSUB 7490 : REM * Place into quadrant
2110 FOR I=1 TO S3
2120 GOSUB 7410 : REM * Get empty sector coordinates
2130 A$=" * " : Z1=R1 : Z2=R2 : GOSUB 7490 : REM * Place into quadrant
2140 NEXT I
2150 GOSUB 5670 : REM * Short-range scan and game startup
2160 IF S+E>10 THEN IF E>10 OR D(7)=0 THEN 2240
2170 PRINT
2180 PRINT "*** FATAL ERROR ***"
2190 PRINT "You've just stranded your ship in space."
2200 PRINT "You have insufficient maneuvering energy and Shield Control is"
2210 PRINT "presently incapable of cross-circuiting to Engine Room!!!"
2220 PRINT
2230 GOTO 5440
2240 INPUT "Command ";A$
2250 IF LEN(A$)=0 THEN 2320
2260 X$=""
2270 FOR I=1 TO LEN(A$)
2280 C$=MID$(A$,I,1)
2290 IF C$>="a" AND C$<="z" THEN X$=X$+CHR$(ASC(C$)-32) ELSE X$=X$+C$
2300 NEXT I
2310 A$=X$
2320 FOR I=1 TO 10
2330 IF LEFT$(A$,3)<>MID$(A1$,3*I-2,3) THEN 2350
2340 ON I GOTO 2520,2150,3680,3890,4240,4770,4930,6100,5490,6970
2350 NEXT I
2360 PRINT "Enter one of the following:"
2370 PRINT "   NAV  (To set course)"
2380 PRINT "   SRS  (For short-range sensor scan)"
2390 PRINT "   LRS  (For long-range sensor scan)"
2400 PRINT "   PHA  (To fire phasers)"
2410 PRINT "   TOR  (To fire photon torpedoes)"
2420 PRINT "   SHE  (To raise or lower shields)"
2430 PRINT "   DAM  (For Damage Control Report)"
2440 PRINT "   COM  (To call on Library-Computer)"
2450 PRINT "   XXX  (To resign your command)"
2460 PRINT "   HLP  (To display help)"
2470 PRINT
2480 GOTO 2160
2490 REM
2500 REM *** Course control begins here ****************************************
2510 REM
2520 INPUT "Course (1-9) ";C1
2530 IF C1=9 THEN C1=1
2540 IF C1>=1 AND C1<9 THEN 2580
2550 PRINT "Lt. Sulu reports:"
2560 PRINT "   'Incorrect course data, Sir!'"
2570 GOTO 2160
2580 X$="8" : IF D(1)<0 THEN X$="0.2"
2590 PRINT "Warp Factor (0-";X$;") "; : INPUT W1
2600 IF D(1)<0 AND W1>0.2 THEN 2660
2610 IF W1>0 AND W1<=8 THEN 2680
2620 IF W1=0 THEN 2160
2630 PRINT "Chief Engineer Scott reports:"
2640 PRINT "   'The engines won't take Warp";STR$(W1);"!'"
2650 GOTO 2160
2660 PRINT "Warp engines are damaged. Maximum speed is Warp 0.2."
2670 GOTO 2160
2680 N=INT(W1*8+0.5)
2690 IF E-N>=0 THEN 2770
2700 PRINT "Engineering reports:"
2710 PRINT "   'Insufficient energy available for maneuvering at Warp";STR$(W1);"!'"
2720 IF S<N-E OR D(7)<0 THEN 2160
2730 PRINT "Deflector Control Room acknowledges:"
2740 PRINT "   '";MID$(STR$(S),2);" units of energy presently deployed to shields.'"
2750 GOTO 2160
2760 REM *** Klingons move/fire on moving Starship ***
2770 FOR I=1 TO K3
2780 IF K(I,3)=0 THEN 2830
2790 A$="   " : Z1=K(I,1) : Z2=K(I,2) : GOSUB 7490 : REM * Place into quadrant
2800 GOSUB 7410 : REM * Get empty sector coordinates
2810 K(I,1)=Z1 : K(I,2)=Z2
2820 A$="+K+" : GOSUB 7490 : REM * Place into quadrant
2830 NEXT I
2840 GOSUB 5220 : REM * Klingons fire
2850 D1=0
2860 D6=W1
2870 IF W1>=1 THEN D6=1
2880 FOR I=1 TO 8
2890 IF D(I)>=0 THEN 2970
2900 D(I)=D(I)+D6
2910 IF D(I)>-0.1 AND D(I)<0 THEN D(I)=-0.1 : GOTO 2970
2920 IF D(I)<0 THEN 2970
2930 IF D1<>1 THEN D1=1 : PRINT "Damage Control Report:"
2940 R1=I
2950 GOSUB 7550 : REM * Get device name
2960 PRINT "   '";G2$;": Repair completed.'"
2970 NEXT I
2980 IF RND(1)>0.2 THEN 3130
2990 R1=FNR(1)
3000 IF RND(1)>=0.6 THEN 3070
3010 D(R1)=D(R1)-(RND(1)*5+1)
3020 PRINT "Damage Control Report:"
3030 GOSUB 7550 : REM * Get device name
3040 PRINT "   '";G2$;": Damaged.'"
3050 PRINT
3060 GOTO 3130
3070 D(R1)=D(R1)+RND(1)*3+1
3080 PRINT "Damage Control Report:";
3090 GOSUB 7550 : REM * Get device name
3100 PRINT "   '";G2$;": State of Repair Improved.'"
3110 PRINT
3120 REM *** Begin moving Starship ***
3130 A$="   " : Z1=INT(S1) : Z2=INT(S2) : GOSUB 7490 : REM * Place into quadrant
3140 GOSUB 8020 : REM * Calc direction vector
3150 X=S1 : Y=S2 : Q4=Q1 : Q5=Q2
3160 FOR I=1 TO N
3170 S1=S1+X1 : S2=S2+X2
3180 IF S1<1 OR S1>=9 OR S2<1 OR S2>=9 THEN 3350
3190 S8=INT(S2)*24+INT(S1)*3-26
3200 IF MID$(Q$,S8,2)="  " THEN 3240
3210 S1=INT(S1-X1) : S2=INT(S2-X2)
3220 PRINT "Warp Engines shut down at sector";S1;",";S2;"due to bad navigation."
3230 GOTO 3260
3240 NEXT I
3250 S1=INT(S1) : S2=INT(S2)
3260 A$="<*>" : Z1=INT(S1) : Z2=INT(S2) : GOSUB 7490 : REM * Place into quadrant
3270 GOSUB 3590 : REM * Handle maneuver energy
3280 T8=1
3290 IF W1<1 THEN T8=0.1*INT(10*W1)
3300 T=T+T8
3310 IF T>T0+T9 THEN 5440
3320 REM *** See if docked, then get command ***
3330 GOTO 2150
3340 REM *** Exceeded quadrant limits ***
3350 X=8*Q1+X+N*X1 : Y=8*Q2+Y+N*X2
3360 Q1=INT(X/8) : Q2=INT(Y/8)
3370 S1=INT(X-Q1*8) : S2=INT(Y-Q2*8)
3380 IF S1=0 THEN Q1=Q1-1 : S1=8
3390 IF S2=0 THEN Q2=Q2-1 : S2=8
3400 X5=0
3410 IF Q1<1 THEN X5=1 : Q1=1 : S1=1
3420 IF Q1>8 THEN X5=1 : Q1=8 : S1=8
3430 IF Q2<1 THEN X5=1 : Q2=1 : S2=1
3440 IF Q2>8 THEN X5=1 : Q2=8 : S2=8
3450 IF X5=0 THEN 3520
3460 PRINT "Lt. Uhura reports message from Starfleet Command:"
3470 PRINT "   'Permission to attempt crossing of Galactic perimeter is hereby"
3480 PRINT "    *DENIED*. Shut down your engines.'"
3490 PRINT "Chief Engineer Scott reports:"
3500 PRINT "   'Warp Engines shut down at sector";S1;",";S2;"of quadrant";Q1;",";STR$(Q2);".'"
3510 IF T>T0+T9 THEN 5440
3520 IF 8*Q1+Q2=8*Q4+Q5 THEN 3260
3530 T=T+1
3540 GOSUB 3590 : REM * Handle maneuver energy
3550 GOTO 1800
3560 REM
3570 REM *** Handle maneuver energy ********************************************
3580 REM
3590 E=E-N-10
3600 IF E>=0 THEN RETURN
3610 PRINT "Shield Control supplies energy to complete the maneuvre."
3620 S=S+E : E=0
3630 IF S<=0 THEN S=0
3640 RETURN
3650 REM
3660 REM *** Long-range sensor scan code ***************************************
3670 REM
3680 IF D(3)<0 THEN PRINT "Long-range sensors are inoperable." : GOTO 2160
3690 PRINT "Long-range scan for quadrant";Q1;",";Q2
3700 O1$="-------------------"
3710 PRINT O1$
3720 FOR J=Q2-1 TO Q2+1
3730 N(1)=-1 : N(2)=-1 : N(3)=-1
3740 FOR I=Q1-1 TO Q1+1
3750 IF I>0 AND I<9 AND J>0 AND J<9 THEN N(I-Q1+2)=G(I,J) : Z(I,J)=G(I,J)
3760 NEXT I
3770 FOR L=1 TO 3
3780 PRINT ": ";
3790 IF N(L)<0 THEN PRINT "*** "; : GOTO 3810
3800 PRINT RIGHT$(STR$(N(L)+1000),3);" ";
3810 NEXT L
3820 PRINT ":"
3830 PRINT O1$
3840 NEXT J
3850 GOTO 2160
3860 REM
3870 REM *** Phaser Control code begins here ***********************************
3880 REM
3890 IF D(4)<0 THEN PRINT "Phasers inoperative." : GOTO 2160
3900 IF K3>0 THEN 3940
3910 PRINT "Science Officer Spock reports:"
3920 PRINT "   'Sensors show no enemy ships in this quadrant.'"
3930 GOTO 2160
3940 IF D(8)<0 THEN PRINT "Computer failure hampers accuracy."
3950 PRINT "Phasers locked on target. ";
3960 PRINT "Energy available =";E
3970 INPUT "Number of units to fire ";X
3980 IF X<=0 THEN 2160
3990 IF E-X<0 THEN 3960
4000 E=E-X
4010 IF D(7)<0 THEN X=X*RND(1)
4020 H1=INT(X/K3)
4030 FOR I=1 TO 3
4040 IF K(I,3)<=0 THEN 4180
4050 H=INT((H1/FND(0))*(RND(1)+2))
4060 IF H>0.15*K(I,3) THEN 4090
4070 PRINT "Sensors show no damage to enemy at";K(I,1);",";STR$(K(I,2));"."
4080 GOTO 4180
4090 K(I,3)=INT(K(I,3)-H)
4100 PRINT H;"unit hit on Klingon at sector";K(I,1);",";STR$(K(I,2));"."
4110 IF K(I,3)<=0 THEN PRINT "*** KLINGON DESTROYED ***" : GOTO 4140
4120 PRINT "   (Sensors show";K(I,3);"units remaining)"
4130 GOTO 4180
4140 K3=K3-1 : K9=K9-1
4150 A$="   " : Z1=K(I,1) : Z2=K(I,2) : GOSUB 7490 : REM * Place into quadrant
4160 K(I,3)=0 : G(Q1,Q2)=G(Q1,Q2)-100 : Z(Q1,Q2)=G(Q1,Q2)
4170 IF K9<=0 THEN 5570
4180 NEXT I
4190 GOSUB 5220 : REM * Klingons fire
4200 GOTO 2160
4210 REM
4220 REM *** Photon torpedo code begins here ***********************************
4230 REM
4240 IF P<=0 THEN PRINT "All photon torpedos expended." : GOTO 2160
4250 IF D(5)<0 THEN PRINT "Photon tubes are not operational." : GOTO 2160
4260 INPUT "Photon torpedo course (1-9) ";C1
4270 IF C1=9 THEN C1=1
4280 IF C1>=1 AND C1<9 THEN 4320
4290 PRINT "Ensign Chekov reports:"
4300 PRINT "   'Incorrect course data, Sir!'"
4310 GOTO 2160
4320 GOSUB 8020 : REM * Calc direction vector
4330 X=S1 : Y=S2 : E=E-2 : P=P-1
4340 PRINT "Torpedo track:"
4350 X=X+X1 : Y=Y+X2
4360 X3=INT(X+0.5) : Y3=INT(Y+0.5)
4370 IF X3<1 OR X3>8 OR Y3<1 OR Y3>8 THEN 4710
4380 PRINT "  ";X3;",";Y3
4390 A$="   " : Z1=X : Z2=Y : GOSUB 7670 : REM * Match sector
4400 IF Z3<>0 THEN 4350
4410 A$="+K+" : Z1=X : Z2=Y : GOSUB 7670 : REM * Match sector
4420 IF Z3=0 THEN 4520
4430 PRINT "*** KLINGON DESTROYED ***"
4440 K3=K3-1 : K9=K9-1
4450 IF K9<=0 THEN 5570
4460 FOR I=1 TO 3
4470 IF X3=K(I,1) AND Y3=K(I,2) THEN 4500
4480 NEXT I
4490 I=3
4500 K(I,3)=0
4510 GOTO 4670
4520 A$=" * " : Z1=X : Z2=Y : GOSUB 7670 : REM * Match sector
4530 IF Z3=0 THEN 4570
4540 PRINT "Star at";X3;",";Y3;"absorbed torpedo energy."
4550 GOSUB 5220 : REM * Klingons fire
4560 GOTO 2160
4570 A$=">!<" : Z1=X : Z2=Y : GOSUB 7670 : REM * Match sector
4580 IF Z3=0 THEN 4260
4590 PRINT "*** STARBASE DESTROYED ***"
4600 B3=B3-1 : B9=B9-1
4610 IF B9>0 OR K9>T-T0-T9 THEN 4650
4620 PRINT "That does it, Captain!!! You are hereby relieved of command and"
4630 PRINT "sentenced to 99 Stardates at hard labor on Cygnus 12!!!"
4640 GOTO 5490
4650 PRINT "Starfleet Command revieving your record to consider court martial!"
4660 D0=0
4670 A$="   " : Z1=X : Z2=Y : GOSUB 7490 : REM * Place into quadrant
4680 G(Q1,Q2)=K3*100+B3*10+S3 : Z(Q1,Q2)=G(Q1,Q2)
4690 GOSUB 5220 : REM * Klingons fire
4700 GOTO 2160
4710 PRINT "Torpedo missed."
4720 GOSUB 5220 : REM * Klingons fire
4730 GOTO 2160
4740 REM
4750 REM *** Shield Control ****************************************************
4760 REM
4770 IF D(7)<0 THEN PRINT "Shield Control inoperable." : GOTO 2160
4780 PRINT "Energy available =";E+S
4790 INPUT "Number of units to shields ";X
4800 IF X<0 OR S=X THEN PRINT "   (Shields unchanged.)" : GOTO 2160
4810 IF X<=E+S THEN 4860
4820 PRINT "Shield Control reports:"
4830 PRINT "   'This is not the Federation Treasury.'"
4840 PRINT "   (Shields unchanged.)"
4850 GOTO 2160
4860 E=E+S-X : S=X
4870 PRINT "Deflector Room reports:"
4880 PRINT "   'Shields now at";INT(S);"units per your command.'"
4890 GOTO 2160
4900 REM
4910 REM *** Damage Control ****************************************************
4920 REM
4930 IF D(6)>=0 THEN 5110
4940 PRINT "Damage Control Report not available."
4950 IF D0=0 THEN 2160
4960 D3=0
4970 FOR I=1 TO 8
4980 IF D(I)<0 THEN D3=D3+0.1
4990 NEXT I
5000 IF D3=0 THEN 2160
5010 PRINT
5020 D3=D3+D4 : IF D3>=1 THEN D3=0.9
5030 PRINT "Technician standing by to effect repairs to your ship;"
5040 PRINT "Estimated time to repair:";0.01*INT(100*D3);"Stardates."
5050 INPUT "Will you authorize the repair order (Y/N) ";A$
5060 IF A$<>"Y" THEN 2160
5070 FOR I=1 TO 8
5080 IF D(I)<0 THEN D(I)=0
5090 NEXT I
5100 T=T+D3+0.1
5110 PRINT
5120 PRINT "Device             State of Repair"
5130 FOR R1=1 TO 8
5140 GOSUB 7550 : REM * Get device name
5150 Z$=SPACE$(25)
5160 PRINT G2$;LEFT$(Z$,25-LEN(G2$));INT(D(R1)*100)*0.01
5170 NEXT R1
5180 PRINT
5190 IF D0<>0 THEN 4960
5200 GOTO 2160
5210 REM *** Klingons shooting ***
5220 IF K3<=0 THEN RETURN
5230 IF D0<>0 THEN PRINT "Starbase shields protect the ENTERPRISE." : RETURN
5240 FOR I=1 TO 3
5250 IF K(I,3)<=0 THEN 5390
5260 H=INT((K(I,3)/FND(0))*(2+RND(1)))
5270 S=S-H
5280 K(I,3)=K(I,3)/(3+RND(0))
5290 PRINT H;"unit hit on ENTERPRISE from sector";K(I,1);",";STR$(K(I,2));"."
5300 IF S<=0 THEN 5460
5310 PRINT "   (Shields down to";S;"units.)"
5320 IF H<20 THEN 5390
5330 IF RND(1)>0.6 OR H/S<=0.02 THEN 5390
5340 R1=FNR(1)
5350 D(R1)=D(R1)-H/S-0.5*RND(1)
5360 GOSUB 7550 : REM * Get device name
5370 PRINT "Damage Control reports:"
5380 PRINT "   '";G2$;" damaged by the hit.'"
5390 NEXT I
5400 RETURN
5410 REM
5420 REM *** End of game *******************************************************
5430 REM
5440 PRINT "It is Stardate";STR$(T);"."
5450 GOTO 5490
5460 PRINT
5470 PRINT "The ENTERPRISE has been destroyed. The Federation will be conquered."
5480 GOTO 5440
5490 PRINT "There were";K9;"Klingon Battle Cruisers left at the end of your mission."
5500 PRINT
5510 PRINT
5520 IF B9=0 THEN 5560
5530 PRINT "The Federation is in need of a new Starship commander for a similar mission."
5540 INPUT "If there is a volunteer, let him step forward and enter 'AYE' ";A$
5550 IF A$="AYE" THEN 1080
5560 END
5570 PRINT
5580 PRINT "CONGRATULATIONS, Captain!"
5590 PRINT
5600 PRINT "The last Klingon Battle Cruiser menacing the Federation has been destroyed."
5610 PRINT
5620 PRINT "Your efficiency rating is";STR$(1000*(K7/(T-T0))^2);"."
5630 GOTO 5500
5640 REM
5650 REM *** Short-range sensor scan & Startup subroutine **********************
5660 REM
5670 FOR J=S2-1 TO S2+1
5680 FOR I=S1-1 TO S1+1
5690 IF INT(I+0.5)<1 OR INT(I+0.5)>8 OR INT(J+0.5)<1 OR INT(J+0.5)>8 THEN 5720
5700 A$=">!<" : Z1=I : Z2=J : GOSUB 7670 : REM * Match sector
5710 IF Z3=1 THEN 5760
5720 NEXT I
5730 NEXT J
5740 D0=0
5750 GOTO 5800
5760 D0=1 : C$="Docked" : E=E0 : P=P0
5770 PRINT "Shields dropped for docking purposes."
5780 S=0
5790 GOTO 5830
5800 IF K3>0 THEN C$="*RED*" : GOTO 5830
5810 C$="Green"
5820 IF E<E0*0.1 THEN C$="Yellow"
5830 IF D(2)>=0 THEN 5880
5840 PRINT
5850 PRINT "*** SHORT-RANGE SENSORS ARE OUT ***"
5860 PRINT
5870 RETURN
5880 O1$="---------------------------------"
5890 PRINT O1$
5900 FOR J=1 TO 8
5910 FOR I=(J-1)*24+1 TO (J-1)*24+22 STEP 3
5920 PRINT " ";MID$(Q$,I,3);
5930 NEXT I
5940 PRINT "        ";
5950 ON J GOTO 5960,5970,5980,5990,6000,6010,6020,6030
5960 PRINT "Stardate          ";INT(T*10)*0.1 : GOTO 6040
5970 PRINT "Condition          ";C$ : GOTO 6040
5980 PRINT "Quadrant          ";Q1;",";Q2 : GOTO 6040
5990 PRINT "Sector            ";S1;",";S2 : GOTO 6040
6000 PRINT "Photon Torpedoes  ";INT(P) : GOTO 6040
6010 PRINT "Total Energy      ";INT(E+S) : GOTO 6040
6020 PRINT "Shields           ";INT(S) : GOTO 6040
6030 PRINT "Klingons Remaining";INT(K9)
6040 NEXT J
6050 PRINT O1$
6060 RETURN
6070 REM
6080 REM *** Library-Computer code *********************************************
6090 REM
6100 IF D(8)<0 THEN PRINT "Computer disabled." : GOTO 2160
6110 INPUT "Computer active and awaiting command ";A
6120 IF A<0 THEN 2160
6130 PRINT
6140 H8=1
6150 ON A+1 GOTO 6310,6580,6700,6900,6790,6260
6160 PRINT "Functions avilable from Library-Computer:"
6170 PRINT "   0 = Cumulative Galactic Record"
6180 PRINT "   1 = Status Report"
6190 PRINT "   2 = Photon Torpedo Data"
6200 PRINT "   3 = Starbase Nav Data"
6210 PRINT "   4 = Direction/Distance Calculator"
6220 PRINT "   5 = Galaxy 'Region Name' Map"
6230 PRINT
6240 GOTO 6110
6250 REM *** Setup to change Cum.Gal.Record to Galaxy Map ***
6260 H8=0
6270 G5=1
6280 PRINT "                       The Galaxy"
6290 GOTO 6320
6300 REM *** Cum. Galactic Record ***
6310 PRINT "      Computer Record of Galaxy for Quadrant";Q1;",";Q2
6320 PRINT
6330 PRINT "       1     2     3     4     5     6     7     8"
6340 O1$="     ----- ----- ----- ----- ----- ----- ----- -----"
6350 PRINT O1$
6360 FOR J=1 TO 8
6370 PRINT J;
6380 IF H8=0 THEN 6450
6390 FOR I=1 TO 8
6400 PRINT "   ";
6410 IF Z(I,J)=0 THEN PRINT "***"; : GOTO 6430
6420 PRINT RIGHT$(STR$(Z(I,J)+1000),3);
6430 NEXT I
6440 GOTO 6520
6450 Z5=J
6460 Z4=1 : GOSUB 7740 : REM * Get quadrant name
6470 J0=INT(18-0.5*LEN(G2$))
6480 PRINT TAB(J0);G2$;
6490 Z4=5 : GOSUB 7740 : REM * Get quadrant name
6500 J0=INT(42-0.5*LEN(G2$))
6510 PRINT TAB(J0);G2$;
6520 PRINT
6530 PRINT O1$
6540 NEXT J
6550 PRINT
6560 GOTO 2160
6570 REM *** Status Report ***
6580 PRINT "STATUS REPORT"
6590 PRINT
6600 X$="" : IF K9>1 THEN X$="s"
6610 PRINT "Klingon";X$;" left:";K9
6620 PRINT "Mission must be completed in";0.1*INT((T0+T9-T)*10);"Stardates."
6630 X$="s" : IF B9<2 THEN X$="" : IF B9<1 THEN 6660
6640 PRINT "The Federation is maintaining";B9;"Starbase";X$;" in the Galaxy."
6650 GOTO 4930
6660 PRINT "Your stupidity has left you on your own in the Galaxy."
6670 PRINT "--- You have no Starbases left!"
6680 GOTO 4930
6690 REM *** Torpedo, Base Nav, D/D Calculator ***
6700 IF K3<=0 THEN 3910
6710 X$="" : IF K3>1 THEN X$="s"
6720 PRINT "From ENTERPRISE to Klingon Battle Cruiser";X$;":"
6730 H8=0
6740 FOR I=1 TO 3
6750 IF K(I,3)<=0 THEN 6880
6760 DSTX=K(I,1) : DSTY=K(I,2)
6770 SRCX=S1 : SRCY=S2
6780 GOTO 6830
6790 PRINT "Direction/Distance Calculator:"
6800 PRINT "You are in quadrant";Q1;",";Q2;"sector";S1;",";STR$(S2);"."
6810 INPUT "Enter initial coordinates (X,Y) ";SRCX,SRCY
6820 INPUT "Enter final coordinates (X,Y) ";DSTX,DSTY
6830 DY=DSTY-SRCY : DX=DSTX-SRCX
6840 GOSUB 8120 : REM * Calc direction
6850 PRINT "Direction =";DIR
6860 PRINT "Distance =";SQR(DY^2+DX^2)
6870 IF H8=1 THEN 2160
6880 NEXT I
6890 GOTO 2160
6900 IF B3<>0 THEN PRINT "From ENTERPRISE To Starbase:" : DSTX=B4 : DSTY=B5 : GOTO 6770
6910 PRINT "Mr. Spock reports:"
6920 PRINT "   'Sensors show no Starbases in this quadrant.'"
6930 GOTO 2160
6940 REM
6950 REM *** Help **************************************************************
6960 REM
6970 PRINT
6980 PRINT "You are the Captain of the Starship ENTERPRISE. Navigate the universe and"
6990 PRINT "destroy all Klingon cruisers. Dock at a Starbase to refuel and repair the"
7000 PRINT "ENTERPRISE."
7010 PRINT
7020 PRINT "The universe consists of 8 x 8 quadrants, each quadrant of 8 x 8 sectors."
7030 PRINT
7040 PRINT "Available commands are:"
7050 PRINT
7060 PRINT "  NAV  Set the course of the ENTERPRISE. Directions are given as"
7070 PRINT "          4 3 2"
7080 PRINT "           \|/"
7090 PRINT "          5-+-1"
7100 PRINT "           /|\"
7110 PRINT "          6 7 8"
7120 PRINT "  SRS  Display a short-range-sensor scan, a map of the sector you are in."
7130 PRINT "       It displays the following objects:"
7140 PRINT "          <*> - ENTERPRISE"
7150 PRINT "          +K+ - Klingon cruiser"
7160 PRINT "          >!< - Starbase"
7170 PRINT "           *  - Star"
7180 PRINT "  LRS  Display a long-range-sensor scan, a map of the surrounding"
7190 PRINT "       quadrants. They are encoded as ABC with"
7200 PRINT "          A   - Number of Klingon cruisers"
7210 PRINT "          B   - Number of Starbases"
7220 PRINT "          C   - Number of Stars"
7230 PRINT "  PHA  Fire phasers"
7240 PRINT "  TOR  Fire photon torpedoes"
7250 PRINT "  SHE  Raise or lower shields"
7260 PRINT "  DAM  Display Damage-Control Report"
7270 PRINT "  COM  Call on Library-Computer. Available computer commands are:"
7280 PRINT "          0   - Cumulative Galactic Record"
7290 PRINT "          1   - Status Report"
7300 PRINT "          2   - Photon Torpedo Data"
7310 PRINT "          3   - Starbase Nav Data"
7320 PRINT "          4   - Direction/Distance Calculator"
7330 PRINT "          5   - Galaxy 'Region Name' Map"
7340 PRINT "  XXX  Resign your command"
7350 PRINT "  HLP  Display help"
7360 PRINT
7370 GOTO 2160
7380 REM
7390 REM *** Get empty sector coordinates **************************************
7400 REM
7410 R1=FNR(1)
7420 R2=FNR(1)
7430 A$="   " : Z1=R1 : Z2=R2 : GOSUB 7670 : REM * Match sector
7440 IF Z3=0 THEN 7410 : REM * Get empty sector coordinates
7450 RETURN
7460 REM
7470 REM *** Place into quadrant ***********************************************
7480 REM
7490 S8=INT(Z2-0.5)*24+INT(Z1-0.5)*3+1
7500 Q$=LEFT$(Q$,S8-1)+A$+RIGHT$(Q$,190-S8)
7510 RETURN
7520 REM
7530 REM *** Get device name ***************************************************
7540 REM
7550 ON R1 GOTO 7560,7570,7580,7590,7600,7610,7620,7630
7560 G2$="Warp Engines" : RETURN
7570 G2$="Short-Range Sensors" : RETURN
7580 G2$="Long-Range Sensors" : RETURN
7590 G2$="Phaser Control" : RETURN
7600 G2$="Photon Tubes" : RETURN
7610 G2$="Damage Control" : RETURN
7620 G2$="Shield Control" : RETURN
7630 G2$="Library-Computer" : RETURN
7640 REM 
7650 REM *** Match sector ******************************************************
7660 REM 
7670 Z1=INT(Z1+0.5) : Z2=INT(Z2+0.5)
7680 S8=(Z2-1)*24+(Z1-1)*3++1
7690 Z3=0 : IF MID$(Q$,S8,3)=A$ THEN Z3=1
7700 RETURN
7710 REM 
7720 REM *** Get quadrant name *************************************************
7730 REM 
7740 IF Z4>4 THEN 7840
7750 ON Z5 GOTO 7760,7770,7780,7790,7800,7810,7820,7830
7760 G2$="ANTARES" : GOTO 7930
7770 G2$="RIGEL" : GOTO 7930
7780 G2$="PROCYON" : GOTO 7930
7790 G2$="VEGA" : GOTO 7930
7800 G2$="CANOPUS" : GOTO 7930
7810 G2$="ALTAIR" : GOTO 7930
7820 G2$="SAGITTARIUS" : GOTO 7930
7830 G2$="POLLUX" : GOTO 7930
7840 ON Z5 GOTO 7850,7860,7870,7880,7890,7900,7910,7920
7850 G2$="SIRIUS" : GOTO 7930
7860 G2$="DENEB" : GOTO 7930
7870 G2$="CAPELLA" : GOTO 7930
7880 G2$="BETELGEUSE" : GOTO 7930
7890 G2$="ALDEBARAN" : GOTO 7930
7900 G2$="REGULUS" : GOTO 7930
7910 G2$="ARCTURUS" : GOTO 7930
7920 G2$="SPICA"
7930 IF G5<>1 THEN ON Z4 GOTO 7950,7960,7970,7980,7950,7960,7970,7980
7940 RETURN
7950 G2$=G2$+" I" : RETURN
7960 G2$=G2$+" II" : RETURN
7970 G2$=G2$+" III" : RETURN
7980 G2$=G2$+" IV" : RETURN
7990 REM
8000 REM *** Calc direction vector *********************************************
8010 REM
8020 PI4=3.14159265/4
8030 IF C1<2 THEN X1=1 : X2=-TAN((C1-1)*PI4) : GOTO 8080
8040 IF C1>=2 AND C1<4 THEN X1=-TAN((C1-3)*PI4) : X2=-1 : GOTO 8080
8050 IF C1>=4 AND C1<6 THEN X1=-1 : X2=TAN((C1-5)*PI4) : GOTO 8080
8060 IF C1>=6 AND C1<8 THEN X1=TAN((C1-7)*PI4) : X2=1 : GOTO 8080
8070 IF C1>=8 THEN X1=1 : X2=-TAN((C1-9)*PI4)
8080 RETURN
8090 REM
8100 REM *** Calc direction ****************************************************
8110 REM
8120 PI4=3.14159265/4
8130 IF ABS(DX)<ABS(DY) THEN 8170
8140 ANG=-ATN(DY/DX)/PI4
8150 IF (DX>=0) THEN OFF=1 ELSE OFF=5
8160 GOTO 8190
8170 ANG=ATN(DX/DY)/PI4
8180 IF (DY>=0) THEN OFF=7 ELSE OFF=3
8190 DIR=OFF+ANG
8200 IF DIR<1 THEN DIR=DIR+8
8210 RETURN