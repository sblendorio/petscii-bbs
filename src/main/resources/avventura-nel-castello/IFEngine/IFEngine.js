class IFEngine{
	// Questa classe deve essere estesa
	constructor(){
		

		if(this.constructor === IFEngine)
			throw i18n.IFEngine.warnings.mustBeExtended;
		
		// Lo schermo
		this.CRT = new CRT();
		
		this.Sound = new Sound();

		// Costante da modificare, è la chiave di salvataggio
		// nel localstorage
		this.SAVED = "Avventura";

		this.defaultInput = "\n>";

		//La  stanza corrente
		this.stanzaCorrente = null;

		// L'inventario
		this.inventario = {};

		// Dati specifici di ogni avventura da salvare
		this.altriDati = {};

		// Strong Check delle frasi
		this.strongCheck = true;

		// Elenco degli eventi "a tempo"
		this.timedEvents = [];

		// Menu
		this.menu = {
			principale: {
				label: i18n.IFEngine.menu.choose,
				opzioni: {
					1: {
						label: i18n.IFEngine.menu.new,
						callback: async () => {
							await this.CRT.clear();
							this.restart();
						}
					},
					2: {
						label: i18n.IFEngine.menu.load,
						callback: async () => {
							return await this.restore();
						}
					},
					3: {
						label: i18n.IFEngine.menu.readInstructions,
						callback: async () => {
							await this.istruzioni();
							await this.CRT.clear();
							await this.run();
						}
					},
					4: {
						label: i18n.IFEngine.menu.quit,
						callback: () => {
							this.byebye();
						}
					}
				}
			},
			contestuale: {
				label: i18n.IFEngine.menu.choose,
				opzioni: {
					1: {
						label: i18n.IFEngine.menu.restart,
						callback: async () => {
							this.restart(false);
						}
					},
					2: {
						label: i18n.IFEngine.menu.load,
						callback: async () => {
							return await this.restore();
						}
					},
					3: {
						label: i18n.IFEngine.menu.stop,
						callback: () => {
							this.byebye();
						}
					}
				}
			}
		}
		
		this.Thesaurus = new Thesaurus();
		// Comandi particolari
		this.Thesaurus.commands = { 
			...this.Thesaurus.commands, 
			...{
				// Salva 
				salva:{
					callback: async () =>{
						await this.save();
						this.gameLoop(true,true);
						return false;
					},
				},
				
				// Carica 
				carica: {
					callback: async () =>{
						let res = await this.restore();
						return !res;
					},
				},

				// Istruzioni 
				istruzioni: {
					callback: async () => {
						await this.istruzioni();
						return true;
					},
				},
				
				// Inventario 
				inventario: {
					callback: async () => {
						await this._inventario();
						return true;
					},
				},

				// Esci
				basta: {
					callback: async () => {
						let answer = await this.yesNoQuestion(i18n.IFEngine.questions.stopQuestion);
						if(answer){
							this.displayMenu(this.menu.contestuale);
							return false;
						}
						return true;
					}
				}
			}
		}

		return this;

	}

	// Inizia l'avventura
	start(){
		//Il parser delle azioni
		this.Parser = new Parser(this.Thesaurus.verbs, this.Thesaurus.commands);
		

		if(this.datiAvventura === undefined){
			throw i18n.IFEngine.warnings.notLoaded;
		}

		
		// Setta il valore key di ogni oggetto
		for (let o in this.datiAvventura.objects){
			this.datiAvventura.objects[o].key = o;
			this.datiAvventura.objects[o].type = "oggetto";
		}

		// Setta il valore key di ogni stanza
		for (let s in this.datiAvventura.stanze){
			this.datiAvventura.stanze[s].key = s;
		}

		let datiIniziali = this._getTbs();

		this.datiIniziali = JSON.stringify(datiIniziali, (k,v) => typeof v === 'function' ? "" + v : v);
		// Si parte!
		this.run();
	}

	async restart(prologo) {
		if(prologo === undefined)
			prologo = true;
		await this.CRT.clear();
		if(this.datiIniziali != undefined)
			this.reload(this.datiIniziali);
		if(prologo){
			await this.runSequence("prologo");
		} 
		this.entra(this.datiAvventura.stanzaIniziale);
	}

	// Mostra il menù
	async run(){
		this.displayMenu(this.menu.principale);
	}
	
	// Mostra un menu
	async displayMenu(aMenu) {
		let menu = { ...aMenu }
		await this.CRT.printTyping(menu.label+"\n");
		for(let o in menu.opzioni){
			if(menu.opzioni[o].displayIf ===undefined || menu.opzioni[o].displayIf)
				await this.CRT.printTyping("("+o+") "+menu.opzioni[o].label+"\n");
			else 
				delete menu.opzioni[o];
		}
		
		let res;
		res = await this.scelta(menu.opzioni, undefined, true);
		if(res == false)
			this.displayMenu(aMenu);
	}

	// si o no
	async yesNoQuestion(question, cr){
		let options = {}
		options[i18n.IFEngine.yesOrNo.yes] = () => true;
		options[i18n.IFEngine.yesOrNo.no] = () => false;
		
		return await this.scelta(
			options,
			question == undefined ? i18n.IFEngine.questions.areYouSureQuestion : question,
			cr)
		;
	}

	// Parsing delle scelte
	async scelta(opzioni, question, cr){
		if(cr == undefined) 
			cr = true;

		let listaOpzioni = "("+Object.keys(opzioni).join(",")+") ";
		if(question === undefined)
			question = "";
		let scelta;
		do {
			await this.CRT.printTyping(question+i18n.IFEngine.questionMark+" ",{cr:false});
			scelta = await this.CRT.input(false);
			
			if(opzioni[scelta] === undefined){
				question = listaOpzioni;
			}
		} while (opzioni[scelta] === undefined);
		let callback = typeof opzioni[scelta] === 'function' ? 
			opzioni[scelta] :
			opzioni[scelta].callback;

		let result = await callback();
	
		this.CRT.currentCol = 1;
		return result;
	}
	
	// Esci dal gioco
	async byebye(){
		await this.CRT.printTyping(i18n.IFEngine.messages.tanksForPlaying, {nlAfter:1,nlBefore:1});
		await this.CRT.wait();
		await this.CRT.clear();
	}
	
	
	
	// Entra nella stanza
	async entra(labelStanza){
		if(await this._breakRoomAction("onExit"))
			return;

		this.stanzaCorrente = this.datiAvventura.stanze[labelStanza];

		this.Parser.setOverride(this.stanzaCorrente.override);

		if(await this._breakRoomAction("onEnter"))
			return;

		if(this.stanzaCorrente.label !== undefined){
		    bridge.underlineOn();
			await this.CRT.print("<strong style='text-decoration:underline'>"+this.stanzaCorrente.label.toUpperCase()+"</strong>");
			bridge.underlineOff();
		    await this.CRT.println("");
		}
		this.refreshOggettiInStanza();
		await this.descriviStanzaCorrente();
		await this.gameLoop();
	}

	// CONTROLLO CHE UN'AZIONE DI INGRESSO O USCITA NON SIA "definitiva...."
	async _breakRoomAction(action){
		if(this.stanzaCorrente && this.stanzaCorrente[action]){
			let ret = await this.stanzaCorrente[action]();
			return ret === false;
		}
	}

	// Aggiorna gli oggetti nella stanza in base alla loro posizione
	refreshOggettiInStanza(){
		this.stanzaCorrente.objects = this._filter(o => {
			return o.posizione == this.stanzaCorrente.key;
		}, this.datiAvventura.objects);
		//console.log(this.stanzaCorrente.objects);
	}

	// Descrive la stanza corrente
	async descriviStanzaCorrente(){
		await this.CRT.printTyping(this.stanzaCorrente.description);
	}
	
	// Avvia un evento a tempo
	startTimedEvent(eventLabel){
		if(this.timedEvents.indexOf(eventLabel) < 0)
			this.timedEvents.push(eventLabel);
	}

	// Ferma un evento a tempo.
	// Eventualmente resetta l'indice del currentStep
	stopTimedEvent(eventLabel, resetIndex){
		if(resetIndex === undefined)
			resetIndex = true;
		
		let timedEvent = this.datiAvventura.timedEvents[eventLabel];
		
		if(timedEvent !== undefined){
			if(resetIndex)
				timedEvent.currentStep = timedEvent.start;
			this.timedEvents = this.timedEvents.filter(e => e != eventLabel);
		}
	}
	
	// Il loop del gioco
	async gameLoop(descriviStanzaCorrente, ignoraTimedEvents){
		
		if(descriviStanzaCorrente){
			await this.CRT.println("");
			await this.descriviStanzaCorrente();
		}

		// Esistono eventi a tempo attivi?
		if(this.timedEvents.length > 0 && !ignoraTimedEvents){
			for(let i in this.timedEvents){
				let timedEvent = this.datiAvventura.timedEvents[this.timedEvents[i]];
				if(timedEvent !== undefined){
					var limit = 0;
					
					// Se non ho definito currentStep, lo definisco ora
					// con valore uguale a start;
					if(timedEvent.currentStep === undefined)
						timedEvent.currentStep = timedEvent.start;
					
					// ho raggiunto il limite ?
					if(timedEvent.currentStep <= limit){
						this.stopTimedEvent(this.timedEvents[i]);
						let goOn = await timedEvent.onLimit();
						if(goOn)
							break;
						return;
					}
					

					// Eseguo se esiste lo step i-esimo
					if(timedEvent.steps && timedEvent.steps[timedEvent.currentStep] !== undefined)
						await timedEvent.steps[timedEvent.currentStep]();

					timedEvent.currentStep--;
				}
			}
			

		}

		this.input();
	}

	async input(){
		// Attendo il comando
		await this.CRT.print(this.defaultInput);
		let input = await this.CRT.input();
		// Rimuovo gli n>1 spazi dal comando inviato
		input = this._prepare(input);
		// Faccio il parsing del comando. 
		// Se la funzione mi restituisce undefined o true allora ciclo nuovament il loop
		let repeat = await this._parse(input);

		if(repeat === undefined || repeat == true){
			this.gameLoop(repeat);
		}
	}

	_prepare(input){
		input = input.trim();
		input = input.replace(/[\.,:;!"£\$%&\/\(\)=à°èé\+\*]*/,"");
		input = input.replace(/\s+/gmi," ");
		return input;		
	}
	
	// Salva il gioco
	async save() {
		let etichetta;

        await this.CRT.printTyping(i18n.IFEngine.questions.saveLabel+" ",this.CRT.printDelay,false);

        etichetta = await this.CRT.inputOptional();
        etichetta = etichetta.trim();
        if (bridge.fileExists(etichetta, i18n.lang)) {
                await this.CRT.print(i18n.IFEngine.warnings.doYouWantToOverwrite);
                let resp = await this.CRT.inputOptional();
                resp = resp.toLowerCase().substring(0);
                if (!resp || resp === '' || resp === 'n' || resp === 'no' || resp == 'nope') etichetta = "";
        }
        if(etichetta == i18n.IFEngine.questions.cancelLetter.toLowerCase()){
            return;
        }

		if (etichetta && etichetta !== "") {
            let tbs = this._getTbs();
            let saveState = JSON.stringify(tbs, (k,v) => typeof v === 'function' ? "" + v : v)
            bridge.save(etichetta, saveState, i18n.lang)
            await this.CRT.printTyping(i18n.IFEngine.messages.saved);
		} else {
            await this.CRT.println(i18n.IFEngine.warnings.saveAborted);
		}
	}

	// Ritorna i dati principali da salvare
	_getTbs(){
		return { 
			stanzaCorrente: this.stanzaCorrente == null ? 
				this.datiAvventura.stanzaIniziale : 
				this.stanzaCorrente.key, 
			timedEvents: this.timedEvents,
			datiAvventura: this.datiAvventura, 
			inventario: this.inventario,
			altriDati: this.altriDati
		};
	}

	// Carica il gioco
	async restore() {
		let etichetta;

		do {
			await this.CRT.printTyping(i18n.IFEngine.questions.restoreLabel,{nlBefore:1});
		
			etichetta = await this.CRT.inputOptional();
			etichetta = etichetta.trim();

			if(etichetta == i18n.IFEngine.questions.cancelLetter.toLowerCase() || etichetta == ""){
			    return false;
			}

			if(!bridge.fileExists(etichetta, i18n.lang)){
				await this.CRT.println(i18n.IFEngine.warnings.notExistingFile)
				return false;
            }

		} while (etichetta == i18n.IFEngine.questions.cancelLetter.toLowerCase())
		
		let stored = bridge.restore(etichetta, i18n.lang);
		if(stored == null || stored === "") {
			await this.CRT.printTyping(i18n.IFEngine.warnings.noData+"\n");
			return false;	
		}

		let tbr = await this.reload(stored);
		await this.CRT.printTyping(i18n.IFEngine.messages.loaded+"\n");
		
		this.entra(tbr.stanzaCorrente);
		return true;
	}
	
	reload (stored){
		let tbr = JSON.parse(stored, (k,v) => typeof v === "string"? (v.indexOf('=>') >=0 ? eval(v) : v): v);
		
		if(tbr.stanzaCorrente === undefined)
			tbr.stanzaCorrente = this.datiAvventura.stanzaIniziale;

		for (let k in tbr)
			this[k] = Array.isArray(tbr[k]) ? [ ...tbr[k] ] : { ...tbr[k] };

		return tbr;
	}

	async istruzioni(){
		await this.CRT.printTyping(i18n.IFEngine.messages.noInstructions+"\n");
		await this.CRT.wait();
	}
	
	// Morte
	async die(){
		await this.CRT.printTyping(i18n.IFEngine.messages.death);
		this.displayMenu(this.menu.contestuale);
		return false;
	}

	// Scopri oggetto, quindi diventa visibile
	scopri(oggetto){
		oggetto.visibile = true;
		this.refreshOggettiInStanza();		
	}

	// Abilita direzione in una stanza
	abilitaDirezione(direzione,stanza){
		if(stanza === undefined) stanza = this.stanzaCorrente;
		delete stanza.unavaiableDirections[stanza.unavaiableDirections.indexOf(direzione)];
	}

	// Disabilita direzione in una stanza
	disabilitaDirezione(direzione,stanza){
		if(stanza === undefined) stanza = this.stanzaCorrente;
		if(stanza.unavaiableDirections === undefined)
			stanza.unavaiableDirections = [];
		if(stanza.unavaiableDirections.indexOf(direzione) < 0)
			stanza.unavaiableDirections.push(direzione);
	}

	// Esegui una sequenza di azioni
	async runSequence(labelSequenza, args){
    	let sequence = this.datiAvventura.sequenze[labelSequenza];
		return await sequence(args);
	}
	
	// Stampa i punti del gioco
	async _punti(){
		if (this.datiPunti.puntiAzione === undefined)
			await this.CRT.printTyping(i18n.IFEngine.messages.noPoints);
		else{
			await this.CRT.printTyping(i18n.IFEngine.messages.points(this.altriDati.punti, this.altriDati.puntiMax)+".\n");
		}
		return true;
	}

	// AggiungePunti
	async aggiungiPunti(action){
		if (this.datiPunti.puntiAzione === undefined)
			return 0;
		let puntiAzione = this.datiPunti.puntiAzione;
		if(this.altriDati.puntiAzioneGiocati == undefined)
			this.altriDati.puntiAzioneGiocati = [];
		if(this.altriDati.puntiAzioneGiocati.indexOf(action) == -1){
			this.altriDati.puntiAzioneGiocati.push(action)
			this.altriDati.punti += puntiAzione[action].i;
		}
	}

	async wtf(APO, wtf){
		if(wtf.indexOf(" ") >=0)
			wtf = wtf.substring(0,wtf.indexOf(" "));
		await this.CRT.printTyping("   "+wtf.toUpperCase()+" "+i18n.IFEngine.questionMark+i18n.IFEngine.questionMark+i18n.IFEngine.questionMark);
		return;
	}

	async inputNotUnderstood(){
		await this.CRT.printTyping(this.Thesaurus.defaultMessages.NON_HO_CAPITO);
		return true;
	}

	// Parsing del comando
	async _parse(input){
		// Approfondiamo
		let APO = this.Parser.parse(input);
		
		if(APO === false){
			return this.inputNotUnderstood(input);
		}
		
		if(typeof APO == 'string'){
			// comando = verbo, manca il resto
			await this.CRT.printTyping(APO.charAt(0).toUpperCase() + APO.slice(1)+" "+i18n.IFEngine.questions.what+" "+this.Thesaurus.defaultMessages.SII_PIU_SPECIFICO);
			return true;
		}
		
		// è un comando imperativo con callback
		if(APO.command && APO.actionObject.callback !== undefined){
			let ret = await this._callbackOrString(APO.actionObject.callback, input);
			if(ret !== null)
				return ret;
		}

		// Azione riconosciuta, ovvero promessa
		return await this._action(APO, input);
	}

	async _action(APO, input){
		let actionObject = APO.actionObject;
		if(actionObject.movimento){
			return await this._vai(APO.subjects[0], actionObject.defaultMessage);
		}

		// è un'azione!
		// vediamo se è fattibile
		
		let testVerb = APO.subjects[0];
		if(testVerb !== undefined){
			if(testVerb.indexOf(" ") >=0)
				testVerb = testVerb.substring(0,testVerb.indexOf(" "));	
			if(typeof this.Parser.parse(testVerb) == 'string'){
				await this.CRT.printTyping(this.Thesaurus.defaultMessages.NON_HO_CAPITO);
				return true;
			} 
		}
		
		// Mappo i complementi con 
		// - interattori della stanza
		// - oggetti nella stanza
		// - oggetti nell'inventario
		let mSubjects = APO.subjects.map(subject => {
			let interattore = this._get(subject,this.stanzaCorrente.interactors);
			let oggettoInStanza = this._get(subject, this.stanzaCorrente.objects);
			let oggettoInInventario = this._get(subject, this.inventario);

			return interattore ? interattore : 
				(oggettoInStanza ? oggettoInStanza : 
				(oggettoInInventario ? oggettoInInventario : null));
		}); 

		//mSubjects = mSubjects.filter( i => {return i != null});

		// non sono riuscito a mappare tutto
		if(APO.subjects.length != mSubjects.filter(i=>{return i!=null}).length){
			// Recupero l'indice dell'oggetto
			let nullIndex = mSubjects.indexOf(null);
			let wtf = this.Thesaurus.verbs[APO.verb] === undefined ? input : APO.subjects[nullIndex];
			return await this.wtf(APO, wtf);
		}

		// Ho scritto solo il verbo
		if(APO.subjects.length == 0){
			// Se è un verbo che necessita di un complemento
			// Sii più preciso!
			if(actionObject.singolo === undefined || actionObject.singolo == false){
				await this.CRT.printTyping(this.Thesaurus.defaultMessages.SII_PIU_SPECIFICO);
				return true;
			} 

			// Ok, si può usare da solo.
			if(actionObject.callback){
				let ret = this._callbackOrString(actionObject.callback);
				if(ret !== null)
					return ret;
			}
			// Scrivi il messaggio di default se c'è altrimenti messaggio generico
			await this.CRT.printTyping(actionObject.defaultMessage === undefined ? this.Thesaurus.defaultMessages.PREFERISCO_DI_NO : actionObject.defaultMessage);
			return true;
			
		}


		// è definito un override con callback
		if(actionObject.callback !== undefined){

			let ret = await this._callbackOrString(actionObject.callback,mSubjects);
			if(ret !== null)
				return ret;
		}

		let visibile = mSubjects[0].visibile === undefined ? true : mSubjects[0].visibile;

		if(visibile){
			// Eseguji l'azione sugli oggetti/interattori mappati
			let actionResult = await this._playAction(APO, mSubjects);
			
			// null: azione non definita a livello di oggetto
			// true: cicla di nuovo il gameloop
			// false: ci sarà un redirect nell'azione stessa, quini non ciclare il gameloop
			if(actionResult != null)
				return actionResult;
		}
		
		if(visibile == false && actionObject.inventario == undefined){
			return this._notSeen(mSubjects[0]);
		}
		switch (APO.verb){
			case "guarda":

				let descrizione = mSubjects[0].description ?  
					(Array.isArray(mSubjects[0].description) ? mSubjects[0].description[mSubjects[0].status] : mSubjects[0].description) :
					actionObject.defaultMessage;
				await this.CRT.printTyping(descrizione);
				return true;	
			
			case "prendi":
				let ret = await this._prendi(mSubjects[0]);
				return ret === undefined ? true : ret;
			
			case "lascia":
	 			if(this.inventario[mSubjects[0].key] !== undefined){
					this._rimuoviDaInventario(mSubjects[0]);
					await this.CRT.printTyping(this.Thesaurus.defaultMessages.FATTO);
					return true;
				}

				await this.CRT.printTyping(this.Thesaurus.defaultMessages.NON_NE_POSSIEDI);
				return true;
		}


		// non posso applicarlo al soggetto/ai soggetti
		let errorMessage = 
				actionObject.defaultMessage === undefined ? 
				this.Thesaurus.defaultMessages.NON_HO_CAPITO : 
				actionObject.defaultMessage
			;
		
		await this.CRT.printTyping(errorMessage);
		
		return (errorMessage == this.Thesaurus.defaultMessages.NON_HO_CAPITO) ? undefined : true;

	}

	async _notSeen(s){
		await this.CRT.printTyping(this.Thesaurus.defaultMessages.QUI_NON_NE_VEDO);
		return true;
	}

	// Esegui l'azione richiesta
	async _playAction(APO, s){
		
		let verb = APO.verb;
		s = [...s];

		let ai = await this._azioneInventario(APO,s);
		if(ai)
			return true;

		if(s[0] !== undefined && s[0].on !== undefined){
			let play = this.Parser._getSource(verb, s[0].on);
			if(play)
				return await this._callbackOrString(play, s);
		}
				
		return null;
	}

	async _callbackOrString(source, arg){
		if(typeof source == 'string'){
			await this.CRT.printTyping(source);
			return true;
		}
		
		let ret = await source(arg);

		if(typeof ret == 'string'){
			await this.CRT.printTyping(ret);
			return true;
		}

		if(ret === undefined)
			ret = true;

		// console.log(ret);
		return ret;
	}

	async _azioneInventario(APO,s){
		if(APO.actionObject.inventario){
			let inventarioKey = typeof APO.actionObject.inventario == 'boolean' ? [APO.actionObject.inventario] : APO.actionObject.inventario;
			for(let i in s){
				if(inventarioKey[i] && /*s[i].type == "oggetto" &&*/ this.inventario[s[i].key] === undefined){
					await this.CRT.println(this.Thesaurus.defaultMessages.NON_NE_POSSIEDI);
					return true;
				}
			}
		}
	}

	// Movimento
	async _vai(direzione, defaultMessage){
		let direzioni = this.stanzaCorrente.directions;
		let direzioniBloccate = this.stanzaCorrente.unavaiableDirections === undefined ? [] : this.stanzaCorrente.unavaiableDirections; 
		
		//Esiste la direzione
		if(direzioni !== undefined && direzioni[direzione] !== undefined && direzioniBloccate.includes(direzione) === false){
			if(typeof direzioni[direzione] == 'string'){
				this.entra(direzioni[direzione]);
				return false;
			}
			let ret = await direzioni[direzione]();
			if(typeof ret == 'string'){
				await this.CRT.printTyping(ret);
				return true;
			}
			return ret === undefined ? false : ret;
		}

		// No way, non si può andare di là
		await this.CRT.printTyping(defaultMessage);
		return;
	}

	// Mostra l'inventario
	async _inventario(action){
		let output;
		if(Object.keys(this.inventario).length == 0){
			output = i18n.IFEngine.messages.noObjects
		} else {
			output = "* "+i18n.IFEngine.messages.carriedObjectsLabel+" *"+"\n"
			for(let i in this.inventario){
				let label = Array.isArray(this.inventario[i].label) ? 
					this.inventario[i].label[this.inventario[i].status] : 
					this.inventario[i].label
				;
				output += "\n- "+label.trim()+".";
			}
		}
		await this.CRT.printTyping(output);
	}

	// Aggiungi oggetto nell'inventario
	_aggiungiInInventario(oggetto){
		this.scopri(oggetto);
		this.stanzaCorrente.objects[oggetto.key].posizione = null;
		//this.inventario[oggetto.key] = { ...oggetto };
		this.inventario[oggetto.key] = oggetto;
		this.refreshOggettiInStanza();
	}

	// Rimuovi oggetto dall'inventario
	_rimuoviDaInventario(oggetto,posizione){
		oggetto.posizione = 
			posizione === undefined ? 
			this.stanzaCorrente.key :
			posizione;
		this.datiAvventura.objects[oggetto.key] = oggetto

		delete this.inventario[oggetto.key];
		this.refreshOggettiInStanza();
	}

	// Prendi 
	async _prendi(oggetto){
		if(this.stanzaCorrente.objects[oggetto.key] !== undefined){
			this._aggiungiInInventario(oggetto);
			await this.CRT.printTyping(this.Thesaurus.defaultMessages.FATTO);
		} else if(this.inventario[oggetto.key] !== undefined){
			await this.CRT.printTyping(i18n.IFEngine.messages.alreadyHaveIt);
		} else
			await this.CRT.printTyping(this.Thesaurus.verbs.prendi.defaultMessage);
	}

	// Filtra JSON
	_filter(callback, jsonObj){
		let filtered = {}
		for (let k in jsonObj){
			if(callback(jsonObj[k]) == false)
				continue;
			filtered[k] = jsonObj[k];
		}

		return filtered;
	}

	// Recuprea Oggetto da JSON in base al pattern
	// definito nell'oggetto stesso (se definito)
	// altrimenti ritorna false
	_get(needle, jsonObjList){
		for (let k in jsonObjList){
			
			let jsonObj = jsonObjList[k];

			let res = this._match(needle, jsonObj);
			
			if(!res) {
				if(jsonObj.linkedObjects){
					for(let j in jsonObj.linkedObjects){
						let linked = this.Parser._getSource(jsonObj.linkedObjects[j],this.datiAvventura.objects);
						if(linked){
							if(this._match(needle, linked))
								return linked;
						}
					}
				}
				continue;
			} else {
				jsonObj.key = k;
				return jsonObj;

			}
		
		}
		
		return false;
	}

	_match(needle, obj){
		let pattern;

		// Se non è definito il patten ma il label si
		// provo a ricostruire il pattern dalla label
		if(obj.pattern === undefined) {
			if(obj.label === undefined)
				return false;

			pattern = this._simplePattern(obj.label);
		}
		else
			pattern = obj.pattern;
	
		
		let regExp = new RegExp("^(?:"+pattern+")$","i");
		let res = needle.match(regExp);
		return res;
	}

	_simplePattern(string){
		let chunks = string.split(/\s+/);
		chunks[0] = "("+chunks[0]+"\\s+)?";
		
		return chunks[0]+chunks.slice(1).join("\\s+")
	}


}
