class AvventuraNelCastelloJSEngine extends IFEngine{
	constructor() {
		super();

		if(this.constructor === AvventuraNelCastelloJSEngine)
			throw i18n.AvventuraNelCastelloJSEngine.warnings.mustBeExtended;
		
		// Override di IFEngine.SAVED
		this.SAVED = "AvventuraNelCastello";
		
		//Override di IFEngine.altriDati
		this.altriDati = {
			mosse: 0,
			punti: 0,
			bigmeow: {
				attivo: false,
			},
			iotaid: {
				iota:false,
				id: false,
				pronunciato: false
			},
			pensa: 0,
			golaSecca: true
		}

		this.pesoMassimoInventario = 4,
			
		this.defaultInputOverride = "\n\n "+i18n.AvventuraNelCastelloJSEngine.defaultInput+" ";
		this.defaultInput = this.defaultInput;
		
		this.nonhocapito = 0;

		this.datiPunti = {
			puntiMax: 1000,
			puntiLevel: {
				34: i18n.AvventuraNelCastelloJSEngine.pointsLabel[0],
				58: i18n.AvventuraNelCastelloJSEngine.pointsLabel[1],
				149: i18n.AvventuraNelCastelloJSEngine.pointsLabel[2],
				299: i18n.AvventuraNelCastelloJSEngine.pointsLabel[3],
				449: i18n.AvventuraNelCastelloJSEngine.pointsLabel[4],
				649: i18n.AvventuraNelCastelloJSEngine.pointsLabel[5],
				898: i18n.AvventuraNelCastelloJSEngine.pointsLabel[6],
				948: i18n.AvventuraNelCastelloJSEngine.pointsLabel[8],
				998: i18n.AvventuraNelCastelloJSEngine.pointsLabel[9],
				999: i18n.AvventuraNelCastelloJSEngine.pointsLabel[10]
			},
			puntiAzione: { 
				paracaduteIndossato:	{ i: 15 },
				saltoAereo:				{ i: 20 },
				apertoPortone:			{ i: 6  },
				entrato:				{ i: 18 },
				cadutoInSegreta:		{ i: 17 },
				apertaFessura:			{ i: 35 },
				uscitoDaSegreta:		{ i: 15 },
				apertoVolume:			{ i: 35 },
				lettoBigMeow:			{ i: 28 },
				scesoInSotterranei:		{ i: 33 },
				eliminatoOrco:			{ i: 50 },
				vistoSortilegio:		{ i: 40 },
				eliminatoFantasma:		{ i: 50 },
				apertoForziere:			{ i: 15 },
				trovatoCorno:			{ i: 12 },
				salutatoNano:			{ i: 50 },
				presoDiamante:			{ i: 5  },
				lettoId:				{ i: 23 },
				apertoLibro:			{ i: 10 },
				lettoIota:				{ i: 18 },
				pronunciatoIotaid:		{ i: 25 },
				entratoSalaTrono:		{ i: 25 },
				trovatoAstuccio:		{ i: 15 },
				trovataPergamena:		{ i: 20 },
				scopertoDizionario:		{ i: 25 },
				tradottaPergamena:		{ i: 53 },
				entratoLabirinto:		{ i: 31 },
				risoltoLabirinto:		{ i: 75 },
				trovataChiave:			{ i: 50 },
				caricatoOrologio:		{ i: 30 },
				suonaMezzanotte:		{ i: 50 },
				salitoTorre:			{ i: 5  },
				presoDaAquila:			{ i: 50 }
			}
		
		}
		// Menu
		this.menu.principale.opzioni[4] = {
			label: i18n.AvventuraNelCastelloJSEngine.menuOption4LabelOverride,
			callback: () => {
				this.peggioPerTe();
			}
		}
				
		// Override del testo mostrato da CRT.wait()
		this.CRT.waitText = ">>";
		
		// Attivo il caps lock per l'iuput
		this.CRT.capsLock = true;

		// Alcuni pattern comuni
		this.commonPatterns = {
			pronuncia: i18n.AvventuraNelCastelloJSEngine.commonPatterns.say,
			muro: i18n.AvventuraNelCastelloJSEngine.commonPatterns.wall
		}

		// Aggiungo messaggi di default a quelli esistenti
		this.Thesaurus.defaultMessages = {...this.Thesaurus.defaultMessages, 
			...{
				SII_SERIO: i18n.AvventuraNelCastelloJSEngine.defaultMessages.beSerious,
				NON_SERVE_A_NIENTE: i18n.AvventuraNelCastelloJSEngine.defaultMessages.notUseful,
				CE_LHAI_GIA: i18n.AvventuraNelCastelloJSEngine.defaultMessages.alreadyHaveIt,  
				NON_HO_CAPITO: i18n.AvventuraNelCastelloJSEngine.defaultMessages.didNotUnderstand,
				ANCORA: i18n.AvventuraNelCastelloJSEngine.defaultMessages.again, 
				NON_CONOSCI: i18n.AvventuraNelCastelloJSEngine.defaultMessages.youDontKnow,
				GIA_APERTO: i18n.AvventuraNelCastelloJSEngine.defaultMessages.isOpened,
				GIA_ADDOSSO: i18n.AvventuraNelCastelloJSEngine.defaultMessages.wearing,
				E_CHIUSO: i18n.AvventuraNelCastelloJSEngine.defaultMessages.isClosed, 
				NON_TROVATO: i18n.AvventuraNelCastelloJSEngine.defaultMessages.notFound,  
			}
		}
		// Refresh dei verbi con il nuovo dizionario
		this.Thesaurus.loadVerbs();
		
		// Sovrascrivo alcuni pattern di verbi esistenti:
		this.Thesaurus.verbs.guarda.pattern = i18n.AvventuraNelCastelloJSEngine.verbs.look.pattern;
		this.Thesaurus.verbs.lascia.pattern = i18n.AvventuraNelCastelloJSEngine.verbs.drop.pattern;
		this.Thesaurus.verbs.premi.pattern = i18n.AvventuraNelCastelloJSEngine.verbs.press.pattern;
		


		// Il verbo "dai a" non ci serve
		delete this.Thesaurus.verbs.dai;
		
		// Aggiungo verbi a quelli di default
		this.Thesaurus.verbs = { ...this.Thesaurus.verbs, 
			...{
				spingi: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.push.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.NON_SERVE_A_NIENTE,
					callback: async(targets) => {
						let target = targets[0];
						if (this.Parser._getSource("spingi", target.on))
							return null;
						if(this.inventario[target.key])
							return i18n.AvventuraNelCastelloJSEngine.defaultMessages.inYourHand;
						/*
						if(target.type == undefined){
							target.peso = 99;
						}
						*/
						if(target.peso !== undefined){
							switch (target.peso){
								case -1:
									return this.Thesaurus.defaultMessages.SII_SERIO;
								case 99:
									return this.Thesaurus.verbs.spingi.defaultMessage;
							}
						}
						return null;
					}
				},
				offri: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.offer.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				aggiusta: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.repair.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.NON_HO_CAPITO
				},

				traduci: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.translate.pattern,
					inventario:true,
					defaultMessage: this.Thesaurus.defaultMessages.NON_HO_CAPITO
				},

