1 CLS: RANDOMIZE TIMER
2 PRINT "    ORBIT":PRINT:PRINT
4 PRINT "    CREATIVE COMPUTING"
5 PRINT "    MORRISTOWN, NEW JERSEY"
6 PRINT:PRINT
10 PRINT "Somewhere above your planet there is a "
15 PRINT "romulan ship. the ship is in a constant"
20 PRINT "polar orbit. its distance from the "
25 PRINT "center of your planet is from 10,000 to"
30 PRINT "30,000 miles and at its present "
31 PRINT "velocity can circle your planet"
35 PRINT "once every 12 to 36 hours."
38 PRINT
40 PRINT "Unfortunately, they are using a "
45 PRINT "cloaking device so you are unable to"
50 PRINT "see them, but with a special instrument"
52 PRINT "you can tell how near their ship your"
53 PRINT "photon bomb exploded."
54 PRINT: PRINT: PRINT TAB(8)"Hit ENTER to continue ";
55 LINE INPUT A$:IF A$="." THEN GOSUB 10000
58 CLS
60 PRINT "You have seven hours until they have"
64 PRINT "built up sufficient power in order to"
65 PRINT "escape your planet's gravity."
70 PRINT
75 PRINT "Your planet has enough power to fire "
80 PRINT "one bomb an hour."
82 PRINT
85 PRINT "At the beginning of each hour you will "
90 PRINT "be asked to give:":PRINT
91 PRINT "- an ANGLE (between 0 and 360)"
92 PRINT "- a DISTANCE in unit of 100 miles"
93 PRINT "  (between 100 and 300)":PRINT
96 PRINT "...after which your bomb's distance"
100 PRINT "from the enemy ship will be given."
105 PRINT
110 PRINT "An explosion within 5,000 miles of the"
111 PRINT "romulan ship will destroy it."
112 PRINT"Here is the orbit and its angles..."
115 PRINT: PRINT: PRINT TAB(8)"Hit ENTER to continue ";
116 LINE INPUT A$:IF A$="." THEN GOSUB 10000
117 CLS
168 PRINT "                  90"
170 PRINT "            OOOOOOOOOOOOOOOO"
171 PRINT "          OOOOOOOOOOOOOOOOOOOO"
173 PRINT "        OOOOO             OOOOO"
174 PRINT "       OOOOO    XXXXXXX    OOOOO"
175 PRINT "      OOOOO    XXXXXXXXX    OOOOO"
176 PRINT "     OOOO     XXXXXXXXXXX     OOOO"
177 PRINT "    OOOO     XXXXXXXXXXXXX     OOOO"
178 PRINT "1  OOOO     XXXXXXXXXXXXXXX     OOOO"
179 PRINT "8<OOOOO     XXXXXXXXXXXXXXX     OOOOO>0"
180 PRINT "0  OOOO     XXXXXXXXXXXXXXX     OOOO"
181 PRINT "    OOOO     XXXXXXXXXXXXX     OOOO"
182 PRINT "     OOOO     XXXXXXXXXXX     OOOO"
183 PRINT "      OOOOO    XXXXXXXXX    OOOOO"
184 PRINT "       OOOOO    XXXXXXX    OOOOO"
185 PRINT "         OOOO             OOOO"
186 PRINT "          OOOOOOOOOOOOOOOOOOO"
187 PRINT "           OOOOOOOOOOOOOOOOO"
188 PRINT "                  270"
190 PRINT " X - Your planet"
191 PRINT " O - The orbit of the romulan ship":PRINT
192 PRINT TAB(8)"Hit ENTER to continue ";
193 LINE INPUT A$:IF A$="." THEN GOSUB 10000
197 CLS
200 PRINT "On the above diagram, the romulan ship"
207 PRINT "is circling counterclockwise around"
208 PRINT "your planet. Don't forget that without"
212 PRINT "sufficient power the romulan ship's"
215 PRINT "altitude and orbital rate will remain"
218 PRINT "constant."
220 PRINT
230 PRINT "Good luck."
240 PRINT "The federation is counting on you."
270 A=INT(360*RND(1))
280 D=INT(200*RND(1)+200)
290 R=INT(20*RND(1)+10)
300 H=0
310 IF H=7 THEN 490
320 H=H+1
325 PRINT
326 PRINT "---------------------------------------"
330 PRINT "This is hour";H
332 PRINT "* At what angle do you wish to send"
335 PRINT "  your photon bomb";
340 INPUT Z$:IF Z$="." THEN GOSUB 10000:GOTO 332
341 A1=VAL(Z$)
350 PRINT "* How far out do you wish"
355 PRINT "  to detonate it";
360 INPUT Z$:IF Z$="." THEN GOSUB 10000:GOTO 350
361 D1=VAL(Z$)
365 REM PRINT
366 PRINT
370 A=A+R
380 IF A<360 THEN 400
390 A=A-360
400 T=ABS(A-A1)
410 IF T<180 THEN 430
420 T=360-T
430 C=SQR(D*D+D1*D1-2*D*D1*COS(T*3.14159/180))
440 PRINT "Your photon bomb exploded"
442 PRINT C;"* 10^2 "
445 PRINT "miles from the romulan ship."
450 IF C<=50 THEN 470
460 GOTO 310
470 PRINT:PRINT "**************************"
472 PRINT "*  YOU HAVE SUCCESFULLY  *"
475 PRINT "* COMPLETED YOUR MISSION *"
477 PRINT "**************************":PRINT
480 GOTO 500
490 PRINT
492 PRINT "======================================="
493 PRINT "YOU HAVE ALLOWED THE ROMULANS"
495 PRINT "TO ESCAPE. YOU LOST!":PRINT
500 PRINT "Another romulan ship"
505 PRINT "has gone into orbit."
510 PRINT "Do you wish to try to destroy it";
520 INPUT C$
530 IF C$="yes" OR C$="y" THEN 270
540 PRINT "Goodbye."
999 END
10000 INPUT "QUIT: ARE YOU SURE";KK$
10010 IF KK$<>"Y" AND KK$<>"S" AND KK$<>"." THEN RETURN
10020 END
