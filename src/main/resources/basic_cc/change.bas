2 PRINT TAB(33);"CHANGE"
4 PRINT TAB(15);"CREATIVE COMPUTING  MORRISTOWN, NEW JERSEY"
5 PRINT:PRINT:PRINT
6 PRINT "I, YOUR FRIENDLY MICROCOMPUTER, WILL DETERMINE"
8 PRINT "THE CORRECT CHANGE FOR ITEMS COSTING UP TO $100."
9 PRINT:PRINT
10 PRINT "COST OF ITEM";:INPUT A:PRINT "AMOUNT OF PAYMENT";:INPUT P
20 C=P-A:M=C:IF C<>0 THEN 90
25 PRINT "CORRECT AMOUNT, THANK YOU."
30 GOTO 400
90 IF C>0 THEN 120
95 PRINT "SORRY, YOU HAVE SHORT-CHANGED ME $";A-P
100 GOTO 10
120 PRINT "YOUR CHANGE, $";C
130 D=INT(C/10)
140 IF D=0 THEN 155
150 PRINT D;"TEN DOLLAR BILL(S)"
155 C=M-(D*10)
160 E=INT(C/5)
170 IF E=0 THEN 185
180 PRINT E;"FIVE DOLLARS BILL(S)"
185 C=M-(D*10+E*5)
190 F=INT(C)
200 IF F=0 THEN 215
210 PRINT F;"ONE DOLLAR BILL(S)"
215 C=M-(D*10+E*5+F)
220 C=C*100
225 N=C
230 G=INT(C/50)
240 IF G=0 THEN 255
250 PRINT G;"ONE HALF DOLLAR(S)"
255 C=N-(G*50)
260 H=INT(C/25)
270 IF H=0 THEN 285
280 PRINT H;"QUARTER(S)"
285 C=N-(G*50+H*25)
290 I=INT(C/10)
300 IF I=0 THEN 315
310 PRINT I;"DIME(S)"
315 C=N-(G*50+H*25+I*10)
320 J=INT(C/5)
330 IF J=0 THEN 345
340 PRINT J;"NICKEL(S)"
345 C=N-(G*50+H*25+I*10+J*5)
350 K=INT(C+.5)
360 IF K=0 THEN 380
370 PRINT K;"PENNY(S)"
380 PRINT "THANK YOU, COME AGAIN."
390 PRINT:PRINT
400 GOTO 10
410 END