				suona: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.play.pattern,
					inventario: true,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				entra: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.enter.pattern,
					singolo: true,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.enter.defaultMessage
				},

				indossa: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.wear.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				alza: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.liftUp.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.liftUp.defaultMessage,
					callback: async(targets) => {
						let target = targets[0];
						if (this.Parser._getSource("alza", target.on))
							return null;
						if(this.inventario[target.key])
							return i18n.AvventuraNelCastelloJSEngine.defaultMessages.inYourHand;
						
						if(this.stanzaCorrente.interactors.hasOwnProperty(target.key))
							return this.Thesaurus.defaultMessages.SII_SERIO;
							
						if(target.peso !== undefined){
							switch (target.peso){
								case -1:
									return this.Thesaurus.defaultMessages.SII_SERIO;
								case 99:
									return this.Thesaurus.defaultMessages.NON_SERVE_A_NIENTE;
							}
						}
						return null;
					}
				},
				abbassa: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.lower.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.lower.defaultMessage
				},

				prendi: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.take.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO,
					callback: async (targets) => {
						let target = targets[0];
						if (this.Parser._getSource("prendi", target.on))
							return null;
						if(this.inventario[target.key])
							return target.key == "paracadute" ? 
								i18n.AvventuraNelCastelloJSEngine.defaultMessages.wearing : 
								i18n.AvventuraNelCastelloJSEngine.defaultMessages.inYourHand;
						
						if(target.peso !== undefined){
							switch (target.peso){
								case -1:
									return this.Thesaurus.defaultMessages.SII_SERIO;
								case 99:
									return this.Thesaurus.defaultMessages.NON_E_POSSIBILE;
							}
						}
						return null;
					}
					
				},

				leggi: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.read.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.NON_HO_CAPITO
				},

				infilaIn: {
					inventario: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.insertInto.pattern,
					complex: true,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				infila: {
					inventario: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.insert.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				prega: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.pray.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.pray.defaultMessage
				},

				atterra:{
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.land.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.land.defaultMessage
				},

				salta: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.jump.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.jump.defaultMessage
				},

				siedi: {
					singolo: true,
					complex: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.sitDown.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.sitDown.defaultMessage
				},
				
				saluta: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.greet.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.greet.defaultMessage
				},

				scava: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.dig.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.dig.defaultMessage
				},

				mangia: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.eat.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.eat.defaultMessage
				},

				bussa: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.knock.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.knock.defaultMessage
				},

				grazie: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.thank.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.thank.defaultMessage
				},
				
				aspetta: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.wait.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO,
					callback: async () => {
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.verbs.wait.defaultMessage, {cr: false});
						await this.CRT.printTyping(". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .", {printDelay: 180});
					}
				},
			
				parla: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.talk.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.talk.defaultMessage
				},

				ascolta: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.listen.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.listen.defaultMessage
				},

				compra: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.buy.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.buy.defaultMessage
				},

				rompi: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.break.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				bevi: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.drink.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				carica: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.wind.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				uccidi: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.kill.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				nutri: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.feed.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				accarezza: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.pet.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				monta: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.mount.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				chiediA: {
					complex:true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.askTo.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.askTo.defaultMessage
				},

				chiedi: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.ask.pattern,
					defaultMessage: i18n.AvventuraNelCastelloJSEngine.verbs.ask.defaultMessage
				},

				svita: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.skrewOff.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.SII_SERIO
				},

				ciao: {
					singolo: true,
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.hello.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.NON_HO_CAPITO
				},

				buongiorno: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.greeting.pattern,
					defaultMessage: this.Thesaurus.defaultMessages.NON_HO_CAPITO
				},
			}
		}

		// Aggiungo comandi a quelli di default
		this.Thesaurus.commands = { ...this.Thesaurus.commands, 
			...{
				dove: {
					// STANZA,CAMERA,SALA,LOCALE,PAVIMENTO,SOFFITTO
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.where.pattern,
					callback: async () => {
						await this.descriviStanzaCorrente(true);
						this.gameLoop(false);
						return false;
					}
				},
				
				punti:{
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.points.pattern,
					callback: async () => {
						await this._punti();
						return true;
					}
				},
				
				istruzioni: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.instructions.pattern,
					callback: async () => {
						await this.istruzioni();
						return true;
					},
				},
				
				basta: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.stop.pattern,
					callback: async () => {
						let answer = await this.yesNoQuestion(i18n.IFEngine.questions.stopQuestion);
						if(answer){
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.stop.defaultMessage,{nlAfter:1, nlBefore:1});
							await this._punti();
							await this.CRT.print("\n\n");
							this.displayMenu(this.menu.contestuale);
							return false;
						}
						return;
					}
				},
				// override Inventario
				inventario: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.inventory.pattern,
					callback: async () => {
						await this._inventario();
						this.gameLoop(false);
						return false;
					}
				},
				
				// override Salva 
				salva:{
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.save.pattern,
					callback: async () =>{
						await this.save();
						this.gameLoop(true, true);
						return false;
					}
				},
					
				// override Carica 
				carica:{
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.load.pattern,
					callback: async () =>{
						let res = await this.restore();
						return !res;
					}
				},
					
				offesa: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.insult.pattern,
					callback:async (offesa) => {
						await this.offesa(offesa);
						return false;
					}
				},

				aiuto: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.help.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.help.defaultMessage
				},

				chiama: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.call.pattern,
					
					callback: async (t) => {
						let msg = i18n.AvventuraNelCastelloJSEngine.commands.call.defaultMessage;
						if(t.indexOf(" ") >= 0){
							let altro = t.substring(t.indexOf(" ")+1, t.length);
							if(altro.match(new RegExp(this.Thesaurus.commands.aiuto.pattern)) != null){
								if(this.stanzaCorrente.key == "aereo")
									return this.stanzaCorrente.override.commands.aiuto();
								return msg;
							}

							await this.CRT.printTyping(this.Thesaurus.defaultMessages.NON_HO_CAPITO);
							this.gameLoop(false);
							return false;
								
						} 
						
						return msg;
					}
				},

				piangi:{
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.cry.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.cry.defaultMessage
				},

				turni:{
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.moves.pattern,
					callback: async () => {
						return i18n.AvventuraNelCastelloJSEngine.commands.moves.defaultMessage(this.altriDati.mosse)
					}
				},

				idiota: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.idiot.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.idiot.defaultMessage
				},

				abracadabra: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.abracadabra.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.abracadabra.defaultMessage
				},

				muori: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.die.pattern,
					callback: async () => {
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.die.defaultMessage);
						this.die();
						return false;
					}
				},

				pensa: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.think.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.think.defaultMessage
				},

				esci: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.getOut.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.getOut.defaultMessage
				},

				dormi: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.sleep.pattern,
					callback: async () => {
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.sleep.defaultMessage, {printDelay: 180});
						return true;
					}
				},

				// Non dir boh!
				boh: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.maybe.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.maybe.defaultMessage
				},

				bravo: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.good.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.good.defaultMessage
				},

				prego: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.youAreWelcome.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.youAreWelcome.defaultMessage
				},
				
				apritiSesamo:{
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.openSesame.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.openSesame.defaultMessage
				},

				aspettaMezzanotte:{
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.waitForMidnight.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.waitForMidnight.defaultMessage
				},

				saluta: {
					pattern: i18n.AvventuraNelCastelloJSEngine.verbs.greet.pattern,
					callback: this.Thesaurus.verbs.saluta.defaultMessage
				},

				buongiorno: {
					pattern: this.Thesaurus.verbs.buongiorno.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.greeting.defaultMessage
				},

				ciao: {
					pattern: this.Thesaurus.verbs.ciao.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.hello.defaultMessage
				},

				senno: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.senno.pattern(this.commonPatterns.pronuncia),
					callback: i18n.AvventuraNelCastelloJSEngine.commands.senno.defaultMessage
				},

				usaSenno: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.useSenno.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.useSenno.defaultMessage
				},

				cercaDizionario: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.lookForDictionary.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.lookForDictionary.defaultMessage
				},
				/*
				rompiMuro: {
					pattern: "rompi\\s+"+this.commonPatterns.muro,
					callback: "Il muro è duro."
				},

				guardaMuro: {
					pattern: this.Thesaurus.verbs.guarda.pattern+"\\s+"+this.commonPatterns.muro,
					callback: async () => {
						if(this.stanzaCorrente.interactors.muro){
							
							let play = this.Parser._getSource("guarda", this.stanzaCorrente.interactors.muro.on);
							if(!play)
								return this.stanzaCorrente.interactors.muro.descrizione;
							if(typeof play == 'string')
								return play;
							return await play();
						}
						return this.Thesaurus.defaultMessages.NULLA_DI_PARTICOLARE;
					}	
				},
				*/
				pronunciaSortilegio: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.saySpell.pattern(this.commonPatterns.pronuncia),
					callback: async () => {
						return this.datiAvventura.objects.spada.sortilegio();
					}

				},

				presentati: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.introduceYourself.pattern,
					callback: async () => {
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.introduceYourself.defaultMessage, {printDelay: 75});
					}
				},

				si: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.yes.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.yes.defaultMessage
				},

				no: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.no.pattern,
					callback: i18n.AvventuraNelCastelloJSEngine.commands.no.defaultMessage
				},


				// COMANDI SPECIALI 
				
				bigmeow: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.bigmeow.pattern(this.commonPatterns.pronuncia),
					callback: async () => {
						if(this.altriDati.bigmeow.attivo == false)
							return this.Thesaurus.defaultMessages.NON_CONOSCI;

						if(this.inventario.gatto === undefined && this.stanzaCorrente.objects.gatto === undefined)
							return this.Thesaurus.defaultMessages.NON_SUCCEDE_NIENTE;
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.bigmeow.defaultMessage.prelude[0], {printDelay:150});
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.bigmeow.defaultMessage.prelude[1], {printDelay: 150});
						await this.CRT.sleep(1500);
						if(this.stanzaCorrente.objects.orco){
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.bigmeow.defaultMessage.success[0], {printDelay:150});
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.bigmeow.defaultMessage.success[1]);
							delete this.inventario.gatto;
							this.datiAvventura.objects.gatto.posizione = null;
							this.datiAvventura.objects.orco.posizione = null;
							this.aggiungiPunti("eliminatoOrco");
							this.refreshOggettiInStanza();
							return true;
						} 
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.commands.bigmeow.defaultMessage.fail);
						this.die();
						return false;
					}	
				},

				iotaid: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.iotid.pattern(this.commonPatterns.pronuncia),
					callback: async () => {
						let self = this.altriDati.iotaid;
						return self.id && self.iota ? 
							this.Thesaurus.defaultMessages.NON_SUCCEDE_NIENTE:
							this.Thesaurus.defaultMessages.NON_CONOSCI;
					}	
				},
				leggiSortilegio: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.readSpell.pattern,
					callback: async () => {
						return this.datiAvventura.objects.spada.sortilegio(true);
					}	
				},
				nuota: {
					pattern: i18n.AvventuraNelCastelloJSEngine.commands.swim.pattern,
					callback: this.Thesaurus.defaultMessages.NON_HO_CAPITO
				},
				
			}
		}
	}

	// Override di IFEngine.run
	async run(){
		await this.runSequence("titolo");
		this.displayMenu(this.menu.principale);
	}

	// Override di IF.descriviStanzaCorrente
	async descriviStanzaCorrente(descrizioneLunga){
		if(this.stanzaCorrente.interactors === undefined)
			this.stanzaCorrente.interactors = {};
		
		let description;

		if(this.stanzaCorrente.interactors.pareti === undefined)
			this.stanzaCorrente.interactors.pareti = { ...this.commonInteractors.pareti};
		if(this.stanzaCorrente.primaEntrata === undefined || descrizioneLunga){
			this.stanzaCorrente.primaEntrata = true;
			description = this._descrizione(this.stanzaCorrente.description)
		} else {
			description = this._descrizione(this.stanzaCorrente.shortDescription)
		}

		await this.CRT.printTyping(description);
		await this.elenca(this.stanzaCorrente.interactors);
		await this.elenca(this.stanzaCorrente.objects);
	}

	// restituisce un array descrittivo come una stringa separata da newlines
	_descrizione(d){
		return Array.isArray(d) ? d.join("\n") : d
	}

	// Elenca una lista "cose" visibili
	async elenca(lista){
		if (lista == null)
			return;
		if( Object.keys(lista).length > 0){
			for(let i in lista){
				if(lista[i].visibile){
					let cosaVedo = Array.isArray(lista[i].label) ? 
						lista[i].label[lista[i].status] : 
						lista[i].label;
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.prefixLabels.ISee+" "+cosaVedo.trim()+".");
				}
			}
		}
	}


	
	// Override di IFEngine.die()
	async die(){
		await this.CRT.printTyping("           @     ",{printDelay:60, nlBefore: 2, m: true});
		await this.CRT.printTyping("           @     ",{printDelay:60, m: true});
		await this.CRT.printTyping("        @@@@@@@  ",{printDelay:60, m: true});
		await this.CRT.printTyping("           @     ",{printDelay:60, m: true});
		await this.CRT.printTyping("           @     ",{printDelay:60, m: true});
		await this.CRT.printTyping("           @     ",{printDelay:60, m: true});
		await this.CRT.printTyping("       ____#____ ",{printDelay:60, m: true});
		await this.CRT.printTyping("      /        / ",{printDelay:60, m: true});
		await this.CRT.printTyping("     /  ~~~~  /  ",{printDelay:60, m: true});
		await this.CRT.printTyping("    /  ~~~~  /   ",{printDelay:60, m: true});
		await this.CRT.printTyping("   /  ~~~~  /   ",{printDelay:60, m: true});
		bridge.pause();
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.dieText,{printDelay:50, nlBefore: 1,nlAfter: 1});
		await this._punti();
		await this.CRT.println("");

		await this.CRT.wait();
		this.displayMenu(this.menu.contestuale);
	}

	async wtf(APO, wtf){

		let interattore = this._getInterattore(wtf);
		let oggetto = this._get(wtf, this.datiAvventura.objects);
		
		let s = interattore ? interattore : ( oggetto ? oggetto : false);

		if(s) {
			let label = s.dlabel ? s.dlabel : (Array.isArray(s.label) ? s.label[0] : s.label)
			let msg = APO.verb == "cerca" ?
				this.Thesaurus.verbs.cerca.defaultMessage : 
				(	
					APO.actionObject.inventario === undefined ?
					(s == interattore && s.peso === undefined) || s.label === undefined ? this.Thesaurus.defaultMessages.QUI_NON_NE_VEDO : i18n.AvventuraNelCastelloJSEngine.prefixLabels.cantSeeHere+" "+label+"." :
					(s == interattore && s.peso === undefined) || s.label === undefined ? this.Thesaurus.defaultMessages.NON_NE_POSSIEDI : i18n.AvventuraNelCastelloJSEngine.prefixLabels.youDontOwn+" "+label+"."
				);

			await this.CRT.printTyping(msg);
		}
		else { 
			if(wtf.indexOf(" ") >=0)
				wtf = wtf.substring(0,wtf.indexOf(" "));
			await this.CRT.printTyping("   "+wtf.toUpperCase()+" "+i18n.IFEngine.questionMark+i18n.IFEngine.questionMark+i18n.IFEngine.questionMark);
		}
		return;
	}
	
	async _azioneInventario(APO,s){
		if(APO.actionObject.inventario){
			let inventarioKey = typeof APO.actionObject.inventario == 'boolean' ? [APO.actionObject.inventario] : APO.actionObject.inventario;
			for(let i in s){
				if(s[i].overrideAzioneInventario !== undefined && s[i].overrideAzioneInventario)
					continue;

				let label = s[i].dlabel ? s[i].dlabel : (Array.isArray(s[i].label) ? s[i].label[0] : s[i].label)
			
				if(inventarioKey[i] && this.inventario[s[i].key] === undefined){
					await this.CRT.println(s[i].label === undefined ? this.Thesaurus.defaultMessages.NON_NE_POSSIEDI : i18n.AvventuraNelCastelloJSEngine.prefixLabels.youDontOwn+" "+label+".");
					return true;
				}
			}
		}
	}

	async inputNotUnderstood(){
		this.nonhocapito = 1;
		this.input();
		return false;
	}

	async input(){
		let mossa = this.nonhocapito == 0;
		this.defaultInput = this.nonhocapito ? "   "+i18n.AvventuraNelCastelloJSEngine.messages.huh+" " : this.defaultInputOverride;
		this.nonhocapito = 0;

		// Attendo il comando
		await this.CRT.print(this.defaultInput);
		let input = await this.CRT.input();
		// Rimuovo gli n>1 spazi dal comando inviato
		input = this._prepare(input);

		if(input.length == 0){
			await this.CRT.printTyping("- "+i18n.AvventuraNelCastelloJSEngine.messages.somethingSensible);
			this.input();
			return;
		}
		/*
		let leiInput = input.indexOf(" ") == -1 ? input : input.substring(0,input.indexOf(" "));
		if(leiInput.substring(leiInput.length-2) == "re"){
			await this.CRT.printTyping("- "+i18n.AvventuraNelCastelloJSEngine.messages.dontBeFormal);
			this.input();
			return;	
		}
		*/
		bridge.println();
		if(mossa)
			this.altriDati.mosse++;

		// Faccio il parsing del comando. 
		// Se la funzione mi restituisce undefined o true allora ciclo nuovament il loop
		let repeat = await this._parse(input);

		if(repeat === undefined || repeat == true){
			this.gameLoop(repeat);
		}
	}

	// @Override _prepare
	_prepare(input){
		input = input.trim().toLowerCase()
		// tolgo gli accenti alle parole
		input = input.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
		
		for (let step of i18n.AvventuraNelCastelloJSEngine.prepareInputSteps){
			let pattern = RegExp(step.pattern,"g");
			input = input.replace(pattern,step.replaceWith);
		}
		
		return input;
		/*
		input = input.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
		return input;
		*/
	}

	_getInterattore(key){
		let stanze = this.datiAvventura.stanze;
		for(let i in stanze){
			if (stanze[i].interactors !== undefined){
				let int = this._get(key,stanze[i].interactors)
				if(int)
					return int;
			}
		}
		return false;
	}

	// @Override Prendi 
	async _prendi(oggetto){
		if(await this.canITakeThat(oggetto))
			return await super._prendi(oggetto);
		this.gameLoop(false);
		return false;
	}

	async canITakeThat(oggetto){
		if(this.stanzaCorrente.objects[oggetto.key] !== undefined){
			let peso = 0;
			for (let item in this.inventario){
				peso += this.inventario[item].peso === undefined ? 1 : this.inventario[item].peso;
			}
			if(peso < this.pesoMassimoInventario){
				return true;
			}

			await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.messages.overloaded, {nlAfter: 1});
			await this._inventario();
			return false;
		}

		return true;
	}
	// OVERRIDE Punti
	async _punti(){
		let livello;
		for(let p in this.datiPunti.puntiLevel){
			if(this.altriDati.punti <= p){
				livello = this.datiPunti.puntiLevel[p];
				break;
			}
		}
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.messages.points(this.altriDati.punti,this.datiPunti.puntiMax), {nlAfter:1});
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.prefixLabels.title,{nlAfter:1});
		await this.CRT.print("   ");
		await this.CRT.print("   "+livello.toUpperCase()+"   ",{reversed:true});
		
		if(livello == i18n.AvventuraNelCastelloJSEngine.pointsLabel[6])
			await this.CRT.print("\n   "+i18n.AvventuraNelCastelloJSEngine.pointsLabel[7]);
		await this.CRT.println("");
		
		return true;
	}

	/* NUOVE FUNZIONI */

	// Stampa le istruzioni del gioco
	async istruzioni(){
		let i=0;
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++],{nlBefore:1});
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.wait();
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++],{nlBefore:1});
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++],{nlBefore:1});
		await this.CRT.wait();
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++],{nlBefore:1});
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.wait();
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++],{nlBefore:1});
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.wait();
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]); 
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++],{nlBefore:1}); 
		await this.CRT.wait();
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++],{nlBefore:1});
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.wait();
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.instructions[i++]);
		await this.CRT.wait();
	}

	async offesa(offesa){
		await this.s0(3);
		await this.CRT.println(i18n.AvventuraNelCastelloJSEngine.insult.toMe(offesa.toUpperCase()),{reversed:true});
		await this.CRT.sleep(2000);
		await this.CRT.print("\n");
		let chunks = i18n.AvventuraNelCastelloJSEngine.insult.nowYourTurn.split(" ");
		for(let i=0; i<chunks.length; i++){
			await this.CRT.print(`${chunks[i]} `);
			await this.s0();
		}
        await this.CRT.println("");
		bridge.pause();
        bridge.joke();
		this.CRT.currentCol = 1;
		await this.CRT.println(i18n.AvventuraNelCastelloJSEngine.insult.fuck,{reversed: true, nlBefore: 2, nlAfter: 1});
		await this.s0(2);
		this.displayMenu(this.menu.contestuale);
	}

	getRandomIntInclusive(min, max) {
		min = Math.ceil(min);
		max = Math.floor(max);
		return Math.floor(Math.random() * (max - min + 1)) + min; //Il max è incluso e il min è incluso
	}

	async s0(times){
		if(times === undefined)
			times = 1;
		for (let i=0; i<times; i++){
			this.Sound.playTone(1200,"square",0.100);
			await this.CRT.sleep(125);
		}
	}

	async s1(){
		for (let j=0; j<4; j++){
			await this.CRT.sleep(2000);
			
			for (let i=0; i<11; i++){
				this.Sound.playTone("F2","square",0.04);
				await this.CRT.sleep(75);
			}
		}
		
	}

	async s2(){
		for (let j=0; j<12; j++){
			await this.CRT.sleep(2000);
			this.Sound.playTone("D8","square",0.05);
			await this.CRT.sleep(50);
			this.Sound.playTone("A7","square",0.05);
			await this.CRT.sleep(50);
			this.Sound.playTone("C8","square",0.05);
			await this.CRT.sleep(50);
			this.Sound.playTone("C8","square",0.05);
			await this.CRT.sleep(50);
		}
		
	}

	async s3() {
		await this.CRT.sleep(200);
		for (let j=0; j<3; j++){
			this.Sound.playTone("C#7","square",0.04);
			await this.CRT.sleep(75);
		}
	}

	async s4() {
		this.Sound.playTone(this.getRandomIntInclusive(1000,3000),"square",0.015);
	}

	async fakeInput(){
		// Attendo il finto comando
		await this.CRT.print(this.defaultInput);
		let fakeInput = await this.CRT.input();
		bridge.println();
		return;
	}

	// Esci dal gioco
	async peggioPerTe(){
		await this.CRT.printTyping(i18n.AvventuraNelCastelloJSEngine.messages.tough,{nlAfter:1, nlBefore:1});
		await this.CRT.wait();
		await this.CRT.clear();
		window.close();
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
	
		let regExp = new RegExp("^(?:"+pattern+")","i");
		let res = needle.match(regExp);
		return res;
	}

	async _notSeen(s){
		await this.CRT.printTyping(s.label === undefined ? this.Thesaurus.defaultMessages.QUI_NON_NE_VEDO : i18n.AvventuraNelCastelloJSEngine.prefixLabels.cantSeeHere+" "+(s.dlabel ? s.dlabel : (Array.isArray(s.label) ? s.label[0] : s.label))+".");
		return true;
	}
}
