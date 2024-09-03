1 rem Dobble - 2024 by Jader Monari - Massimo Ferri
2 rem Written for Apple-1's Applesoft BASIC by Claudio Parmigiani - P-LAB
5 randomize timer
6 dim dk$(57): for i=1 to 57: read dk$(i):next
10 dim m(456):dim a1(8):dim b1(8): dim h1(8)
20 gosub 1000: rem intro
30 gosub 2200
40 gosub 2300
50 gosub 2400
60 gosub 2500
70 cls:print:gosub 2600
80 gosub 2800
999 end

1000 rem generate cards and symbols
1020 bs=1:n=8:f=1:c=2
1030 nc=n+(n-1)*(n-1):pt=1
1040 print "Please wait, generating cards";
1050 fori=1ton: rem generate first n*n matrix
1060 print".";
1070 m(pt)=f:pt=pt+1
1080 forj=1to(n-1)
1090 m(pt)=c:pt=pt+1:c=c+1
1100 nextj,i
1120 fork=2ton:rem generate series from n+1 to n+(n-1)*(n-1)
1130 print".";
1140 fori=0to(n-2)
1150 m(pt)=k:pt=pt+1
1160 forj=0to(n-2)
1170 r=j:c=(i+(k-2)*j)-(n-1)*int((i+(k-2)*j)/(n-1))
1180 el=m(bs+(r+1)*8+(c+1))
1190 m(pt)=el:pt=pt+1
1200 nextj,i,k
1210 print
1220 return

2000 rem
2001 cls
2010 print "HOW TO PLAY:"
2020 print: print "There is only one three-char symbol in"
2030 print"common between any two card."
2040 print"Be quick to identify the one in common"
2050 print"between your card and the deck."
2060 print"Press the corresponding letter before"
2070 print"your opponent!"
2080 print:print"Two possible games:"
2090 print:print"- The Towering Inferno: accumulate more"
2100 print"  cards than your opponent."
2120 print:print"- The Well: dispose of all the cards"
2130 print"  initially in your possession."
2140 print:print"On screen counters will indicate the"
2150 print"game progress.":print
2160 print:print:print"Press ENTER to continue ";
2170 line input g$: if g$="." then gosub 10000
2172 cls: print
2199 goto 2220

2200 cls:print:print:print "              D O B B L E":print
2205 print:print "   C64 version by Jader Monari (2024)"
2206 print "Card generation algorithm: Massimo Ferri"
2210 print "   Coded in Applesoft BASIC by P-LAB"
2220 print:print "Select the game:":print
2230 print"(T) for The Towering Inferno"
2240 print"(W) for The Well"
2245 print:print"(I) for instructions"
2250 fori=1to7:print:nexti:d=17
2251 input g$: if g$="." then gosub 10000: goto 2251
2253 if g$<>"I" and g$<>"T" and g$<>"W" then 2251
2255 if g$="I" then 2000
2260 if g$="W" then a=26:b=26:o=0: goto 2280
2270 a=0:b=0:o=52
2280 cls
2281 input"Player A: your name ";a$: if a$="" then a$="PLAYER ONE"
2282 if a$="." then gosub 10000: goto 2281
2283 input"Player B: your name ";b$: if b$="" then b$="PLAYER TWO"
2284 if a$="." then gosub 10000: goto 2283
2290 return

2300 print "Please wait"
2305 print "Shuffling card symbols...";
2310 for c=0 to 455 step 8
2315 print ".";
2320 for s=1 to 8
2330 r=int(rnd(10)*6)+1: rem Random gen for Apple-1
2331 rem r=int(rnd(-1*(PEEK(78)+256*PEEK(79)))*6)+1: rem Random gen for Apple-2
2340 t=m(s+c): m(s+c)=m(r+c): m(r+c)=t
2350 next s
2360 next c
2370 print" done.":return

2400 print "Shuffling deck...";
2410 for i=1 to 57
2420 x=(int(rnd(10)*(57-i))+1): rem Random gen for Apple-1
2421 rem x=(int(rnd(-1*(PEEK(78)+256*PEEK(79)))*(57-i))+1): rem Random gen for Apple-2
2430 c=i*8+1-8:r=x*8+1
2440 for s=0 to 7
2450 t=m(c+s): m(c+s)=m(r+s): m(r+s)=t
2460 next s
2465 print ".";
2470 next i
2480 print" done.":return

2500 rem initial loading
2510 for c=0 to 16 step 8
2520 for s=1 to 8
2530 if c=0 then a1(s)=m(s+c)
2540 if c=8 then b1(s)=m(s+c)
2550 if c=16 then d1(s)=m(s+c)
2560 next s:next c
2570 return

