# PETSCII BBS Builder
A Java framework for building highly customizable **PETSCII**-enabled **BBS**es, accessible from 8-bit Commodore computers

## Purpose
This framework provides base classes for build your own **BBS** in PETSCII mode, accessibile through:
- a Commodore 64 with a [RR-NET](http://wiki.icomp.de/wiki/RR-Net) compatible card, running [KipperTerm](https://csdb.dk/release/index.php?id=174537)
- a Commodore 64 with a [WiFi modem card](http://www.codingkoala.com/kc64wifi/), running [CCGMS](https://csdb.dk/release/?id=156523)
- a Commodore 64/128 with a [1541Ultimate](http://www.1541ultimate.net), running [UltimateTerm](https://csdb.dk/release/?id=174462)
- an [Ultimate 64](https://ultimate64.com/), running [UltimateTerm](https://csdb.dk/release/?id=174462)
- a Commodore 64 with an [Easy Flash 3](http://store.go4retro.com/easyflash-3/) (with [CCGMS-EF](https://csdb.dk/release/?id=158760)) + a PC running [EF3USB](https://csdb.dk/release/?id=144900&show=notes)
- a common PC/Mac running [SyncTerm](https://sourceforge.net/projects/syncterm/) (*ConnectionType*=_Telnet_, *ScreenMode*=_C64_)

## System requirements
- Java Development Kit (JDK) and JRE version 1.7+
- A machine that will act as server

## Required skills
- Knowledge of Java language (compiler version 1.7+)
- BASIC TCP/IP concepts
- Knowledge of PETSCII encoding

## Getting started
Let's suppose to build a very simple BBS that asks your name welcomes you. The basic operation is to extend **PetsciiThread** class implementing **doLoop()** method, such as:

    public class WelcomeBBS extends PetsciiThread {
        
        // NEVER forget default (empty) constructor
        public WelcomeBBS() {}
        
        @Override
        public void doLoop() throws Exception {
        
            // clear screen
            cls();
            
            println("This is your brand-new BBS");
            println();
            print("Enter your name: ");

            // flush output 
            flush();
            
            // clear input buffer
            resetInput();
            
            String name = readLine();
            println();
            println("Welcome, " + name + "!");
            println("Press a key to exit");
            flush();
            readKey();

        }
    }

this piece of code is enough to create a fully-functional but pretty simple BBS. The result will look like this:

![BBS sample screenshot](./bbs-sample-screenshot.jpg)

All you have to do now is to build and run the BBS on your server, ready to be called by a **PETSCII**-enabled terminal client. Let's see how to do it in the following sections.

## Building the server
Once you have written your own BBS as an extension of *PetsciiThread* class, simply build the *fat jar* with this command:

    mvn package

The build process will result in the file **petscii-bbs-1.0-SNAPSHOT.jar**, it will be found in the **target** directory. So you can run it with:

    java -jar target/petscii-bbs-1.0-SNAPSHOT.jar

## Running the BBS Server
Running the server with no parameters, a help screen will be displayed:

    usage: target/petscii-bbs-1.0-SNAPSHOT.jar
     -b,--bbs <arg>       Run specific BBS (mandatory - see list below)
     -h,--help            Displays help
     -p,--port <arg>      TCP port used by server process (default 6510)
     -t,--timeout <arg>   Socket timeout in millis (default 60 minutes)
    List of available BBS:
     * ...
     * WelcomeBBS

So we can rename the -jar file in **bbs.jar**, and the basic syntax for running our sample BBS is:

    java -jar bbs.jar -b WelcomeBBS

by default, the port where the service will run is **6510** and the timeout is **3600000** milliseconds (1 hour). We can change those parameters with **-p** and **-t** switches:

    java -jar bbs.jar -b WelcomeBBS -p 8088 -t 7200000
    
(so the port will be **8088** with a timeout of **2 hours**)

### Keep it running
This **.jar** is intended to be a *server process*: it has to run all time. So, it's a good thing to run it in background if you use a *UNIX* shell using **nohup** command with bash **"&"** operator:

    nohup java -jar bbs.jar -b WelcomeBBS &

It's **VERY** important not to forget the final **&** symbol to keep it running. After launching that, you can logoff from your server.

### Stopping it
It's a plain process, so use plain **ps** and **kill** commands. If this *jar* is the **only one** running on your server, this command will do the work:

    killall java
    
## Sample BBSes in the package
You can study the sample BBSes (all classes that extend **PetsciiThread**) in the package **eu.sblendorio.bbs.tenants** as example of complete task. The package includes some proxies for accessing *WordPress* sites through Commodore 64 and a two classic strategy games (**tic-tac-toe** and **connect-4**) 


## Sample online BBSes
- **bbs.sblendorio.eu** - port **6510**
- **bbs.retrocampus.com** - port **8086**
 
## Credits
Thanks to:
- [**Brian W. Howell**](https://github.com/bigbhowell/tic-tac-toe) for the **tic-tac-toe** AI
- [**Jatin Thakur**](https://github.com/jn1772/Connect4AI) for the **connect-4** AI.

## Sample screenshot of the demo pack
![bbs1](http://www.sblendorio.eu/attachments/bbs-tictactoe.jpg)

![bbs2](http://www.sblendorio.eu/attachments/bbs-menu.jpg)

![bbs3](http://www.sblendorio.eu/attachments/bbs-connect4.jpg)

![bbs4](http://www.sblendorio.eu/attachments/bbs-vcf.jpg)

