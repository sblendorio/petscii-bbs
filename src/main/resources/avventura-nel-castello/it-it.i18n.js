var i18n = {
	htmlTitle: `Avventura nel Castello in Javascript`,
	title: `   AVVENTURA NEL CASTELLO JS   `,
	CRT: {
		waitText: `Premere invio per continuare...`
	},
	specificMaze: {
		sennoSsenseN: 's',
		sennoNsenseS: 'n',
		sennoEsenseO: 'e',
		sennoOsenseE: 'o'
	},
	IFEngine: {
		warnings: {
			mustBeExtended: `IFEngine deve essere esteso`,
			notLoaded: `Nessuna avventura caricata`,
			localstorageInactive: `Per poter effettuare salvataggi è necessario attivare il localstorage nel browser.`,
			localstorageMustBeActivated: `Attivare il localstorage nel browser.`,
			labelNotValid: `Etichetta non valida. Riprovare.`,
			doYouWantToOverwrite: `File esistente. Sovrascrivo? `,
			saveAborted: `Salvataggio abortito.`,
			notExistingFile: `Errore: file non esistente`,
			noData: `Nessun dato da caricare...`,
			notFound: (filename) => `Salvataggio "${filename}" non trovato.`
		},
		menu: {
			choose: `Vuoi:`,
			new: `Iniziare una nuova avventura`,
			load: `Riprendere una situazione salvata`,
			readInstructions: `Ripassare le istruzioni`,
			quit: `Uscire dal gioco`,
			restart: `Rincominciare dall'inizio`,
			stop: `Smettere di giocare`
		},
		questions: {
			stopQuestion: `Vuoi smettere di giocare `,
			areYouSureQuestion: `Sei sicuro `,
			saveLabel: `Etichetta salvataggio (X annulla):`,
			restoreLabel: `Etichetta (X annulla):`,
			cancelLetter: `X`,
			listLetter: `E`,
			what: `che cosa?`
		},
		yesOrNo:{
			yes: `si`,
			no: `no`
		},
		messages: {
			tanksForPlaying: `Grazie per aver giocato. Ciao! :)`,
			saved: `Dati salvati!`,
			loaded: `Dati caricati...`,
			noInstructions: `Nessuna istruzione qui...`,
			death: `SEI MORTO!!!`,
			noPoints: `Quest'avventura non prevede un punteggio`,
			points: (points, maxPoints) => `Hai conquistato ${points} punti su ${maxPoints}`,
			noObjects: `Non hai con te nessun oggetto.`,
			carriedObjectsLabel: `Attualmente possiedi:`,
			alreadyHaveIt: `Ce l'hai già`
		},
		questionMark: `?`
	},
	Thesaurus: {
		defaultMessages: {
			done: `Fatto!`,
			preferNot: `Preferisco di no.`,
			notFound: `Ricerca infruttuosa.`,
			didNotUnderstand: `Non ho capito...`,
			dontNoticeAnythingInParticular: `Non noto nulla di particolare.`,
			notSeenHere: `Qui non ne vedo.`, 
			dontHaveAny: `Non ne possiedi.`, 
			nothingHappens: `Non succede niente.`,
			beMoreSpecific: `Sii più specifico.`,
			notPossible: `Non è possibile.`
		},
		commands: {
			north: {
				pattern: `(vai verso |vai a |vai )?(n(ord)?)`,
				defaultMessage: `A nord non puoi andare.`
			},
			south: {
				pattern: `(vai verso |vai a |vai )?(s(ud|outh)?)`,
				defaultMessage: `A sud non puoi andare.`
			},
			east: {
				pattern: `(vai verso |vai a |vai )?(e(st)?|east)`,
				defaultMessage: `A est non puoi andare.`
			},
			west: {
				pattern: `(vai verso |vai a |vai )?(o(vest)?|w(est)?)`,
				defaultMessage: `Ad ovest non puoi andare.`
			},
			up: {
				pattern: `(sali|(vai verso |vai in |vai )?a(lto)?|u(p)?|su)`,
				defaultMessage: `In alto non puoi andare.`
			},
			down: {
				pattern: `(scendi|(vai verso |vai in |vai )?b(asso)?|d(own)?|giu)`,
				defaultMessage: `In basso non puoi andare.`
			}
		},
		verbs: {
			open: {
				pattern: `apri`,
				defaultMessage: `Non si apre`
			},
			close: {
				pattern: `chiudi`,
				defaultMessage: `Non si chiude`
			},
			pull: {
				pattern: `tira`,
			},
			press: {
				pattern: `premi`
			},
			push: {
				pattern: `spingi`,
				defaultMessage: `Non si muove.`
			},
			take: {
				pattern: `prendi`
			},		
			drop: {
				pattern: `lascia`
			},
			give: {
				pattern: `(dai) (.+) (?:a) (.+)`,
			},
			look: {
				pattern: `(guarda|esamina)`
			},
			useWith: {
				pattern: `(usa) (.+) (?:con) (.+)`,
				defaultMessage: `Non posso usarli insieme.`
			},
			use:{
				pattern: `usa`
			},
			lookFor:{
				pattern: `(cerca|trova)`
			},
			goUp: {
				pattern: `sali`
			}, 
			goDown: {
				pattern: `scendi`
			}
		}
	},
	AvventuraNelCastelloJSEngine: {
		warnings: {
			mustBeExtended: `AvventuraNelCastelloEngine deve essere esteso`
		},
		defaultInput: `Cosa devo fare ?`,
		prefixLabels: {
			ISee: `Vedo`,
			cantSeeHere: `Qui non vedo`,
			youDontOwn: `Non possiedi`,
			title: `Hai il diritto di fregiarti del titolo di:`
		},
		pointsLabel: [
			`Avventuriero dei miei stivali`,
			`Scemo del villaggio`,
			`Servo della gleba`,
			`Vile Meccanico`,
			`Vice Palafreniere aggiunto`,
			`Lanzichenecco`,
			`Arcivescovo  di Canterbury`,
			`(disarcivescoviscanterburyzzato)`,
			`Barone Rampante`,
			`Visconte dimezzato`,
			`Conte della malora`
		],
		menuOption4LabelOverride: `Smettere prima ancora di cominciare`,
		commonPatterns: {
			say: `(pronuncia|di)`,
			wall: `(muro|mura|pareti|parete)`
		},
		defaultMessages: {
			beSerious: `Sii Serio!`,
			notUseful: `Non serve a niente.`,
			alreadyHaveIt: `Ce l'hai già.`,
			inYourHand: `Ce l'hai in mano.`,
			wearing: `L'hai già addosso`, 
			didNotUnderstand: `- Non capisco.`,
			again: `Cos'altro speri di ottenere ?`, 
			youDontKnow: `Tu non conosci questa parola.`,
			isOpened: `E' già aperto.`,
			isClosed: `E' chiuso.`, 
			notFound: `Chi cerca trova.`,
		},
		messages: {
			huh: `Eh?`,
			somethingSensible: `Dimmi qualcosa di sensato.`,
			dontBeFormal: `Dammi del tu, per favore.`,
			overloaded: `Sei già troppo carico; devi lasciare qualcosa.`,
			points: (points, maxPoints) => `Hai duramente conquistato ${points} punti, su un possibile massimo di ${maxPoints}.`,
			tough: `Peggio per te!`
		},
		verbs: {
			look: {
				pattern: `(guarda|osserva|esamina)`
			},
			drop: {
				pattern: `(lascia|posa|molla|getta)`
			},
			press: {
				pattern: `(premi|schiaccia)`
			},
			push: {
				pattern: `(spingi|sposta|muovi)`
			},
			offer: {
				pattern: `(offri|dai)`
			},
			repair: {
				pattern: `(aggiusta|ripara)`					
			},
			translate: {
				pattern: `traduci`					
			},
			play: {
				pattern: `suona`					
			},
			enter: {
				pattern: `entra(?: in)?`,
				defaultMessage: `Da che parte ? (N/S/E/O/A/B)`					
			},
			wear: {
				pattern: `(mettiti|indossa|metti|infilati)`,
			},
			liftUp: {
				pattern: `(alza|solleva)`,
				defaultMessage: `Non c'è sotto niente.`
			},
			lower: {
				pattern: `abbassa`,
				defaultMessage: `Non si abbassa.`
			},
			take: {
				pattern: `(prendi|ruba|afferra)`
			},
			read: {
				pattern: `leggi`
			},
			insert: {
				pattern: `(infila|inserisci)`
			},
			insertInto: {
				pattern: `(infila|inserisci) (.+) (?:in) (.+)`
			},
			pray: {
				pattern: `prega`,
				defaultMessage: `  Aiutati che Dio ti aiuterà.`
			},
			land: {
				pattern: `(atterra|cabra|plana|picchia|vira|manovra)`,
				defaultMessage: `Più a terra di così!`
			},
			jump: {
				pattern: `(lanciati|gettati|buttati|salta)(?: .+)?`,
				defaultMessage: `Faccio già ginnastica tutte le mattine.`
			},
			sitDown: {
				pattern: `(siedi(?:ti)?|sdraiati)(?: su )?(.+)?`,
				defaultMessage: `Un po' di riposo fa sempre bene.`
			},
			greet: {
				pattern: `saluta`,
				defaultMessage: `Nessuna risposta.`
			},
			dig: {
				pattern: `scava`,
				defaultMessage: `Non sono tagliato per i lavori di bassa manovalanza.`
			},
			eat: {
				pattern: `(mangia|divora)`,
				defaultMessage: `Non mi sembra molto digeribile.`
			},
			knock: {
				pattern: `bussa`,
				defaultMessage: `Nessuna risposta.`
			},
			thank: {
				pattern: `(grazie|ringrazia)`,
				defaultMessage: `Prego.`
			},
			wait: {
				pattern: `aspetta`,
				defaultMessage: `D'accordo`
			},
			talk: {
				pattern: `(parla(?: con)?|interroga)`,
				defaultMessage: `Se una volta tanto pensassi invece di parlare, non sarebbe meglio?`
			},
			listen: {
				pattern: `ascolta`,
				defaultMessage: `Restando immobile con l'orecchio teso, ti par di udire lontano un rumore come di catene strascicate. Ma forse è solo uno scherzo giocato alla tua fantasia dal remoto sibilare del vento.`
			},
			buy: {
				pattern: `(compra|compera|acquista)`,
				defaultMessage: `Non hai una lira.`
			},
			break: {
				pattern: `(rompi|spacca|spezza|frantuma|distruggi|sfonda|strappa)`
			},
			drink: {
				pattern: `bevi`
			},
			wind: {
				pattern: `(carica|ricarica)`
			},
			kill: {
				pattern: `(uccidi|attacca|colpisci|ferisci|ammazza|picchia)`,
			},
			feed: {
				pattern: `(nutri|sfama|ciba)`
			},
			pet: {
				pattern: `(carezza|accarezza|coccola)`
			},
			mount: {
				pattern: `(monta|rimonta|costruisci|ricostruisci)`
			},
			ask: {
				pattern: `(chiedi|domanda)`,
				defaultMessage: `Nessuno è disposto ad offrirti l'oggetto del tuo desiderio.`
			},
			askTo: {
				pattern: `(chiedi|domanda) (.+) (?:a) (.+)`,
				defaultMessage: `Nessuno è disposto ad offrirti l'oggetto del tuo desiderio.`
			},
			skrewOff:{
				pattern: `svita`
			},
			hello: {
				pattern: `ciao`
			},
			greeting: {
				pattern: `(buongiorno|buonasera|buonanotte)`,
			}

			
		},
		commands: {
			where: {
				pattern: `(dove|guarda|esamina|osserva)( (stanza|camera|sala|pavimento|soffitto|locale))?`
			},
			points: {
				pattern: `(punti|quanto)`,
			},
			stop: {
				pattern: `(basta|stop|fine|abbandono)`,
				defaultMessage: `Mi spiace che tu voglia abbandonare la tua esplorazione, proprio quando...`					
			},
			instructions: {
				pattern: `istruzioni`
			},
			inventory: {
				pattern: `(cosa|inv(?:en(?:tario)?)?|\\?)`
			},
			save: {
				pattern: `(salva|save|registra)`
			},
			load: {
				pattern: `(carica|load|riprendi)`
			},
			insult: {
				pattern: `(idiota|scemo|cretino|merda|inculati|pirla|vaffanculo|deficiente|stupido|stronzo|imbecille)`					
			},
			help: {
				pattern: `(aiuto|sos|help|soccors(?:o|i))`,
				defaultMessage: `Arrangiati!`
			},
			call: {
				pattern: `(chiama|grida|urla)(?: (.+))?`,
				defaultMessage: `Senti in risposta un lontano suono cavernoso, e ti ci vuole qualche secondo per capire che è solo l'eco della tua voce roca.`
			},
			cry: {
				pattern: `piangi`,
				defaultMessage: `Ora che ti sei sfogato, alzati e combatti da uomo!`
			},
			moves: {
				pattern: `(turni|mosse)`,
				defaultMessage: (moves) => `Sei giunto or ora a ${moves} mosse.`
			},
			idiot: {
				pattern: `(id|iota)`,
				defaultMessage: `T cpsc sltnt a mt'`
			},
			abracadabra: {
				pattern: `abracadabra`,
				defaultMessage: `Temo che questa vecchia parola magica sia ormai consunta dal troppo uso.`

			},
			die: {
				pattern: `(muori|impiccati|crepa|sparati)`,
				defaultMessage: `D'accordo.`					
			},
			think: {
				pattern: `(pensa|ragiona|cogita|medita|deduci|ingegnati)`,
				defaultMessage: `Non mi sembra il posto adatto`
			},
			getOut: {
				pattern: `(esci(?: da)?|corri|scappa|fuggi|cammina|torna|ritorna|vai)( (?:a )?(?:n(ord)?|s(ud)?|e(st)?|o(vest)?|a(lto)?|b(asso)?))?`,
				defaultMessage: `Sono indeciso: da che parte ? (N/S/E/O/A/B)`
			},
			sleep: {
				pattern: `(dormi|riposa(?:ti)?)`,
				defaultMessage: `Z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z`
			},
			maybe: {
				pattern: `(boh|mah|forse|probabilmente)`,
				defaultMessage: `Non essere così indeciso!`
			},
			good: {
				pattern: `bravo`,
				defaultMessage: `Grazie!`
			},
			youAreWelcome: {
				pattern: `prego`,
				defaultMessage: `Non c'è di che.`
			},
			openSesame: {
				pattern: `apriti sesamo`,
				defaultMessage: `Guarda che questa è AVVENTURA NEL CASTELLO, non LE MILLE E UNA NOTTE.`
			},
			waitForMidnight:{
				pattern: `aspetta mezzanotte`,
				defaultMessage: `E' strano: quando aspetti qualcosa, sembra davvero che il tempo non passi mai.`
			},
			sayHello: {
				pattern: `saluta`,
			},
			greeting: {
				defaultMessage: `Ciao. Bella giornata, vero?`
			},
			hello: {
				defaultMessage: `Ciao. Bella giornata, vero?`
			},
			senno: {
				pattern: (sayPattern) => `(?:${sayPattern} )?senno`,
				defaultMessage: `Non è una parola magica, stupido!`
			},
			useSenno: {
				pattern: `usa senno`,
				defaultMessage: `Non mi sembra il posto adatto!`
			},
			lookForDictionary: {
				pattern: `cerca dizionario`,
				defaultMessage: `Non pretenderai che lo cerchi per tutto il castello!`
			},
			saySpell: {
				pattern: (sayPattern) => `(${sayPattern} )?(sortilegio|incantesimo)`
			},
			introduceYourself: {
				pattern: `presentati`,
				defaultMessage: `Elenchi doviziosamente i tuoi numerosi titoli onorifici, ma pare che non vi sia nessuno disposto a prestarti ascolto.`
			},
			yes: {
				pattern: `(si|certo|certamente|sicuro)`,
				defaultMessage: `O forse no.`
			},
			no: {
				pattern: `(no|mai)`,
				defaultMessage: `O forse si`
			},
			bigmeow: {
				pattern: (sayPattern) => `(${sayPattern} )?bigmeow`,
				defaultMessage: {
					prelude: [
						`Il gatto cresce fino a diventare enorme............`,
						`ti osserva con attenzione............`
					],
					success: [
						`osserva con attenzione l'orco..........`,
						`Il gatto divora l'orco e muore di indigestione.`,
					],
					fail: `e ti divora.`
				}
					
			},
			iotid: {
				pattern: (sayPattern) => `(${sayPattern} )?iotaid`,
			},
			readSpell: {
				pattern: `leggi (incantesimo|sortilegio)`,
			},
			swim: {
				pattern: `nuota`
			},

		},
		dieText: `Sono molto addolorato per la tua prematura scomparsa... D'altronde sono sempre i migliori che se ne vanno (non è vero?). Consolati comunque pensando che:`,
		instructions: [
			`Il tuo obbiettivo principale è uscire vivo dal castello.`,
			`Per farcela dovrai affrontare molti pericoli e risolvere problemi che metteranno a dura prova la tua astuzia.`,
			`In questa avventura, io sarò il tuo alter ego, i tuoi occhi e le tue orecchie, ma tu dovrai prendere le decisioni (e subirne le conseguenze).`,
			`Per muoverti usa:`,
			`- NORD, SUD, EST, OVEST, ALTO, BASSO oppure soltanto:`,
			`- N, S, E, O, A, B`,
			`Io ti darò la descrizione completa di ogni luogo la prima volta che vi entri, poi darò solo una descrizione breve. Se vuoi la descrizione completa dimmi:`,
			`- GUARDA o`,
			`- GUARDA LA STANZA`,
			`Azioni fondamentali sono:`,
			`- PRENDI qualcosa`,
			`- LASCIA qualcosa`,
			`- GUARDA qualcosa, ad esempio GUARDA LO SCALONE.`,
			`Io non sono molto furbo, per cui usa frasi come APRI LA PORTA o SALTA e non frasi elaborate come GUARDA DIETRO IL DIVANO o avverbi (GUARDA ATTENTAMENTE), che sono al di là della mia comprensione.`,
			`Per agire su un oggetto, di solito è necessario possederlo. Inoltre, ricorda che un'azione che non ha effetto in un posto (es. CERCA) può averne da qualche altra parte.`,
			`Altri comandi importanti:`,
			`- DOVE ti ricorda dove ti trovi,`,
			`- COSA elenca gli oggetti che possiedi,`,
			`- MOSSE ti dice da quanto giochi,`,
			`- PUNTI quanto sei riuscito a scoprire,`,
			`- SAVE serve a registrare la situazione su disco,`,
			`- LOAD ripristina la situazione su disco,`,
			`- BASTA termina il gioco,`,
			`- ISTRUZIONI ti ripete questa descrizione.`,
			`Buona Fortuna! (ne avrai bisogno)`

		],
		insult: {
			toMe: (insult) => ` ${insult} A ME???? `,
			nowYourTurn: `ADESSO TI FACCIO VEDERE IO!!!!`,
			fuck: ` Tié! `
		},
		prepareInputSteps: [
			{
				pattern: `[\\.,:;!"£\\$%&\\/\\(\\)=°\\+\\*]*`,
				replaceWith: ``
			},
			{
				pattern: `'`,
				replaceWith: ` `
			},
			{
				pattern: ` (il|la|lo|le|li|l|gli|un|uno|una) `,
				replaceWith: ` `
			},
			{
				pattern: ` +`,
				replaceWith: ` `
			},
			{
				pattern: ` (del|dell|dello|degli|dei|della|delle) `,
				replaceWith: ` di `
			},
			{
				pattern: ` (al|all|allo|agli|alla|alle) `,
				replaceWith: ` a `
			},
			{
				pattern: ` (dal|dall|dallo|dagli|dalla|dalle) `,
				replaceWith: ` da `
			},
			{
				pattern: ` (nel|nell|nello|negli|nella|nelle|dentro) `,
				replaceWith: ` in `
			},
			{
				pattern: ` (col|coi) `,
				replaceWith: ` con `
			},
			{
				pattern: ` (sul|sull|sullo|sugli|sulla|sulle|sopra) `,
				replaceWith: ` su `
			},
			{
				pattern: ` (tra|fra) `,
				replaceWith: ` tra `
			},

		]
	},
	AvventuraNelCastelloJS: {
		commonInteractors: {
			steps: {
				label: `i gradini`,
				pattern: `gradin(?:i|o)`,
			},
			stairs: {
				label: `la scala`,
				pattern: `scal(?:a|e|one)`,
				description: `E' a gradini`,
			},
			walls: {
				label: `le pareti`,
				onBreak: `Il muro è duro`
			},
			armor: {
				label: `l'armatura`,
				pattern: `armatur(?:a|e)`,
				description: `Ciascuna armatura sembra guardarti dall'alto in basso con aria altezzosa.`,
				onTakeOrWear: `Scherzi? Pesa il doppio di te. A quei tempi non erano certo dei rammolliti, come al giorno d'oggi.`
			},
			hallway: {
				label: `il corridoio`,
				pattern: `corridoio`,
			},
			skeletons: {
				label: `gli scheletri`,
				pattern: `(scheletr(?:o|i)|ossa)`,
				description: `E' la nostra comune sorte. Ma non puoi pensare a qualcosa di più allegro?`,
			},
			labyrinth: {
				label: `il labirinto`,
				pattern: `(labirinto|passaggi(?:o)?)`,
				description: `Sembra facile! In questo incrociarsi sfumato di piani inclinati lungo curve impossibili, non si distingue il pavimento dal soffitto, e tutte le direzioni sembrano uguali.`
			},
			door: {
				label: `la porta`,
				pattern: `porta`
			},
			table: {
				label: `il tavolo`,
				pattern: `tavol(?:o|a|ino)`,
			},
			bows: {
				label: `gli archi`,
				pattern: `arc(?:o|hi)`,
			},
			shields: {
				label: `gli scudi`,
				pattern: `scud(?:o|i)`,
			},
			fireplace: {
				label: `il camino`,
				pattern: `camino|focolare`
			},
			armchairs: {
				label: `le poltrone`,
				pattern: `poltron(?:a|e)`
			},
			chairs: {
				label: `le sedie`,
				pattern: `sedi(?:a|e)`
			},
			monster: {
				label: `il mostro`,
				pattern: `(mostr(?:o|i)|nessie)`
			},
			shelves: {
				label: `gli scaffali`,
				pattern: `scaffal(?:e|i)`
			},
			bed: {
				label: `il letto`,
				pattern: `baldacchino|letto`,
			},
			weapons: {
				label: `le armi`,
				pattern: `arm(?:a|i)`,
			},
			fog: {
				label: `la nebbia`,
				pattern: `nebbia`,
				description: `E' difficile mettere a fuoco la vista: non capisci se l'uniforme granulosità grigia che stai fissando è a dieci metri o a dieci centimetri dal tuo naso. Gli ululati che giungono fin qui hanno un timbro ovattato, ma non per questo più rassicurante.`
			},
			crows: {
				label: `i corvi`,
				pattern: `corv(?:o|i)`,
				description: `Sono del tutto invisibili, persi da qualche parte nell'uniforme luminosità lattiginosa. Puoi solo sentirne il verso stridente proveniende da un punto indefinito sopra la tua testa.`,
				onListen: `Gracchiano la loro derisione nei confronti dei tuoi inutili e ridicoli sforzi, o almeno questa è la tua impressione.`
			},
			ramparts: {
				label: `gli spalti`,
				pattern: `(mura|spalti)`,
				description: `Guardando giù dagli spalti, la nebbia ti causa come una specie di vertigine. Non vedi nulla di solido che ti possa dare un riferimento per capire a che altezza ti trovi. Ma non puoi essere molto in alto, a giudicare dalla scala a chiocciola che hai salito per arrivare quassù.`
			},
			pass: {
				label: `il passaggio`,
				pattern: `passaggi(?:o)?`
			},
			tunnel: {
				label: `il cunicolo`,
				pattern: `cunicolo`
			},
			rock: {
				label: `la roccia`,
				pattern: `roccia`
			},
			tower: {
				label: `la torre`,
				pattern: `torre`
			}
		},
		commonRooms:{
			spiralStaircase: {
				description: `Sei in una stanza con una scala a chiocciola.`,
			},
			ramparts: {
				description: [
					`Sei sugli spalti delle mura. La visibilità è molto ridotta dalla nebbia che sale dalla brughiera in larghe, lente spirali. Senti, senza vederli, il gracchiare dei corvi che volteggiano sopra di te.`,
					`Una scala a chiocciola porta in basso.`
				],
				shortDescription: `Sei sugli spalti delle mura.`,
				onJump: [
					`Grazie alla tua agilità, atterri senza danni.`,
					`I lupi sono felici di avere un po' di carne fresca.`
				]
			},
			labyrinth: {
				description: `Sei nel labirinto.`,
				onDrop: `Si allontana lungo i percorsi sinuosi di una gravità contorta.`,
				onGetOut: `Illuso! Credi di poter riuscire dove tanti hanno fallito?`,
				onThink: [
					`Dunque, vediamo...`,
					`Mumble...`,
					`mumble...`,
					`Non ho concluso niente.`,
				],
				onThinkQuestion: `Devo pensare ancora `
			}
		},
		rooms: {
			plane: {
				description: `Stai precipitando!`,
				directions: {
					down: `Stai già scendendo, e velocemente:`
				},
				commands: {
					help: [
						`Selezioni sulla radio di bordo la frequenza di soccorso e lanci un S.O.S.`,
						`Per qualche secondo tutto tace...`,
						`Poi ricevi chiara una voce registrata:`,
						`- Comunichiamo a tutti gli utenti del soccorso aereo che il servizio è momentaneamente sospeso a causa di un improvviso sciopero del personale.`,
						`In sostituzione, vogliate recitare con me: `,
						`- Pater noster qui es in coelis... `
					]
				},
				verbs: {
					jump: `Non hai addosso nessun paracadute.`,
					look: `C'è troppo fumo!`,
					land: `Non posso: i comandi non rispondono.`
				},
				interactors: {
					cloche: {
						label: `la cloche`,
						pattern: `cloche`,
						onPullOrPush: [
							`I comandi rispondono un poco!`,
							`...quanto basta per peggiorare la situazione.`
						]					
					},
					engine: {
						label: `il motore`,
						pattern: `motore`,
						onRepair: `Non me ne intendo.`
					},
					plane: {
						label: `l'aereo`,
						pattern: `aereo`
					}

				}
			},
			paradeGround: {
				description: [
					`Sei nella piazza d'armi: un vasto spiazzo quadrato in terra battuta cinto da alte mura di pietra grigia.`,
					`Al centro della spianata, un lastrone massiccio copre l'imboccatura di quello che era il pozzo del castello.`,
					`Sento, in lontananza, ululati di lupi.`
				],
				shortDescription: [
					`Sei nella piazza d'armi.`,
					`Sento lupi ululare.`
				],
				directions: {
					north: {
						success: `Il portone si richiude pesantemente, senza lasciare la minima fessura.`,
						fail: `Il portone è chiuso.`
					},
					south: `Non puoi, il ponte è alzato.`,
					up: `Nel caso non te l'avessero insegnato, sappi che il paracadute è un dispositivo che funziona solo in discesa.`
				},
				interactors: {
					drawbridge: {
						label: `un ponte levatoio alzato`,
						pattern: `ponte`,
						description: `E' alzato, ma lo si può abbassare senza difficoltà.`,
						onLower: `I lupi entrano e ti sbranano.`
					},
					doorway: {
						dlabel: `un portone`,
						label: [
							`un portone chiuso`,
							`un portone aperto`
						],
						pattern: `portone`,
						description: `E' molto massiccio.`,
						onClose: `E' già chiuso.`
					},
					stoneSlab: {
						label: `il lastrone di pietra`,
						pattern: `lastrone|pozzo`,
						onLook: [
							`Il lastrone di pietra, corroso dai secoli, cede sotto il tuo peso. Precipiti nel pozzo infestato da esseri viscidi e repellenti`,
							`(e carnivori).`
						]
					},
					walls: { 
						description: `Sono massicce, squadrate e solidamente costruite per resistere ad ogni assalto.`
					},
					esplanade: {
						label: `la spianata`,
						pattern: `spianata`
					},
					castle: {
						label: `il castello`,
						pattern: `castello`
					}
				}

			},
			atrium:{
				description: [
					`Sei in un grande atrio immerso nella penombra. Una lugubre fosforescenza che emana dalle pareti ti permette di distinguere i contorni della stanza.`,
					`Uno scalone di marmo sale perdendosi nell'oscurità attenuata soltanto dal diffuso chiarore verdastro.`
				],
				shortDescription: `Sei nell'atrio.`,
				directions: {
					south: `Niente da fare. Temo che dovrai trovare un'altra via d'uscita.`
				},
				interactors: {
					blazon: {
						label: `un blasone dipinto sul soffitto`,
						pattern: `blasone`,
						onLook: [
							`C'è scritto il motto della dinastia`,
							`"Cadrà lo straniero"`,
							`Improvvisamente una botola si apre sotto i tuoi piedi.`
						]
					},
					doorway: {
						label: `un portone`,
						pattern: `portone`
					}
				}
			},
			lounge: {
				description: [
					`Sei nel grande salone, arredato con numerosi divani e comode poltrone. Al centro di una parete è posto un monumentale camino costruito con blocchi di pietra lavorata.`,
					`Benché‚ il fuoco sia spento da secoli, la stanza sembra illuminata da una ondeggiante luce rossastra.`
				],
				shortDescription: `Sei nel salone.`,
				interactors: {
					sofa: {
						label: `il divano`,
						pattern: `divan(?:o|i)`,
					},
					fireplace: { 
						description: `E' tanto grande che ci potresti agevolmente passare, se ci fosse un passaggio. Peccato che non se ne veda traccia. Forse a cercare...`
					},
					light: {
						label: `la luce`,
						pattern: `luce`,
						description: `Strano, quando volgi lo sguardo dove pensavi di intravvedere la luce, questa pare spostarsi da un'altra parte. Forse è solo un riflesso, o la secolare memoria del sangue versato tra queste mura.`
					},
					pass: {
						pattern: `passaggio(?: segreto)?`,
						onLookFor: `Nel camino non c'è proprio traccia di passaggi segreti. Tu leggi troppi libri di avventure.`
					}
				},
			},
			hallway:{
				description: [`Sei in un largo corridoio, il cui pavimento porta i segni del passaggio di innumerevoli generazioni. Lungo la parete è allineata una fila di armature, reggenti ciascuna una lunga picca.`,
					`Verso il centro del corridoio sembra esservi stata una porta, ora murata.`
				],
				shortDescription: `Sei nel corridoio.`,
				interactors: {
					spades: {
						label:`le picche`,
						pattern: `picc(?:a|he)`,
						description: `Lunga e munita di un'acuta punta in ferro, si direbbe un'arma micidiale. Chissà come veniva usata?`,
						onTake: {
							question: `Prendi la picca e la tiri verso di te, ma l'armatura non sembra voler mollare la presa. Devo insistere `,
							answer: [
								`Con uno strattone deciso, riesci finalmente ad appropriarti della picca.`,
								`L'armatura, sbilanciata, traballa leggermente...`,
								`...`,
								`e mentre indietreggi con la punta della picca stretta tra le mani, l'armatura cade con tutto il suo peso sull'altra estremità dell'arma, trafiggendoti da parte a parte.`,
								`Ecco come veniva usata in battaglia!`
							] 
						}

					},
					door: {
						description: `Ho detto che sembra esservi stata una porta, della quale non resta che la traccia di un profilo sul muro.`,
					}
				}
			},
			diningRoom: {
				description: [
					`Sei nella grande sala da pranzo, occupata per tutta la sua lunghezza da un enorme tavolo in quercia, circondato da molte pesanti sedie con alti schienali.`,
					`A differenza di tutte le altre stanze, la finestra è aperta.`
				],
				shortDescription: `Sei nella sala da pranzo.`,
				directions: {
					down: {
						question: `Devo saltare dalla finestra `
					} 
				},
				interactors: {
					table: { 
						description: `E' solido e ben costruito: serviva per i Banchetti Reali.`
					},
					chairs: { 
						description: `Vedendo le sedie intorno al tavolo, ti immagini il Re e la Regina graziosamente accomodati alle due estremità, con gli invitati disposti secondo il proprio grado di nobiltà, in base al rigido protocollo imposto dal cerimoniale di corte.`
					},
					window: {
						label: `la finestra`,
						pattern: `(da )?finestra`,
						description: `Dalla finestra si gode una magnifica vista sulle torbide acque del fossato, popolate di mostri scagliosi dai denti aguzzi.`,
						onJump: `Non sono mica scemo!`
					}
				}
			},
			library: {
				description: `Sei nella biblioteca del castello, le cui pareti sono completamente occupate da scaffali e scaffali, carichi di libri d'ogni genere. Al centro del pavimento, davanti ad una comoda poltrona, è fissato un leggio in ferro lavorato.`,
				shortDescription: `Sei in biblioteca.`,
				HUGE: `Il peso dell'antica sapienza è molto maggiore di quanto potessi sospettare.`,
				override: {
					commands: {
						lookForDictionary: `Ho esaminato accuratamente tutti gli scaffali, ma non ho trovato nessun dizionario.`,
						iotid: [
							`Il suono della parola magica riecheggia tra le antiche volte...`,
							`Un'intera parete di scaffali ruota su se stessa. Intravedo una grande sala.`
						]
					}
				},
				interactors: {
					shelves: {
						description: `Veramente una Biblioteca Reale! C'è tutto o quasi. Sono convinto che a ben cercare si possa trovare qualunque cosa, qui dentro.`
					},
					lectern: {
						label: `il leggio`,
						pattern: `leggio`,
						description: `Un'opera davvero pregevole.`,
					},
					books: {
						label: `i libri`,
						pattern: `libri`,
						description: `Veramente una Biblioteca Reale! C'è tutto o quasi. Sono convinto che a ben cercare si possa trovare qualunque cosa, qui dentro.`,
						onTake: `Sono un po' troppi per trasportarli.`,
						onRead: `Ci vorrebbero secoli per sfogliarli tutti. Quelli che prendi in mano sono indubbiamente interessanti, ma non risolvono alcuno dei tuoi problemi. Dovresti almeno dirmi cosa cercare.`
					},
					book: {
						dlabel: `un libro`,
						label: [
							`un libro chiuso sul leggio`,
							`un libro sul leggio`
						],
						pattern: `libro`,
						description: `E' posato sul leggio, e sembra insieme antico e nuovissimo.`,
						onOpen: `Dalle vecchie pagine ingiallite scivola fuori un foglio bruciacchiato.`,
						onRead: `E' un dizionario di antico gaelico.`
					},
					dictionary: {
						label: `un dizionario`,
						pattern: `(dizionario|vocabolario)`,
						description: `E' posato sul leggio, e sembra insieme antico e nuovissimo.`,
						posizione: `biblioteca`,
						onRead: `E' costituito da un elenco di parole in gaelico, ciascuna corredata dalla traduzione (con alcuni commenti) in lingua moderna. Le parole sono in ordine alfabetico, dalla A alla Z. C'è anche un elenco di proverbi, modi di dire e regole grammaticali e di pronuncia. Ma possibile che tu non abbia mai visto un dizionario?`
					},
				},
			},
			wideTunnel: {
				description: `Stai percorrendo un largo cunicolo, ricavato nella roccia che costituisce le fondamenta del castello.`,
				shortDescription: `Sei in un largo cunicolo.`,
				directions: {
					north: {
						fail: `L'orco ti fa a pezzi.`
					}
				}
			},
			castleDungeon: {
				description: `Sei nella segreta del castello, un tempo chiamata 'La Tomba'. Il pavimento è coperto di scheletri.`,
				shortDescription: `Sei nella segreta.`,
				interactors: {
					hole: {
						label: `un foro sulla parete`,
						pattern: `foro|buco`,
						description: `E' stretto e profondo: in fondo c'è qualcosa che sembra un bottone.`
					},
					button: {
						label: `bottone`,
						pattern: `bottone|pulsante`,
						description: `Guardarlo non serve a niente. Forse a premerlo...`,
					},
					slit: {
						label: `la fessura`,
						pattern: `fessura`,
						description: `E' nella parete a ovest, ed è abbastanza larga per passarci.`,
						invisibleMessage: `Non c'è nessuna fessura qui.`
					},
					arm: {
						label: `il braccio`,
						pattern: `braccio`
					}
				}
			},
			toolshed: {
				description: `Sei in una piccola stanza quadrata, adibita un tempo a deposito degli attrezzi.`,
				shortDescription: `Sei nel deposito degli attrezzi.`,
				directions: {
					up: `Per salire devi sollevare una lastra di pietra, che ricade alle tue spalle.`
				}
			},
			narrowTunnel: {
				description: `Sei in uno stretto cunicolo, le cui pareti sono ricoperte da muffe verdastre e funghi putrescenti.`,
				shortDescription: `Sei in uno stretto cunicolo.`,
				interactors: {
					moulds: {
						label: `le muffe`,
						pattern: `muff(?:a|e)`,
						description: `Sono affascinanti, nella loro venefica peluria vellutata.`		
					},
					mushrooms: {
						label: `i funghi`,
						pattern: `fung(?:o|hi)(?: putrescent(?:e|i))?`,
						description: `Da quello che puoi capire in base alla tua limitata competenza in materia, sono uno pi— velenoso dell'altro.`
					}
				},

			},
			longTunnel: {
				description: `Sei in un lungo cunicolo, ricavato in parte nella roccia e in parte nel terreno. Le pareti mostrano tracce di scavi.`,
				shortDescription: `Sei in un lungo cunicolo.`,
				interactors: {
					ground: {
						label: `il terreno`,
						pattern: `terreno`
					},
					excavations: {
						label: `gli scavi`,
						pattern: `scavi|tracce`,
						description: `Sono solo dei tentativi, evidentemente falliti. Forse era qualche prigioniero del castello che faceva un ultimo tentativo disperato.`				
					}
				}
			},
			treasureChamber: {
				description: `Sei nella camera del tesoro, un immenso locale sostenuto da possenti archi e posto esattamente sotto la sala del trono.`,
				shortDescription: `Sei nella camera del tesoro.`,
				interactors: {
					coffer: {
						dlabel: `un forziere`,
						label: [
							`un pesante forziere`,
							`un pesante forziere aperto`
						],
						pattern: `(?:pesante )?forziere`,
						onLook: [
							`Del favoloso Tesoro di Malcolm IV è rimasto soltanto un vecchio corno.`,
							`E' vuoto.`
						],
						onOpen:[
							`Il fantasma di Edgar mac Douglas, fedele scudiero di Sire Malcolm, sorge a difendere il tesoro del suo antico Re dal profanatore straniero.`,
							`Il fantasma ti avvolge soffocandoti nel suo abbraccio mortale.`
						],
						onClose: `I cardini si sono rotti.`
					},
					ghost: {
						label: `un fantasma`,
						pattern: `fantasma`,
						description: `Lo vedi e non lo vedi. A tratti pare incorporeo, tanto che puoi chiaramente osservare i dettagli del muro retrostante. Ma in altri momenti sembra prendere sostanza, e farsi pi— minaccioso.`,
						onKill: `E' già morto da settecento anni.`,
						onTalk: [
							`Un lontano strascichio di oggetti metallici (forse catene?) pare formulare una lamentosa riposta:`,
							`- Ricordati che sei venuto dal Nulla ed al Nulla dovrai tornare...`
						]
					}
				}
			},
			woodshed: {
				description: `Sei nel deposito della legna, dove sono accatastati in perfetto ordine rami secchi e ceppi di varie grandezze.`,
				shortDescription: `Sei nella legnaia.`,
				interactors: {
					wood: {
						label: `la legna`,
						pattern: `legna|ram(?:o|i)|cepp(?:o|i)`,
						description: `Si direbbe legna da ardere.`,
						onTake: `- Ehi! Giù le mani dalle mie scorte per l'inverno! Fa freddo, da queste parti.`
					}
				},
				override: {
					commands: {
						helloPrefix: `Sia pure un poco sconcertato dalla tua eccessiva confidenza...`,
						introduceYourself: [
							`- Qfwfq, al vostro servizio. Ma la vostra faccia non mi giunge del tutto nuova. Non ci siamo già conosciuti da qualche parte?`,
							`Elenchi doviziosamente i tuoi numerosi titoli onorifici, mentre il nano ascolta con espressione sempre più dubbiosa. Tuttavia...`
						]
					},
					verbs: {
						askForDiamond: [
							`Te l'ha già regalato, non ricordi?`,
							`Dubito che sia disposto a regalartelo.`
						],
					}
				},
			},
			topOfStairs: {
				description: `Sei in cima alle scale. I gradini terminano bruscamente di fronte ad una liscia parete di pietra.`,
				shortDescription: `Sei in cima alle scale.`,
				interactors: {
					walls: {
						description: `Non vedo appigli o fessure.`,
						onPush: [
							`La parete ruota su se stessa...`,
							` e si richiude di scatto dietro di te.`
						]
					}
				},
			},
			labyrinthEntrance: {
				description: [
					`Sei all'entrata dell'immenso labirinto magico, del quale si dice che tutti i passaggi portano a quest'unica stanza, alla cui attrazione non possono sfuggire né uomini né cose.`,
					`Per terra ci sono due scheletri. Sul muro è scritto col sangue:`,
					``,
					`        'Impossibile uscire di qui'`
				],
				shortDescription: `Sei all'entrata del labirinto.`,
				interactors: {
					writing: {
						label: `la scritta`,
						pattern: `scritta`
					}
				}
			},
			secretRoom: {
				description:`Sei nella grande stanza segreta, sotto la torre del castello. Una corrente di aria gelida sibila entrando da invisibili fessure.`,
				shortDescription:`Sei nella stanza segreta.`,
				interactors: {
					slits: {
						label: `le fessure`,
						pattern: `fessure`,
						description: `Come ho detto, sono invisibili.`
					},
					lever: {
						label: `una leva`,
						pattern: `leva`,
						onPush: `Si apre una botola...`
					},
					pendulumClock: {
						dlabel: `un orologio`,
						label: [
							`un vecchio orologio a pendolo fermo`,
							`un vecchio orologio a pendolo`,
							`un vecchio orologio a pendolo`,
							`un vecchio orologio a pendolo`,
							`un vecchio orologio a pendolo`,
							`un vecchio orologio a pendolo`,
							`un vecchio orologio a pendolo`
						],
						pattern: `(?:vecchio )?orologio(?: a pendolo)?`,
						description: [
							`Segna le undici e cinquantasei.`,
							``,
							`Segna le undici e cinquantasette.`,
							`Segna le undici e cinquantotto.`,
							`Segna le undici e cinquantanove.`,
							``,
							`Le lancette sono bloccate su mezzanotte.`
						],
						onLook: [
							`Segna mezzanotte in punto.`,
							`Un blocco di pietra si sposta, rivelando una scala a chiocciola.`
						],
						onCharge: {
							fail: `Non hai la chiave.`,
							success: `L'orologio riprende a funzionare.`,
							working: `Sta già funzionando.`,
							blocked: `E' definitivamente bloccato.`
						} 
					}
				},
			},
			L29: {
				dodgersHatch: `Questa botola è riservata ai furbi che pretendono di essere arrivati qui senza aver tradotto la pergamena...`
			},
			throneRoom: {
				description: `Sei nell'antica sala del trono, dove il Re soleva amministrare la giustizia e ricevere gli ambasciatori. Ai lati della stanza, due file di nicchie in cui trovavano posto le guardie personali del Sovrano. L'imponente trono in legno è finemente lavorato nei minimi dettagli. Di fronte al trono, una porta murata che doveva costituire un tempo l'ingresso principale dal corridoio.`,
				shortDescription: `Sei nella sala del trono.`,
				interactors: {
					door: {
						description: `Ovviamente è murata anche da questa parte.`
					},
					hollows: {
						label: `le nicchie`,
						pattern: `nicchi(?:a|e)`,
						description: `Sono delle semplici rientranze nelle pareti. Il lavoro delle guardie personali del Re doveva essere alquanto noioso, se consisteva nello stare lì impalati per tutto il santo giorno.`
					},
					throne: {
						label: `il trono`,
						pattern: `trono`,
						description: `Troneggia in mezzo alla stanza. Su di esso sedeva il Potere.`,
						onSitDown: {
							question: `Vuoi che mi sieda sul trono ??`,
							answer: `Il cuscino non è comodo come pensavo.`
						}
					}
				}
			},
			topOfTower: {
				description: `Sei in cima alla torre, dove lo sguardo può spaziare al di sopra della nebbia che copre la brughiera, fino alle lontane montagne.`,
				shortDescription: `Sei in cima alla torre.`,
				interactors: {
					fog:{
						description: `La superficie della nebbia è increspata da onde in lento perenne movimento, come se la torre fosse un'isola galleggiante in questo strano mare senza pesci.`
					},
					moor: {
						label: `la brughiera`,
						pattern: `brughiera`,
						description: `E' nascosta sotto la nebbia, ma a tratti, in lontananza, questa si affievolisce un poco, lasciando intravvedere il brullo e giallastro terreno sottostante.`,
					},
					mountains: {
						label: `le montagne`,
						pattern: `montagn(?:a|e)`,
						description: `Dai picchi innevati più lontani, le montagne digradano in una successione di alture, colline, dolci declivi, fino a confondersi con la brughiera sommersa nel mare di nebbia. Hai l'impressione che qualcosa di minuscolo si muova intorno alle cime, ma probabilmente è solo un'illusione ottica.`							
					},
					flag: {
						label: `una bandiera a brandelli`,
						pattern: `bandiera`,
						description:  `Portata con gloria in cento battaglie, sventola ancora sulle terre che un tempo dominava.`,
						onTake: [
							`La vecchia asta oppone resistenza...`,
							` e cede improvvisamente facendoti perdere l'equilibrio. Precipiti nella piazza d'armi.`
						],
						onLiftUp: `Queste cerimonie mi commuovono sempre...`
					},
					tower: {
						description: `Non sapresti dire di quanto s'innalza al di sopra delle mura, perché la nebbia arriva con le sue spire inconsistenti fino a rendere a tratti invisibili i tuoi stessi piedi.`
					}
				},
				override: {
					verbs: {
						jump: `- SPLAT! -`
					}
				}
			},
			undergroundSpiralStaircase: {
				description: `Sei in una stanza con una scala a chiocciola ed uno stretto passaggio verso nord.`,
				interactors: {
					aisle: {
						description: `Si addentra nei sotterranei del castello. Meglio tornare indietro, non credi?`
					}
				}
			},
			trap: {
				description: `Sei nella stanza della trappola, che sembra completamente vuota.`,
				shortDescription: `Sei nella stanza della trappola.`,
				directions: {
					west: [
						`Una massiccia lastra di ferro cade all'improvviso bloccandoti ogni via di uscita.`,
						`La stanza inizia a riempirsi d'acqua... glu... glu... glu... glu... glu... glu... glu... glu... glu... glu... glu... glu... glu... glu... glu...`,
						` GLUB.`
					]
				},
				interactors: {
					trap: {
						label: `la trappola`,
						pattern: `trappola`,
						onLookOrLookFor: `Non vedo proprio nessuna trappola. Non capisco perché la stanza abbia questo curioso nome.`
					}
				}

			},
			winePantry: {
				description: `Sei nella dispensa dove venivano conservati i vini. Sugli scaffali sono rimasti solo alcuni cocci di vetro.`,
				shortDescription: `Sei nella dispensa dei vini.`,
				directions: {
					north: [
						`Vedendoti, il nano esclama:`,
						`- Anche gli ubriachi, adesso!`,
						`Mai una persona civile!`,
						`Questo è troppo! -`,
						`Ciò detto, estrae dalla cintura una pesante ascia e ti colpisce con rabbia.`
					]
				},
				interactors: {
					fragments: {
						label: `i cocci`,
						pattern: `cocci(?:o)?`,
						onTake: `Oltre a rischiare di tagliarti malamente, non vedo proprio a cosa potrebbero servirti.`
					}
				}
			},
			coldCutsPantry: {
				description: `Sei nella dispensa dove venivano conservati i salumi. Rimangono ormai soltanto dei ganci arrugginiti.`,
				shortDescription: `Sei nella dispensa dei salumi.`,
				interactors: {
					hooks: {
						label: `i ganci`,
						pattern: `ganci(?:o)?`,
						onTake: `Oltre a rischiare di prendere il tetano, non vedo proprio cosa potresti fartene.`
					}
				}
			},
			vegetablePantry: {
				description: `Sei nella dispensa dove venivano tenute le provviste di verdura, unica traccia delle quali sono alcune viscide macchie verdastre sul pavimento.`,
				shortDescription: `Sei nella dispensa della verdura.`,
				interactors: {
					stains: {
						label: `le macchie`,
						pattern: `macchi(?:a|e)(?: verdastr(?:a|e))?`
					}
				}
			},
			cheesePantry: {
				description: `Sei nella dispensa dove venivano conservati i formaggi, dei quali non rimangono che alcune croste mangiucchiate dai topi.`,
				shortDescription: `Sei nella dispensa dei formaggi.`,
				interactors: {
					crusts: {
						label: `le croste`,
						pattern: `crost(?:a|e)`,
						onTakeOrEat: `Non puoi avere TANTA fame!`
					}
				}
			},				
			gamePantry: {
				description: `Sei nella dispensa dove venivano tenute le provviste di cacciagione, delle quali non resta che la carcassa raggrinzita di un vecchio cervo.`,
				shortDescription: `Sei nella dispensa della cacciagione.`,
				interactors: {
					deer: {
						label: `il cervo`,
						pattern: `cervo|carcassa`,
						onTake: `Oltre che portarti dietro un carico supplementare, non vedo proprio di che utilità potrebbe esserti.`
					}
				}
			},				
			whiskeyPantry: {
				description: `Sei nella dispensa dove venivano tenute consistenti scorte del celebre whisky della regione. Tutto ciò che ne resta è una botticella dall'aria straordinariamente ben conservata.`,
				shortDescription: `Sei nella dispensa del whisky.`,
				interactors: {
					keg: {
						label: `la botticella di whisky`,
						pattern: `botticella`,
						description: `C'è scritto sopra: '700 yrs old'`,
						onOpenQuestion: `Devo bere il whisky `
					},
					whiskey: {
						label: `il whisky`,
						pattern: `whisky`,
						onTakeOrDrink: `Ottimo, veramente ottimo!`
					}
				}
			},
			guardRoom:{
				description: `La stanza dove ti trovi doveva essere la sala delle guardie che vigilavano sull'ingresso del castello. A parte una rozza tavola e delle pesanti panche, la stanza è completamente spoglia.`,
				shortDescription: `Sei nella sala delle guardie.`,
				interactors: {
					table: { 
						description: `E' una tavolaccia di legno non certo elegante ma molto robusto.`
					},
					benches: {
						label: `le panche`,
						pattern: `panc(?:a|he)`,
						description: `Sono dello stesso legno della tavola, levigato dal lungo uso.`
					}
				},

			},
			catapultRoom:{
				description: [
					`Sei in una stanza di forma irregolare, occupata da ingombranti strutture in legno e metallo. Sembrano pezzi di un'antica macchina da guerra, probabilmente una piccola catapulta.`,
					`Pesanti palle di pietra sono allineate lungo la parete.`
				],
				shortDescription: `Sei nella stanza della catapulta.`,
				commonAnswers: [
					`Ma chi ti credi di essere? Maciste?`,
					`Non sono mai stato molto bravo col meccano.`
				],
				interactors: {
					catapult: {
						label: `la catapulta`,
						pattern: `catapulta|macchina`,
						description: `E' stata smontata in tanti pezzi, per occupare meno spazio e conservarla in condizioni migliori.`,
					},
					balls: {
						label: `le palle`,
						pattern: `(pall(?:a|e))`,
						onBreak: `Non essere volgare.`
					},
					pieces: {
						label: `i pezzi`,
						pattern: `(pezz(?:o|i)|struttur(?:a|e))`,
						description: `Andrebbero montati insieme per avere la catapulta, o almeno credo.`
					},
				},
			},
			armory: {
				description: `Sei nell'armeria del castello. Fissati alle pareti sono alcuni archi, pugnali, scudi, lance, asce ed altre armi, tutte corrose e rese inutilizzabili dal tempo.`,
				shortDescription: `Sei nell'armeria.`,
				interactors: {
					daggers: {
						label: `i pugnali`,
						pattern: `pugnal(?:e|i)`,
					},
					spears: {
						label: `le lance`,
						pattern: `lanc(?:ia|e)`,
					},
					axes: {
						label: `le asce`,
						pattern: `asc(?:ia|e)`,
					},
					weapons: {
						description: `Come ti ho detto, sono tutte corrose e rese inutilizzabili dal tempo.`
					}
				},

			},

			// 46.SOTTOSCALA
			understairs: {
				description: `Sei in un piccolo sottoscala, completamente vuoto.`,
				shortDescription: `Sei nel sottoscala.`,
				interactors: {
					understairs: {
						label: `il sottoscala`,
						pattern: `sottoscala`
					}
				},
			},
			servantsHall: {
				description: `In questa stanza, dove alloggiavano i servitori del Re, non c'è proprio niente di particolare.`,
				shortDescription: `Sei nella sala dei servitori.`
			},
			columnsHall: {
				description:  `Sei in una lunga sala, ai lati della quale si innalzano due file di alte colonne, che sostengono il soffitto arcuato. Le colonne, benché‚ corrose dal tempo, portano ancora i segni di una paziente lavorazione per mano di abili artigiani. Al centro della sala, una piccola colonna in pietra poggia su un basso piedestallo.`,
				shortDescription: `Sei nella sala delle colonne.`,
				interactors: {
					column: {
						label: `la colonna`,
						pattern: `(?:piccol(?:a|o) )?(colonna|capitello)`,
						description: `Sul capitello è scolpita in lucenti lettere d'argento metà di una potente parola magica: 'ID'`
					},
					columns: {
						label: `le colonne`,
						pattern: `colonne`,
						description: `Le colonne ai lati della sala sono in stile alquanto austero: semplici cilindri di pietra rastremati verso l'estremità superiore.`
					},
					pedestal: {
						label: `il piedistallo`,
						pattern: `piedistallo`,
						description: `Il piedestallo che sorregge la piccola colonna è un parallelepipedo di pietra grezza, probabilmente granito.`
					}
				},

			},

			// 49.SALA DEGLI ARAZZI
			tapestriesRoom: {
				description: `Sei in una sala le cui pareti sono completamente tappezzate di arazzi di squisita fattura. La maggior parte sono raffigurazioni di scene di caccia, ma non mancano campi di battaglia e scene di vita agreste. I colori sono ben conservati, nonostante i secoli.`,
				shortDescription: `Sei nella sala degli arazzi.`,
				interactors: {
					tapestries: {
						label: `arazzi`,
						pattern: `arazz(?:o|i)`,
						onLook: [
							`Sono eseguiti con grande maestria, e meritano maggiore attenzione. Ti perdi nella contemplazione...`,
							`Poi, improvvisamente, ti ricordi che hai qualcosa di più importante da fare.`
						]
					}
				},
			},
			portraitsGallery: {
				description: [
					`Sei in una sala di forma allungata e senza alcun mobile. Sulle pareti si allineano i ritratti dei Re e dei dignitari che nel corso dei secoli hanno governato il castello ed i suoi feudi.`,
					`I ritratti paiono fissarti con occhio malevolo. Uno in particolare, quello di Malcolm IV, sembra seguire i tuoi movimenti con uno sguardo carico di odio mortale.`
				],
				shortDescription: `Sei nella galleria dei ritratti.`,
				interactors: {
					portrait: {
						label: `il ritratto`,
						pattern: `(ritratt(?:o|i)|quadr(?:o|i)|malcolm|tel(?:a|e))`,
						onLookQuestion: `La mano del pittore pare quasi aver colto la vita stessa dal volto dei soggetti, specialmente nel caso di Malcolm IV. Questo ritratto ha qualcosa di strano. Devo osservarlo più da vicino `
					}
				}
			},
			trophiesRoom: {
				description: `Sei in una corta sala letteralmente stipata di trofei di caccia e di guerra. Fissati alle pareti vedo animali d'ogni specie, armi, scudi, perfino un'intera armatura appartenuta probabilmente ad un Re nemico ucciso in battaglia dal Sovrano in persona.`,
				shortDescription: `Sei nella sala dei trofei.`,
				interactors: {
					armor: { 
						spadeText: `, appoggiata alla spada`,
						onLook: (spadeText) => `E' l'armatura di sir Crawdolf, il valente mago guerriero che per lunghi anni tenne in scacco Re Malcolm con la sua prodezza e le sue terribili arti. L'armatura conserva ancora un portamento altero e pare perfino fissarti${spadeText}.`
					},
					trophies: {
						label: `trofei`,
						pattern: `trofe(?:o|i)|animal(?:e|i)`,
					},
				},

			},
			kitchen: {
				description: `La stanza in cui ti trovi doveva essere la cucina del castello. Vedo infatti un grande focolare ed i resti di vasellame, pentolame e grosse tinozze.`,
				shortDescription: `Sei in cucina.`,
				interactors: {
					kitchenStuff: {
						label: `cucina`,
						pattern: `(cucina|tinozz(?:a|e)|vasellame|pentolame|resti)`,
						description: `Sono del tutto inutilizzabili.`					
					}
				},
			},
			alchemistCell: {
				description: [
					`Sei nella cella dell'Alchimista. Tutto intorno crogiuoli, pestelli, alambicchi e bizzarri recipienti in vetro di forma quanto mai contorta. Sugli scaffali sono molti pesanti in-folio di magia, alchimia, sortilegi. Al centro, un tavolino che poggia su tre gambe sagomate a forma di zampe di qualche mostruoso animale. Sul tavolino, un unico pesante volume rilegato in cuoio nero:`,
					``,
					`        "L'Apprendista Stregone"`
				],
				shortDescription: `Sei nella cella dell'Alchimista.`,
				interactors: {
					infolio: {
						label: `in-folio`,
						pattern : `pestell(?:o|i)|recipient(?:e|i)|crogiuol(?:o|i)|in-folio|alambicc(?:o|hi)`,
						description: `E' il tipico armamentario di un alchimista dell'epoca, mago, studioso e guaritore al tempo stesso. Gli alchimisti erano tenuti in alta considerazione e molto temuti e rispettati, a meno che non cadessero in disgrazia per qualche fatale errore commesso ai danni del Re.`,
						onTakeOrRead: `Una forza occulta pare metterti in guardia: frugare tra le proprietà personali di un alchimista può essere molto pericoloso.`
					},
					bookmark: {
						label: `il segnalibro`,
						pattern: `segnalibro`,
					},
					volume: {
						label: `volume`,
						pattern: `volume|librone|pagina`,
						description: `Ora che lo vedo più da vicino, mi accorgo che forse non è cuoio, ma pelle umana conciata.`,
						onOpen: `Devi usare lo strumento adatto.`,
						onRead: [
							`Riesco a leggere una sola parola:`,
							``,
							`  'BIGMEOW'`
						],
						onTake: `E' impossibile smuoverlo.`,
						onLiftUp: `Alzare il volume ?  Ma sei sordo ??`
					}
				},
			},
			boardRoom: {
				description: `Sei nella sala del consiglio, dove venivano prese le decisioni più gravi ed importanti. Tutto il mobilio consiste in una tavola rotonda circondata da otto sedie.`,
				shortDescription: `Sei nella sala del consiglio.`,
				interactors: {
					table: {
						description: [
							`Sul bordo del tavolo è incisa una saggia massima:`,
							`'Non tutte le spade feriscono di lama.'`
						]
					},
					chairs: {
						description: `Qui sedevano i nobili cavalieri del Re, per aiutare il Sovrano nelle difficili decisioni sulla condotta delle interminabili guerre.`
					}
				},

			},
			wardrobe: {
				description: `Sei nel guardaroba del Re. Tarme e tarli hanno ridotto gli imponenti armadi e le ricercate vesti a pochi brandelli e mucchi di segatura.`,
				shortDescription: `Sei nel guardaroba.`,
				interactors: {
					shreds: {
						label: `i brandelli`,
						pattern: `brandell(?:o|i)|mucchi(?:o)?`,
						description: `Sic transit gloria mundi.`,
					}
				},
			},
			musicHall: {
				description: [
					`Sei nella sala di musica, dove si svolgevano feste, trattenimenti e rappresentazioni che rallegravano la vita del castello.`,
					`Tutto è ormai ricoperto da ragnatele.`
				],
				shortDescription: `Sei nella sala di musica.`,
				interactors: {
					cobwebs: {
						label: `le ragnatele`,
						pattern: `ragnatel(?:a|e)`,
						description: `Anche le ragnatele sono ormai deserte. Nulla rimane della vita che faceva di questa stanza il luogo più allegro del castello.`
					}
				},

			},
			princessRoom: {
				description: `Sei nella stanza della Principessa. Il passare degli anni ha lasciato quasi intatti i delicati colori dei tendaggi ed il fragile baldacchino a cui manca soltanto il morbidissimo piumino che addolciva il lieve riposo della esile fanciulla.`,
				shortDescription: `Sei nella stanza della Principessa.`,
				interactors: {
					bed: {
						description: `E' decorato con tinte delicate dai toni sfumati.`
					},
					curtains: {
						label: `le tende`,
						pattern: `tend(?:a|e|aggi)`,
						description: `Sulle tende è ricamato con un sottile filo d'oro:\n'Saggio è colui che distrugge la propria ricchezza per prendere le vie del cielo'`
					}
				},

			},
			kingRoom: {
				description: `Sei nella camera da letto del Re. Un sontuoso baldacchino troneggia in mezzo alla stanza, occupandone la maggior parte. Dei ricchi arredi non è rimasto altro.`,
				shortDescription: `Sei nella camera del Re.`,
				interactors: {
					bed: {
						description: `E' dipinto a colori forti e sanguigni.`
					}
				},

			},
			mirrorsHall: {
				description: `Sei in un salone dalle pareti coperte di specchi di ogni forma e dimensione. Specchi curvi, specchi giganti, specchi concavi riflettono infinite volte la tua immagine dando l'impressione che facce maligne ti scrutino da ogni angolo della stanza. Era la stanza preferita dalla vanitosa Regina.`,
				shortDescription: `Sei nella sala degli specchi.`,
				bonk: `BONK!`,
				notADoor: `Non è la porta, è uno specchio!`,
				override: {
					commands: {
						getOut: `Con tutti questi specchi, non distinguo più le direzioni. Vado sempre a finire dalla parte sbagliata.`
					}
				},
				interactors: {
					mirrors: {
						label: `gli specchi`,
						pattern: `specchi(?:o)?`,
						description: `Vedo una brutta faccia.`,
						onBreak: `Non ci tengo proprio ad andare incontro a sette anni di guai.`
					}
				},
			},
			pass: {
				description: `Sei in uno scuro, stretto, tortuoso passaggio che sei costretto a percorrere carponi avanzando in direzione del barlume di luce che riesce a penetrare dall'altra estremità.`,
				shortDescription: `Sei nel tortuoso passaggio.`,
				interactors: {
					pass: {
						description: ` Che buio! Non si vede un accidente.`
					}
				},
			},
			rock: {
				description: [
					`Sei solo e abbandonato su un nero scoglio che emerge dalle acque gelide.`,
					`Mi correggo, non sei solo: ti fa compagnia il mostro di Loch Ness (Nessie per gli amici).`
				],
				shortDescription: `Sei sullo scoglio.`,
				interactors: {
					nessie: {
						description: `Sta guardando te.`,
						onGreet: [
							`Anche lui è`, 
							`MOLTO`,
							`contento di vederti.`
						],
						onKill: `Fai presto a dirlo!`
					},
					rock: {
						label: `lo scoglio`,
						pattern: `scoglio`,
						description: [
							`E' costituito da roccia persilicica a struttura granulare oligocristallina, per lo più a grana medio-fine.`,
							`L'aggregato cristallino fondamentale è composto in prevalenza da quarzo e feldspati, con laminette micacee in quantità subordinata.`,
							`Osservando in dettaglio i feldspati, si possono notare ortoclasio (o microclino), albite ed altri plagioclasi sodici. Le miche sono rappresentate da biotite e muscovite, frammiste ad altri componenti femici, anfiboli in particolare.`,
							`Nella struttura sono presenti anche piccoli cristalli di apatite e zircone, come pure granuli di magnetite e pirite.`
						]
					},
					lake: {
						label: `il lago`,
						pattern: `lago|acqua`
					}
				},
				override: {
					commands: {
						swim: [
							`Sei un buon nuotatore.`,
							`Ma`,
							`LUI`,
							`di più.`
						]
					}
				}
			}
		},
		objects: {
			parachute: {
				label: `un paracadute`,
				pattern: `paracadute`,
				onWear: `L'hai già addosso.`,
				onLook: `Non hai mai visto un paracadute? Ma da dove vieni?`,
				onOpen: {
					notHere: `Qui dentro? Ma non farmi ridere!`,
					dontHaveIt:	`Non hai addosso nessun paracadute.`
				}
			},
			bone: {
				dlabel: `un osso`,
				label: [
					`un osso`,
					`un osso tranciato`
				],
				pattern: `osso|femore|tibia`,
				onLook: `Potrebbe essere un femore, o forse una tibia. Non so, non me ne intendo di anatomia.`,
				onLookFor: { 
					dontHaveIt: `Il pavimento ne è pieno.`,
					inYourHand: `Ne hai uno in mano.`
				}
			},
			bludgeon: {
				label: `una mazza ferrata`,
				pattern: `mazza(?: ferrata)?`,
				description: `E' un'arma da battaglia. Non può sfondare un muro, ma può senz'altro venirti utile.`,
			},
			cat: {
				dlabel: `un gatto`,
				label: [
					`un gatto accovacciato per terra`,
					`un gatto`
				],
				pattern: `(gatto|micio)`,
				description: `E' un caro, piccolo, simpatico micino.`,
				onTake: {
					alreadyIn: `Ce l'hai già in braccio.`,
					gotIt: `Il gatto è affamato e si lascia prendere facilmente.`
				},
				onPet: [
					`ronf... ronf... ronf... ronf... `,
					`Il gatto fa le fusa.`
				],
				onTalkOrGreet: `Il gatto ti fissa con aria interessata, le oblique pupille ridotte a due strette fessure, poi decide (probabilmente) che la tua intelligenza è solo apparente, e volge altrove lo sguardo.`,
				onKill: [
					`Con un ultimo miagolio lamentoso, il povero micino muore fissandoti con uno sguardo carico di rimprovero.`,
					`Il cadavere diventa sempre più indistinto, fino a scomparire del tutto.`
				],
				onFeed: {
					nothingSuitable: `Non hai nulla di adatto.`,
					lep: `lep...`,
					finished: [
						`lep...miao!`,
						``,
						`Il gatto ha molto gradito il lattemiele.`
					]					
				},

			},
			sword: {
				dlabel: `una spada`,
				label: [
					`una spada`,
					`una spada incantata`
				],
				pattern: `spada`,
				posizione: `salaTrofei`,
				description: `E' di pregevole fattura.`,
				onLook: `Sulla lama è scritto un sortilegio.`,
				spell: {
					dontKnow: `Non conosci alcun sortilegio.`, 
					dontRemember: `Non me lo ricordo, era troppo complicato.`,
					question: `Devo pronunciarlo ad alta voce `,
					fail: [
						`Hai la gola secca dalla paura...`,
						`Non riesci a parlare...`,
						`Il fantasma ne approfitta per assalirti.`
					],
					success: `Con un lungo lamento disperato, il fantasma torna nel Nulla dal quale era venuto.`
				}
			},
			spell: {
				label: `il sortilegio`,
				pattern: `sortilegio|incantesimo`,
				onLook: `Penso che la lettura degli strani simboli e delle strane formule potrebbe evocare forze da lungo tempo sopite.`
			},
			milkAndHoneyCup: {
				dlabel: `una coppa`,
				label: [
					`una coppa colma di lattemiele`,
					`una coppa vuota`
				],
				pattern: `coppa`,
				description: {
					full: `E' piena di lattemiele.`, 
					empty: `E' completamente vuota.`
				},
				EMPTY: `La coppa è vuota.`
			},
			milkAndHoney: {
				label: `il lattemiele`,
				pattern: `lattemiele`,
				description: `A guardarlo ed annusarlo sembra buono.`,
				onDrink: {
					success: `Ottimo, veramente ottimo!`,
					fail: `Prendi in mano la coppa, prima.`
				},
				onOffer: {
					toGhost: `Non gradisce.`,
					toOgre: `Non ha fame (per fortuna!).`,
					toWho: `A chi?`
				}
			},
			lute: {
				label: `un liuto`,
				pattern: `liuto`,
				description: `Questo strumento rallegrava le feste danzanti che si svolgevano in occasione dei raccolti, delle vittorie, delle cerimonie, e di tutti i pretesti che potessero giustificare una festa danzante.`,
				onPlay: `Le corde sono rotte.`
			},
			harp: {
				label: `un'arpa`,
				pattern: `arpa`,
				description: `Questo strumento accompagnava le tristi e le liete storie che il bardo del castello cantilenava nelle lunghe sere d'inverno, mentre tutti erano raccolti intorno, rapiti dall'incanto delle antiche leggende.`,
				onPlay: `Le corde sono rotte.`
			},
			bagpipe: {
				label: `una cornamusa`,
				pattern: `cornamusa`,
				description: `Questo strumento accompagnava le spedizioni militari condotte personalmente dal Re, scandendo con il suo alto lamento ritmato il passo pesante dei soldati in marcia.`,
				onPlay:{
					fail: [
						`Meriteresti di far parte della Banda Reale di Edimburgo!`,
						`(...come tamburo)`
					],
					success: `Il volume si apre alla pagina indicata da un segnalibro decorato.`
				}
			},
			sheet: {
				label: `un foglio bruciacchiato`,
				pattern: `foglio|carta`,
				description: `Benché‚ sia parzialmente consumato dalle fiamme, si riesce ancora a leggere.`,
				onRead:{
					dontHaveIt: `Non possiedi un foglio.`,
					success: `C'è scritto soltanto: 'IOTA'`
				}
			},
			cushion: {
				label: `un cuscino`,
				pattern: `cuscino`,
				description: `Non pensare a dormire: pensa a come uscire di qui.`,
				onLiftUp:`C'è sotto un astuccio in legno.`
			},
			case: {
				label: `un astuccio`,
				pattern: `astuccio`,
				description: `Il legno dell'astuccio è intarsiato con grande cura.`,
				onTake: `E' avvitato al sedile del trono.`,
				onSkrewOff: `Non possiedi lo strumento adatto.`,
				onOpen: `L'astuccio contiene una vecchia pergamena.`
			},
			scroll: {
				label: `una pergamena`,
				pattern: `pergamena`,
				description: `E' scolorita dal tempo, ma si legge ancora bene.`,
				onRead: {
					dontHaveIt: `Non possiedi una pergamena.`,
					fail: `Mi spiace, è scritta in una lingua che non conosco.`
				},
				onTranslate: {
					fail: `Come faccio senza dizionario ?`,
					success: [
						`Dice:`,
						`'Solo col senno puoi trovare l'uscita del labirinto'`
					]
				}								
			},
			ogre: {
				label: `un feroce orco dalle zanne aguzze`,
				pattern: `(?:feroce )?orco`,
				onLook: [
					`A ben guardare, non sembra poi così feroce...`,
					`...ma molto, molto di più!`
				],
				onKill: `Fai presto a dirlo!`,
				onFeed: `Non ha fame (per fortuna!).`,
				onTalkOrGreet: `- GRUNT -`
			},
			dwarf: {
				dlabel: `un nano`,
				label: [
					`un piccolo nano con un grosso diamante`,
					`un piccolo nano molto simpatico`
				],
				pattern: `(?:piccolo )?nano`,
				description: `E' piuttosto piccolo.`,
				onGreet: {
					withoutDiamond: `- Buonasera a Voi! - risponde il nano.`,
					withDiamond: `Il nano è così contento di incontrare finalmente una persona cortese che ti regala il diamante.`
				},
				onFeed: `Ha appena cenato.`,
				onKill: [
					`Il nano, velocissimo, estrae un affilato pugnale e ti colpisce con forza, esclamando:`,
					`- Possibile che di qui passino soltanto ladri ed assassini ? -`
				]
			},
			diamond: {
				label: `un diamante`,
				pattern: `diamante`,
				description: `Più osservi il meraviglioso gioiello, più cresce dentro di te una sfrenata bramosia di possederlo.`,
				MINE: `Il nano dice: - E' mio! -`,
				onBreak: {
					needSomethingHard: `Ci vuole qualcosa di molto duro.`,
					success: `Al primo colpo di mazza, il diamante va in mille pezzi.`
				},
				onLook: `E' magnifico: la luce riflessa e rifratta da mille perfette sfaccettature costruisce infiniti giochi di colore. Ne sei affascinato e rimarresti a guardarlo per ore ed ore. Penso che abbia un valore inestimabile, e che ti convenga trattarlo con la massima cura.`
			},
			key: {
				label: `una piccola chiave di cristallo`,
				pattern: `(piccola )?chiave`,
				description: `E' molto strana, troppo piccola per essere la chiave di una porta o di un portone. E sembra anche fragile. A cosa mai potrà servire?`
			},
			horn: {
				label: `un corno`,
				pattern: `corno`,
				description: `E' decorato con scene di caccia che si avvolgono a spirale partendo dall'imboccatura. Cavalieri al galoppo inseguono la preda, mentre grandi uccelli volteggiano sopra le loro teste.`,
				onPlay: `Il suono cavernoso riecheggia tra le mura del castello.`
			}
		},
		sequences: {
			title: [
				`Adattamento Javascript di`,
				`"AVVENTURA NEL CASTELLO"`,
				`realizzato da Federico Volpini`,
				`(volpini.federico79@gmail.com)`,
				`Dalla versione originale 4.1 per MS-DOS`,
				`di Enrico Colombini e Chiara Tovena`,
				`(C) Dinosoft 1982,1984,1987,1996`,
				`Riprodotta col consenso degli autori`,
				`Licenza di distribuzione: CC BY-NC-ND 4.0`,
				`(Attribution-NonCommercial-`,
				` NoDerivatives 4.0 International)`,
				`-----`
			],
			prologue: [
				` PROLOGO: `,
				`Si narra nelle antiche leggende scozzesi delle eroiche gesta e delle oscure stregonerie degli antichi Re.`,
				`Si narra anche dei favolosi tesori, mai ritrovati.`,
				`Il tuo ardente spirito avventuroso, mosso da sete di gloria e bramosia di ricchezze, non esita un istante:`,
				`noleggi un piccolo aereo da turismo, e parti per la tua Grande Avventura!`
			],
			intro: [
				` * AVVENTURA NEL CASTELLO! * `,
				`Stai pilotando il tuo monoposto sopra la desolata regione scozzese delle Highlands. Hai appena sorvolato il lago di Loch Ness...`,
				`Improvvisamente il motore perde colpi.`,
				`I comandi non rispondono!`
			],
			parachute: [
				`Toh guarda. C'è un paracadute. Non l'avevo proprio visto.`,
				`Ti prometto che d'ora in poi starò più attento e ti riferirò scrupolosamente tutti gli oggetti presenti.`,
				`Comunque, adesso l'ho indossato.`
			],
			fly: {
				string: [
					`Sicuro di non aver dimenticato`,
					`qualcosaaaaa`,
					`             aaa`,
					`                 aa`,
					`                    a`,
					`                     a`,
					`                      a`,
					`                      a`,
					`                      a`,
					`                      :`,
					`                      :`,
					`                      :`,
					`                  \\   :   /`,
					`                 -  SPLAT! -`
				],
				reversed: ` SPLAT!`
			},
			jumpFromPlane:[
				`Appena in tempo!`,
				`l'aereo si schianta al suolo mentre il paracadute si apre.`,
				`Scendi dolcemente nella luce del giorno morente. Sotto di te una brughiera desolata. Il vento ti spinge verso un castello diroccato. Prendi terra nella grande piazza d'armi del castello.`,
				`Mentre ripieghi il paracadute ti guardi attorno:`
			],
			arm: {
				question: `Devo infilare il braccio nel foro `,
				answer: [
					`Una lama scende di scatto tagliandoti di netto il braccio.`,
					`Intanto che muori dissanguato lascia che ti dica che ti stai comportando in maniera imprudente.`
				]
			},
			castleDungeon: {
				success: [
					`Una lama scende di scatto tranciando di netto l'osso.`,
					` Per fortuna non era il tuo braccio!`,
					`Una fessura si allarga lentamente.....`
				],
				fail: `Non ci passa.`
			}, 
			portrait: [
				`Non appena fissi gli occhi sul ritratto di Malcolm IV, ti senti inesorabilmente attratto dal suo sguardo magnetico. Non riesci in alcun modo ad interrompere la linea che unisce i tuoi occhi a quelli del ritratto, e ti porti tuo malgrado sempre più vicino alla tela. Una mano invisibile pare serrarti la gola in una morsa d'acciaio, mentre un sorriso beffardo si disegna sul volto del Re, raffigurato nel sontuoso abbigliamento da parata.`,
				`Quando senti ormai di essere agli estremi, la mano proveniente dagli abissi del tempo rilascia all'improvviso la presa mortale.`,
				`Crolli sul pavimento e perdi i sensi, mentre tutto ruota intorno a te. Non puoi vederla, ma sul volto di Malcolm IV è apparsa un'espressione soddisfatta.`
			],
			eat: [
				`Hai prima un gran mal di pancia... `,
				`poi delle bellissime allucinazioni...`,
				`credi di aver finalmente capito tutte le verità universali...`,
				`ma alla fine ti resta solo un pesante cerchio alla testa.`
			],
			eagle: [
				`Il suono dell'antico corno corre per la brughiera, riecheggiando fino alle lontane montagne.`,
				`Un punto nero si stacca dalle montagne e si ingrandisce mentre si avvicina.`,
				`Velocemente giunge alla torre: è una grande Aquila Reale che si lancia verso di te con gli artigli protesi.`,
				`Non hai nessuna possibilità:`,
				`l'Aquila ti afferra sollevandoti rapidamente a grande altezza.`,
				`L'Aquila vola a lungo mentre il paesaggio scorre sotto di te... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ...il lago di Loch Ness appare lontano... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ...`,
				`Improvvisamente l'Aquila ti lascia andare.`
			],
			rock:  [
				`Scendi dolcemente nella luce del giorno morente. Sotto di te le scure acque del lago di Loch Ness. Il vento ti spinge verso il centro del lago. Prendi terra fortunosamente su un piccolo scoglio roccioso.`,
				`Mentre ripieghi il paracadute ti guardi attorno:`
			], 
			again: {
				string: [
					`Possibile che tu sia riuscito a dimenticartelo anche`,
					`stavoltaaaaa`,
					`             aaa`,
					`                 aa`,
					`                    a`,
					`                     a`,
					`                      a`,
					`                      a`,
					`                      a`,
					`                      :`,
					`                      :`,
					`                      :`,
					`                  \\   :   /`,
					`                 -  SPLAT! -`,
					`Sei incorreggibile!`
				],
				reversed: ` SPLAT!`
			},
			final: [
				`Il suono dell'antico corno corre per la brughiera, riecheggiando fino alle lontane montagne.`,
				`Un punto nero si stacca dalle montagne e si ingrandisce mentre si avvicina.`,
				`Velocemente giunge allo scoglio: è un elicottero del Reale Servizio Archeologico che ti lancia una scaletta di salvataggio. Sali la scaletta mentre le mascelle del mostro si chiudono a vuoto pochi centimetri sotto di te.`,
				`Mentre l'elicottero si dirige verso Edimburgo, racconti la tua avventura, mostrando il corno, unico oggetto che sei riuscito a conservare. Vedendolo, il prof. Mac Anthrop (direttore del museo di Edimburgo) esclama:`,
				`- Ma questo è il corno di Malcolm IV, che si credeva perduto per sempre!`,
				`- Varrà almeno un milione di sterline! - dichiara il suo assistente.`,
				`L'elicottero atterra ad Edimburgo.`,
				`Vieni immediatamente arrestato per:`,
				`- Ingombro di suolo pubblico con rottami di aereo.`,
				`- Violazione di Domicilio Reale.`,
				`- Furto di bevande (con deglutizione).`,
				`- Maltrattamenti a gatto di nobile stirpe.`,
				`- Trafugamento di reperti archeologici.`,
				`- Molestie a fantasma statale.`,
				`- Porto abusivo di arma da taglio.`,
				`- Ubriachezza molesta.`,
				`- Esercizio di stregoneria senza licenza.`,
				`- Disturbo a mostro di specie protetta.`,
				`Il corno ti viene naturalmente sequestrato per essere affidato al museo.`,
				`Il direttore, ancora emozionato per l'importante ritrovamento, interviene in tuo favore. Sei liberato e ti viene persino assegnato un premio dagli Scozzesi riconoscenti (cosa alquanto insolita):`, 
				`Un biglietto `,
				`GRATUITO`,
				` per una gita turistica sul lago di Loch Ness! -`,
				`Comunque consolati: hai finalmente raggiunto i `,
				`1000`,
				` punti e il diritto di fregiarti dell'ambito titolo di:`,
				` LUOGOTENENTE DEL DIAVOLO!!!`,
				`Arrivederci alla prossima avventura!`,
			]
		},
		timedEvents: {
			plane: [
				`La carlinga è invasa dal fumo.`,
				`Presto, fai qualcosa!`,
				`Hai i secondi contati!!!`,
				`@@@@@@@@@@@@@@@ CRASH! @@@@@@@@@@@@@@@`
			],
			whiskey: `- HIC! -`,
			nessie: [
				`Vedo il mostro di Loch Ness che si avvicina.`,
				`Vedo il mostro di Loch Ness che si avvicina troppo.`,
				`>CHOMP!<`,
				`  (gustosi questi avventurieri)`
			]
		}
	}
}
