class Avventura extends AvventuraNelCastelloJSEngine{
	constructor(){
		super();
		
		// Interattori comuni
		this.commonInteractors = {
			gradini: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.steps.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.steps.pattern,
				on: {
					sali: async () => {
						return await this._vai("a",this.Thesaurus.commands.alto.defaultMessage)
					},
					scendi: async () => {
						return await this._vai("b",this.Thesaurus.commands.basso.defaultMessage)
					}
				}
			},
			scala: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.stairs.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.stairs.pattern,
				description: i18n.AvventuraNelCastelloJS.commonInteractors.stairs.description,
				on: {
					sali: async () => {
						return await this._vai("a",this.Thesaurus.commands.alto.defaultMessage)
					},
					scendi: async () => {
						return await this._vai("b",this.Thesaurus.commands.basso.defaultMessage)
					}
				}
			},
			pareti: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.walls.label,
				pattern: this.commonPatterns.muro,
				on: {
					rompi: i18n.AvventuraNelCastelloJS.commonInteractors.walls.onBreak,
					"premi|spingi": this.Thesaurus.defaultMessages.SII_SERIO
				}
			},
			armatura:{
				label: i18n.AvventuraNelCastelloJS.commonInteractors.armor.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.armor.pattern,
				description: i18n.AvventuraNelCastelloJS.commonInteractors.armor.description,
				peso: 99,
				on: {
					"prendi|indossa": i18n.AvventuraNelCastelloJS.commonInteractors.armor.onTakeOrWear
				}
				
			},
			corridoio: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.hallway.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.hallway.pattern
			},

			scheletri:{
				label: i18n.AvventuraNelCastelloJS.commonInteractors.skeletons.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.skeletons.pattern,
				description: i18n.AvventuraNelCastelloJS.commonInteractors.skeletons.description,
				peso: 99
			},
				
			labirinto: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.labyrinth.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.labyrinth.pattern,
				description: i18n.AvventuraNelCastelloJS.commonInteractors.labyrinth.description
			},

			porta: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.door.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.door.pattern,
				on: {
					apri: async () => this.stanzaCorrente.interactors.porta.description	
				}
			},

			tavolo: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.table.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.table.pattern,
				peso: 99
			},

			archi: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.bows.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.bows.pattern,
				peso: 99
			},

			scudi: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.shields.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.shields.pattern,
				peso: 99
			},

			camino: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.fireplace.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.fireplace.pattern
			},

			poltrone: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.armchairs.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.armchairs.pattern,
				peso: 99
			},
			
			sedie: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.chairs.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.chairs.pattern,
				peso: 99
			},

			mostro: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.monster.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.monster.pattern
			},

			scaffali: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.shelves.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.shelves.pattern,
				peso: 99
			},

			letto: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.bed.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.bed.pattern
			},

			armi: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.weapons.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.weapons.pattern,
				peso: 99
			},

			nebbia: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.fog.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.fog.pattern,
				description: i18n.AvventuraNelCastelloJS.commonInteractors.fog.description
			},

			corvi: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.crows.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.crows.pattern,
				description: i18n.AvventuraNelCastelloJS.commonInteractors.crows.description,
				on: {
					ascolta: i18n.AvventuraNelCastelloJS.commonInteractors.crows.onListen
				}
			},

			spalti: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.ramparts.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.ramparts.pattern,
				description: i18n.AvventuraNelCastelloJS.commonInteractors.ramparts.description
			},

			passaggio: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.pass.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.pass.pattern
			},

			cunicolo: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.tunnel.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.tunnel.pattern
			},

			roccia: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.rock.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.rock.pattern
			},

			torre: {
				label: i18n.AvventuraNelCastelloJS.commonInteractors.tower.label,
				pattern: i18n.AvventuraNelCastelloJS.commonInteractors.tower.pattern
			}

		}

		this.stanzeComuni = {
			scalaChiocciola: {
				description: i18n.AvventuraNelCastelloJS.commonRooms.spiralStaircase.description,
				shortDescription: i18n.AvventuraNelCastelloJS.commonRooms.spiralStaircase.description,
				interactors: {
					gradini: this.commonInteractors.gradini,
					scala: this.commonInteractors.scala
				}
			},
			spaltiMura: {
				description: i18n.AvventuraNelCastelloJS.commonRooms.ramparts.description.join("\n"),
				shortDescription: i18n.AvventuraNelCastelloJS.commonRooms.ramparts.shortDescription,
				interactors: {
					gradini: this.commonInteractors.gradini,
					scala: this.commonInteractors.scala,
					spalti: this.commonInteractors.spalti,
					nebbia: this.commonInteractors.nebbia,
					corvi: this.commonInteractors.corvi
				},
				override: {
					verbs: {
						salta: async () => {
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.commonRooms.ramparts.onJump[0]);
							await this.CRT.sleep(2000);
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.commonRooms.ramparts.onJump[1]);
							this.die();
							return false;
						}
					}
					
				}
			},

			labirinto: {
				description: i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.description,
				shortDescription: i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.description,
				interactors: {
					labirinto: this.commonInteractors.labirinto
				},
				override: {
					verbs: {
						lascia: async (items) => {
							this._rimuoviDaInventario(items[0], "entrataLabirinto");
							return i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.onDrop;
						},
					}, 
					commands: {
						esci: async() => {
							return i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.onGetOut;
						},
						pensa: async() => {
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.onThink[0]);
							this.altriDati.pensa++;
							for (let i=0; i<=30*this.altriDati.pensa; i++){
								await this.CRT.printTyping( i == 0 ? i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.onThink[1] : i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.onThink[2],{printDelay: 75, cr: false});
							}

							await this.CRT.printTyping( i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.onThink[3], {nlBefore:2} );
							let answer = await this.yesNoQuestion("\n"+i18n.AvventuraNelCastelloJS.commonRooms.labyrinth.onThinkQuestion);
							if(answer){
								this.CRT.println("");
								this.stanzaCorrente.override.commands.pensa();
								return false;
							}
							this.gameLoop(false);
							return false;
						},
						usaSenno: async () => this.stanzaCorrente.override.commands.pensa()
					}
				}
			}
		}

		// DATI AVVENTURA
		this.datiAvventura = {
			// stanza iniziale
			stanzaIniziale: "aereo",
			
			/* STANZE */
			stanze: {

				// 01.AEREO
				aereo: {
					description: i18n.AvventuraNelCastelloJS.rooms.plane.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.plane.description,
					directions: {
						b: async () => i18n.AvventuraNelCastelloJS.rooms.plane.directions.down
					},
					override: {
						commands: {
							aiuto: async () => {
								let i=0;
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.commands.help[i++]);
								await this.CRT.sleep(1000);
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.commands.help[i++]);
								await this.CRT.sleep(1500);
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.commands.help[i++],{nlAfter: 1});
								await this.CRT.sleep(500);
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.commands.help[i++], {printDelay: 100});
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.commands.help[i++], {printDelay: 100});
								await this.CRT.sleep(500);
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.commands.help[i++], {printDelay: 150});
								//return true;
							}
						},
						verbs: {
							salta: async (targets) => {
								if(targets !== undefined) {
									if(targets[0] == this.datiAvventura.objects.paracadute && this.inventario.paracadute == undefined)
										return this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.verbs.jump);
								}
								if(this.inventario.paracadute == undefined){
									this.runSequence("volo");
								} else {
									this.aggiungiPunti("saltoAereo");
									this.runSequence("saltoAereo");
								}
								return false;
							},
							guarda: async (targets) => {
								if(targets !== undefined){
									if(targets[0] == this.datiAvventura.objects.paracadute && this.inventario.paracadute)
									return this.inventario.paracadute.on.guarda
								}
								return i18n.AvventuraNelCastelloJS.rooms.plane.verbs.look
							},
							atterra: async () => i18n.AvventuraNelCastelloJS.rooms.plane.verbs.land
						}
					},
					interactors: {
						cloche: {
							label: i18n.AvventuraNelCastelloJS.rooms.plane.interactors.cloche.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.plane.interactors.cloche.pattern,
							on: {
								"tira|spingi": async () => {
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.interactors.cloche.onPullOrPush[0]);
									await this.CRT.sleep(1500);
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.plane.interactors.cloche.onPullOrPush[1]);
								}
							}
						
						},
						motore: {
							label: i18n.AvventuraNelCastelloJS.rooms.plane.interactors.engine.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.plane.interactors.engine.pattern,
							on: {
								"aggiusta": i18n.AvventuraNelCastelloJS.rooms.plane.interactors.engine.onRepair
							}
						
						},
						aereo: {
							...i18n.AvventuraNelCastelloJS.rooms.plane.interactors.plane
						}
					},
					onEnter: async () => this.runSequence("intro"),
					onExit: async () => this.stopTimedEvent("aereo")
				},

				// 02.PIAZZA D'ARMI
				piazzaArmi: {
					description: i18n.AvventuraNelCastelloJS.rooms.paradeGround.description.join("\n"),
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.paradeGround.shortDescription.join("\n"),
					override: {
						verbs:{
							entra: {
								...this.Thesaurus.verbs.entra, 
								...{
									callback: async (targets) => {

										if(targets === undefined || targets[0].key == "portone")
											return await this._vai("n");

										return null;

									}
								}	
							}
						}
					},
					directions: {
						n: async () => {
							if(this.stanzaCorrente.interactors.portone.status == 1){
								this.aggiungiPunti("entrato");
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.paradeGround.directions.north.success, {nlAfter:1});
								this.entra('atrioCastello');
								return false;
							} 
							return i18n.AvventuraNelCastelloJS.rooms.paradeGround.directions.north.fail;
						},
						s: async () => i18n.AvventuraNelCastelloJS.rooms.paradeGround.directions.south,
						a: async () => i18n.AvventuraNelCastelloJS.rooms.paradeGround.directions.up
					},
					interactors: {
						ponteLevatoio: {
							label: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.drawbridge.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.drawbridge.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.drawbridge.description,
							visibile: true,
							peso: -1,
							on: {
								abbassa: async () => {
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.drawbridge.onLower);
									this.die();
									return false;
								}
							},
						
						},
						portone: {
							dlabel: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.doorway.dlabel, 
							label: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.doorway.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.doorway.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.doorway.description,
							status: 0,
							visibile: true,
							peso: -1,
							on: {
								"apri|spingi": async () => {
									if(this.stanzaCorrente.interactors.portone.status == 0){
										this.stanzaCorrente.interactors.portone.status = 1;
										this.aggiungiPunti("apertoPortone");
										return this.Thesaurus.defaultMessages.FATTO;
									} 
									return this.Thesaurus.defaultMessages.GIA_APERTO;
								},
								chiudi: async () => {
									if(this.stanzaCorrente.interactors.portone.status == 1){
										this.stanzaCorrente.interactors.portone.status = 0;
										return this.Thesaurus.defaultMessages.FATTO;
									}
									return i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.doorway.onClose;
								}
							}
					
						},
						lastrone: {
							label: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.stoneSlab.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.stoneSlab.pattern,
							on: {
								guarda: async () => {
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.stoneSlab.onLook[0], {printDelay: 54});
									await this.CRT.sleep(2000);
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.stoneSlab.onLook[1], {printDelay: 54});
									this.die();
									return false;
								}
							}
							

						},
						mura: { 
							...this.commonInteractors.spalti,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.walls.description,
							}
						},
						spianata: {
							...i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.esplanade
						},
						castello: {
							...i18n.AvventuraNelCastelloJS.rooms.paradeGround.interactors.castle
						}					
					},
								
				},

				// 03.ATRIO DEL CASTELLO
				atrioCastello:{
					description: i18n.AvventuraNelCastelloJS.rooms.atrium.description.join("\n"),
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.atrium.shortDescription,
					directions: {
						n: 'corridoio',
						s: async () => i18n.AvventuraNelCastelloJS.rooms.atrium.directions.south,
						e: 'salaGuardie',
						o: 'salaServitori',
						a: 'cimaScale'
					},
					interactors: {
						gradini: this.commonInteractors.gradini,
						scala: this.commonInteractors.scala,
						blasone: {
							label: i18n.AvventuraNelCastelloJS.rooms.atrium.interactors.blazon.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.atrium.interactors.blazon.pattern,
							visibile: true,
							peso: -1,
							on: {
								guarda: async () => {
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.atrium.interactors.blazon.onLook[0]+"\n\n\t"+i18n.AvventuraNelCastelloJS.rooms.atrium.interactors.blazon.onLook[1]);
									await this.CRT.sleep(1500);
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.atrium.interactors.blazon.onLook[2],{nlAfter:1, nlBefore: 1});
									await this.CRT.sleep(1000);
									this.aggiungiPunti("cadutoInSegreta");
									this.entra('segretaCastello');	
									return false;
								}
							}

						},
						portone: {
							label: i18n.AvventuraNelCastelloJS.rooms.atrium.interactors.doorway.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.atrium.interactors.doorway.pattern,
							peso: -1,
							on: {
								'guarda|apri|spingi': async () => this._vai("s")
							}
						}

					}
					
				},

				// 04.SCALA A CHIOCCIOLA SUD-OVEST
				scalaChiocciolaSudOvest: { 
					...this.stanzeComuni.scalaChiocciola, 
					...{
						directions: {
							e: 'salaServitori',
							a: 'spaltiMuraSudOvest'
						}
					}
				},

				// 05.SALONE
				salone: {
					description: i18n.AvventuraNelCastelloJS.rooms.lounge.description.join("\n"),
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.lounge.shortDescription,
					directions: {
						s: 'salaArazzi',
						e: 'salaTrofei',
					},
					interactors: {
						divano: {
							...i18n.AvventuraNelCastelloJS.rooms.lounge.interactors.sofa,
							...{
								peso: 99
							}
						},
						poltrone: this.commonInteractors.poltrone,
						camino: { 
							...this.commonInteractors.camino, 
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.lounge.interactors.fireplace.description
							}
						},
						luce: {
							...i18n.AvventuraNelCastelloJS.rooms.lounge.interactors.light
						},
						passaggio: {
							...this.commonInteractors.passaggio, 
							...{
								pattern: i18n.AvventuraNelCastelloJS.rooms.lounge.interactors.pass.pattern,
								on: {
									cerca: i18n.AvventuraNelCastelloJS.rooms.lounge.interactors.pass.onLookFor
								}
							}
						}
					},
				},

				// 06.CORRIDOIO
				corridoio:{
					description: i18n.AvventuraNelCastelloJS.rooms.hallway.description.join("\n"),
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.hallway.shortDescription,
					directions: {
						n: 'salaPranzo',
						s: 'atrioCastello',
						o: 'galleriaRitratti'
					},
					interactors: {
						corridoio: this.commonInteractors.corridoio,
						armature: this.commonInteractors.armatura,
						picche: {
							label: i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.pattern,
							peso: 99,
							description: i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.description,
							on: {
								prendi: async () => {
									
									let answer = await this.yesNoQuestion(i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.onTake.question+" ");
									if(answer){
										let i=0;
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.onTake.answer[i++],{nlAfter:1});
										await this.CRT.sleep(1200);
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.onTake.answer[i++], {printDelay: 100, cr: false});
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.onTake.answer[i++], {printDelay: 1000});
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.onTake.answer[i++],{printDelay: 150});
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.spades.onTake.answer[i++]);
										await this.CRT.wait();
										this.die();
										return false;
									}
									this.gameLoop(false);
									return false;
								}
							}

						},
						porta: {
							...this.commonInteractors.porta,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.hallway.interactors.door.description,
							}
						}
					}
				},

				// 07.SALA DA PRANZO
				salaPranzo: {
					description: i18n.AvventuraNelCastelloJS.rooms.diningRoom.description.join("\n"),
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.diningRoom.shortDescription,
					directions: {
						s: 'corridoio',
						e: 'salaMusica',
						o: 'cucina',
						b: async () => {
							let res = await this.yesNoQuestion(i18n.AvventuraNelCastelloJS.rooms.diningRoom.directions.down.question);
							if(res){
								//this.CRT.println("");
								return await this.stanzaCorrente.interactors.finestra.on.salta;
							}
							this.gameLoop(false);
									return false;
						} 
					},
					interactors: {
						poltrone: this.commonInteractors.poltrone,
						tavola: { 
							...this.commonInteractors.tavolo, 
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.diningRoom.interactors.table.description
							}
						},
						sedie: { 
							...this.commonInteractors.sedie, 
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.diningRoom.interactors.chairs.description
							}
						},
						mostri: this.commonInteractors.mostro,
						finestra: {
							label: i18n.AvventuraNelCastelloJS.rooms.diningRoom.interactors.window.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.diningRoom.interactors.window.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.diningRoom.interactors.window.description,
							on: {
								salta: i18n.AvventuraNelCastelloJS.rooms.diningRoom.interactors.window.onJump,
								chiudi: async () => this.Thesaurus.defaultMessages.NON_E_POSSIBILE,
							},
							
						}
					},
					override: {
						verbs: {
							salta: async () => this.stanzaCorrente.interactors.finestra.on.salta
						}
					}
				},

				// 08.SCALA A CHIOCCIOLA NORD-OVEST
				scalaChiocciolaNordOvest: { 
					...this.stanzeComuni.scalaChiocciola, 
					...{
						directions: {
							e: 'cucina',
							s: 'cellaAlchimista',
							a: 'spaltiMuraNordOvest'
						}
					}
				},

				// 09.SCALA A CHIOCCIOLA NORD-EST
				scalaChiocciolaNordEst: { 
					...this.stanzeComuni.scalaChiocciola, 
					...{
						directions: {
							o: 'salaMusica',
							a: 'spaltiMuraNordEst'
						}
					}
				},

				// 10.SCALA A CHIOCCIOLA SUD-EST
				scalaChiocciolaSudEst: { 
					...this.stanzeComuni.scalaChiocciola, 
					...{
						directions: {
							o: 'passaggio',
							a: 'spaltiMuraSudEst',
							b: async () => {
								if(this.inventario.gatto && this.inventario.coppaLattemiele && this.inventario.coppaLattemiele.status == 0){
									await this.inventario.gatto.on.nutri();
								}
								this.aggiungiPunti("scesoInSotterranei");
								this.entra('scalaChiocciolaSotterranei');
							}
						}
					}
				},

				// 11.BIBLIOTECA
				biblioteca: {
					description: i18n.AvventuraNelCastelloJS.rooms.library.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.library.shortDescription,
					unavaiableDirections: ['o'],
					directions: {
						s: 'passaggio',
						e: 'salaSpecchi',
						o: 'salaTrono'
					},
					HUGE: i18n.AvventuraNelCastelloJS.rooms.library.HUGE,
					override: {
						commands: {
							cercaDizionario: async () => i18n.AvventuraNelCastelloJS.rooms.library.override.commands.lookForDictionary,
							iotaid: async () => {
								let self = this.altriDati.iotaid;
								if(self.id && self.iota && self.pronunciato == false){
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.library.override.commands.iotid[0]);
									await this.CRT.sleep(2000);
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.library.override.commands.iotid[1]);
									this.abilitaDirezione("o");
									self.pronunciato = true;
									this.aggiungiPunti("pronunciatoIotaid");
									return true;
								}
								return this.Thesaurus.commands.iotaid.callback();
							},
						}
					},

					interactors: {
						poltrone: this.commonInteractors.poltrone,
						scaffali: {
							...this.commonInteractors.scaffali, 
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.library.interactors.shelves.description
							}
						},
						leggio: {
							label: i18n.AvventuraNelCastelloJS.rooms.library.interactors.lectern.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.library.interactors.lectern.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.library.interactors.lectern.description,
							peso: 99
			
						},
						libri: {
							label: i18n.AvventuraNelCastelloJS.rooms.library.interactors.books.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.library.interactors.books.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.library.interactors.books.description,
							peso: 99,
							on: {
								prendi: i18n.AvventuraNelCastelloJS.rooms.library.interactors.books.onTake,
								leggi: i18n.AvventuraNelCastelloJS.rooms.library.interactors.books.onRead
							},
						},

						libro: {
							dlabel: i18n.AvventuraNelCastelloJS.rooms.library.interactors.book.dlabel,
							label: i18n.AvventuraNelCastelloJS.rooms.library.interactors.book.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.library.interactors.book.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.library.interactors.book.description,
							posizione: "biblioteca",
							status:0,
							visibile: true,
							peso: -1,
							on: {
								prendi: async () => this.stanzaCorrente.HUGE,
								apri: async () => {
									if(this.stanzaCorrente.interactors.libro.status == 1){
										return this.Thesaurus.defaultMessages.GIA_APERTO;
									}
									this.stanzaCorrente.interactors.libro.status = 1;
									this.aggiungiPunti("apertoLibro");
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.library.interactors.book.onOpen);
									this.scopri(this.datiAvventura.objects.foglio);
									return true;
								},
								leggi: async () => {
									if(this.stanzaCorrente.interactors.libro.status == 0){
										return this.Thesaurus.defaultMessages.E_CHIUSO;
									}
									this.aggiungiPunti("scopertoDizionario");
									delete this.stanzaCorrente.interactors.dizionario.visibile
									return i18n.AvventuraNelCastelloJS.rooms.library.interactors.book.onRead
								}
							}
						},

						dizionario: {
							label: i18n.AvventuraNelCastelloJS.rooms.library.interactors.dictionary.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.library.interactors.dictionary.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.library.interactors.dictionary.description,
							posizione: "biblioteca",
							visibile: false,
							peso: -1,
							on: {
								prendi: async () => this.stanzaCorrente.interactors.libro.on.prendi(),
								apri: async () => this.stanzaCorrente.interactors.libro.on.apri(),
								leggi: i18n.AvventuraNelCastelloJS.rooms.library.interactors.dictionary.onRead
							}
						},


					},
					

				},

				// 12.SPALTI MURA SUD-OVEST
				spaltiMuraSudOvest: {
					...this.stanzeComuni.spaltiMura, 
					...{
						directions: {
							n: 'spaltiMuraNordOvest',
							e: 'spaltiMuraSudEst',
							b: 'scalaChiocciolaSudOvest'
						}
					},
				},
				
				// 13.SPALTI MURA NORD-OVEST
				spaltiMuraNordOvest: {
					...this.stanzeComuni.spaltiMura, 
					...{
						directions: {
							s: 'spaltiMuraSudOvest',
							e: 'spaltiMuraNordEst',
							b: 'scalaChiocciolaNordOvest'
						}
					},
				},
				
				// 14.SPALTI MURA NORD-EST
				spaltiMuraNordEst: {
					...this.stanzeComuni.spaltiMura, 
					...{
						directions: {
							s: 'spaltiMuraSudEst',
							o: 'spaltiMuraNordOvest',
							b: 'scalaChiocciolaNordEst'
						}
					},
				},
				
				// 15.SPALTI MURA SUD-EST
				spaltiMuraSudEst: {
					...this.stanzeComuni.spaltiMura, 
					...{
						directions: {
							n: 'spaltiMuraNordEst',
							o: 'spaltiMuraSudOvest',
							b: 'scalaChiocciolaSudEst'
						}
					},
				},

				
				// 16. LARGO CUNICOLO
				largoCunicolo: {
					description: i18n.AvventuraNelCastelloJS.rooms.wideTunnel.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.wideTunnel.shortDescription,
					directions: {
						n: async () => {
							if(this.stanzaCorrente.objects.orco){
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.wideTunnel.directions.north.fail);
								this.die();
								return false;
							}
							this.entra("strettoCunicolo");
						},
						s: 'scalaChiocciolaSotterranei'
					},
					interactors: {
						roccia: this.commonInteractors.roccia,
						cunicolo: this.commonInteractors.cunicolo
					},

				},

				// 17.SEGRETA DEL CASTELLO
				segretaCastello: {
					description: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.shortDescription,
					directions: {
						o: async () => {
							this.aggiungiPunti("uscitoDaSegreta");
							this.entra('depositoAttrezzi');
						}
					},
					unavaiableDirections: ['o'],
					override: {
						verbs: {
							'infilaIn|infila': async (mSubjects) => this.runSequence("segretaCastello", mSubjects)
						}
					},
					interactors: {
						scheletri: this.commonInteractors.scheletri,
						foro: {
							label: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.hole.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.hole.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.hole.description,
							visibile: true,
							peso: -1
						},
						bottone: {
							label: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.button.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.button.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.button.description,
							peso: 99,
							on: {
								"premi|spingi": async () => this.runSequence("braccio")
							}
						},
						fessura: {
							label: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.slit.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.slit.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.slit.description,
							visibile: false,
							invisibleMessage: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.slit.invisibleMessage,
							on: {
								entra: async () => this._vai("o")
							}

						},
						braccio: {
							label: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.arm.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.castleDungeon.interactors.arm.pattern
						}
					}
				},
				
				// 18.DEPOSITO DEGLI ATTREZZI
				depositoAttrezzi: {
					description: i18n.AvventuraNelCastelloJS.rooms.toolshed.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.toolshed.shortDescription,
					directions: {
						e: 'segretaCastello',
						a: async () => {
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.toolshed.directions.up,{nlAfter:1});
							await this.CRT.sleep(1200);
							this.entra('scalaChiocciolaSudOvest');
						}
					}
				},

				// 19.STRETTO CUNICOLO
				strettoCunicolo: {
					description: i18n.AvventuraNelCastelloJS.rooms.narrowTunnel.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.narrowTunnel.shortDescription,
					directions: {
						s: 'largoCunicolo',
						o: 'lungoCunicolo'
					},
					interactors: {
						cunicolo: this.commonInteractors.cunicolo,
						muffe: {
							label: i18n.AvventuraNelCastelloJS.rooms.narrowTunnel.interactors.moulds.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.narrowTunnel.interactors.moulds.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.narrowTunnel.interactors.moulds.description,
							on: {
								mangia: async () => this.runSequence("mangia")
							}						
						},
						funghi: {
							label: i18n.AvventuraNelCastelloJS.rooms.narrowTunnel.interactors.mushrooms.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.narrowTunnel.interactors.mushrooms.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.narrowTunnel.interactors.mushrooms.description,
							on: {
								mangia: async () => this.runSequence("mangia")
							}
							
						}
					},

				},

				// 20.LUNGO CUNICOLO
				lungoCunicolo: {
					description: i18n.AvventuraNelCastelloJS.rooms.longTunnel.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.longTunnel.shortDescription,
					directions: {
						s: 'cameraTesoro',
						e: 'strettoCunicolo',
						o: 'legnaia',
					},
					interactors: {
						cunicolo: this.commonInteractors.cunicolo,
						roccia: this.commonInteractors.roccia,
						terreno: {
							label: i18n.AvventuraNelCastelloJS.rooms.longTunnel.interactors.ground.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.longTunnel.interactors.ground.pattern
						},
						scavi: {
							label: i18n.AvventuraNelCastelloJS.rooms.longTunnel.interactors.excavations.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.longTunnel.interactors.excavations.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.longTunnel.interactors.excavations.description
						}
					}

				},

				// 21.CAMERA DEL TESOSO
				cameraTesoro: {
					description: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.shortDescription,
					directions: {
						n: 'lungoCunicolo',
						e: 'trappola'
					},
					interactors: {
						archi: this.commonInteractors.archi,
						forziere: {
							dlabel: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.coffer.dlabel,
							label: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.coffer.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.coffer.pattern,
							status: 0, 
							visibile:true,
							peso: -1,
							on: {
								guarda: async() => {
									if(this.stanzaCorrente.interactors.forziere.status == 0)
										return this.Thesaurus.defaultMessages.E_CHIUSO;
									
									this.scopri(this.datiAvventura.objects.corno);
									this.aggiungiPunti("trovatoCorno");
									if(this.stanzaCorrente.objects.corno){
										return i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.coffer.onLook[0];
									}
										
									return i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.coffer.onLook[1]
								},
								apri: async () => {
									if(this.stanzaCorrente.interactors.fantasma.neutralizzato === false){
										if(this.stanzaCorrente.interactors.fantasma.visibile === false){
											this.scopri(this.stanzaCorrente.interactors.fantasma);
											return i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.coffer.onOpen[0]
										}
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.coffer.onOpen[1], {printDelay: 125});
										this.die();
										return false;
									}
									if(this.stanzaCorrente.interactors.forziere.status == 1)
										return this.Thesaurus.defaultMessages.GIA_APERTO
									this.stanzaCorrente.interactors.forziere.status = 1;
									this.aggiungiPunti("apertoForziere");
									return this.Thesaurus.defaultMessages.FATTO;
								},
								chiudi: async () => {
									return this.stanzaCorrente.interactors.forziere.status == 0 ?
										this.Thesaurus.defaultMessages.E_CHIUSO :
										i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.coffer.onClose;

								}
							}
						},
						fantasma: {
							label: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.ghost.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.ghost.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.ghost.description,
							visibile: false,
							neutralizzato: false,
							peso: -1,
							on: {
								uccidi: i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.ghost.onKill,
								parla: async () => {
									await this.CRT.printTyping (i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.ghost.onTalk[0]);
									await this.CRT.printTyping (i18n.AvventuraNelCastelloJS.rooms.treasureChamber.interactors.ghost.onTalk[1], {printDelay: 150});
								}
							}
						}
					}
					

				},

				// 22.LEGNAIA
				legnaia: {
					description: i18n.AvventuraNelCastelloJS.rooms.woodshed.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.woodshed.shortDescription,
					directions: {
						s: 'dispensaVini',
						e: 'lungoCunicolo',
					},
					interactors: {
						legna: {
							label: i18n.AvventuraNelCastelloJS.rooms.woodshed.interactors.wood.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.woodshed.interactors.wood.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.woodshed.interactors.wood.description,
							peso: 99,
							on: {
								prendi: i18n.AvventuraNelCastelloJS.rooms.woodshed.interactors.wood.onTake,
							},
							
						}
					},
					override: {
						commands: {
							ciao: {
								pattern: () => "(?:"+this.commonPatterns.pronuncia+")?"+this.Thesaurus.verbs.ciao.pattern+"( "+this.stanzaCorrente.objects.nano.pattern+")?",
								callback: async () => {
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.woodshed.override.commands.helloPrefix, {nlAfter:1});
									await this.CRT.sleep(1500);
									return await this.stanzaCorrente.objects.nano.on.saluta();
								}
							},
							buongiorno: {
								pattern: () => "(?:"+this.commonPatterns.pronuncia+")?"+this.Thesaurus.verbs.buongiorno.pattern+"( "+this.stanzaCorrente.objects.nano.pattern+")?",
								callback: async () => this.stanzaCorrente.objects.nano.on.saluta() 
							},
							/*
							saluta: {
								pattern: "saluta(\\s+nano)?",
								callback: async () => {
									return await this.stanzaCorrente.objects.nano.on.saluta();
								}
							},
							*/
							presentati: async () => {
								if(this.datiAvventura.objects.diamante.visibile)
									return i18n.AvventuraNelCastelloJS.rooms.woodshed.override.commands.introduceYourself[0];

								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.woodshed.override.commands.introduceYourself[1],{nlAfter: 1,printDelay: 75});

								await this.CRT.sleep(2000);
								return await this.stanzaCorrente.objects.nano.on.saluta();
							}
						},
						verbs: {
							chiedi: async (targets) => {
								if(targets[0] == this.datiAvventura.objects.diamante){

									if(this.datiAvventura.objects.nano.status == 1){
										return i18n.AvventuraNelCastelloJS.rooms.woodshed.override.verbs.askForDiamond[0];
									}
									return i18n.AvventuraNelCastelloJS.rooms.woodshed.override.verbs.askForDiamond[1];
								}
								return this.Thesaurus.verbs.chiedi.defaultMessage;
							},

							chiediA: async (targets) => {
								if(targets[0] == this.datiAvventura.objects.diamante && targets[1] == this.datiAvventura.objects.nano){
									return this.stanzaCorrente.override.verbs.chiedi(targets);
								}
								return this.Thesaurus.verbs.chiedi.defaultMessage;	
							}

						}
					},
				},
				// 23.IN CIMA ALLE SCALE
				cimaScale: {
					description: i18n.AvventuraNelCastelloJS.rooms.topOfStairs.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.topOfStairs.shortDescription,
					directions: {
						b: 'atrioCastello'
					},
					interactors: {
						gradini: this.commonInteractors.gradini,
						scale: this.commonInteractors.scala,
						pareti: {
							...this.commonInteractors.pareti,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.topOfStairs.interactors.walls.description,
								on: {
									spingi: async () => {
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.topOfStairs.interactors.walls.onPush[0],{cr:false});
										await this.CRT.sleep(1500);
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.topOfStairs.interactors.walls.onPush[1],{nlAfter:1});
										this.aggiungiPunti("entratoLabirinto");
										this.entra('entrataLabirinto');
										return false;
									},
									rompi: i18n.AvventuraNelCastelloJS.commonInteractors.walls.onBreak
								}
							}
						}
					
					},

				},

				// 24.ENTRATA DEL LABIRINTO
				entrataLabirinto: {
					...this.stanzeComuni.labirinto,
					...{
						description: i18n.AvventuraNelCastelloJS.rooms.labyrinthEntrance.description.join("\n"),
						shortDescription: i18n.AvventuraNelCastelloJS.rooms.labyrinthEntrance.shortDescription,
						directions: {
							n: 'L32',
							s: 'L26',
							e: 'L32',
							o: 'L31',
							a: 'L30',
							b: 'L32'
						},
						interactors: {
							labirinto: this.commonInteractors.labirinto,
							scheletri: this.commonInteractors.scheletri,
							scritta: {
								label: i18n.AvventuraNelCastelloJS.rooms.labyrinthEntrance.interactors.writing.label,
								pattern: i18n.AvventuraNelCastelloJS.rooms.labyrinthEntrance.interactors.writing.pattern,
							}
						},
						override: {
							commands:{
								pensa: async () => this.stanzeComuni.labirinto.override.commands.pensa(),
								usaSenno: async () => this.stanzeComuni.labirinto.override.commands.pensa()
							}
						}
					}
				
				},

				// 25.STANZA SEGRETA
				stanzaSegreta: {
					description: i18n.AvventuraNelCastelloJS.rooms.secretRoom.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.secretRoom.shortDescription,
					directions: {
						e: 'L29',
						a: 'cimaTorre'
					},
					unavaiableDirections : ['a'],
					interactors: {
						torre: this.commonInteractors.torre,
						fessure: {
							label: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.slits.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.slits.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.slits.description
						},
						leva: {
							label: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.lever.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.lever.pattern,
							visibile: true,
							peso: -1,
							on: {
								spingi: async() => {
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.lever.onPush,{nlAfter: 1});
									await this.CRT.sleep(1500);
									this.entra('corridoio');
									return false;
								},
								tira: this.Thesaurus.defaultMessages.NON_SUCCEDE_NIENTE
							}
						},
						orologio: {
							dlabel: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.dlabel,
							label: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.description,
							status: 0,
							peso: 99,
							visibile: true,
							on: {
								guarda: async () => {
									if(this.stanzaCorrente.interactors.orologio.status == 0)
										return null;
									if(this.stanzaCorrente.interactors.orologio.status < 5){
										this.stanzaCorrente.interactors.orologio.status++;
									}
									if(this.stanzaCorrente.interactors.orologio.status == 5){
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.onLook[0]);
										await this.s2();
										this.aggiungiPunti("suonaMezzanotte");
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.onLook[1], {nlBefore:2});
										this.abilitaDirezione("a");
										this.stanzaCorrente.interactors.orologio.status++;
										return true;	
									}
									return null;
								},
								carica: async () => {
									if(this.inventario.chiave === undefined)
										return i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.onCharge.fail;
									if(this.stanzaCorrente.interactors.orologio.status == 0){
										await this.s1();
										this.aggiungiPunti("caricatoOrologio");
										this.stanzaCorrente.interactors.orologio.status++;
										return i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.onCharge.success;
									}
									if(this.stanzaCorrente.interactors.orologio.status < 5)
										return i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.onCharge.working
									return i18n.AvventuraNelCastelloJS.rooms.secretRoom.interactors.pendulumClock.onCharge.blocked;
								}
							}
						}
					},
					onEnter: async () => {
						this.aggiungiPunti("risoltoLabirinto");
						return null;
					}
				},

				// 26.LABIRINTO
				L26: {
					...this.stanzeComuni.labirinto,
					...{
						directions: {
							n: 'L32',
							s: 'L31',
							e: 'L27',
							o: 'L32',
							a: 'entrataLabirinto',
							b: 'L30'
						},
						
					}
				},
				
				// 27.LABIRINTO
				L27: {
					...this.stanzeComuni.labirinto,
					...{
						directions: {
							n: 'L28',
							s: 'L31',
							e: 'L31',
							o: 'L32',
							a: 'L30',
							b: 'L31'
						},
						
					}
				},
				
				// 28.LABIRINTO
				L28: {
					...this.stanzeComuni.labirinto,
					...{
						directions: {
							n: 'L29',
							s: 'L31',
							e: 'L30',
							o: 'L31',
							a: 'L32',
							b: 'entrataLabirinto'
						},
						
					}
				},
				
				// 29.LABIRINTO
				L29: {
					...this.stanzeComuni.labirinto,
					...{
						directions: {
							n: 'L30',
							s: 'L31',
							e: 'L30',
							o: async () => {
								if(this.datiAvventura.objects.pergamena.tradotta){
									this.entra('stanzaSegreta');
									return false;
								}
								await this.CRT.clear();
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.L29.dodgersHatch,{nlAfter:1});
								await this.CRT.sleep(1500);
								this.entra('atrioCastello');
							},
							a: 'L32',
							b: 'L32'
						},
						
					}
				},
				
				// 30.LABIRINTO
				L30: {
					...this.stanzeComuni.labirinto,
					...{
						directions: {
							n: 'L30',
							s: 'L30',
							e: 'entrataLabirinto',
							o: 'entrataLabirinto',
							a: 'L31',
							b: 'L32'
						},
						
					}
				},
				
				// 31.LABIRINTO
				L31: {
					...this.stanzeComuni.labirinto,
					...{
						directions: {
							n: 'L30',
							s: 'entrataLabirinto',
							e: 'L31',
							o: 'L31',
							a: 'L30',
							b: 'L32'
						},
						
					}
				},
				
				// 32.LABIRINTO
				L32: {
					...this.stanzeComuni.labirinto,
					...{
						directions: {
							n: 'entrataLabirinto',
							s: 'L32',
							e: 'L31',
							o: 'L32',
							a: 'L30',
							b: 'L31'
						},
						
					}
				},
				
				// 33.SALA DEL TRONO
				salaTrono: {
					description: i18n.AvventuraNelCastelloJS.rooms.throneRoom.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.throneRoom.shortDescription,
					directions: {
						n: 'salaConsiglio',
						e: 'biblioteca',
					},
					override: {
						verbs: {
							siedi: async (target) => {
								if(!target){
									let res = await this.yesNoQuestion(i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.throne.onSitDown.question);
									if(res){
										//this.CRT.println("");
										return await this.stanzaCorrente.interactors.trono.on.siedi();
									}
									this.gameLoop(false);
									return false;
								}
								if(target == this.stanzaCorrente.interactors.trono)
									return await this.stanzaCorrente.interactors.trono.on.siedi();
								return null;
							}
						}
					},
					interactors: {
						porta: {
							...this.commonInteractors.porta,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.door.description,
							}
						},
						nicchie: {
							label: i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.hollows.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.hollows.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.hollows.description
						},
						trono: {
							label: i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.throne.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.throne.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.throne.description,
							peso: 99,
							on: {
								siedi: async () => {
									delete this.stanzaCorrente.objects.cuscino.visibile;
									return i18n.AvventuraNelCastelloJS.rooms.throneRoom.interactors.throne.onSitDown.answer
								}

							},
							
						}
					},
					onEnter: async () => this.aggiungiPunti("entratoSalaTrono")
				},

				// 34.CIMA DELLA TORRE
				cimaTorre: {
					description: i18n.AvventuraNelCastelloJS.rooms.topOfTower.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.topOfTower.shortDescription,
					directions: {
						b: 'stanzaSegreta'
					},
					interactors: {
						nebbia:{
							...this.commonInteractors.nebbia,
							... {
								description: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.fog.description
							}
						},
						brughiera: {
							label: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.moor.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.moor.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.moor.description
						},
						montagne: {
							label: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.mountains.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.mountains.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.mountains.description							
						},
						bandiera: {
							label: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.flag.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.flag.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.flag.description,
							visibile: true,
							peso: 99,
							on: {
								prendi: async () => {
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.flag.onTake[0],{cr:false});
									await this.CRT.sleep(1500);
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.flag.onTake[1]);
									this.die();
									return false;
								},
								alza: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.flag.onLiftUp
							}
						},
						torre: {
							...this.commonInteractors.torre,
							... {
								description: i18n.AvventuraNelCastelloJS.rooms.topOfTower.interactors.tower.description
							}
						}

					},
					override: {
						verbs: {
							salta: async () => {
								this.CRT.println(i18n.AvventuraNelCastelloJS.rooms.topOfTower.override.verbs.jump,{reversed: true});
								await this.s0();
								this.die();
								return false;
							}
						}
					},
					onEnter: async () => this.aggiungiPunti("salitoTorre")
				},

				// 35.SCALA CHIOCCIOLA SOTTERRANEI
				scalaChiocciolaSotterranei: {
					...this.stanzeComuni.scalaChiocciola,
					... {
						description: i18n.AvventuraNelCastelloJS.rooms.undergroundSpiralStaircase.description,
						directions: {
							a: 'scalaChiocciolaSudEst',
							n: 'largoCunicolo'
						},
						interactors: {
							passaggio: {
								...this.commonInteractors.passaggio,
								... {
									description: i18n.AvventuraNelCastelloJS.rooms.undergroundSpiralStaircase.interactors.aisle.description
								}
							}
							
						}
					}
					
				},
					
				// 36.TRAPPOLA
				trappola: {
					description: i18n.AvventuraNelCastelloJS.rooms.trap.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.trap.shortDescription,
					uscito:false,
					directions: {
						o: async () => {
							if(this.stanzaCorrente.uscito == false){
								this.stanzaCorrente.uscito = true;
								this.entra("cameraTesoro");
								return false;
							}
							await this.s0();
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.trap.directions.west[0]);
							await this.CRT.sleep(1500);
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.trap.directions.west[1],{printDelay: 100, cr:false});
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.trap.directions.west[2],{printDelay: 100, reversed: true});
							this.die();
						}
					},
					interactors: {
						trappola: {
							label: i18n.AvventuraNelCastelloJS.rooms.trap.interactors.trap.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.trap.interactors.trap.pattern,
							on: {
								"guarda|cerca": i18n.AvventuraNelCastelloJS.rooms.trap.interactors.trap.onLookOrLookFor
							},
						}
					}

				},	

				// 37.DISPENSA DEI VINI
				dispensaVini: {
					description: i18n.AvventuraNelCastelloJS.rooms.winePantry.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.winePantry.shortDescription,
					directions: {
						n: async () => {
							if(this.timedEvents.indexOf("whisky") < 0){
								this.entra('legnaia');
								return false;
							}
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.winePantry.directions.north.join("\n"));
							this.die();
							return false;
						},
						s: 'dispensaSalumi'
					},
					interactors: {
						scaffali: this.commonInteractors.scaffali,
						cocci: {
							label: i18n.AvventuraNelCastelloJS.rooms.winePantry.interactors.fragments.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.winePantry.interactors.fragments.pattern,
							on: {
								prendi: i18n.AvventuraNelCastelloJS.rooms.winePantry.interactors.fragments.onTake
							},
						}
					}
				},

				// 38.DISPENSA DEI SALUMI
				dispensaSalumi: {
					description: i18n.AvventuraNelCastelloJS.rooms.coldCutsPantry.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.coldCutsPantry.shortDescription,
					directions: {
						n: 'dispensaVini',
						e: 'dispensaVerdura'
					},
					interactors: {
						ganci: {
							label: i18n.AvventuraNelCastelloJS.rooms.coldCutsPantry.interactors.hooks.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.coldCutsPantry.interactors.hooks.pattern,
							on: {
								prendi: i18n.AvventuraNelCastelloJS.rooms.coldCutsPantry.interactors.hooks.onTake
							},
							
						}
					}
				},

				// 39.DISPENSA DELLE VERDURE
				dispensaVerdura: {
					description: i18n.AvventuraNelCastelloJS.rooms.vegetablePantry.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.vegetablePantry.shortDescription,
					directions: {
						e: 'dispensaFormaggi',
						o: 'dispensaSalumi'
						
					},
					interactors: {
						macchie: {
							label: i18n.AvventuraNelCastelloJS.rooms.vegetablePantry.interactors.stains.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.vegetablePantry.interactors.stains.pattern
						}
					}
				},

				// 40.DISPENSA DEI FORMAGGI
				dispensaFormaggi: {
					description: i18n.AvventuraNelCastelloJS.rooms.cheesePantry.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.cheesePantry.shortDescription,
					directions: {
						e: 'dispensaCacciagione',
						o: 'dispensaVerdura'
					},
					interactors: {
						croste: {
							label: i18n.AvventuraNelCastelloJS.rooms.cheesePantry.interactors.crusts.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.cheesePantry.interactors.crusts.pattern,
							on: {
								"prendi|mangia": i18n.AvventuraNelCastelloJS.rooms.cheesePantry.interactors.crusts.onTakeOrEat
							},
							
						}
					}
				},				
				
				// 41.DISPENSA DELLA CACCIAGIONE
				dispensaCacciagione: {
					description: i18n.AvventuraNelCastelloJS.rooms.gamePantry.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.gamePantry.shortDescription,
					directions: {
						n: 'dispensaWhisky',
						o: 'dispensaFormaggi'
					},
					interactors: {
						cervo: {
							label: i18n.AvventuraNelCastelloJS.rooms.gamePantry.interactors.deer.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.gamePantry.interactors.deer.pattern,
							on: {
								prendi: i18n.AvventuraNelCastelloJS.rooms.gamePantry.interactors.deer.onTake
							},
							
						}
					}
				},				
				
				// 42.DISPENSA DEL WHISKY
				dispensaWhisky: {
					description: i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.shortDescription,
					directions: {
						s: 'dispensaCacciagione'
					},
					interactors: {
						botticella: {
							label: i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.interactors.keg.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.interactors.keg.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.interactors.keg.description,
							peso: 99,
							on: {
								apri: async () => {
									if(await this.yesNoQuestion(i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.interactors.keg.onOpenQuestion)){
										//this.CRT.println("");
										return await this.stanzaCorrente.interactors.whisky.on["prendi|bevi"]();
									}
									this.gameLoop(false);
									return false;
								}
							},
							
						},
						whisky: {
							label: i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.interactors.whiskey.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.interactors.whiskey.pattern,
							peso: 99,
							on: {
								"prendi|bevi": async () => {
									this.startTimedEvent("whisky");
									return i18n.AvventuraNelCastelloJS.rooms.whiskeyPantry.interactors.whiskey.onTakeOrDrink
								},
							}
						}
					}
				},	

				// 43.SALA DELLE GUARDIE
				salaGuardie:{
					description: i18n.AvventuraNelCastelloJS.rooms.guardRoom.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.guardRoom.shortDescription,
					directions: {
						e: 'catapulta',
						o: 'atrioCastello'	
					},
					interactors: {
						tavolo: { 
							...this.commonInteractors.tavolo,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.guardRoom.interactors.table.description
							}
						},
						panche: {
							label: i18n.AvventuraNelCastelloJS.rooms.guardRoom.interactors.benches.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.guardRoom.interactors.benches.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.guardRoom.interactors.benches.description,
							peso: 99
						}
					},

				},

				// 44.STANZA DELLA CATAPULTA
				catapulta:{
					description: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.description.join("\n"),
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.shortDescription,
					directions: {
						n: 'armeria',
						o: 'salaGuardie'	
					},
					risposteComuni: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.commonAnswers,
					interactors: {
						catapulta: {
							label: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.catapult.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.catapult.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.catapult.description,
							peso: 99,
							on: {
								carica: async () => this.stanzaCorrente.risposteComuni[0],
								monta: async () => this.stanzaCorrente.risposteComuni[1]
							}

						},
						palle: {
							label: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.balls.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.balls.pattern,
							peso: 99,
							on: {
								'prendi|alza': async () => this.stanzaCorrente.risposteComuni[0],
								rompi: async () => i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.balls.onBreak
							},

							
						},
						pezzi: {
							label: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.pieces.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.pieces.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.catapultRoom.interactors.pieces.description,
							on: {
								monta: async () => this.stanzaCorrente.risposteComuni[1]
							}
							
						},
							
					},

				},

				// 45.ARMERIA
				armeria: {
					description: i18n.AvventuraNelCastelloJS.rooms.armory.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.armory.shortDescription,
					directions: {
						s: 'catapulta',
						o: 'sottoscala'
					},
					interactors: {
						archi: this.commonInteractors.archi,
						scudi: this.commonInteractors.scudi,
						pugnali: {
							label: i18n.AvventuraNelCastelloJS.rooms.armory.interactors.daggers.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.armory.interactors.daggers.pattern,
							peso: 99
						},
						lance: {
							label: i18n.AvventuraNelCastelloJS.rooms.armory.interactors.spears.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.armory.interactors.spears.pattern,
							peso: 99
						},
						asce: {
							label: i18n.AvventuraNelCastelloJS.rooms.armory.interactors.axes.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.armory.interactors.axes.pattern,
							peso: 99
						},
						armi: {
							...this.commonInteractors.armi,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.armory.interactors.weapons.description
							}
						}
					},

				},

				// 46.SOTTOSCALA
				sottoscala: {
					description: i18n.AvventuraNelCastelloJS.rooms.understairs.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.understairs.shortDescription,
					directions: {
						e: 'armeria'
					},
					interactors: {
						understais: {
							label: i18n.AvventuraNelCastelloJS.rooms.understairs.interactors.understairs.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.understairs.interactors.understairs.pattern
						}
					},
				},

				// 47.SALA DELLA SERVITU'
				salaServitori: {
					description: i18n.AvventuraNelCastelloJS.rooms.servantsHall.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.servantsHall.shortDescription,
					directions: {
						e: 'atrioCastello',
						n: 'salaColonne',
						o: 'scalaChiocciolaSudOvest'
					}
				},

				// 48.SALA DELLE COLONNE
				salaColonne: {
					description: i18n.AvventuraNelCastelloJS.rooms.columnsHall.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.columnsHall.shortDescription,
					directions: {
						s: 'salaServitori',
						o: 'salaArazzi',
					},
					interactors: {
						colonna: {
							label: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.column.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.column.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.column.description,
							peso: 99,
							on: {
								guarda: async () => {
									this.aggiungiPunti("lettoId");
									this.altriDati.iotaid.id = true;
									return null;
								}
							}
						},
						colonne: {
							label: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.columns.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.columns.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.columns.description
							
						},
						piedistallo: {
							label: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.pedestal.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.pedestal.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.columnsHall.interactors.pedestal.description,
						}
					},

				},

				// 49.SALA DEGLI ARAZZI
				salaArazzi: {
					description: i18n.AvventuraNelCastelloJS.rooms.tapestriesRoom.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.tapestriesRoom.shortDescription,
					directions: {
						e: 'salaColonne',
						n: 'salone'
					},
					interactors: {
						arazzi: {
							label: i18n.AvventuraNelCastelloJS.rooms.tapestriesRoom.interactors.tapestries.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.tapestriesRoom.interactors.tapestries.pattern,
							on: {
								guarda: async () => {
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.tapestriesRoom.interactors.tapestries.onLook[0]);
									await this.CRT.sleep(2000);
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.tapestriesRoom.interactors.tapestries.onLook[1]);
								}

							}
							
						}
					},

				},
				
				// 50.GALLERIA DEI RITRATTI
				galleriaRitratti: {
					description: i18n.AvventuraNelCastelloJS.rooms.portraitsGallery.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.portraitsGallery.shortDescription,
					directions: {
						n: 'salaTrofei',
						e: 'corridoio'
					},
					interactors: {
						ritratti: {
							label: i18n.AvventuraNelCastelloJS.rooms.portraitsGallery.interactors.portrait.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.portraitsGallery.interactors.portrait.pattern,
							on: {
								guarda: async () => {
									let res = await this.yesNoQuestion(i18n.AvventuraNelCastelloJS.rooms.portraitsGallery.interactors.portrait.onLookQuestion);
									if(res){
										//this.CRT.println("");
										await this.runSequence("ritratti");
										return false;
									}
									this.gameLoop(false);
									return false;
								}

							},
							
						}
					}

				},
				
				// 51.SALA DEI TROFEI
				salaTrofei: {
					description: i18n.AvventuraNelCastelloJS.rooms.trophiesRoom.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.trophiesRoom.shortDescription,
					directions: {
						n: 'cucina',
						s: 'galleriaRitratti',
						o: 'salone'
					},
					interactors: {
						armatura: { 
							...this.commonInteractors.armatura, 
							...{
								on: {
									...this.commonInteractors.armatura.on,
									...{
										guarda: async () => {
											let spada = this.datiAvventura.objects.spada.visibile ? "" : i18n.AvventuraNelCastelloJS.rooms.trophiesRoom.interactors.armor.spadeText
											if(spada.length > 0)
												delete this.datiAvventura.objects.spada.visibile;
											return i18n.AvventuraNelCastelloJS.rooms.trophiesRoom.interactors.armor.onLook(spada);
										}	
									}
								}
							}
						},
						scudi: this.commonInteractors.scudi,
						trofei: {
							label: i18n.AvventuraNelCastelloJS.rooms.trophiesRoom.interactors.trophies.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.trophiesRoom.interactors.trophies.pattern,
							peso: 99
						},
						armi: this.commonInteractors.armi,
					},

				},

				// 52.CUCINA
				cucina: {
					description: i18n.AvventuraNelCastelloJS.rooms.kitchen.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.kitchen.shortDescription,
					directions: {
						s: 'salaTrofei',
						e: 'salaPranzo',
						o: 'scalaChiocciolaNordOvest'
					},
					interactors: {
						focolare: this.commonInteractors.camino,
						cucina: {
							label: i18n.AvventuraNelCastelloJS.rooms.kitchen.interactors.kitchenStuff.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.kitchen.interactors.kitchenStuff.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.kitchen.interactors.kitchenStuff.description						
						}
					},
				},

				// 53.CELLA DELL'ALCHIMISTA
				cellaAlchimista: {
					description: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.description.join("\n"),
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.shortDescription,
					directions: {
						n: 'scalaChiocciolaNordOvest'
					},
					interactors: {
						scaffali: this.commonInteractors.scaffali,
						tavolino: this.commonInteractors.tavolo,
						infolio: {
							label: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.infolio.label,
							pattern : i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.infolio.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.infolio.description,
							peso: 99,
							on: {
								'prendi|leggi': i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.infolio.onTakeOrRead
							},
							
						},
						segnalibro: {
							label: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.bookmark.label,
							pattern : i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.bookmark.pattern,
							peso: 99
						},
						volume: {
							label: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.volume.label,
							pattern : i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.volume.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.volume.description,
							aperto: false,
							on: {
								apri: async () => {
									return this.stanzaCorrente.interactors.volume.aperto ?
										this.Thesaurus.defaultMessages.GIA_APERTO :
										i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.volume.onOpen;
								},
								leggi: async () => {
									if(this.stanzaCorrente.interactors.volume.aperto){
										this.altriDati.bigmeow.attivo=true;
										this.aggiungiPunti("lettoBigMeow");
										return i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.volume.onRead.join("\n");
									} 
									return this.Thesaurus.defaultMessages.E_CHIUSO;
								},
								prendi: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.volume.onTake,
								alza: i18n.AvventuraNelCastelloJS.rooms.alchemistCell.interactors.volume.onLiftUp
							}
						}
					},

				},
				
				// 54.SALA DEL CONSIGLIO
				salaConsiglio: {
					description: i18n.AvventuraNelCastelloJS.rooms.boardRoom.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.boardRoom.shortDescription,
					directions: {
						s: 'salaTrono'
					},
					interactors: {
						tavola: {
							...this.commonInteractors.tavolo,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.boardRoom.interactors.table.description.join("\n")
							}
						},
						sedie: {
							...this.commonInteractors.sedie,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.boardRoom.interactors.chairs.description
							}
						}
					},

				},
				
				// 55.GUARDAROBA
				guardaroba: {
					description: i18n.AvventuraNelCastelloJS.rooms.wardrobe.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.wardrobe.shortDescription,
					directions: {
						n: 'salaMusica',
						e: 'stanzaRe'
					},
					interactors: {
						brandelli: {
							label: i18n.AvventuraNelCastelloJS.rooms.wardrobe.interactors.shreds.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.wardrobe.interactors.shreds.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.wardrobe.interactors.shreds.description,
						}
					},

				},
				
				// 56.SALA DI MUSICA
				salaMusica: {
					description: i18n.AvventuraNelCastelloJS.rooms.musicHall.description.join("\n"),
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.musicHall.shortDescription,
					directions: {
						s: 'guardaroba',
						e: 'scalaChiocciolaNordEst',
						o: 'salaPranzo'
					},
					interactors: {
						ragnatele: {
							label: i18n.AvventuraNelCastelloJS.rooms.musicHall.interactors.cobwebs.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.musicHall.interactors.cobwebs.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.musicHall.interactors.cobwebs.description
						}
					},

				},

				// 58.STANZA DELLA PRINCIPESSA
				stanzaPrincipessa: {
					description: i18n.AvventuraNelCastelloJS.rooms.princessRoom.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.princessRoom.shortDescription,
					directions: {
						s: 'stanzaRe'
					},
					interactors: {
						letto: {
							...this.commonInteractors.letto,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.princessRoom.interactors.bed.description
							}
						},
						tende: {
							label: i18n.AvventuraNelCastelloJS.rooms.princessRoom.interactors.curtains.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.princessRoom.interactors.curtains.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.princessRoom.interactors.curtains.description
						}
					},

				},

				// 58.STANZA DEL RE
				stanzaRe: {
					description: i18n.AvventuraNelCastelloJS.rooms.kingRoom.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.kingRoom.shortDescription,
					directions: {
						n: 'stanzaPrincipessa',
						s: 'salaSpecchi',
						o: 'guardaroba'
					},
					interactors: {
						letto: {
							...this.commonInteractors.letto,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.kingRoom.interactors.bed.description
							}
						}
					},

				},

				// 59.SALA DEGLI SPECCHI
				salaSpecchi: {
					description: i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.shortDescription,
					directions: {
						n: async () => this.stanzaCorrente.bonk(),
						o: async () => this.stanzaCorrente.bonk()
					},
					bonk: async () => {
						if(this.getRandomIntInclusive(1,100) < 26){
							this.entra('stanzaRe');
							return false;
						}

						await this.CRT.print( "- ");
						await this.CRT.print(`&gt;${i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.bonk}&lt;`, {reversed: true});
						await this.CRT.println( " -");
						await this.s0();
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.notADoor);
						return true;
					},
					override: {
						commands: {
							esci: async () => i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.override.commands.getOut
						}
					},
					interactors: {
						specchi: {
							label: i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.interactors.mirrors.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.interactors.mirrors.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.interactors.mirrors.description,
							peso: 99,
							on: {
								rompi: i18n.AvventuraNelCastelloJS.rooms.mirrorsHall.interactors.mirrors.onBreak
							},
							
						}
					},

				},

				// 60.PASSAGGIO
				passaggio: {
					description: i18n.AvventuraNelCastelloJS.rooms.pass.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.pass.shortDescription,
					directions: {
						e: 'scalaChiocciolaSudEst',
						n: 'biblioteca',
					},
					interactors: {
						passaggio: {
							...this.commonInteractors.passaggio,
							... {
								description: i18n.AvventuraNelCastelloJS.rooms.pass.interactors.pass.description
							}
						}
					},

				},

				// 61.LO SCOGLIO
				scoglio: {
					description: i18n.AvventuraNelCastelloJS.rooms.rock.description,
					shortDescription: i18n.AvventuraNelCastelloJS.rooms.rock.shortDescription,
					interactors: {
						mostro: {
							...this.commonInteractors.mostro,
							...{
								description: i18n.AvventuraNelCastelloJS.rooms.rock.interactors.nessie.description,
								on: {
									saluta: async () => {
										let i=0;
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.rock.interactors.nessie.onGreet[i++]+" ", {cr:false});
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.rock.interactors.nessie.onGreet[i++], {reversed: true, cr:false});
										await this.CRT.printTyping(" "+i18n.AvventuraNelCastelloJS.rooms.rock.interactors.nessie.onGreet[i++]);
									},
									uccidi: i18n.AvventuraNelCastelloJS.rooms.rock.interactors.nessie.onKill
								}
							}
						},
						scoglio: {
							label: i18n.AvventuraNelCastelloJS.rooms.rock.interactors.rock.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.rock.interactors.rock.pattern,
							description: i18n.AvventuraNelCastelloJS.rooms.rock.interactors.rock.description.join("\n")
						},
						lago: {
							label: i18n.AvventuraNelCastelloJS.rooms.rock.interactors.lake.label,
							pattern: i18n.AvventuraNelCastelloJS.rooms.rock.interactors.lake.pattern,
						}
					},
					override: {
						commands: {
							nuota: async() => {
								let i=0;
										
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.rock.override.commands.swim[i++]);
								await this.CRT.sleep(1200);
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.rock.override.commands.swim[i++]+" ",{cr:false});
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.rooms.rock.override.commands.swim[i++],{reversed: true, cr:false});
								await this.CRT.printTyping(" "+i18n.AvventuraNelCastelloJS.rooms.rock.override.commands.swim[i++]);
								this.die();
								return false;
							}
						}
					},
					onEnter: async() => {
						this.startTimedEvent("nessie");
						this.altriDati.punti = 999;
					}
				}

			},

			/* OGGETTI */
			objects: {
				paracadute: {
					label: i18n.AvventuraNelCastelloJS.objects.parachute.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.parachute.pattern,
					posizione: "aereo",
					on: {
						"cerca|prendi|usa|indossa": async () => {
							
							if(this.inventario.paracadute)
								return i18n.AvventuraNelCastelloJS.objects.parachute.onWear
							if(this.stanzaCorrente.key == 'aereo')
								return await this.runSequence("paracadute");
							return null;
						},
						guarda: i18n.AvventuraNelCastelloJS.objects.parachute.onLook,
						apri: async () => {
							return this.inventario.paracadute ?
								i18n.AvventuraNelCastelloJS.objects.parachute.onOpen.notHere :
								i18n.AvventuraNelCastelloJS.objects.parachute.onOpen.dontHaveIt
						}
					}

				},
				osso: {
					dlabel: i18n.AvventuraNelCastelloJS.objects.bone.dlabel,
					label: i18n.AvventuraNelCastelloJS.objects.bone.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.bone.pattern,
					posizione: "segretaCastello",
					status:0,
					on: {
						guarda: i18n.AvventuraNelCastelloJS.objects.bone.onLook,
						cerca: async () => {
							return this.inventario.osso == undefined ? 
								i18n.AvventuraNelCastelloJS.objects.bone.onLookFor.dontHaveIt :
								i18n.AvventuraNelCastelloJS.objects.bone.onLookFor.inYourHand;
						}
					}
				},

				mazza: {
					label: i18n.AvventuraNelCastelloJS.objects.bludgeon.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.bludgeon.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.bludgeon.description,
					posizione: "depositoAttrezzi",
					visibile: true
				},

				gatto: {
					dlabel: i18n.AvventuraNelCastelloJS.objects.cat.dlabel,
					label: i18n.AvventuraNelCastelloJS.objects.cat.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.cat.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.cat.description,
					posizione: "salone",
					status: 0,
					visibile: true,
					on: {
						prendi: async () => {
							if(this.inventario.gatto !== undefined)
								return i18n.AvventuraNelCastelloJS.objects.cat.onTake.alreadyIn;
							if(await this.canITakeThat(this.stanzaCorrente.objects.gatto)){
								this.stanzaCorrente.objects.gatto.status=1;
								this._aggiungiInInventario(this.stanzaCorrente.objects.gatto);
								return i18n.AvventuraNelCastelloJS.objects.cat.onTake.gotIt;
							}
							this.gameLoop(false);
							return false;
						},
						lascia: async () => {
							this.inventario.gatto.status=0;
							return null;
						},
						accarezza: async () => {
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.cat.onPet[0],{printDelay: 75, cr:false});
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.cat.onPet[1]);
						},
						'parla|saluta': i18n.AvventuraNelCastelloJS.objects.cat.onTalkOrGreet,
						uccidi: async() => {
							delete this.inventario.gatto;
							this.datiAvventura.objects.gatto.posizione = null;
							this.refreshOggettiInStanza();
							return i18n.AvventuraNelCastelloJS.objects.cat.onKill.join("\n");
						},
						nutri: async () => {
							if(this.inventario.coppaLattemiele == undefined){
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.cat.onFeed.nothingSuitable);
								return true;
							}
							switch(this.inventario.coppaLattemiele.status){
								case 0:
									for(let i=1; i<=10; i++){
										await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.cat.onFeed.lep,{printDelay:75});
									}
									await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.cat.onFeed.finished.join("\n"));
									this.inventario.coppaLattemiele.status = 1;
									break;
								case 1:
									return this.inventario.coppaLattemiele.EMPTY;
							}
						}
						
					},

				},

				spada: {
					dlabel: i18n.AvventuraNelCastelloJS.objects.sword.dlabel,
					label: i18n.AvventuraNelCastelloJS.objects.sword.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.sword.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.sword.description,
					posizione: "salaTrofei",
					status:0,
					visibile: false,
					on: {
						guarda: () => {
							if(this.inventario.spada){
								this.inventario.spada.status = 1;
								this.aggiungiPunti("vistoSortilegio");
								return i18n.AvventuraNelCastelloJS.objects.sword.onLook
							}
							return null;
						}
					},
					linkedObjects: ['sortilegio'],
					sortilegio: async (ask) =>{
						if(this.inventario.spada === undefined){
							if(this.datiAvventura.objects.spada.status == 0)
								return i18n.AvventuraNelCastelloJS.objects.sword.spell.dontKnow
						
							return i18n.AvventuraNelCastelloJS.objects.sword.spell.dontRemember
						}
						if(this.inventario.spada){
							if(this.inventario.spada.status == 0)
								return i18n.AvventuraNelCastelloJS.objects.sword.spell.dontKnow

							if(ask){
								let res = await this.yesNoQuestion(i18n.AvventuraNelCastelloJS.objects.sword.spell.question);
								if(res == false){
									this.gameLoop(false);
									return false;
								}
							}

							let pre = ask ? "\n" : "";

							if(this.stanzaCorrente.interactors.fantasma !== undefined && this.stanzaCorrente.interactors.fantasma.visibile){
								if(this.altriDati.golaSecca){
									await this.CRT.printTyping(pre+i18n.AvventuraNelCastelloJS.objects.sword.spell.fail.join("\n"), {printDelay: 100});
									this.die();
									return false;
								}

								this.stanzaCorrente.interactors.fantasma.neutralizzato = true;
								this.stanzaCorrente.interactors.fantasma.visibile = false;
								this.aggiungiPunti("eliminatoFantasma");
								return pre+i18n.AvventuraNelCastelloJS.objects.sword.spell.success;
							}

							return pre+this.Thesaurus.defaultMessages.NON_SUCCEDE_NIENTE;
						}
					}
				},

				sortilegio: {
					label: i18n.AvventuraNelCastelloJS.objects.spell.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.spell.pattern,
					posizione: null,
					on: {
						guarda: async () =>{
							if(this.inventario.spada && this.inventario.spada.status == 1){
								return i18n.AvventuraNelCastelloJS.objects.spell.onLook;
					
							}
							return this.Thesaurus.defaultMessages.QUI_NON_NE_VEDO;
						}
					}
				},
				
				coppaLattemiele: {
					dlabel: i18n.AvventuraNelCastelloJS.objects.milkAndHoneyCup.dlabel,
					label: i18n.AvventuraNelCastelloJS.objects.milkAndHoneyCup.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.milkAndHoneyCup.pattern,
					description: [
						i18n.AvventuraNelCastelloJS.objects.milkAndHoneyCup.description.full, 
						i18n.AvventuraNelCastelloJS.objects.milkAndHoneyCup.description.empty
					],
					posizione: "salaPranzo",
					status:0,
					visibile: true,
					linkedObjects: ["latteMiele"],
					EMPTY: i18n.AvventuraNelCastelloJS.objects.milkAndHoneyCup.EMPTY
				},

				latteMiele: {
					label: i18n.AvventuraNelCastelloJS.objects.milkAndHoney.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.milkAndHoney.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.milkAndHoney.description,
					posizione: null,
					bevuto: false,
					on: {
						bevi: async () => {
							if(this.inventario.coppaLattemiele){
								if(this.inventario.coppaLattemiele.status == 0){
									this.inventario.coppaLattemiele.status = 1;
									this.datiAvventura.objects.latteMiele.bevuto = true;
									this.altriDati.golaSecca = false;
									this.startTimedEvent("latteMiele");
									return i18n.AvventuraNelCastelloJS.objects.milkAndHoney.onDrink.success;
								} else {
									return this.inventario.coppaLattemiele.EMPTY;
								}
							} else {
								return i18n.AvventuraNelCastelloJS.objects.milkAndHoney.onDrink.fail;
							}
						},
						offri: async() => {
							// Gatto?
							if(this.stanzaCorrente.objects.gatto || this.inventario.gatto){
								let gatto = this.stanzaCorrente.objects.gatto ? 
									this.stanzaCorrente.objects.gatto :
									this.inventario.gatto;
								await gatto.on.nutri();
								return true;
							}

							if(this.stanzaCorrente.objects.fantasma)
								return i18n.AvventuraNelCastelloJS.objects.milkAndHoney.onOffer.toGhost;

							if(this.stanzaCorrente.objects.orco)
								return i18n.AvventuraNelCastelloJS.objects.milkAndHoney.onOffer.toOgre;

							return i18n.AvventuraNelCastelloJS.objects.milkAndHoney.onOffer.toWho;
						}
					}
				},

				liuto: {
					label: i18n.AvventuraNelCastelloJS.objects.lute.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.lute.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.lute.description,
					posizione: "salaMusica",
					visibile: true,
					on: {
						suona: i18n.AvventuraNelCastelloJS.objects.lute.onPlay
					}
				},

				arpa: {
					label: i18n.AvventuraNelCastelloJS.objects.harp.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.harp.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.harp.description,
					posizione: "salaMusica",
					visibile: true,
					on: {
						suona: i18n.AvventuraNelCastelloJS.objects.harp.onPlay
					}
				},

				cornamusa: {
					label: i18n.AvventuraNelCastelloJS.objects.bagpipe.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.bagpipe.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.bagpipe.description,
					posizione: "salaMusica",
					visibile: true,
					on: {
						suona: async () => {
							if(this.stanzaCorrente.key != 'cellaAlchimista'){
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.bagpipe.onPlay.fail[0]);
								await this.CRT.sleep(1500);
								await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.bagpipe.onPlay.fail[1]);
								return true;
							}
							if(this.stanzaCorrente.interactors.volume.aperto){
								await this.CRT.printTyping(this.Thesaurus.defaultMessages.ANCORA);
								return true;
							}
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.bagpipe.onPlay.success);
							this.aggiungiPunti("apertoVolume");
							this.stanzaCorrente.interactors.volume.aperto = true;
						}
					}
				},

				foglio: {
					label: i18n.AvventuraNelCastelloJS.objects.sheet.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.sheet.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.sheet.description,
					posizione: "biblioteca",
					visibile: false,
					on: {
						leggi: async () => {
							if(this.inventario.foglio === undefined){
								return i18n.AvventuraNelCastelloJS.objects.sheet.onRead.dontHaveIt;
							}
							this.altriDati.iotaid.iota = true;
							this.aggiungiPunti("lettoIota");
							return i18n.AvventuraNelCastelloJS.objects.sheet.onRead.success;
						}
					}
				},

				
				cuscino: {
					label: i18n.AvventuraNelCastelloJS.objects.cushion.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.cushion.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.cushion.description,
					posizione: "salaTrono",
					on: {
						"alza|spingi": async () => {
							this.scopri(this.datiAvventura.objects.astuccio);
							this.aggiungiPunti("trovatoAstuccio");
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.cushion.onLiftUp);
						},
						prendi: async () => {
							if(await this.canITakeThat(this.stanzaCorrente.objects.cuscino) == false){
								this.gameLoop(false);
								return false;
							}
							if( this.stanzaCorrente.objects.cuscino ){
								await this.stanzaCorrente.objects.cuscino.on['alza|spingi']();
								await this.CRT.println("");
							}
							return null;
						}
					}
					
				},

				astuccio: {
					label: i18n.AvventuraNelCastelloJS.objects.case.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.case.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.case.description,
					posizione: "salaTrono",
					visibile: false,
					peso: -1,
					on: {
						prendi: i18n.AvventuraNelCastelloJS.objects.case.onTake,
						svita: i18n.AvventuraNelCastelloJS.objects.case.onSkrewOff,
						apri: async () => {
							this.scopri(this.datiAvventura.objects.pergamena);
							this.aggiungiPunti("trovataPergamena");
							return i18n.AvventuraNelCastelloJS.objects.case.onOpen;
						}
					}
					
				},

				pergamena: {
					label: i18n.AvventuraNelCastelloJS.objects.scroll.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.scroll.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.scroll.description,
					posizione: "salaTrono",
					visibile: false,
					tradotta: false,
					on: {
						leggi: async () => {
							if(this.inventario.pergamena === undefined){
								return i18n.AvventuraNelCastelloJS.objects.scroll.onRead.dontHaveIt;
							}
							return i18n.AvventuraNelCastelloJS.objects.scroll.onRead.fail;
						},
						traduci: async () => {
							if(this.stanzaCorrente.interactors.dizionario && this.stanzaCorrente.interactors.dizionario.visibile === undefined){
								this.aggiungiPunti("tradottaPergamena");
								this.datiAvventura.objects.pergamena.tradotta = true;
								return i18n.AvventuraNelCastelloJS.objects.scroll.onTranslate.success.join("\n");
							}
							return i18n.AvventuraNelCastelloJS.objects.scroll.onTranslate.fail;
						}
					}
					
				},

				orco: {
					label: i18n.AvventuraNelCastelloJS.objects.ogre.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.ogre.pattern,
					posizione: "largoCunicolo",
					visibile: true,
					peso: -1,
					on: {
						guarda: async () => {
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.ogre.onLook[0]);
							await this.CRT.sleep(1800)
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.ogre.onLook[1]);
							return true;
						},
						uccidi: i18n.AvventuraNelCastelloJS.objects.ogre.onKill,
						nutri: i18n.AvventuraNelCastelloJS.objects.ogre.onFeed,
						"parla|saluta": async () => {
							await this.CRT.println(i18n.AvventuraNelCastelloJS.objects.ogre.onTalkOrGreet, {reversed:true});
							await this.s0();
							return true;
						}
					}
						
				},
				nano: {
					dlabel: i18n.AvventuraNelCastelloJS.objects.dwarf.dlabel,
					label: i18n.AvventuraNelCastelloJS.objects.dwarf.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.dwarf.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.dwarf.description,
					posizione: "legnaia",
					status: 0,
					visibile: true,
					peso: -1,
					on: {
						saluta: async() => {
							if(this.datiAvventura.objects.diamante.visibile)
								return i18n.AvventuraNelCastelloJS.objects.dwarf.onGreet.withoutDiamond;

							this.scopri(this.datiAvventura.objects.diamante);
							this.stanzaCorrente.objects.nano.status = 1;
							this.aggiungiPunti("salutatoNano");
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.dwarf.onGreet.withDiamond, {printDelay:80});
						},
						nutri: i18n.AvventuraNelCastelloJS.objects.dwarf.onFeed,
						uccidi: async () => {
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.objects.dwarf.onKill.join("\n"));
							this.die();
							return false;
						},
						parla: this.Thesaurus.defaultMessages.SII_PIU_SPECIFICO
					}
				},

				diamante: {
					label: i18n.AvventuraNelCastelloJS.objects.diamond.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.diamond.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.diamond.description,
					posizione: "legnaia",
					on: {
						prendi: async () => {
							if(this.datiAvventura.objects.nano.status == 0)
								return i18n.AvventuraNelCastelloJS.objects.diamond.MINE;
							
							if(await this.canITakeThat(this.stanzaCorrente.objects.diamante) == false){
								this.gameLoop(false);
								return false;
							}
							
							this._aggiungiInInventario(this.datiAvventura.objects.diamante);
							await this.CRT.printTyping(this.Thesaurus.defaultMessages.FATTO);
							this.aggiungiPunti("presoDiamante");							
						},
						rompi: async () => {
							if(this.datiAvventura.objects.nano.status == 0)
								return i18n.AvventuraNelCastelloJS.objects.diamond.MINE;
							if(this.inventario.mazza === undefined)
								return i18n.AvventuraNelCastelloJS.objects.diamond.onBreak.needSomethingHard;
							delete this.inventario.diamante;
							this.datiAvventura.objects.diamante.posizione = null;
							this.datiAvventura.objects.chiave.posizione = this.stanzaCorrente.key;
							this.scopri(this.datiAvventura.objects.chiave);

							this.aggiungiPunti("trovataChiave");
							return i18n.AvventuraNelCastelloJS.objects.diamond.onBreak.success;
						},

						guarda: async () => {
							if(this.datiAvventura.objects.diamante.visibile)
								return i18n.AvventuraNelCastelloJS.objects.diamond.onLook;
							return null;
						}


					}
				},

				chiave: {
					label: i18n.AvventuraNelCastelloJS.objects.key.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.key.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.key.description,
					posizione: null,
					visibile: false
				},

				corno: {
					label: i18n.AvventuraNelCastelloJS.objects.horn.label,
					pattern: i18n.AvventuraNelCastelloJS.objects.horn.pattern,
					description: i18n.AvventuraNelCastelloJS.objects.horn.description,
					posizione: "cameraTesoro",
					visibile: false,
					on: {
						suona: async() => {
							if(this.stanzaCorrente.key == "cimaTorre"){
								this.aggiungiPunti("presoDaAquila");
								this.runSequence("aquila");
								return false;
							}
							
							if(this.stanzaCorrente.key == "scoglio"){
								this.runSequence("finale");
								return false;
							}
							
							return i18n.AvventuraNelCastelloJS.objects.horn.onPlay
						}
					}
				}

			},

			/* SEQUENZE */
			sequenze:{
				titolo: async () => {
					this.CRT.clear();
					await this.CRT.sleep(1000);
					await this.CRT.println("    |>                    |>",{m: true});
					await this.CRT.println("    |                     |",{m: true});
					await this.CRT.println("   / \\     _   _   _     / \\",{m: true});
					await this.CRT.println("  /   \\   | |_| |_| |   /   \\",{m: true});
					await this.CRT.println(" /  #  \\  |         |  /  #  \\",{m: true});
					await this.CRT.println("/=======\\ |   <#>   | /=======\\",{m: true});
					await this.CRT.println("  |   |___|         |___|   |",{m: true});
					await this.CRT.println("  |   |       ###       |   |",{m: true});
					await this.CRT.println("  |   |      #####      |   |",{m: true});
					await this.CRT.println("  |   |      #####      |   |",{m: true});
					await this.CRT.println("===============================",{m: true});
					bridge.revOn(); await this.CRT.print(i18n.title); bridge.revOff(); await this.CRT.println("");
					await this.CRT.println("===============================\n",{m: true});
					await this.CRT.wait();
					let i=0;
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++], {nlAfter: 1});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++], {nlAfter: 1});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.title[i++], {nlAfter: 1});
					await this.CRT.wait();
				},
				prologo: async () => {
					this.CRT.clear();
					let i=0;
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.prologue[i++],{reversed:true, nlBefore:2});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.prologue[i++]);
					await this.CRT.sleep(1000);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.prologue[i++]);
					await this.CRT.wait();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.prologue[i++]);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.prologue[i++],{nlBefore: 1});
					await this.CRT.wait();
				},
				intro: async () => {
					await this.CRT.clear();
					let i=0;
					await this.CRT.print("     ");
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.intro[i++],{reversed:true, nlAfter: 1});

					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.intro[i++]);
					await this.CRT.sleep(1500);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.intro[i++]);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.intro[i++],{nlAfter:1});
					this.startTimedEvent("aereo");
				},
				paracadute: async () => {
					if(this.inventario.paracadute == undefined){
						this._aggiungiInInventario(this.datiAvventura.objects.paracadute);
						this.aggiungiPunti("paracaduteIndossato");
						let i=0;
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.parachute[i++]);
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.parachute[i++]);
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.parachute[i++],{nlBefore:1});
						return true;
					}
					return this.Thesaurus.defaultMessages.GIA_ADDOSSO
				},
				volo: async () => {
					let i=0;
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++]);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++]);
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:25});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:25});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:100});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:100});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:100});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i++],{printDelay:1});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.fly.string[i].replace(i18n.AvventuraNelCastelloJS.sequences.fly.reversed,`<span class='reversed'>${i18n.AvventuraNelCastelloJS.sequences.fly.reversed}</span>`),{printDelay:1,nlAfter:1});
					//await this.CRT.println("                 ===========",{printDelay:1});
					await this.s0();
					await this.CRT.wait();
					this.die();
				},
				saltoAereo: async () => {
					let i=0;
					
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.jumpFromPlane[i++]);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.jumpFromPlane[i++]);
					await this.CRT.sleep(1500);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.jumpFromPlane[i++],{printDelay:75});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.jumpFromPlane[i++],{printDelay:75,nlAfter:1});
					await this.CRT.sleep(1000);
					await this.entra('piazzaArmi');
				},
				braccio: async () => {
					let answer = await this.yesNoQuestion(i18n.AvventuraNelCastelloJS.sequences.arm.question);
					if(answer){
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.arm.answer[0],{nlBefore: 1});
						await this.s0();
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.arm.answer[1],{printDelay:75});
						this.die();
						return false;
					}
					this.gameLoop(false);
					return false;
				},
				segretaCastello: async(mSubjects) => {
						
					let cosa = mSubjects[0];
					let target = mSubjects[1] === undefined ? this.stanzaCorrente.interactors.foro : mSubjects[1];
					
					
					if(cosa == this.stanzaCorrente.interactors.braccio && target == this.stanzaCorrente.interactors.foro) 
						return await this.runSequence("braccio"); 
								
					if(cosa == this.inventario.osso && target == this.stanzaCorrente.interactors.foro){

						if(this.inventario.osso.status == 0){

							this.inventario.osso.status = 1;
							this.aggiungiPunti("apertaFessura");
							let i=0;
							await this.CRT.printTyping (i18n.AvventuraNelCastelloJS.sequences.castleDungeon.success[i++],{cr: false});
							await this.s0();
							await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.castleDungeon.success[i++]);
							await this.CRT.sleep(1000);
							await this.CRT.printTyping (i18n.AvventuraNelCastelloJS.sequences.castleDungeon.success[i++],{printDelay:200});
							delete this.stanzaCorrente.interactors.fessura.visibile;
							this.abilitaDirezione("o");
						} else {
							await this.CRT.printTyping (this.Thesaurus.defaultMessages.ANCORA);
						}
						
						return true;	

					}

					if(this.inventario[cosa.key] === undefined)
						return null;
				
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.castleDungeon.fail);
				},
				ritratti: async () => {
					await this.CRT.printTyping (i18n.AvventuraNelCastelloJS.sequences.portrait[0]+"\n"+i18n.AvventuraNelCastelloJS.sequences.portrait[1],{nlBefore:1, printDelay:150});
					await this.CRT.printTyping (i18n.AvventuraNelCastelloJS.sequences.portrait[2]);
					await this.CRT.wait();
					this.entra('salaSpecchi');
				},
				mangia: async () => {
					let i=0;
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eat[i++],{cr:false});
					await this.CRT.sleep(1500);
					await this.CRT.printTyping("@*!+#*",{printDelay:160});
					await this.CRT.sleep(1500);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eat[i++],{nlAfter: 1});
					await this.CRT.printTyping("    ~_~_~_~_~_~     \\/\\/\\/\\/\\/     {*} {*} {*}     =|=|=|=|=     >><<>><<     ",{printDelay:160, reversed:true, nlAfter:1});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eat[i++],{printDelay:160});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eat[i++]);
				},
				aquila: async() => {
					let i=0;
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eagle[i++],{printDelay:120});
					await this.CRT.sleep(2400);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eagle[i++],{printDelay:120});
					await this.CRT.sleep(1800);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eagle[i++],{printDelay:120});
					await this.fakeInput();
					await this.s3();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eagle[i++],{nlBefore:2});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eagle[i++]);
					await this.CRT.sleep(2400);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eagle[i++],{printDelay:30, cr:false, nlBefore:1});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.eagle[i++]);
					await this.CRT.wait();
					this.runSequence(this.inventario.paracadute ? "scoglio" : "ancora");
				},
				scoglio: async () => {
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.rock[0],{printDelay:75});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.rock[1],{printDelay:75, nlAfter:1});
					await this.CRT.sleep(1000);
					await this.entra('scoglio');
				}, 
				ancora: async () => {
					let i=0;

					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:120});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:75});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:25});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:25});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:50});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:100});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:100});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:100});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++],{printDelay:1});
					await this.CRT.println(i18n.AvventuraNelCastelloJS.sequences.again.string[i++].replace(i18n.AvventuraNelCastelloJS.sequences.again.reversed,`<span class='reversed'>${i18n.AvventuraNelCastelloJS.sequences.again.reversed}</span>`,{printDelay:1}));
					await this.s0();

					await this.CRT.wait();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.again.string[i], {nlBefore:1});
					this.die();
				},
				finale: async () => {
					let i=0;
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:120});
					await this.CRT.sleep(2400);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:120});
					await this.CRT.sleep(1800);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:120});
					await this.CRT.wait();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30,nlBefore:1});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30,nlBefore:1});
					await this.CRT.wait();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30});
					await this.CRT.sleep(1800);
					for(let j=1; j<=12; j++){
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30, nlBefore: j == 2 || j == 12 ? 1: 0});
					}
					await this.CRT.wait();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++], {printDelay:30});
					await this.CRT.wait();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30, cr:false});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30, cr:false, reversed: true});
					await this.s0();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30});
					await this.CRT.wait();
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30, cr:false});
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30, cr:false, reversed: true});
					
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:30});
					await this.CRT.sleep(1800);
					await this.CRT.printTyping("    ",{cr:false, nlBefore:1});
					let ldd = i18n.AvventuraNelCastelloJS.sequences.final[i++];

					for(let i in ldd){
						await this.CRT.printTyping(ldd[i], {blinkingText:true, cr:false});
						await this.s0();
					}

					await this.CRT.sleep(1800);
					await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.sequences.final[i++],{printDelay:120,nlBefore:2});
					await this.s0(2);
					await this.CRT.wait();
					await this.CRT.clear();
					this.reload(this.datiIniziali);
					await this.run();
				}
			},
			timedEvents: {
				aereo: {
					start: 7,
					onLimit: async () => {
						await this.CRT.sleep(500);
						await this.CRT.println(i18n.AvventuraNelCastelloJS.timedEvents.plane[3],{blinking:true, nlBefore: 1});
						await this.s0(2);
						this.die();
					},
					steps: {
						3: async () => this.CRT.printTyping(i18n.AvventuraNelCastelloJS.timedEvents.plane[0],{nlBefore:1}),
						2: async () => this.CRT.printTyping(i18n.AvventuraNelCastelloJS.timedEvents.plane[1],{nlBefore:1}),
						1: async () => this.CRT.printTyping(i18n.AvventuraNelCastelloJS.timedEvents.plane[2],{nlBefore:1}),
					}
				},
				whisky: {
					start: 1,
					steps: {
						1: async () => {
							this.datiAvventura.timedEvents.whisky.currentStep = 2
							let n = this.getRandomIntInclusive(1,3);
							if(n == 1){
								this.CRT.println(i18n.AvventuraNelCastelloJS.timedEvents.whiskey,{reversed:true, nlBefore: 1});
								await this.s0();
							}
						}
					}
				},
				latteMiele: {
					start: 2,
					onLimit: async () => {
						this.altriDati.golaSecca = true;
						return true;
					}
				},
				nessie: {
					start:3,
					onLimit: async () => {
						await this.CRT.print(i18n.AvventuraNelCastelloJS.timedEvents.nessie[2],{reversed:true, nlBefore: 1});
						await this.CRT.printTyping(i18n.AvventuraNelCastelloJS.timedEvents.nessie[3]);
						this.die();
						return false;
					},
					steps: {
						2: async () => this.CRT.printTyping(i18n.AvventuraNelCastelloJS.timedEvents.nessie[0],{nlBefore:1}),
						1: async () => this.CRT.printTyping(i18n.AvventuraNelCastelloJS.timedEvents.nessie[1],{nlBefore:1}),
					}
				}
			}
		}
	}
}