2600 rem print current game
2610 print:print a$;":";:for i=1 to 28-len(a$):print" ";:next:print"cards:";a
2615 print"--------------------------------------"
2620 for i=1 to 8
2625 rem if a1(i)<10 then print "0";
2630 print dk$(a1(i));:if i<8 then print "  ";
2640 next i
2645 print:print " A    B    C    D    E    F    G    H"
2647 print"--------------------------------------":print
2650 print:print b$;":";:for i=1 to 28-len(b$):print" ";:next:print"cards:";b:
2655 print"--------------------------------------"
2660 for i=1 to 8
2665 rem if b1(i)<10 then print "0";
2670 print dk$(b1(i));:if i<8 then print "  ";
2680 next i
2685 print:print " I    J    K    L    M    N    O    P"
2687 print"--------------------------------------"
2690 print:print:print "DECK:                        cards:";o
2695 print"--------------------------------------"
2700 for i=1 to 8
2705 rem if d1(i)<10 then print "0";
2710 print dk$(d1(i));:if i<8 then print "  ";
2720 next i
2725 print:print"--------------------------------------"
2730 print:print:print "SPOT-IT! ";
2740 return

2800 w=0:y=0
2810 x$=inkey$:if x$="" then 2810
2815 if x$="." then gosub 10000: goto 2810
2820 id=asc(x$) and 95:rem uppercase
2830 if id<73 then id=id-64:p=1:goto 2850
2840 id=id-72:p=2
2850 if id<1 or id>8 then 2810: rem char not allowed
2870 if p=1 then gosub 3000
2880 if p=2 then gosub 3100
2890 if w=1 or y=1 then gosub 4000:w=0:y=0:gosub 2600
2900 goto 2810

3000 for i=1 to 8
3010 if a1(id)=d1(i) then print:cls:print a$;" is correct: ";dk$(d1(i)): w=1: return
3020 next i
3030 beep:print "x";
3040 return

3100 for i=1 to 8
3110 if b1(id)=d1(i) then print:cls:print b$;" is correct: ";dk$(d1(i)): y=1:return
3120 next i
3130 beep:print "x";
3140 return

4000 rem check win
4010 if g$="W" and w=1 then a=a-1:o=o+1
4020 if g$="W" and y=1 then b=b-1:o=o+1
4030 if g$="T" and w=1 then a=a+1:o=o-1
4040 if g$="T" and y=1 then b=b+1:o=o-1
4050 if g$="W" and a=0 then print:print a$;:goto 5000
4060 if g$="W" and b=0 then print:print b$;:goto 5000
4070 if g$="T" and o=0 then 4500
4080 rem no win, update decks
4100 if g$="W" then gosub 6000
4110 if g$="T" then gosub 6100
4120 return

4500 if a>b then print a$;: goto 5000
4510 print b$;
5000 print " WINS! CONGRATULATIONS!"
5010 print:input "Another game ?";g$
5020 if g$="Y" or g$="S" then 30
5030 end

6000 rem Well game: player card goes to top deck
6010 for i=1 to 8
6020 if w=1 then d1(i)=a1(i)
6030 if y=1 then d1(i)=b1(i)
6040 next i
6050 rem pick player's next card
6060 d=d+8:v=1
6070 for i=d to d+7
6080 if w=1 then a1(v)=m(i)
6085 if y=1 then b1(v)=m(i)
6087 v=v+1
6090 next i
6099 return

6100 rem Tower game: deck card goes to player deck: ok
6110 for i=1 to 8
6120 if w=1 then a1(i)=d1(i)
6130 if y=1 then b1(i)=d1(i)
6140 next i
6150 rem pick deck's next card
6160 d=d+8:v=1
6170 for i=d to d+7
6180 if w=1 then d1(v)=m(i)
6185 if y=1 then d1(v)=m(i)
6187 v=v+1
6190 next i
6199 return

10000 input "QUIT: ARE YOU SURE";kk$
10010 if kk$<>"y" and kk$<>"s" and kk$<>"." then return
10020 end

50001 data "<->"
50002 data "<=>"
50003 data "<<="
50004 data ">>="
50005 data "(+)"
50006 data "!!!"
50007 data "-->"
50008 data "<--"
50009 data "(!)"
50010 data ">!<"
50011 data ":-)"
50012 data "==="
50013 data "==>"
50014 data "<=="
50015 data ">>>"
50016 data "<<<"
50017 data ":-D"
50018 data ":-("
50019 data ":))"
50020 data "(O)"
50021 data ">-<"
50022 data ">=<"
50023 data ">--"
50024 data "--<"
50025 data ">=="
50026 data "==<"
50027 data ":-/"
50028 data ":=)"
50029 data ":-!"
50030 data ":=!"
50031 data "..."
50032 data "..$"
50033 data "$.."
50034 data "[@]"
50035 data ".-."
50036 data ":-:"
50037 data ".-:"
50038 data ":-."
50039 data "-=="
50040 data "==-"
50041 data "-=-"
50042 data "=-="
50043 data "-=>"
50044 data "<=-"
50045 data "!--"
50046 data "--!"
50047 data "-=!"
50048 data "!=-"
50049 data "!=="
50050 data "==!"
50051 data "(@)"
50052 data ":-@"
50053 data ":+)"
50054 data ":+D"
50055 data "<@>"
50056 data ">@<"
50057 data "@.@"
