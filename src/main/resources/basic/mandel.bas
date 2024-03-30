10 REM MANDELBROT-Altair /IMSAI Basic 2022
20 REM Code optimization by Francesco Sblendorio 2022
30 REM AltairDuino test by Stefano Paganini 2022
110 FOR Y=-12 TO 12
120   FOR X=-39 TO 38
130     CA=X*.0458
140     CB=Y*.08333
150     A=CA*CA-CB*CB+CA
160     B=2*CA*CB+CB
170     I=1
180     IF (A*A+B*B)>4 .OR. I>16 THEN 240
190       T=A*A-B*B+CA
200       B=2*A*B+CB
210       A=T
220       I=I+1
230     GOTO 180
240     PRINT MID$("0123456789ABCDEF " ,I,1);
250   NEXT X
260   PRINT
270 NEXT Y
RUN
