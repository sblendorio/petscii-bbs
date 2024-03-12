var i18n = {
	htmlTitle: `Castle Adventure in JavaScript`,
	title: `CASTLE ADVENTURE  IN JAVASCRIPT`,
	CRT: {
		waitText: `Press enter to continue...`
	},
	specificMaze: {
		sennoSsenseN: 'n',
		sennoNsenseS: 's',
		sennoEsenseO: 'o',
		sennoOsenseE: 'e'
	},
	IFEngine: {
		warnings: {
			mustBeExtended: `The IFEngine must be extended.`,
			notLoaded: `No adventure loaded.`,
			localstorageInactive: `To save, enable local storage in your browser.`,
			localstorageMustBeActivated: `Enable local storage in your browser.`,
			labelNotValid: `Label not valid. Try again.`,
			doYouWantToOverwrite: `File already present. Overwrite it? `,
			saveAborted: `Save operation aborted.`,
			notExistingFile: `Error: not existing file`,
			noData: `No data to load...`,
			notFound: (filename) => `Save "${filename}" not found.`
		},
		menu: {
			choose: `Do you want to:`,
			new: `Start a new adventure`,
			load: `Resume a saved game`,
			readInstructions: `Review the instructions`,
			quit: `Exit the game`,
			restart: `Start over from the beginning`,
			stop: `Stop playing`
		},
		questions: {
			stopQuestion: `Do you want to stop playing`,
			areYouSureQuestion: `Are you sure`,
			saveLabel: `Save label (X cancel):`,
			restoreLabel: `Label (X cancel, L for list):`,
			cancelLetter: `X`,
			listLetter: `L`,
			what: `what?`
		},
		yesOrNo:{
			yes: `yes`,
			no: `no`
		},
		messages: {
			tanksForPlaying: `Thank you for playing. Bye for now! :)`,
			saved: `Data saved!`,
			loaded: `Data loaded...`,
			noInstructions: `No instructions here...`,
			death: `YOU'RE DEAD!!!`,
			noPoints: `This adventure is unscored.`,
			points: (points, maxPoints) => `You have earned ${points} points out of ${maxPoints}.`,
			noObjects: `You don't have any items with you.`,
			carriedObjectsLabel: `You currently have:`,
			alreadyHaveIt: `You already have it.`
		},
		questionMark: `?`
	},
	Thesaurus: {
		defaultMessages: {
			done: `Done!`,
			preferNot: `Gonnae no dae that. I'd prefer not.`,
			notFound: `Nothing found.`,
			didNotUnderstand: `I don't understand...`,
			dontNoticeAnythingInParticular: `I cannae see anything in particular.`,
			notSeenHere: `I cannae see any here.`, 
			dontHaveAny: `You don't have any.`, 
			nothingHappens: `Nothing is happening.`,
			beMoreSpecific: `Be more specific.`,
			notPossible: `Can't.`
		},
		commands: {
			north: {
				pattern: `(go towards |go to |go )?(n(orth)?)`,
				defaultMessage: `You can't go north.`
			},
			south: {
				pattern: `(go towards |go to |go )?(s(outh)?)`,
				defaultMessage: `You can't go south.`
			},
			east: {
				pattern: `(go towards |go to |go )?(e(ast)?)`,
				defaultMessage: `You can't go east.`
			},
			west: {
				pattern: `(go towards |go to |go )?(w(est)?)`,
				defaultMessage: `You can't go west.`
			},
			up: {
				pattern: `(go )?(u(p)?)`,
				defaultMessage: `You can't go up.`
			},
			down: {
				pattern: `(go )?(d(own)?)`,
				defaultMessage: `You can't go down.`
			}
		},
		verbs: {
			open: {
				pattern: `open`,
				defaultMessage: `It won't open.`
			},
			close: {
				pattern: `close`,
				defaultMessage: `It won't close.`
			},
			pull: {
				pattern: `pull`,
			},
			press: {
				pattern: `press`
			},
			push: {
				pattern: `push`,
				defaultMessage: `It won't budge.`
			},
			take: {
				pattern: `take`
			},		
			drop: {
				pattern: `leave`
			},
			give: {
				pattern: `(give) (.+) (?:to|for) (.+)`,
			},
			look: {
				pattern: `(look (?:at )?|examine)`
			},
			useWith: {
				pattern: `(use) (.+) (?:with) (.+)`,
				defaultMessage: `I can't use them together.`
			},
			use:{
				pattern: `use`
			},
			lookFor:{
				pattern: `(look for|search)`
			},
			goUp: {
				pattern: `go up`
			}, 
			goDown: {
				pattern: `go down`
			}
		}
	},
	AvventuraNelCastelloJSEngine: {
		warnings: {
			mustBeExtended: `The AvventuraNelCastelloEngine must be extended.`
		},
		defaultInput: `What are you going to do?`,
		prefixLabels: {
			ISee: `I can see`,
			cantSeeHere: `I can't see here`,
			youDontOwn: `You don't have`,
			title: `You have the right to brag the title of:`
		},
		pointsLabel: [
			`Poor Excuse for an Adventurer`,
			`Village Idiot`,
			`Serf`,
			`Vile Mechanic`,
			`Deputy Stable Hand`,
			`Roundhead`,
			`Archbishop of Canterbury`,
			`Mumpsimus`,
			`Baron in the Trees`,
			`Cloven Viscount`,
			`Wee Scunner`
		],
		menuOption4LabelOverride: `Stop before you even start`,
		commonPatterns: {
			say: `say`,
			wall: `wall(?:s)?( of stone)?`
		},
		defaultMessages: {
			beSerious: `Be serious!`,
			notUseful: `That's no use.`,
			alreadyHaveIt: `You already have it.`,
			inYourHand: `You have it in your hand.`,
			wearing: `You already have it on you.`, 
			didNotUnderstand: `- I don't understand.`,
			again: `What else are you hoping to achieve?`, 
			youDontKnow: `You don't know this word.`,
			isOpened: `It's already open.`,
			isClosed: `It's closed.`, 
			notFound: `He who seeks finds.`,
		},
		messages: {
			huh: `Huh?`,
			somethingSensible: `Yer bum's oot the windae. Tell me something that makes sense.`,
			dontBeFormal: `Be more direct, please.`,
			overloaded: `You've already got too much stuff, you'll have to leave something behind.`,
			points: (points, maxPoints) => `You earned a hard-won ${points} points, out of a maximum of ${maxPoints}.`,
			tough: `Too bad!`
		},
		verbs: {
			look: {
				pattern: `(look(?: at)?|observe|examine)`
			},
			drop: {
				pattern: `(leave|put|drop|throw)`
			},
			press: {
				pattern: `press`
			},
			push: {
				pattern: `(push|move|budge)`
			},
			offer: {
				pattern: `(offer|give)`
			},
			repair: {
				pattern: `(fix|repair)`					
			},
			translate: {
				pattern: `translate`					
			},
			play: {
				pattern: `(play|sound)`					
			},
			enter: {
				pattern: `enter(?: in)?`,
				defaultMessage: `Which way? (N/S/E/W/U/D)`					
			},
			wear: {
				pattern: `(put on|wear)`,
			},
			liftUp: {
				pattern: `(raise|lift)`,
				defaultMessage: `There's nothing underneath.`
			},
			lower: {
				pattern: `lower`,
				defaultMessage: `It won't go down.`
			},
			take: {
				pattern: `(take|steal|grab)`
			},
			read: {
				pattern: `read`
			},
			insert: {
				pattern: `(put in|insert)`
			},
			insertInto: {
				pattern: `(put|insert|stick) (.+) (?:in) (.+)`
			},
			pray: {
				pattern: `pray`,
				defaultMessage: `  God helps those who help themselves.`
			},
			land: {
				pattern: `(land|climb|glide|dive|turn|maneuver|manoeuvre)`,
				defaultMessage: `Couldn't be more grounded!`
			},
			jump: {
				pattern: `(jump)(?: .+)?`,
				defaultMessage: `I'm fit enough as it is, thanks.`
			},
			sitDown: {
				pattern: `(sit|lie down)(?: on)?(.+)?`,
				defaultMessage: `A little rest is always good.`
			},
			greet: {
				pattern: `greet`,
				defaultMessage: `No reply.`
			},
			dig: {
				pattern: `dig`,
				defaultMessage: `I'm not cut out for such menial work.`
			},
			eat: {
				pattern: `eat`,
				defaultMessage: `Doesn't seem very appetising.`
			},
			knock: {
				pattern: `knock`,
				defaultMessage: `No reply.`
			},
			thank: {
				pattern: `(thanks|thank you)`,
				defaultMessage: `You're welcome.`
			},
			wait: {
				pattern: `wait`,
				defaultMessage: `Alright`
			},
			talk: {
				pattern: `(speak(?: with)?|question)`,
				defaultMessage: `Haud yer wheesht. Is it not better to remain silent and be thought a fool, than to speak and to remove all doubt?`
			},
			listen: {
				pattern: `listen`,
				defaultMessage: `Remaining still, with your ears pricked up, you seem to hear a distant noise like chains being dragged. But maybe it's a trick played on your mind by the whistling of the wind.`
			},
			buy: {
				pattern: `(buy|purchase)`,
				defaultMessage: `You haven't a penny.`
			},
			break: {
				pattern: `(break|split|divide|shatter|destroy|break through|tear)`
			},
			drink: {
				pattern: `drink`
			},
			wind: {
				pattern: `wind(?: up)?`
			},
			kill: {
				pattern: `(kill|attack|hit|wound|kill|beat)`,
			},
			feed: {
				pattern: `(feed|give food)`
			},
			pet: {
				pattern: `(pet|stroke)`
			},
			mount: {
				pattern: `(assemble|reassemble|build|rebuild)`
			},
			ask: {
				pattern: `(ask|question)`,
				defaultMessage: `No one's willing to give you what you desire.`
			},
			askTo: {
				pattern: `(ask) (.+) (?:for) (.+)`,
				defaultMessage: `No one's willing to give you what you desire.`
			},
			skrewOff:{
				pattern: `unscrew`
			},
			hello: {
				pattern: `hello`
			},
			greeting: {
				pattern: `(good day|g'day|good morning|guid mornin|morning|mornin|good evening|guid evenin|evening|good afternoon|guid efternuin|afternoon|goodnight|guid nicht|night)`,
			}

			
		},
		commands: {
			where: {
				pattern: `(where|look(?: at)?|observe|examine)( (room|chamber|hall|floor|ceiling|place|spot))?`
			},
			points: {
				pattern: `(points|how many)`,
			},
			stop: {
				pattern: `(enough|stop|end|quit|finish)`,
				defaultMessage: `I'm sorry you wanna give up, just when you were getting somewhere...`					
			},
			instructions: {
				pattern: `instructions`
			},
			inventory: {
				pattern: `(what|inv(?:en(?:tory)?)?|\\?)`
			},
			save: {
				pattern: `(save|record)`
			},
			load: {
				pattern: `(load|resume)`
			},
			insult: {
				pattern: `(fool|idiot|nincompoop|pillock|imbecile|nitwit|twit|blockhead|dunce|moron|arse|ass|fuck off|fuck you|eff off|bastard|basturt|clatty basturt|clarty basturt|douchebag|douche)`					
			},
			help: {
				pattern: `(help|sos)`,
				defaultMessage: `You'll manage!`
			},
			call: {
				pattern: `(call|shout|scream)(?: (.+))?`,
				defaultMessage: `You hear a distant cry in response, and it takes you a few seconds to realize that it's just the echo of your cracked voice.`
			},
			cry: {
				pattern: `cry`,
				defaultMessage: `Now you've let off some steam, pull your socks up!`
			},
			moves: {
				pattern: `(turns|moves)`,
				defaultMessage: (moves) => `You have now taken ${moves} turns.`
			},
			idiot: {
				pattern: `(id|iot)`,
				defaultMessage: `Ye dinnae unnerstaun`
			},
			abracadabra: {
				pattern: `abracadabra`,
				defaultMessage: `What? You're gonna pull a rabbit out of a hat?`

			},
			die: {
				pattern: `(die|drop dead|push daisies|eat it|count worms|go west|kick the bucket|cash in yer chips|kick the bucket)`,
				defaultMessage: `Alright.`					
			},
			think: {
				pattern: `(think|reason|cogitate|meditate|deduce|engineer)`,
				defaultMessage: `Doesn't seem the place.`
			},
			getOut: {
				pattern: `(exit(?: from)?|run|get out|escape|walk|return|go back|go)( (?:to )?(?:n(orth)?|s(outh)?|e(ast)?|w(est)?|u(p)?|d(own)?))?`,
				defaultMessage: `Ay dinnae ken, which way? (N/S/E/W/U/D)`
			},
			sleep: {
				pattern: `(sleep|rest)`,
				defaultMessage: `Z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z z`
			},
			maybe: {
				pattern: `(er|dunno|don't know|maybe|probably|possibly)`,
				defaultMessage: `Make up your mind!`
			},
			good: {
				pattern: `good`,
				defaultMessage: `Thanks!`
			},
			youAreWelcome: {
				pattern: `you're welcome`,
				defaultMessage: `Not at all.`
			},
			openSesame: {
				pattern: `open sesame`,
				defaultMessage: `Look, this is CASTLE ADVENTURE, not THE ARABIAN NIGHTS.`
			},
			waitForMidnight:{
				pattern: `wait (?:'til )?midnight`,
				defaultMessage: `Time really stands still when you're waiting for something.`
			},
			sayHello: {
				pattern: `greet`,
			},
			greeting: {
				defaultMessage: `Hullo. Bonny day, isn't it?`
			},
			hello: {
				defaultMessage: `Hullo. Bonny day, isn't it?`
			},
			senno: {
				pattern: (sayPattern) => `(?:${sayPattern} )?sense`,
				defaultMessage: `It's not a magic word, stupid!`
			},
			useSenno: {
				pattern: `use sense`,
				defaultMessage: `Doesn't seem the place!`
			},
			lookForDictionary: {
				pattern: `search dictionary`,
				defaultMessage: `Don't expect me to look for one now!`
			},
			saySpell: {
				pattern: (sayPattern) => `(${sayPattern} )?(spell)`
			},
			introduceYourself: {
				pattern: `introduce yourself`,
				defaultMessage: `You profusely list your numerous honorifics, but no-one seems to be listening...`
			},
			yes: {
				pattern: `(yes|aye|certainly|for sure|sure|surely)`,
				defaultMessage: `Or maybe not.`
			},
			no: {
				pattern: `(no|nah|nae|naw|never)`,
				defaultMessage: `Or maybe yes.`
			},
			bigmeow: {
				pattern: (sayPattern) => `(${sayPattern} )?bigmeow`,
				defaultMessage: {
					prelude: [
						`The cat grows until it becomes huge.............`,
						`It watches you carefully.............`
					],
					success: [
						`observe the ogre carefully..........`,
						`The cat devours the ogre and dies of indigestion.`,
					],
					fail: `and devours you.`
				}
					
			},
			iotid: {
				pattern: (sayPattern) => `(${sayPattern} )?iotid`,
			},
			readSpell: {
				pattern: `read (spell)`,
			},
			swim: {
				pattern: `swim`
			},

		},
		dieText: `I am utterly saddened by your untimely passing... After all, the best are always the first to go, right? Anyway, console yourself knowing that:`,
		instructions: [
			`Your main goal is to get out of the castle alive.`,
			`To succeed, you will have to face many dangers, and solve problems that will put your wits to the test.`,
			`In this adventure, I will be your alter ego, your eyes and your ears, but you will have to make the decisions (and suffer the consequences).`,
			`To move around use:`,
			`- NORTH, SOUTH, EAST, WEST, UP, DOWN, or just:`,
			`- N, S, E, W, U, D`,
			`I will give you the full description of each place the first time you enter it, then I will only give you a short description. If you want the full description tell me:`,
			`- LOOK or`,
			`- LOOK AT THE ROOM`,
			`Basic actions include:`,
			`- TAKE something`,
			`- LEAVE something`,
			`- LOOK AT something, for example, LOOK AT THE STAIRCASE.`,
			`I'm not very smart, so use phrases like OPEN THE DOOR, OPEN DOOR, or JUMP, and not elaborate phrases like LOOK BEHIND THE SOFA, or adverbs like LOOK CAREFULLY, which are beyond my comprehension.`,
			`To use an object, you usually need to have it on you. Also, remember that an action that has no effect in one place (e.g. SEARCH) may have an effect somewhere else.`,
			`Other important commands include:`,
			`- WHERE will remind you where you are`,
			`- WHAT will list the items you have with you`,
			`- TURNS will tell you how many turns you have taken so far`,
			`- POINTS will tell you have many points you have earned so far`,
			`- SAVE will save your game for later`,
			`- LOAD can be used to load a saved game`,
			`- STOP will end the game`,
			`- INSTRUCTIONS will give you this summary again`,
			`Best of luck to you! (you'll need it)`

		],
		insult: {
			toMe: (insult) => ` ${insult}? TO ME????`,
			nowYourTurn: `I'LL SHOW YOU!!!!`,
			fuck: ` Take that!`
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
				pattern: ` (the|a|an) `,
				replaceWith: ` `
			},
			{
				pattern: ` +`,
				replaceWith: ` `
			},
			{
				pattern: ` (inside|into) `,
				replaceWith: ` in `
			},
			{
				pattern: ` (above|over) `,
				replaceWith: ` on `
			},
			{
				pattern: ` among `,
				replaceWith: ` between `
			},
		]
	},
	AvventuraNelCastelloJS: {
		commonInteractors: {
			steps: {
				label: `the steps`,
				pattern: `step(?:s)?`,
			},
			stairs: {
				label: `the staircase`,
				pattern: `(stair(?:s)?|staircase)`,
				description: `It has steps.`,
			},
			walls: {
				label: `the walls`,
				onBreak: `The wall is hard.`
			},
			armor: {
				label: `the armour`,
				pattern: `armo(?:u)?r`,
				description: `Every suit of armour seems to look down on you haughtily.`,
				onTakeOrWear: `You're kidding, right? It weighs twice as much as you. In those days, they weren't wimps like today.`
			},
			hallway: {
				label: `the hallway`,
				pattern: `(hall(?:way)?|corridor)`,
			},
			skeletons: {
				label: `the skeletons`,
				pattern: `(skeleton(?:s)?|bones)`,
				description: `It is our common fate. But can't you think of something happier?`,
			},
			labyrinth: {
				label: `the maze`,
				pattern: `(maze|labyrinth|passage(?:s)?)`,
				description: `Seems easy! In this confuddling criss-crossing of inclined planes, along impossible curves, the floor can't be distinguished from the ceiling, and all directions seem the same.`
			},
			door: {
				label: `the door`,
				pattern: `door`
			},
			table: {
				label: `the table`,
				pattern: `table`,
			},
			bows: {
				label: `the bows`,
				pattern: `bow(?:s)?`,
			},
			shields: {
				label: `the shields`,
				pattern: `shield(?:s)?`,
			},
			fireplace: {
				label: `fireplace`,
				pattern: `fire(?:place)?|hearth`
			},
			armchairs: {
				label: `the armchairs`,
				pattern: `armchair(?:s)?`
			},
			chairs: {
				label: `the chairs`,
				pattern: `chair(?:s)?`
			},
			monster: {
				label: `the monster`,
				pattern: `(monster(?:s)?|nessie)`
			},
			shelves: {
				label: `the shelves`,
				pattern: `shel(?:f|ves)`
			},
			bed: {
				label: `the bed`,
				pattern: `(?:four-poster )?bed`,
			},
			weapons: {
				label: `the weapons`,
				pattern: `weapon(?:s)?`,
			},
			fog: {
				label: `the fog`,
				pattern: `fog`,
				description: `It's difficult to focus. You don't understand whether the grey blur you're staring at is ten meters away, or ten centimetres from your nose. The howls reaching you make a muffled sound, but are no more reassuring for that.`
			},
			crows: {
				label: `the crows`,
				pattern: `crow(?:s)?`,
				description: `They are completely invisible, lost somewhere in the haze. You can only hear a screeching sound coming from an indefinite point above your head.`,
				onListen: `They croak laughingly at your pointless and ridiculous efforts, or at least that's your impression.`
			},
			ramparts: {
				label: `the ramparts`,
				pattern: `(?:stone )?(wall(?:s)?|rampart(?:s)?)`,
				description: `Looking down from the ramparts, the fog makes you feel dizzy. You can't make out anything solid that will give you an idea how high you are. But you can't be up very high, judging by the spiral staircase you climbed to get up here.`
			},
			pass: {
				label: `the passage`,
				pattern: `passage(?:s)?`
			},
			tunnel: {
				label: `the tunnel`,
				pattern: `tunnel`
			},
			rock: {
				label: `the rock`,
				pattern: `rock`
			},
			tower: {
				label: `the tower`,
				pattern: `tower`
			}
		},
		commonRooms:{
			spiralStaircase: {
				description: `You are in a room with a spiral staircase.`,
			},
			ramparts: {
				description: [
					`You are on the ramparts of the castle. Your visibility is greatly reduced by the fog rising from the peatland in broad, slow spirals. You hear, without seeing them, the cawing of the crows circling above you.`,
					`A spiral staircase leads downwards.`
				],
				shortDescription: `You are on the ramparts of the castle.`,
				onJump: [
					`Like a cat, it appears you have nine lives.`,
					`The wolves are happy to have some fresh meat.`
				]
			},
			labyrinth: {
				description: `You are in the maze.`,
				onDrop: `Paths of a twisted gravity snake away in front and behind.`,
				onGetOut: `Who are you kidding?! You think you'll succeed where so many have failed?`,
				onThink: [
					`Let's see...`,
					`Mmmm...`,
					`Mmmm...`,
					`Not getting anywhere.`,
				],
				onThinkQuestion: `Should you think about it some more`
			}
		},
		rooms: {
			plane: {
				description: `You're plummeting!`,
				directions: {
					down: `You're already in a dive, and coming down fast:`
				},
				commands: {
					help: [
						`You dial in the distress frequency on the on-board radio, and launch an S.O.S.`,
						`For a few seconds, complete silence...`,
						`Then you receive a recorded message loud and clear:`,
						`- We would like to inform all air rescue users that this service is temporarily suspended due to a strike.`,
						`Instead, please recite with me:`,
						`- Our Father who art in heaven...`
					]
				},
				verbs: {
					jump: `You're not wearing a parachute.`,
					look: `There's too much smoke!`,
					land: `Can't, the controls have stopped responding!`
				},
				interactors: {
					cloche: {
						label: `the joystick`,
						pattern: `(joystick|(?:control|center|centre) stick)`,
						onPullOrPush: [
							`The controls respond a little!`,
							`...just enough to make the situation worse.`
						]					
					},
					engine: {
						label: `the engine`,
						pattern: `(engine|motor)`,
						onRepair: `Haven't a clue what to do with it.`
					},
					plane: {
						label: `the plane`,
						pattern: `(plane|airplane|aeroplane|aircraft)`
					}

				}
			},
			paradeGround: {
				description: [
					`You're on the parade ground: a vast, square, beaten-earth clearing, surrounded by high, grey stone walls.`,
					`In the center of the courtyard, a massive slab covers the mouth of the castle's well.`,
					`In the distance, you can hear the howling of wolves.`
				],
				shortDescription: [
					`You're on the parade ground.`,
					`You hear wolves howling.`
				],
				directions: {
					north: {
						success: `The door slams shut, without leaving the slightest crack.`,
						fail: `The door is closed.`
					},
					south: `You can't, the bridge is up.`,
					up: `In case you didn't know, a parachute only works on the way down.`
				},
				interactors: {
					drawbridge: {
						label: `a raised drawbridge`,
						pattern: `(?:draw)?bridge`,
						description: `It's raised, but you can lower it without any difficulty.`,
						onLower: `The wolves swarm in and tear you to pieces.`
					},
					doorway: {
						label: [
							`a closed door`,
							`an open door`
						],
						pattern: `door`,
						description: `It's very heavy.`,
						onClose: `It's already closed.`
					},
					stoneSlab: {
						label: `the stone slab`,
						pattern: `(?:stone )?(slab|well)`,
						onLook: [
							`The stone slab, eroded over the centuries, gives way under your weight. You fall into the well infested with slimy and repellent beings`,
							`(that are also carnivorous).`
						]
					},
					walls: { 
						description: `They are massive, square and solidly built to withstand any assault.`
					},
					esplanade: {
						label: `the parade ground`,
						pattern: `(parade ground|square|courtyard|yard)`
					},
					castle: {
						label: `the castle`,
						pattern: `castle`
					}
				}

			},
			atrium:{
				description: [
					`You are in a large atrium, immersed in darkness. An eerie phosphorescence emanating from the walls allows you to just about distinguish the contours of the room.`,
					`A marble staircase rises upwards, dimly lit by the greenish light, but gradually disappearing into the darkness.`
				],
				shortDescription: `You're in the atrium.`,
				directions: {
					south: `No doing. I'm afraid you'll have to find another way out.`
				},
				interactors: {
					blazon: {
						label: `a coat of arms painted on the ceiling`,
						pattern: `(?:painted )?coat`,
						onLook: [
							`The clan motto is written there.`,
							`"The sassenach will fall"`,
							`Suddenly, a trap door opens beneath your feet.`
						]
					},
					doorway: {
						label: `a door`,
						pattern: `door`
					}
				}
			},
			lounge: {
				description: [
					`You're in a large living room, furnished with numerous sofas and comfortable armchairs. In the centre of one wall is a monumental fireplace, built with blocks of carved stone.`,
					`Although the fire has been out for centuries, the room still seems to be illuminated by a wavering reddish light.`
				],
				shortDescription: `You're in the living room.`,
				interactors: {
					sofa: {
						label: `the sofa`,
						pattern: `(sofa|settee|couch)`,
					},
					fireplace: { 
						description: `It's so big that you could easily pass through it, if there was a passage behind. Too bad there's no sign of it. Maybe if you looked a little closer...`
					},
					light: {
						label: `the light`,
						pattern: `(?:reddish )?light`,
						description: `Strange, when you look to where you think the light is coming from, it seems to move somewhere else. Maybe it's just a reflection, or the centuries-old memory of the blood shed within these walls.`
					},
					pass: {
						pattern: `(?:secret )?passage`,
						onLookFor: `There's absolutely no trace of a secret passages behind the fireplace. You read far too many adventure books.`
					}
				},
			},
			hallway:{
				description: [`You're in a large hallway, the floor of which bears the signs of the passage of countless generations. A row of armour is lined up along the wall, each holding a long pike.`,
					`Towards the centre of the hallway, there appears to have once been a door, now bricked up.`
				],
				shortDescription: `You're in the hallway.`,
				interactors: {
					spades: {
						label:`the pikes`,
						pattern: `pike(?:s)?`,
						description: `Long and equipped with a sharp iron tip, a deadly weapon. Who knows exactly how it was used?`,
						onTake: {
							question: `You take the pike and pull it towards you, but the armour doesn't seem to want to let it go. Should you pull it a wee bit harder`,
							answer: [
								`With a firm tug, you finally manage to get hold of the pike.`,
								`The armour, unbalanced, wobbles slightly...`,
								`...`,
								`and as you step back with the tip of the pike gripped in your hands, the armour falls with all its weight onto the other end of the weapon, piercing you through and through.`,
								`This is how it was used in battle!`
							] 
						}

					},
					door: {
						description: `I said that there appears to have once been a door, of which all that remains is the trace of an outline on the wall.`,
					}
				}
			},
			diningRoom: {
				description: [
					`You're in the large dining room, occupied for its entire length by a huge oak table, surrounded by many heavy chairs with high backs.`,
					`Unlike in all the other rooms, the window is open.`
				],
				shortDescription: `You're in the dining room.`,
				directions: {
					down: {
						question: `Should you jump out the window`
					} 
				},
				interactors: {
					table: { 
						description: `It is solid and well-built, it was probably used for clan banquets.`
					},
					chairs: { 
						description: `Seeing the chairs around the table, you imagine the Laird and Lady graciously seated at either end, with the guests arranged according to their degree of nobility, according to the most rigid protocol of the clan gathering.`
					},
					window: {
						label: `the window`,
						pattern: `(?:from )?window`,
						description: `From the window, there's a magnificent view of the murky waters of the moat, populated by wee scaly monsters with sharp teeth.`,
						onJump: `Yer aff yer heid! Gonnae no dae that.`
					}
				}
			},
			library: {
				description: `You're in the castle library, the walls of which are replete with shelf upon shelf, loaded with books of all kinds. Standing in the centre of the floor, in front of a comfortable armchair, is a wrought iron lectern.`,
				shortDescription: `You're in the library.`,
				HUGE: `The weight of ancient wisdom is much greater than I might have suspected.`,
				override: {
					commands: {
						lookForDictionary: `I've carefully examined all the shelves, but couldn't find any dictionary.`,
						iotid: [
							`The sound of the magic word echoes among the ancient vaults...`,
							`An entire wall of shelves rotates on itself. I glimpse a large room.`
						]
					}
				},
				interactors: {
					shelves: {
						description: `Truly a clan chief's library! It's got everything, or almost everything. I'm convinced that if you look carefully, you'll find anything here.`
					},
					lectern: {
						label: `the lectern`,
						pattern: `lectern`,
						description: `A work of art.`,
					},
					books: {
						label: `the books`,
						pattern: `books`,
						description: `Truly a clan chief's library! It's got everything, or almost everything. I'm convinced that if you look carefully, you'll find anything here.`,
						onTake: `That's a wee bit too much to carry, ye ken.`,
						onRead: `It'd take ages to browse through them all. The ones you pick up are undoubtedly interesting, but they won't solve any of your pressing problems. You should at least tell me what to look for.`
					},
					book: {
						label: [
							`a closed book on the lectern`,
							`a book on the lectern`
						],
						pattern: `book`,
						description: `It is placed on the lectern, and seems strangely both ancient and new.`,
						onOpen: `A singed sheet slips out from the old yellowed pages.`,
						onRead: `It's a dictionary of ancient Gaelic.`
					},
					dictionary: {
						label: `a dictionary`,
						pattern: `dictionary`,
						description: `It is placed on the lectern, and seems strangely both ancient and new.`,
						posizione: `library`,
						onRead: `It carries a list of Gaelic words, each accompanied by a translation (and some comments) into standard English. The words are in alphabetical order, from A to Z. There's also a list of proverbs, idioms, and grammar and pronunciation rules. Haven't you ever seen a dictionary?`
					},
				},
			},
			wideTunnel: {
				description: `You're walking along a large tunnel carved into the rock that forms the foundations of the castle.`,
				shortDescription: `You're in a large tunnel.`,
				directions: {
					north: {
						fail: `The ogre tears you to pieces.`
					}
				}
			},
			castleDungeon: {
				description: `You are in the castle dungeon, once called 'The Tomb'. The floor is covered in skeletons.`,
				shortDescription: `You're in the dungeon.`,
				interactors: {
					hole: {
						label: `a hole on the wall`,
						pattern: `(?:key)?hole`,
						description: `It is narrow and deep, and, at the end, there's something that looks like a button.`
					},
					button: {
						label: `button`,
						pattern: `button`,
						description: `Just looking at it won't help. Maybe pressing it...`,
					},
					slit: {
						label: `the crack`,
						pattern: `(slit|crack|gap)`,
						description: `It's in the west wall, and it's wide enough to fit through.`,
						invisibleMessage: `There's no crack here.`
					},
					arm: {
						label: `the arm`,
						pattern: `arm`
					}
				}
			},
			toolshed: {
				description: `You're in a small, square room, once used as a tool room.`,
				shortDescription: `You're in the tool room.`,
				directions: {
					up: `To go up, you have to lift a stone slab, which falls behind you.`
				}
			},
			narrowTunnel: {
				description: `You are in a narrow tunnel, the walls of which are covered in greenish mould and rotting fungi.`,
				shortDescription: `You're in a narrow tunnel.`,
				interactors: {
					moulds: {
						label: `the mould`,
						pattern: `(?:greenish )?mo(?:u)?ld`,
						description: `They're fascinating, with their poisonous velvety down.`		
					},
					mushrooms: {
						label: `the fungi`,
						pattern: `((?:rotting )?fungi|(?:mu)?shrooms)`,
						description: `From what you can understand, based on your limited expertise in the matter, each is more poisonous than the next.`
					}
				},

			},
			longTunnel: {
				description: `You're in a long tunnel, dug partly into the rock and partly into the ground. The walls show traces of burrowing.`,
				shortDescription: `You're in a long tunnel.`,
				interactors: {
					ground: {
						label: `the ground`,
						pattern: `ground`
					},
					excavations: {
						label: `the burrowing`,
						pattern: `burrowing|traces`,
						description: `They are just attempts at burrowing, evidently failed. Perhaps it was some prisoner of the castle making a last desperate attempt to escape.`				
					}
				}
			},
			treasureChamber: {
				description: `You're in the treasure chamber, an immense room supported by mighty arches and located exactly under the throne room.`,
				shortDescription: `You're in the treasure chamber.`,
				interactors: {
					coffer: {
						label: [
							`a heavy chest`,
							`an open heavy chest`
						],
						pattern: `(?:heavy )?chest`,
						onLook: [
							`Of MacCallum the Fourth's famed treasure, only an old drinking horn remains.`,
							`It's empty.`
						],
						onOpen:[
							`The ghost of Malcolm's faithful squire, Edgar MacDouglas, rises to defend the treasure of his ancient Laird from the foreign defiler.`,
							`The ghost envelops you, suffocating you in its deadly embrace.`
						],
						onClose: `The hinges are broken.`
					},
					ghost: {
						label: `a ghost`,
						pattern: `ghost`,
						description: `You see it, you don't see it. At times, it seems incorporeal, so much so that you can clearly observe the details of the wall behind it. But, in other moments, it seems to take on substance and become more threatening.`,
						onKill: `He's already been dead for several hundred years.`,
						onTalk: [
							`A distant shuffle of metal objects (maybe chains?) seems to formulate a plaintive response:`,
							`- Remember that you came from nothingness, and you'll return to nothingness...`
						]
					}
				}
			},
			woodshed: {
				description: `You are in the wood store, where dry branches and logs of various sizes are stacked in perfect order.`,
				shortDescription: `You're in the wood store.`,
				interactors: {
					wood: {
						label: `the wood`,
						pattern: `((?:fire)?wood|log(?:s)?)`,
						description: `It looks like firewood.`,
						onTake: `- Hey! Hands off the winter supplies! It's cold around these parts.`
					}
				},
				override: {
					commands: {
						helloPrefix: `Even if I'm a little disconcerted by your excessive confidence...`,
						introduceYourself: [
							`- Farquhar, at your service! Mmm, your face isn't entirely new to me. Haven't we met someplace before?`,
							`You profusely list your numerous honorifics, while the dwarf listens with an increasingly doubtful expression. However...`
						]
					},
					verbs: {
						askForDiamond: [
							`He already gave it to you, don't you remember?`,
							`I doubt he's willing to give it to you.`
						],
					}
				},
			},
			topOfStairs: {
				description: `You're at the top of the stairs. The steps end abruptly in front of a smooth stone wall.`,
				shortDescription: `You're at the top of the stairs.`,
				interactors: {
					walls: {
						description: `I cannae see any holds or cracks.`,
						onPush: [
							`The wall rotates on itself...`,
							` and snaps shut behind you.`
						]
					}
				},
			},
			labyrinthEntrance: {
				description: [
					`You're at the entrance to the immense magical maze, of which it is said that all passages lead to this one room, from where neither man nor thing can escape.`,
					`There are two skeletons on the ground. On the wall, written in blood are the words:`,
					``,
					`        'Impossible to get out of here'`
				],
				shortDescription: `You're at the entrance to the maze.`,
				interactors: {
					writing: {
						label: `the writing`,
						pattern: `writing`
					}
				}
			},
			secretRoom: {
				description:`You're in the large secret room, under the castle tower. A current of icy air hisses through invisible cracks.`,
				shortDescription:`You're in the secret room.`,
				interactors: {
					slits: {
						label: `the cracks`,
						pattern: `cracks`,
						description: `As I said, they're invisible.`
					},
					lever: {
						label: `a lever`,
						pattern: `lever`,
						onPush: `A trapdoor opens...`
					},
					pendulumClock: {
						label: [
							`a stopped old pendulum clock`,
							`an old pendulum clock`,
							`an old pendulum clock`,
							`an old pendulum clock`,
							`an old pendulum clock`,
							`an old pendulum clock`,
							`an old pendulum clock`
						],
						pattern: `(?:pendulum |grandfather |floor )?clock`,
						description: [
							`It shows the time of eleven fifty-six.`,
							``,
							`It shows eleven fifty-seven.`,
							`It shows eleven fifty-eight.`,
							`It shows eleven fifty-nine.`,
							``,
							`The hands are stuck on midnight.`
						],
						onLook: [
							`It shows dead on midnight.`,
							`A stone block shifts, revealing a spiral staircase.`
						],
						onCharge: {
							fail: `Ach, you don't have the key.`,
							success: `The clock starts working again.`,
							working: `It's already working.`,
							blocked: `It's completely blocked now.`
						} 
					}
				},
			},
			L29: {
				dodgersHatch: `This trapdoor is reserved for smart people who claim to have arrived here without having translated the scroll...`
			},
			throneRoom: {
				description: `You're in the ancient throne room, where the Laird used to administer justice and receive subjects. At the sides of the room are two rows of niches where the Laird's personal guards stood. The imposing wooden throne is finely crafted, down to the smallest details. In front of the throne is a walled-up door, which must have once been the main entrance from the hallway.`,
				shortDescription: `You're in the throne room.`,
				interactors: {
					door: {
						description: `Obviously, it's walled up on this side too.`
					},
					hollows: {
						label: `the niches`,
						pattern: `niche(?:s)?`,
						description: `They're simple recesses in the walls. The work of the Laird's personal guards must have been pretty boring, standing there immobile for the whole day.`
					},
					throne: {
						label: `the throne`,
						pattern: `throne`,
						description: `It reigns in the middle of the room. On it sat the Chief.`,
						onSitDown: {
							question: `Wanna sit on the throne ??`,
							answer: `The cushion isn't as comfortable as you might have thought.`
						}
					}
				}
			},
			topOfTower: {
				description: `You're at the top of the tower, where your gaze sweeps above the fog covering the peatland, and towards the distant mountains.`,
				shortDescription: `You're at the top of the tower.`,
				interactors: {
					fog:{
						description: `The surface of the fog is rippled by slowly moving waves, as if the tower were a floating island in this strange fishless sea.`
					},
					moor: {
						label: `the peatland`,
						pattern: `peatland`,
						description: `It's hidden under the fog, but at times, in the distance, it shows a wee bit, allowing a glimpse of the barren, yellowish ground.`,
					},
					mountains: {
						label: `the mountains`,
						pattern: `montain(?:s)?`,
						description: `From the most distant snow-capped peaks, the mountains descend in a succession of undulations, hills and gentle slopes, until they merge into peatland submerged by the sea of fog. You get the impression that something tiny is moving around the peaks, but it's probably just an optical illusion.`							
					},
					flag: {
						label: `a flag in tatters`,
						pattern: `flag`,
						description:  `Carried with glory in a hundred battles, it still flies over the lands it once dominated.`,
						onTake: [
							`The old flagpole evades your grip...`,
							` and suddenly gives way, making you lose your balance. You fall down onto the parade ground.`
						],
						onLiftUp: `These ceremonies always move me...`
					},
					tower: {
						description: `You wouldn't be able to say how much it rises above the walls, because the fog coils around to the point of making your own feet invisible at times.`
					}
				},
				override: {
					verbs: {
						jump: `- SPLAT! -`
					}
				}
			},
			undergroundSpiralStaircase: {
				description: `You're in a room with a spiral staircase, and a narrow passageway to the north.`,
				interactors: {
					aisle: {
						description: `It looks like it heads into the castle's dungeon. Better turn back, don't you think?`
					}
				}
			},
			trap: {
				description: `You're in the trap room, which seems completely empty.`,
				shortDescription: `You're in the trap room.`,
				directions: {
					west: [
						`A massive slab of iron suddenly falls, blocking your way out.`,
						`The room starts to fill with water... glug... glug... glug... glug... glug... glug... glug... glug... glug... glug... glug... glug... glug... glug... glug...`,
						` GLUB.`
					]
				},
				interactors: {
					trap: {
						label: `the trap`,
						pattern: `trap`,
						onLookOrLookFor: `I cannae see any traps here at all. I dinnae ken why this room has this curious name.`
					}
				}

			},
			winePantry: {
				description: `You're in the cellar where the wines were stored. Only a few shards of glass remain on the shelves.`,
				shortDescription: `You're in the wine cellar.`,
				directions: {
					north: [
						`Seeing you, the dwarf exclaims:`,
						`- Even drunks, noo!`,
						`Never a civilized person!`,
						`This is too much! -`,
						`With that, he draws a heavy axe from his belt and strikes you in anger.`
					]
				},
				interactors: {
					fragments: {
						label: `the shards`,
						pattern: `shard(?:s)?`,
						onTake: `Other than risking cutting yourself badly, I really dinnae see what use it'd be.`
					}
				}
			},
			coldCutsPantry: {
				description: `You're in the pantry where the cured meats were stored. Only some rusty hooks now remain.`,
				shortDescription: `You're in the cured meat pantry.`,
				interactors: {
					hooks: {
						label: `the hooks`,
						pattern: `hook(?:s)?`,
						onTake: `Other than risking tetanus, I really dinnae see what use it'd be.`
					}
				}
			},
			vegetablePantry: {
				description: `You're in the pantry where the vegetable supplies were kept. The only trace of which are some slimy greenish stains on the floor.`,
				shortDescription: `You're in the vegetable pantry.`,
				interactors: {
					stains: {
						label: `the stains`,
						pattern: `(?:greenish )?stain(?:s)?`
					}
				}
			},
			cheesePantry: {
				description: `You're in the pantry where the cheeses were kept, of which all that remains are a few rinds nibbled by mice.`,
				shortDescription: `You're in the cheese pantry.`,
				interactors: {
					crusts: {
						label: `the rinds`,
						pattern: `(?:nibbles )?rinds`,
						onTakeOrEat: `You can't be THAT hungry!`
					}
				}
			},				
			gamePantry: {
				description: `You're in the pantry where the game was kept, of which all that remains is the shrivelled carcass of an old deer.`,
				shortDescription: `You're in the game pantry.`,
				interactors: {
					deer: {
						label: `the deer`,
						pattern: `(deer(?: carcass)?|carcass|shrivelled (?:deer|carcass))`,
						onTake: `Other than carrying an extra load, I really dinnae see what use it'd be.`
					}
				}
			},				
			whiskeyPantry: {
				description: `You're in the cellar where substantial supplies of the region's famous scotch whiskey were kept. All that remains is a small cask that looks extraordinarily well preserved.`,
				shortDescription: `You're in the scotch whiskey cellar.`,
				interactors: {
					keg: {
						label: `the scotch whiskey cask`,
						pattern: `(cask|keg|barrel)`,
						description: `On it, it says: '300 yrs old'`,
						onOpenQuestion: `Should you drink the scotch`
					},
					whiskey: {
						label: `the scotch`,
						pattern: `(?:scotch )?whisk(?:e)?y`,
						onTakeOrDrink: `Lip-lickingly delicious!`
					}
				}
			},
			guardRoom:{
				description: `The room where you are must have been the room of the guards to the entrance of the castle. Apart from a rough table and some heavy benches, the room is completely bare.`,
				shortDescription: `You're in the guard room.`,
				interactors: {
					table: { 
						description: `It's pretty much a wooden plank, which is certainly not elegant, but very sturdy.`
					},
					benches: {
						label: `the benches`,
						pattern: `bench(?:es)?`,
						description: `They are made of the same wood as the table, smoothed by long use.`
					}
				},

			},
			catapultRoom:{
				description: [
					`You're in an irregularly shaped room, filled with bulky, wooden and metal structures. They look like pieces of an ancient war machine, likely a small catapult.`,
					`Heavy stone balls line the wall.`
				],
				shortDescription: `You're in the catapult room.`,
				commonAnswers: [
					`Who do you think you are? Angus MacAskill?`,
					`I've never been any good at Meccano.`
				],
				interactors: {
					catapult: {
						label: `the catapult`,
						pattern: `catapult|machine`,
						description: `It's been dismantled into a thousand pieces, to take up less space, and keep it in better condition.`,
					},
					balls: {
						label: `the balls`,
						pattern: `(?:stone )?ball(?:s)?`,
						onBreak: `Don't be rude.`
					},
					pieces: {
						label: `the pieces`,
						pattern: `(catapult|(?:catapult )?pieces)`,
						description: `The pieces should all go together to make the catapult, or at least I reckon so.`
					},
				},
			},
			armory: {
				description: `You're in the castle armoury. Fixed to the walls are an assortment of bows, daggers, shields, spears, axes and other weapons, all rusted over and made useless over time.`,
				shortDescription: `You're in the armoury.`,
				interactors: {
					daggers: {
						label: `the daggers`,
						pattern: `dagger(?:s)?`,
					},
					spears: {
						label: `the spears`,
						pattern: `spear(?:s)?`,
					},
					axes: {
						label: `the axes`,
						pattern: `ax(?:e|es)?`,
					},
					weapons: {
						description: `As I already said, they're all rusty and useless.`
					}
				},

			},

			// 46.SOTTOSCALA
			understairs: {
				description: `You're in a small, empty under-stair cubbyhole.`,
				shortDescription: `You're in the under-stair cubbyhole.`,
				interactors: {
					understairs: {
						label: `the cubbyhole`,
						pattern: `cubbyhole`
					}
				},
			},
			servantsHall: {
				description: `There's nothing special at all about this room, where the Laird's servants stayed.`,
				shortDescription: `You're in the servants' hall.`
			},
			columnsHall: {
				description:  `You are in a long room with a high-arched ceiling supported by two rows of tall columns. The columns, though eroded by time, still bear the signs of patient workmanship by skilled masons. In the centre of the room, a shorter stone pillar rests on a low pedestal.`,
				shortDescription: `You're in the hall of columns.`,
				interactors: {
					column: {
						label: `the pillar`,
						pattern: `(?:small )?(?:stone )?pillar`,
						description: `On the capital of the pillar is an engraving, bearing, in silvery metallic letters, half of a powerful magical word: 'ID'`
					},
					columns: {
						label: `the columns`,
						pattern: `column(?:s)?`,
						description: `The columns at the sides of the room are quite austere in style, and taper gently upwards.`
					},
					pedestal: {
						label: `the pedestal`,
						pattern: `pedestal`,
						description: `The pedestal that supports the small column is a cube of rough stone, likely granite.`
					}
				},

			},

			// 49.SALA DEGLI ARAZZI
			tapestriesRoom: {
				description: `You're in a room whose walls are completely covered with exquisitely crafted tapestries. Most depict hunting scenes, but there's no shortage of battlefields and scenes of rural life either. The colours are well-preserved, despite the centuries.`,
				shortDescription: `You're in the tapestry room.`,
				interactors: {
					tapestries: {
						label: `the tapestries`,
						pattern: `tapestr(?:y|ies)`,
						onLook: [
							`They've been crafted with great skill, and merit a closer look. You're lost in contemplation...`,
							`Then, suddenly, you remember that you actually have something more important to do.`
						]
					}
				},
			},
			portraitsGallery: {
				description: [
					`You're in an elongated room without any furniture. The walls are lined with portraits of clan chiefs, lairds and dignitaries who have governed the castle and lands over centuries.`,
					`The portraits seem to stare at you with malevolent eyes. One in particular, that of MacCallum IV, seems to follow your movements with a gaze full of murderous hatred.`
				],
				shortDescription: `You're in the portrait gallery.`,
				interactors: {
					portrait: {
						label: `the portrait`,
						pattern: `(portrait(?:s)?|MacCallum(?: IV)?|painting(?:s)?)`,
						onLookQuestion: `In each portrait, the painter's hand seems to have captured life itself from the face of its subject, especially in the case of MacCallum IV. This portrait has something very strange about it. Should I take a closer look`
					}
				}
			},
			trophiesRoom: {
				description: `You're in a short room crammed with hunting and war trophies. Fixed to the walls are stuffed animal head of all kinds, weapons, shields, even an entire suit of armour that probably belonged to a rival clan chief killed in battle by the Laird himself.`,
				shortDescription: `You're in the trophy room.`,
				interactors: {
					armor: { 
						spadeText: `, leaning on the sword`,
						onLook: (spadeText) => `It is the armour of Sir Crawford, the valiant warrior wizard who, for many years, held MacCallum IV in check with his prowess and his fearsome arts. The armour still maintains a haughty bearing, and even seems to stare at you${spadeText}.`
					},
					trophies: {
						label: `trophies`,
						pattern: `animal(?:s)?|troph(?:y|ies)`,
					},
				},

			},
			kitchen: {
				description: `The room you're in was supposedly the castle kitchen. Indeed, I see a large hearth, and the remains of pottery, pots, pans and large tubs.`,
				shortDescription: `You're in the kitchen.`,
				interactors: {
					kitchenStuff: {
						label: `kitchen`,
						pattern: `(kictchen|pottery|pot(?:s)?|pan(?:s)?|(?:large )?tub(?:s))`,
						description: `They're completely useless.`					
					}
				},
			},
			alchemistCell: {
				description: [
					`You are in the Alchemist's cell. All around are crucibles, pestles, copper stills and bizarre glass containers of extremely contorted shapes. On the shelves are many heavy tomes of magic, alchemy and spells. In the centre of the room is a small table that rests on three legs shaped like the paws of some monstrous animal. On the table is a single heavy volume bound in black leather:`,
					``,
					`        "The Sorcerer's Apprentice"`
				],
				shortDescription: `You are in the Alchemist's cell.`,
				interactors: {
					infolio: {
						label: `tomes`,
						pattern : `crucible(?:s)?|pestle(?:s)?|copper still(?:s)?|tome(?:s)?|container(?:s)?`,
						description: `It's the typical paraphernalia of the time of an alchemist, magician, scholar and healer, all together. Alchemists were held in high regard and greatly feared and respected, unless they fell into disgrace, due to some fatal mistake committed against the Laird.`,
						onTakeOrRead: `An occult force seems to be warning you: rummaging through an alchemist's personal property can be very dangerous.`
					},
					bookmark: {
						label: `the bookmark`,
						pattern: `bookmark`,
					},
					volume: {
						label: `volume`,
						pattern: `volume|book|page`,
						description: `Now I see it more closely, I see that perhaps it's not leather, but leathered human skin.`,
						onOpen: `You need to use the right tool.`,
						onRead: [
							`I can only read one word:`,
							``,
							`  'BIGMEOW'`
						],
						onTake: `It's impossible to move it.`,
						onLiftUp: `Raise the volume?  Are you deaf??`
					}
				},
			},
			boardRoom: {
				description: `You are in the war room, where all the most serious and important decisions were made. In terms of furniture, there's a round table surrounded by eight chairs.`,
				shortDescription: `You're in the war room.`,
				interactors: {
					table: {
						description: [
							`A wise maxim is engraved on the edge of the table:`,
							`'Not all swords wound with their blades'`
						]
					},
					chairs: {
						description: `Here the noble warriors of the clan sat helping the Chief make difficult decisions on the conduct of interminable wars.`
					}
				},

			},
			wardrobe: {
				description: `You are in the Laird's dressing room. Moths and woodworms have reduced the imposing wardrobes and refined robes to scraps and shreds.`,
				shortDescription: `You're in the dressing room.`,
				interactors: {
					shreds: {
						label: `the shreds`,
						pattern: `scraps|shreds`,
						description: `Mar so a' dol seachad air a' ghlòir shaoghalta`,
					}
				},
			},
			musicHall: {
				description: [
					`You're in the music room, where parties, entertainment and performances took place, enlivening the life of the castle.`,
					`Now everything is completely covered in cobwebs.`
				],
				shortDescription: `You're in the music room.`,
				interactors: {
					cobwebs: {
						label: `the cobwebs`,
						pattern: `(?:cob|spider(?:'s | )?)?web(?:s)`,
						description: `Even the cobwebs have long been deserted. Nothing remains of the life that made this room the happiest place in the castle.`
					}
				},

			},
			princessRoom: {
				description: `You are in the Laird's daughter's room. Surprisingly, the passing of the years has left the delicate colours of the curtains and fragile canopy of the four-poster bed almost intact, and missing only the soft covers that sweetened the light rest of the slender girl.`,
				shortDescription: `You are in the Laird's daughter's room.`,
				interactors: {
					bed: {
						description: `It is decorated with delicate colours.`
					},
					curtains: {
						label: `the curtains`,
						pattern: `curtain(?:s)?`,
						description: `Embroidered on the curtains, in fine gold thread is the motto:\n'Wise is he who renounces wealth to take the path to heaven'`
					}
				},

			},
			kingRoom: {
				description: `You are in the Laird's bed chamber. A sumptuous four-poster bed stands in the middle of the room, taking up most of it. Nothing else remains of the rich furnishings.`,
				shortDescription: `You're in the Laird's bed chamber.`,
				interactors: {
					bed: {
						description: `It is painted with bold, bloody colours.`
					}
				},

			},
			mirrorsHall: {
				description: `You're in a hall with walls covered in mirrors of all shapes and sizes. Curved, giant mirrors and concave mirrors reflect your image infinitely, giving you the impression that evil faces are peering at you from every corner of the room. This was the vain Lady's favourite room.`,
				shortDescription: `You are in the hall of mirrors.`,
				bonk: `BONK!`,
				notADoor: `That's not a door, it's a mirror!`,
				override: {
					commands: {
						getOut: `With all these mirrors, I dinnae ken me left from me right. Help! I'm always going the wrong way.`
					}
				},
				interactors: {
					mirrors: {
						label: `the mirrors`,
						pattern: `mirror(?:s)?`,
						description: `I see an ugly face.`,
						onBreak: `I really don't want seven years of bad luck.`
					}
				},
			},
			pass: {
				description: `You're in a dark, narrow, tortuous passage that you're forced to navigate on all fours, moving towards a glimmer of light that manages to reach you from the other end.`,
				shortDescription: `You're in the tortuous passage.`,
				interactors: {
					pass: {
						description: ` How dark it is! Ach, cannae see naught.`
					}
				},
			},
			rock: {
				description: [
					`You're alone and abandoned on a black rock peaking above the icy waters.`,
					`Let me correct myself, you are not alone: the Loch Ness Monster (Nessie among friends) is there to keep you company.`
				],
				shortDescription: `You're on rock.`,
				interactors: {
					nessie: {
						description: `She's looking right at you.`,
						onGreet: [
							`She too is`, 
							`VERY`,
							` happy to see you.`
						],
						onKill: `Easy for you to say!`
					},
					rock: {
						label: `the rock`,
						pattern: `rock`,
						description: [
							`It is made up of high-silica granite, with a medium to fine grained structure.`,
							`The crystalline aggregate is mainly composed of quartz and feldspars, with micaceous laminae in subordinate quantities.`,
							`Observing the feldspars in detail, orthoclase (or microcline), albite and other sodium plagioclases can be noted. The micas are composed of biotite and muscovite, mixed with other mafic components, and amphiboles in particular.`,
							`There are also small crystals of apatite and zircon, as well as granules of magnetite and pyrite.`
						]
					},
					lake: {
						label: `the lake`,
						pattern: `lake|water`
					}
				},
				override: {
					commands: {
						swim: [
							`You're a good swimmer.`,
							`But`,
							`SHE`,
							` is better.`
						]
					}
				}
			}
		},
		objects: {
			parachute: {
				label: `a parachute`,
				pattern: `parachute`,
				onWear: `You already have it on you.`,
				onLook: `Haven't you ever seen a parachute before? What rock have you been living under?`,
				onOpen: {
					notHere: `In here? Don't make me laugh!`,
					dontHaveIt:	`You're not wearing a parachute.`
				}
			},
			bone: {
				label: [
					`a bone`,
					`a sliced bone`
				],
				pattern: `bone|femur|tibia`,
				onLook: `It could be a femur, or maybe a tibia. Ach, ay dinnae ken. I don't know much about anatomy.`,
				onLookFor: { 
					dontHaveIt: `The floor is full of them.`,
					inYourHand: `You've got one in your hand already.`
				}
			},
			bludgeon: {
				label: `a studded bludgeon`,
				pattern: `(?:studded )?bludgeon`,
				description: `It's clearly a battle weapon. It won't break through a wall, but could certainly come in handy.`,
			},
			cat: {
				label: [
					`a cat crouched on the ground`,
					`a cat`
				],
				pattern: `(cat|kitty)`,
				description: `It's a cute wee kitty.`,
				onTake: {
					alreadyIn: `You already have it in your arms.`,
					gotIt: `The cat is hungry and easily taken up in your arms.`
				},
				onPet: [
					`purr... purr... purr... purr...`,
					`The cat is purring.`
				],
				onTalkOrGreet: `The cat stares at you with an interested expression, its pupils reduced to two narrow slits. It then (probably) decides that your intelligence is only apparent, and turns its gaze elsewhere.`,
				onKill: [
					`With one last plaintive meow, the poor kitten dies while staring at you with a terrifically reproachful look.`,
					`The corpse begins to fade away, until it disappears completely.`
				],
				onFeed: {
					nothingSuitable: `You have nothing suitable.`,
					lep: `lick...`,
					finished: [
						`lick... meow!`,
						``,
						`The cat really seems to have enjoyed the honeyed milk.`
					]					
				},

			},
			sword: {
				label: [
					`a sword`,
					`an enchanted sword`
				],
				pattern: `sword`,
				posizione: `trophy room`,
				description: `It is of exquisite workmanship.`,
				onLook: `A spell is written on the blade.`,
				spell: {
					dontKnow: `You don't know any spells.`, 
					dontRemember: `I don't remember, it was too complicated.`,
					question: `Should you say it out loud`,
					fail: [
						`Your throat is dry with fear...`,
						`You can't speak...`,
						`The ghost takes advantage of this to attack you.`
					],
					success: `With a long, desperate wail, the ghost returns to the nothingness from which it came.`
				}
			},
			spell: {
				label: `the spell`,
				pattern: `spell`,
				onLook: `I think that reading the strange symbols and formulas will evoke long-dormant forces.`
			},
			milkAndHoneyCup: {
				label: [
					`a cup full of honeyed milk`,
					`an empty cup`
				],
				pattern: `cup`,
				description: {
					full: `It's full of honeyed milk.`, 
					empty: `It's completely empty.`
				},
				EMPTY: `The cup is empty.`
			},
			milkAndHoney: {
				label: `the honeyed milk`,
				pattern: `(?:honeyed )?milk`,
				description: `Looking at it and smelling it is nice.`,
				onDrink: {
					success: `Lip-lickingly delicious!`,
					fail: `Pick up the cup first.`
				},
				onOffer: {
					toGhost: `He doesn't want it.`,
					toOgre: `He's not hungry, thankfully!`,
					toWho: `To who?`
				}
			},
			lute: {
				label: `a lute`,
				pattern: `lute`,
				description: `This instrument cheered up gatherings, cèilidhs and dances that took place on the occasion of harvests, victories, festivities, and any pretext that would warrant a knees-up.`,
				onPlay: `The strings are broken.`
			},
			harp: {
				label: `a harp`,
				pattern: `harp`,
				description: `This instrument accompanied the sad and happy songs the bard of the castle sang, during long winter evenings, while everyone was gathered around, enraptured by the telling of ancient legends.`,
				onPlay: `The strings are broken.`
			},
			bagpipe: {
				label: `a bagpipe`,
				pattern: `bagpipe`,
				description: `This instrument accompanied military expeditions led personally by the Laird, marking, with its loud, rhythmic wail, the heavy steps of the marching soldiers.`,
				onPlay:{
					fail: [
						`You deserve to be part of the Royal Scots Dragoon Guards!`,
						`(...playing the drums)`
					],
					success: `The volume opens to a page carrying a finely decorated bookmark.`
				}
			},
			sheet: {
				label: `a scorched sheet`,
				pattern: `(?:scorched )?sheet|paper`,
				description: `Although partially scorched by flames, it is still possible to read something.`,
				onRead:{
					dontHaveIt: `Ach, you don't have a sheet of paper.`,
					success: `It simply says: 'IOT'`
				}
			},
			cushion: {
				label: `a cushion`,
				pattern: `cushion`,
				description: `Don't even think about sleeping now, think about how to get out of here.`,
				onLiftUp:`There is a wooden box underneath.`
			},
			case: {
				label: `a box`,
				pattern: `(?:wooden )?box`,
				description: `The wood of the box is delicately inlaid.`,
				onTake: `It is screwed to the seat of the throne.`,
				onSkrewOff: `Ach, you don't have the right tool.`,
				onOpen: `The box contains an old scroll.`
			},
			scroll: {
				label: `a scroll`,
				pattern: `scroll`,
				description: `It's faded with age, but can still be read.`,
				onRead: {
					dontHaveIt: `You don't have a scroll.`,
					fail: `Ach, I'm sorry, it's written in a language I don't know.`
				},
				onTranslate: {
					fail: `I'd need a dictionary.`,
					success: [
						`It says:`,
						`'Only by the good use of sense will you find your way out from the labyrinth'`
					]
				}								
			},
			ogre: {
				label: `a ferocious ogre with sharp fangs`,
				pattern: `(?:ferocious )?ogre`,
				onLook: [
					`Upon a closer look, he doesn't seem a wee bit ferocious...`,
					`...but much, much more!`
				],
				onKill: `Easy for you to say!`,
				onFeed: `He's not hungry, thankfully!`,
				onTalkOrGreet: `- GRUNT -`
			},
			dwarf: {
				label: [
					`a wee dwarf with a big diamond`,
					`a very friendly wee dwarf`
				],
				pattern: `(?:little |wee )?dwarf`,
				description: `He's quite small.`,
				onGreet: {
					withoutDiamond: `- Guid evenin' tae ye! - replies the dwarf.`,
					withDiamond: `The dwarf is so happy to finally meet such a courteous person that he simply gives you the diamond.`
				},
				onFeed: `He's just had dinner.`,
				onKill: [
					`The dwarf, whips out a sharp dagger, and slashes at you fiercely, with a cry:`,
					`- We only get thieves and murderers coming through here! -`
				]
			},
			diamond: {
				label: `a diamond`,
				pattern: `diamond`,
				description: `The more you observe the wonderful gemstone, the more you become overwhelmed by an unbridled desire to possess it.`,
				MINE: `The dwarf says: - It's mine! -`,
				onBreak: {
					needSomethingHard: `It needs something very hard.`,
					success: `On the first blow of the bludgeon, the diamond shatters into a thousand pieces.`
				},
				onLook: `It's magnificent: the light reflected and refracted by its a thousand perfect facets creates an infinite play of colour. You are fascinated by it, and would observe it for hours and hours. I think it's of inestimable value, and you should treat it with utmost care.`
			},
			key: {
				label: `a small glass key`,
				pattern: `(?:small )?(?:glass )?key`,
				description: `It's very strange, too small to be the key to a door or gate. And it looks fragile too. What could it possibly be used for?`
			},
			horn: {
				label: `a horn`,
				pattern: `horn`,
				description: `It is decorated with hunting scenes that wrap around in a spiral from its mouth. Galloping riders are seen to chase their prey, while large birds circle overhead.`,
				onPlay: `The cavernous sound echoes within the castle walls.`
			}
		},
		sequences: {
			title: [
				`JavaScript adaptation of`,
				`"CASTLE ADVENTURE"  (original Italian:  "AVVENTURA NEL CASTELLO")`,
				`by Federico Volpini`,
				`(volpini.federico79@gmail.com)`,
				`From the original Avventura nel Castello Version 4.1 for MS-DOS`,
				`by Enrico Colombini and Chiara Tovena`,
				`(C) Dinosoft 1982,1984,1987,1996`,
				`Reproduced with the kind permission of  the original authors`,
				`Distribution Licence: CC BY-NC-ND 4.0`,
				`(Attribution-NonCommercial-NoDerivatives`,
				` 4.0 International)`,
				`Translation into English by Adam Bishop (https://floss.social/@AdamBishop).`
			],
			prologue: [
				` PROLOGUE:`,
				`Ancient Scottish legends tell of the heroic deeds and dark sorceries of the ancient Lairds of the Highlands.`,
				`There are also tales of fabulous treasures, never found.`,
				`Your ardent adventurous spirit, moved by a thirst for glory and a greed for riches, does not hesitate for an instant:`,
				`You rent a small tourist plane, and set off on a great adventure!`
			],
			intro: [
				` * CASTLE ADVENTURE! *`,
				`You're piloting your single-seater over the desolate Highlands of Scotland. You've just flown over Loch Ness...`,
				`Suddenly, the engine misfires.`,
				`The controls aren't responding!`
			],
			parachute: [
				`Oh, look. There is a parachute. I hadn't seen it.`,
				`I promise you that, from now on, I'll be much more careful, and will scrupulously report all the objects around you.`,
				`Anyway, you've got it on now.`
			],
			fly: {
				string: [
					`Sure you haven't forgotten`,
					`somethinnnnn`,
					`             nnn`,
					`                 nn`,
					`                    n`,
					`                     n`,
					`                      n`,
					`                      n`,
					`                      n`,
					`                      :`,
					`                      :`,
					`                      :`,
					`                  \\   :   /`,
					`                 -  SPLAT! -`
				],
				reversed: ` SPLAT!`
			},
			jumpFromPlane:[
				`Just in time!`,
				`The plane crashes to the ground, as your parachute opens.`,
				`You gently descend in the dying daylight. Below you appears a desolate moor. The wind pushes you towards a ruined castle. You land in the castle's large parade ground.`,
				`While you fold away your parachute, you look around:`
			],
			arm: {
				question: `Should you stick your arm through the hole`,
				answer: [
					`A blade snaps down, slicing your arm cleanly off.`,
					`While you're bleeding to death, let me tell you that you've been behaving rather recklessly.`
				]
			},
			castleDungeon: {
				success: [
					`A blade comes down sharply, slicing the bone cleanly in two.`,
					` Lucky it wasn't your arm!`,
					`A crack slowly widens.....`
				],
				fail: `It doesn't work.`
			}, 
			portrait: [
				`As soon as you fix your eyes on the portrait of Malcolm IV, you feel inexorably attracted by his magnetic gaze. You are unable in any way to interrupt the line between your eyes and those of the portrait, and, despite yourself, you take yourself closer and closer to the canvas. An invisible hand seems to grab your throat in a steel grip, while a mocking smirk appears drawn on the face of the Laird, depicted in his sumptuous parade attire.`,
				`When you feel you are at your wits end, the hand reaching through the mists of time suddenly releases its deadly grip.`,
				`You collapse on the floor and pass out, as everything revolves around you. You can't see it, but a satisfied expression appears on Malcolm IV's face.`
			],
			eat: [
				`First, you have a big stomach ache...`,
				`then some beautiful hallucinations...`,
				`you think you have finally understood the universal truth...`,
				`but, in the end, you're just left with a heavy, pulsing sensation behind your eyes.`
			],
			eagle: [
				`The ancient horn sounds across the moor, echoing off the distant mountains.`,
				`A black dot rises from the mountains and grows larger as it approaches.`,
				`Quickly it reaches the tower: it's a large golden eagle that lurches towards you with its claws extended.`,
				`You have no chance:`,
				`The eagle grabs you, quickly lifting you up to a great height.`,
				`The eagle flies for a long time while the landscape races beneath you... ... ... ... ... ... ... ... ... ... ... ... . .. ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ...Loch Ness appears in the distance... ... ... ... ... ... ... ... ... ... ... ... ... . .. ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ... ...`,
				`Suddenly, the eagle lets go of you.`
			],
			rock:  [
				`You gently descend in the dying daylight. Below you are the dark waters of Loch Ness. The wind pushes you towards the centre of the lake. By chance, you land on a small outcrop of rock.`,
				`While you fold away your parachute, you look around:`
			], 
			again: {
				string: [
					`Sure you haven't forgotten something`,
					`againnnnnn`,
					`             nnn`,
					`                 nn`,
					`                    n`,
					`                     n`,
					`                      n`,
					`                      n`,
					`                      n`,
					`                      :`,
					`                      :`,
					`                      :`,
					`                  \\   :   /`,
					`                 -  SPLAT! -`,
					`You're incorrigible!`
				],
				reversed: ` SPLAT!`
			},
			final: [
				`The ancient horn sounds across the moor, echoing off the distant mountains.`,
				`A black dot rises from the mountains and grows larger as it approaches.`,
				`Quickly it reaches the rock: it's a helicopter from the Royal Archaeological Service, which throws you down a rescue ladder. You climb the ladder as the monster's jaws snap shut inches below you.`,
				`As the helicopter heads towards Edinburgh, you tell of your adventure, showing the horn, the only object you have managed to keep. Seeing it, Professor MacAnthrop (Director of the Edinburgh Museum) exclaims:`,
				`- Why, this is Malcolm the Fourth's horn, thought to be lost forever!`,
				`- It'll be worth at least a million pounds or more! - cries his assistant.`,
				`The helicopter lands in Edinburgh.`,
				`You are immediately arrested for:`,
				`- Encumbrance of public land by aircraft wreckage.`,
				`- Violation of Royal Domicile.`,
				`- Theft of drinks (with swallowing).`,
				`- Mistreatment of a cat of noble lineage.`,
				`- Theft of archaeological finds.`,
				`- Harassment of a state ghost.`,
				`- Illegal possession of a sharp weapon.`,
				`- Disturbing drunkenness.`,
				`- Practice of witchcraft without a licence.`,
				`- Disturbance of a monster of a protected species.`,
				`The horn will naturally be confiscated from you and entrusted to the museum.`,
				`The Director, still excited by the important discovery, intervenes on your behalf. You are freed and even awarded a prize by the grateful Scots (which is quite unusual):`, 
				`A `,
				`FREE`,
				` ticket for a sightseeing trip on Loch Ness! -`,
				`Anyhow, console yourself: you have finally earned the `,
				`1000`,
				` points that give you the right to boast the coveted title of:`,
				` THE DEVIL'S LIEUTENANT!!!`,
				`See you on the next adventure!`,
			]
		},
		timedEvents: {
			plane: [
				`The cockpit is filled with smoke.`,
				`Quick, do something!`,
				`You're running out of time!!!`,
				`@@@@@@@@@@@@@@@ CRASH! @@@@@@@@@@@@@@@`
			],
			whiskey: `- HIC! -`,
			nessie: [
				`I see the Loch Ness Monster approaching.`,
				`I see the Loch Ness Monster coming far too close.`,
				`>CHOMP!<`,
				`  (Tasty these adventurers!)`
			]
		}
	}
}
