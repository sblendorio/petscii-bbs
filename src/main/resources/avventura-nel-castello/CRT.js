class CRT{
	constructor(){

		this.crt 				= {}; // document.getElementById("crt");
		this.screen 			= {}; // document.getElementById("screen");
		this.txt 				= {}; // document.getElementById("txt");
		this.fixed 				= {}; // document.getElementById("fixed");
		this.cursor 			= {}; // document.getElementById("cursor");
		this.mobileInput 		= {}; // document.getElementById("mobileInput");

		this.width 				= bridge.width()
		this.spad 				= 16;
		this.currentCol 		= 1;
		this.capsLock 			= false;
		this.acceptedKeyCodes 	= [188,190,13,32,8,49,173,222,59,219,160,220];
		this.waitText 			= "Premere invio per continuare...";
		
		this.printOptions		= {
			printDelay: 5,
			cr: 		true,    
			reversed: 	false,
			blinking: 	false,
			nlBefore: 	0,
			nlAfter: 	0,  
		}

	}

	async sleep(ms){
		// SBLEND return new Promise(resolve => setTimeout(resolve, ms));
	}

	clear(){
        bridge.clear()
		this.currentCol = 1;
	}

	async wait(){
        bridge.print(this.waitText); // fixed
		await bridge.pressAnyKey() // SBLEND AWAIT

		// SBLEND let lines = this.fixed.innerHTML.split("\n")
		// SBLEND lines.pop();
		// SBLEND lines.pop();
		// TODO SBLEND this.fixed.innerHTML = lines.join("\n")+"\n\n";
	}

	async printTyping(text, options){
		if (options === undefined)
			options = this.printOptions
		else
			options = { ...this.printOptions, ...options };

		if(options.cr) text += "\n";
		
		if(options.nlBefore > 0)
		    for (let p=0; p<options.nlBefore; p++) {
                bridge.println();
                this.currentCol = 1;
		    }

		text = this._truncate(text);

		if(options.reversed){
			bridge.revOn();
		}

		if(options.blinking){
			bridge.flashOff()
		}

		for(let i=0; i<text.length;i++){
		    if (text[i] == '\n') {
                if (options.reversed) bridge.revOff()
		        bridge.println();
			    if (options.reversed) bridge.revOn()
    			this.currentCol = 1;
		    } else {
			    bridge.print(text[i]); // text
                this.currentCol++;
			}
			//this.fixed.append(text[i])
			if(this.currentCol > this.width || text[i] == "\n"){

				if(text[i] != "\n"){
					if(text[i+1] !== undefined && (text[i+1] != "\n" && text[i+1] != " ")) {
					    if (options.reversed) bridge.revOff()
						bridge.println(); // text
					    if (options.reversed) bridge.revOn()
                    }
				}
				this.currentCol = 1;

			} else {
				// SBLEND window.scrollTo(0,document.body.scrollHeight);
			}
			if(text[i] != " " && text[i] != "\n") 
				await this.sleep(options.printDelay);
		}

		// SBLEND let cn = this.txt.cloneNode(true);
		// SBLEND cn.removeAttribute('id');
		// SBLEND this.fixed.appendChild(cn);


		if(options.reversed){
		    bridge.revOff()
		}

		if(options.blinking){
			bridge.flashOff()
		}

		if(options.nlAfter > 0)
		    for (let p=0; p<options.nlAfter; p++) {
                bridge.println();
                this.currentCol = 1
		    }

		bridge.flush();
		
	}

	async print(text, options){
		if (options === undefined)
			options = this.printOptions
		else
			options = { ...this.printOptions, ...options };

		if(options.nlBefore > 0)
		    for (let p=0; p<options.nlBefore; p++) {
		        bridge.println();
		        this.currentCol = 1
		    }


		if(options.reversed){
			bridge.revOn()
		}
		if(options.blinking){
			bridge.flashOn()
		}

        let textToPrint = "";
        if (text.slice(-1)=='\n') {
            textToPrint = text.slice(0,-1)
        } else {
            textToPrint = text;
        }
        let splitLines = textToPrint.split('\n');
        for (let rn = 0; rn < splitLines.length; rn++) {
            if (rn > 0) {
                if(options.reversed) bridge.revOff();
                bridge.println();
                if(options.reversed) bridge.revOn();
                this.currentCol = 1;
            }
            bridge.print(splitLines[rn]);
        }

		if(options.reversed){
		    bridge.revOff()
		}
		if(options.blinking){
			bridge.flashOff()
		}

        if (text.slice(-1)=='\n') {
            bridge.println()
            this.currentCol = 1;
        }

		if(options.nlAfter > 0)
		    for (let p=0; p<options.nlAfter; p++) {
		        bridge.println();
		        this.currentCol = 1
		    }

        bridge.flush();

		let lastLine = text.split("\n").pop();
		this.currentCol += lastLine.length;
		//if(this.currentCol >= this.width)
		//	this.currentCol = this.currentCol % this.width
		// SBLEND await window.scrollTo(0,document.body.scrollHeight);
		await this.sleep(25);
	}

	async println(text, options){
		await this.print(text+"\n",options);
	}

	async input(cr, noInput){
	    this.currentCol = 1;
		if(cr==undefined)
			cr = true;
		
		if(noInput==undefined)
			noInput = false;

        let inputTxt = bridge.readLine();

		return inputTxt.trim();
	}

	_truncate(textLines){
		textLines = textLines.split("\n");
		let lines = [];
		
		for(let i in textLines){
			let text = textLines[i];
			let chunks = text.split(" ");
			let line = [];
			for(let i in chunks){
				line.push(chunks[i]);
				let tmpLine = line.join(" ");
				let widthToCheck = lines.length == 0 ? this.width-this.currentCol+1 : this.width;
				if(tmpLine.length > widthToCheck){
					line.pop();
					lines.push(line.join(" "));
					line = [chunks[i]];
				}

			}

			lines.push(line.join(" "));
		
		}

		return lines.join("\n");
	}

}

