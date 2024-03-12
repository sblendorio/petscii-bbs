class Thesaurus{
	constructor(){
		this.defaultMessages = {
			FATTO: 					i18n.Thesaurus.defaultMessages.done,
			PREFERISCO_DI_NO: 		i18n.Thesaurus.defaultMessages.preferNot,
			NON_TROVATO:	 		i18n.Thesaurus.defaultMessages.notFound,
			NON_HO_CAPITO:			i18n.Thesaurus.defaultMessages.didNotUnderstand,
			NULLA_DI_PARTICOLARE:	i18n.Thesaurus.defaultMessages.dontNoticeAnythingInParticular,
			QUI_NON_NE_VEDO: 		i18n.Thesaurus.defaultMessages.notSeenHere, 
			NON_NE_POSSIEDI: 		i18n.Thesaurus.defaultMessages.dontHaveAny, 
			NON_SUCCEDE_NIENTE: 	i18n.Thesaurus.defaultMessages.nothingHappens,
			SII_PIU_SPECIFICO: 		i18n.Thesaurus.defaultMessages.beMoreSpecific,
			NON_E_POSSIBILE: 		i18n.Thesaurus.defaultMessages.notPossible
		}

		this.loadCommands();
		this.loadVerbs();
		
	}

	loadCommands(){
		this.commands = {
			nord: {
				movimento: true,
				pattern: i18n.Thesaurus.commands.north.pattern,
				defaultMessage: i18n.Thesaurus.commands.north.defaultMessage,
				direzione: "n"
			},
			sud: {
				movimento: true,
				pattern: i18n.Thesaurus.commands.south.pattern,
				defaultMessage: i18n.Thesaurus.commands.south.defaultMessage,
				direzione: "s"
			},
			est: {
				movimento: true,
				pattern: i18n.Thesaurus.commands.east.pattern,
				defaultMessage: i18n.Thesaurus.commands.east.defaultMessage,
				direzione: "e"
			},
			ovest: {
				movimento: true,
				pattern: i18n.Thesaurus.commands.west.pattern,
				defaultMessage: i18n.Thesaurus.commands.west.defaultMessage,
				direzione: "o"
			},
			alto: {
				movimento: true,
				pattern: i18n.Thesaurus.commands.up.pattern,
				defaultMessage: i18n.Thesaurus.commands.up.defaultMessage,
				direzione: "a"
			},
			basso: {
				movimento: true,
				pattern: i18n.Thesaurus.commands.down.pattern,
				defaultMessage: i18n.Thesaurus.commands.down.defaultMessage,
				direzione: "b"
			}
		}
	}

	loadVerbs(){
		this.verbs = {
			apri: {
				pattern: i18n.Thesaurus.verbs.open.pattern,
				defaultMessage: i18n.Thesaurus.verbs.open.defaultMessage
			},
			chiudi: {
				pattern: i18n.Thesaurus.verbs.close.pattern,
				defaultMessage: i18n.Thesaurus.verbs.close.defaultMessage

			},
			tira: {
				pattern: i18n.Thesaurus.verbs.pull.pattern,
				defaultMessage: this.defaultMessages.PREFERISCO_DI_NO
			},
			premi: {
				pattern: i18n.Thesaurus.verbs.press.pattern,
				defaultMessage: this.defaultMessages.PREFERISCO_DI_NO
			},
			spingi: {
				pattern: i18n.Thesaurus.verbs.push.pattern,
				defaultMessage: i18n.Thesaurus.verbs.push.defaultMessage
			},
			prendi: {
				pattern: i18n.Thesaurus.verbs.take.pattern,
				defaultMessage: this.defaultMessages.PREFERISCO_DI_NO
			},		
			lascia: {
				inventario: true,
				pattern: i18n.Thesaurus.verbs.drop.pattern,
				defaultMessage: this.defaultMessages.PREFERISCO_DI_NO
			},
			dai: {
				inventario: true,
				pattern: i18n.Thesaurus.verbs.give.pattern,
				complex: true,
				defaultMessage: this.defaultMessages.PREFERISCO_DI_NO
			},
			cerca:{
				pattern: i18n.Thesaurus.verbs.lookFor.pattern,
				defaultMessage: this.defaultMessages.NON_TROVATO
			},
			guarda: {
				pattern: i18n.Thesaurus.verbs.look.pattern,
				defaultMessage: this.defaultMessages.NULLA_DI_PARTICOLARE
			},
			usaCon: {
				pattern: i18n.Thesaurus.verbs.useWith.pattern,
				complex: true,
				defaultMessage: i18n.Thesaurus.verbs.useWith.defaultMessage	
			},
			usa:{
				pattern: i18n.Thesaurus.verbs.use.pattern,
				defaultMessage: this.defaultMessages.SII_PIU_SPECIFICO
			},
			sali: {
				pattern: i18n.Thesaurus.verbs.goUp.pattern,
				defaultMessage: this.defaultMessages.NON_HO_CAPITO
			}, 
			scendi: {
				pattern: i18n.Thesaurus.verbs.goDown.pattern,
				defaultMessage: this.defaultMessages.NON_HO_CAPITO
			}
		};
	}
}
