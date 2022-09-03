import requests
import pprint


MANAGE_CHANNELS = str(1 << 4)
KICK_MEMBERS = str(1 << 1)
BAN_MEMBERS = str(1 << 2)
ADMINISTRATOR = str(1 << 3)

DEGUB_ID = 0
DEBUG_TOKEN = ""
PRD_ID = 0
PRD_TOKEN = ""

payload = [

    {
        "name": "nsfw",
        "description": "Pictures of nature. 🍑",
        "nsfw": True,
        "options": [
            {
                "name": "fantasy",
                "description": "<:Pantsu:443870754107555842> Non-Real",
                "type": 2,  # 2 is type SUB_COMMAND_GROUP
                "options": [
                    {'description': 'Hentai Traps.',
                     'name': 'futa',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Pokemon Porn!',
                     'name': 'poke',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Hentai.',
                     'name': 'hentai',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Boy love.',
                     'name': 'yaoi',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Fucking furries',
                     'name': 'yiff',
                     'nsfw': True,
                     'type': 1}
                ]
            },
            {
                "name": "general",
                "description": "<:Ass:520247323343978509> General NSFW",
                "type": 2,
                "options": [
                    {'description': 'The beautiful thicc and chubby.',
                     'name': 'thicc',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'BlowJobs!',
                     'name': 'blowjob',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Shows some boobs.',
                     'name': 'boobs',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Gotta have that black love as well.',
                     'name': 'black',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Shows some ass.',
                     'name': 'ass',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Got dick?',
                     'name': 'dick',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Gotta get that double love!',
                     'name': 'dp',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Redheads: because redder is better!',
                     'name': 'red',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Pussy!',
                     'name': 'pussy',
                     'nsfw': True,
                     'type': 1},
                    {
                        "name": "magik",
                        "type": 1,
                        "nsfw": True,
                        "description": "Super freaky porn.",
                        "options": [
                            {
                                "name": "category",
                                "description": "type of category",
                                "type": 3,
                                "required": True,
                                "choices": [
                                    {
                                        "name": "dick",
                                        "value": "dick"
                                    },
                                    {
                                        "name": "boobs",
                                        "value": "boobs"
                                    },
                                    {
                                        "name": "ass",
                                        "value": "ass"
                                    }
                                ]
                            }
                        ]
                    },
                    {'description': 'Real girls!',
                     'name': 'real',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Sticky Love! '
                                    '<:stickylove:440557161538650113>',
                     'name': 'cumsluts',
                     'nsfw': True,
                     'type': 1},
                    {'description': '4K Hotness!',
                     'name': '4k',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Sexy gifs!',
                     'name': 'gif',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Random nsfw because why not.',
                     'name': 'random',
                     'nsfw': True,
                     'type': 1}

                ]
            },
            {
                "name": "holiday",
                "description": "🎅 Holiday",
                "type": 2,
                "options": [
                    {'description': 'Valentines ❤',
                     'name': 'vday',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Easter is nice 🐰',
                     'name': 'easter',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Halloween 👻',
                     'name': 'halloween',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Christmas 🎅',
                     'name': 'xmas',
                     'nsfw': True,
                     'type': 1}

                ]
            },
            {
                "name": "video-searching",
                "description": "\uD83D\uDCF9 Video Searching",
                "type": 2,
                "options": [
                    {
                        "name": "pornhub",
                        "type": 1,
                        "nsfw": True,
                        "description": "PornHub video search.",
                        "options": [
                            {
                                "name": "query",
                                "description": "Search query",
                                "type": 3,
                                "required": True,
                            }
                        ]
                    },

                    {
                        "name": "redtube",
                        "type": 1,
                        "nsfw": "true",
                        "description": "RedTube video search.",
                        "options": [
                            {
                                "name": "query",
                                "description": "Search query",
                                "type": 3,
                                "required": True,
                            }
                        ]
                    }

                ]
            },
            {
                "name": "kinks",
                "description": "<:whip:440551663804350495> Kinks",
                "type": 2,
                "options": [

                    {'description': 'Strap-on love!',
                     'name': 'pegged',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Play nice.',
                     'name': 'collared',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Bondage and Discipline (BD), '
                                    'Dominance and Submission (DS), Sadism '
                                    'and Masochism (SM)',
                     'name': 'bdsm',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Tatted up women.',
                     'name': 'tattoo',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'That ass love tho.',
                     'name': 'anal',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Tiny girls!',
                     'name': 'tiny',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Traps are hot!',
                     'name': 'traps',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'feet.',
                     'name': 'feet',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Random thighs',
                     'name': 'thigh',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Sexy!',
                     'name': 'bottomless',
                     'nsfw': True,
                     'type': 1},
                    {'description': "For when 2 aren't enough...",
                     'name': 'group',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'tentacles.',
                     'name': 'tentacle',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Phat Ass White Girls!',
                     'name': 'pawg',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Lesbians are sexy!',
                     'name': 'lesbians',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Got men?',
                     'name': 'gay',
                     'nsfw': True,
                     'type': 1},
                    {'description': 'Everything is better with toys 😉',
                     'name': 'toys',
                     'nsfw': True,
                     'type': 1}

                ]
            }
        ]
    },

    {
        "name": "send",
        "type": 1,
        "nsfw": True,
        "description": "Sends pics to you or another user.",
        "options": [
            {
                "name": "category",
                "description": "type of category",
                "type": 3,
                "required": True,
                "choices": [
                    {
                        "name": "Dick",
                        "value": "senddick"
                    },
                    {
                        "name": "Feet",
                        "value": "sendfeet"
                    },
                    {
                        "name": "Nudes",
                        "value": "sendnudes"
                    },
                    {
                        "name": "pegging",
                        "value": "sendpegging"
                    },
                    {
                        "name": "pussy",
                        "value": "sendpussy"
                    },
                    {
                        "name": "tentacle",
                        "value": "sendtentacle"
                    },
                    {
                        "name": "thigh",
                        "value": "sendthigh"
                    }
                ]
            },
            {
                "name": "member",
                "description": "Member to Dm.",
                "type": 6,
                "required": False,
            }
        ]
    }
    ,
    {
        "name": "meme",
        "description": "<:meme:539601224966864897> Meme",
        "nsfw": False,
        "options": [
            {
                "name": "memes",
                "description": "<:meme:539601224966864897> Image generation with a memey twist.",
                "type": 2,  # 2 is type SUB_COMMAND_GROUP
                "options": [

                    {'description': 'Door.',
                     'name': 'door',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'trigger.',
                     'name': 'triggered',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Dab.',
                     'name': 'dab',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Egg.',
                     'name': 'egg',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Disability.',
                     'name': 'disability',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'aborted.',
                     'name': 'aborted',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'sick.',
                     'name': 'sick',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'America.',
                     'name': 'america',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Satan.',
                     'name': 'satan',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Rip.',
                     'name': 'rip',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Brazzers.',
                     'name': 'brazzers',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Roblox.',
                     'name': 'roblox',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Invert.',
                     'name': 'invert',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Ugly.',
                     'name': 'ugly',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Jail.',
                     'name': 'jail',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Trash.',
                     'name': 'trash',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}

                ]},

            {
                "name": "more-memes",
                "description": "<:meme:539601224966864897> Image generation with a memey twist.",
                "type": 2,  # 2 is type SUB_COMMAND_GROUP
                "options": [

                    {'description': 'Wanted.',
                     'name': 'wanted',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'goggles.',
                     'name': 'goggles',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Failure.',
                     'name': 'failure',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Warp.',
                     'name': 'warp',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'affect.',
                     'name': 'affect',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'blur.',
                     'name': 'blur',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Deepfry.',
                     'name': 'deepfry',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Delete.',
                     'name': 'delete',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Hitler.',
                     'name': 'hitler',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Dank.',
                     'name': 'dank',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Bongocat.',
                     'name': 'bongocat',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Salty.',
                     'name': 'salty',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Laid.',
                     'name': 'laid',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Fakenews.',
                     'name': 'fakenews',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Corporate.',
                     'name': 'corporate',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Fedora.',
                     'name': 'fedora',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Whodidthis.',
                     'name': 'whodidthis',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Cancer.',
                     'name': 'cancer',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'Communism.',
                     'name': 'communism',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}
                    ,
                    {'description': 'airpods.',
                     'name': 'airpods',
                     'nsfw': False,
                     'options': [{'description': 'Member to Meme.',
                                  'name': 'member',
                                  'required': False,
                                  'type': 6}],
                     'type': 1}

                ]
            }
        ]
    },

    {
        "name": "printer",
        "type": 1,
        "nsfw": True,
        "description": "Super freaky text porn.",
        "options": [
            {
                "name": "category",
                "description": "type of category",
                "type": 3,
                "required": True,
                "choices": [
                    {
                        "name": "dick",
                        "value": "dick"
                    },
                    {
                        "name": "boobs",
                        "value": "boobs"
                    },
                    {
                        "name": "ass",
                        "value": "ass"
                    },
                    {
                        "name": "black",
                        "value": "black"
                    },
                    {
                        "name": "tentacle",
                        "value": "tentacle"
                    },
                    {
                        "name": "pawg",
                        "value": "pawg"
                    },
                    {
                        "name": "hentai",
                        "value": "hentai"
                    },
                    {
                        "name": "easter",
                        "value": "easter"
                    }
                ]
            }
        ]
    },
    {
        "name": "Interact",
        "type": 2,
        "nsfw": "false",
    },
    {'description': 'random cat', 'name': 'meow', 'nsfw': False, 'type': 1}
    ,
    {'description': 'random dog', 'name': 'dog', 'nsfw': False, 'type': 1}
    ,
    {'description': 'Random why questions', 'name': 'why', 'nsfw': False, 'type': 1}
    ,
    {'description': 'Random facts', 'name': 'facts', 'nsfw': False, 'type': 1}
    ,
    {'description': 'random Lizard', 'name': 'lizard', 'nsfw': False, 'type': 1}
    ,
    {'description': 'Pong!', 'name': 'ping', 'nsfw': False, 'type': 1, "options": [
        {
            "name": "please",
            "description": "Show Ping info.",
            "type": 5,
            'required': False,

        }
    ]
     },
    {'description': "Overview of BoobBot's process", 'name': 'stats', 'nsfw': False, 'type': 1}
    ,
    {'description': 'Bot and support guild links',
     'name': 'invite',
     'nsfw': False,
     'type': 1},

    {'description': 'Displays bot info', 'name': 'info', 'nsfw': False, 'type': 1},

    {
        "name": "interactions",
        "description": "Interact with someone.",
        "nsfw": True,
        "options": [
            {'description': 'kiss someone.', 'name': 'kiss', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'Play rough with someone.', 'name': 'playrough', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'cum on someone', 'name': 'cum', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'dom someone', 'name': 'dom', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'Fuck someone.', 'name': 'fuck', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'finger someone.', 'name': 'finger', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'Pickup someone.', 'name': 'pickup', 'nsfw': False,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'Suck someone.', 'name': 'suck', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'lick someone.', 'name': 'lick', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'Are you extreme?', 'name': 'extreme', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'duh?', 'name': '69', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'fun interactions.', 'name': 'interact', 'nsfw': False,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'Spank someone.', 'name': 'spank', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
            ,
            {'description': 'Tease someone.', 'name': 'tease', 'nsfw': True,
             'options': [{'description': 'Member to m["name"]}', 'name': 'member', 'required': True, 'type': 6}],
             'type': 1}
        ]
    },

    {'description': 'ship.',
     'name': 'ship',
     'nsfw': False,
     'options': [{'description': 'Member to ship.',
                  'name': 'member',
                  'required': True,
                  'type': 6},

                 {'description': '2nd Member to ship.',
                  'name': 'member2',
                  'required': False,
                  'type': 6}
                 ],
     'type': 1},

    {'description': 'Pickup someone.', 'name': 'pickup', 'nsfw': False,
     'options': [{'description': 'Member to pickup', 'name': 'member', 'required': True, 'type': 6}], 'type': 1},

    {
        "name": "economy",
        "description": ":moneybag: Economy and Games",
        "nsfw": False,
        "options": [

            {'description': 'give rep.',
             'name': 'rep',
             'nsfw': False,
             'options': [{'description': 'Member to Rep.',
                          'name': 'member',
                          'required': True,
                          'type': 6}],
             'type': 1},

            {'description': 'See your current rank info.',
             'name': 'rank',
             'nsfw': False,
             'options': [{'description': 'Member to Check.',
                          'name': 'member',
                          'required': False,
                          'type': 6}],
             'type': 1},

            {'description': 'View your economy profile.',
             'name': 'profile',
             'nsfw': False,
             'options': [{'description': 'Member to Check.',
                          'name': 'member',
                          'required': False,
                          'type': 6}],
             'type': 1},

            {'description': 'Basic daily income.',
             'name': 'payday',
             'type': 1},
            {
                "name": "leaderboard",
                "type": 1,
                "description": "Global leaderboards 🏆",
                "options": [
                    {
                        "name": "leaderboards",
                        "description": "Global leaderboards 🏆",
                        "type": 3,
                        "required": True,
                        "choices": [
                            {
                                "name": "rep",
                                "value": "replb"
                            },
                            {
                                "name": "level",
                                "value": "level"
                            },
                            {
                                "name": "cash",
                                "value": "cash"
                            }
                        ]
                    }
                ]
            },
            {
                "name": "coin",
                "type": 1,
                "description": "Flip a coin.",
                "options": [
                    {
                        "name": "coin",
                        "description": "Heads or Tails",
                        "type": 3,
                        "required": True,
                        "choices": [
                            {
                                "name": "heads",
                                "value": "heads"
                            },
                            {
                                "name": "tails",
                                "value": "tails"
                            }
                        ]
                    },
                    {
                        "name": "bet",
                        "description": "amount to bet",
                        "type": 4,
                        "required": True,
                        "min_value": 1,
                        "max_value": 500
                    }
                ]
            },

            {'description': 'See your current balance.',
             'name': 'balance',
             'nsfw': False,
             'options': [{'description': 'Member to Check.',
                          'name': 'member',
                          'required': False,
                          'type': 6}],
             'type': 1}

        ]
    },
    {
        "name": "bank",
        "type": 1,
        "description": "banking operations 🏦",
        "options": [
            {
                "name": "deposit",
                "description": "deposit funds. 	💰",
                "type": 1,
                "options": [

                    {
                        "name": "amount",
                        "description": "amount to deposit",
                        "type": 4,
                        "required": True,
                        "min_value": 1,
                        "max_value": 500
                    }
                ]

            },
            {
                "name": "withdraw",
                "description": "withdraw funds. 💸",
                "type": 1,
                "options": [

                    {
                        "name": "amount",
                        "description": "amount to withdraw",
                        "type": 4,
                        "required": True,
                        "min_value": 1,
                        "max_value": 500
                    }
                ]

            },
            {
                "name": "balance",
                "description": "check your funds. 💳",
                "type": 1,
                "required": False,
            },
            {
                "name": "transfer",
                "description": "transfer funds. ⇆",
                "type": 1,
                "options": [
                    {'description': 'Member to transfer.',
                     'name': 'member',
                     'required': True,
                     'type': 6},
                    {
                        "name": "amount",
                        "description": "amount to transfer",
                        "type": 4,
                        "required": True,
                        "min_value": 1,
                        "max_value": 500
                    }
                ]

            }
        ]
    },

    {
        "name": "perks",
        "type": 1,
        "description": "Receive your rewards after subscribing on Patreon.",
        "options": [
            {
                "name": "link",
                "description": "Link your Patreon subscription to the bot.",
                "type": 1,
                "required": False,

            },
            {
                "name": "add",
                "description": "Link a server to your subscription.",
                "type": 1,
                "required": False,

            },
            {
                "name": "remove",
                "description": "Remove a server from your subscription.",
                "type": 1,
                "required": False,
            },
            {
                "name": " list",
                "description": "Lists all servers attached to your subscription.",
                "type": 1,
                "required": False,
            }
        ]
    },

    {
        "name": "opt",
        "type": 1,
        "nsfw": True,
        "description": "Changes whether you can receive nudes or not",
        "options": [
            {
                "name": "in",
                "description": "Opt in to receiving nudes.",
                "type": 1,
            },
            {
                "name": "out",
                "description": "Opt out of receiving nudes.",
                "type": 1,
            },
            {
                "name": "status",
                "description": "check if you can receive nudes or not.",
                "type": 1,
            }
        ]
    },

    {
        "name": "toggle",
        "description": "Toggles the current channels nsfw setting",
        "type": 1,
        "default_member_permissions": MANAGE_CHANNELS
    }

]


def count():
    url = f"https://discord.com/api/v10/applications/{PRD_ID}/commands"
    headers = {"Authorization": f"Bot {PRD_TOKEN}"}
    r = requests.get(url, headers=headers)
    pprint.pprint(len(r.json()))


def update_prd():
    url = f"https://discord.com/api/v10/applications/{PRD_ID}/commands"
    headers = {"Authorization": f"Bot {PRD_TOKEN}"}
    r = requests.put(url, headers=headers, json=payload)
    pprint.pprint(r.json())


def update_debug():
    url = f"https://discord.com/api/v10/applications/{DEGUB_ID}/commands"
    headers = {"Authorization": f"Bot {DEBUG_TOKEN}"}
    r = requests.put(url, headers=headers, json=payload)
    pprint.pprint(r.json())


def add_to_prd(command):
    url = f"https://discord.com/api/v10/applications/{PRD_ID}/commands"
    headers = {"Authorization": f"Bot {PRD_TOKEN}"}
    r = requests.post(url, headers=headers, json=command)
    pprint.pprint(r.json())


def add_to_debug(command):
    url = f"https://discord.com/api/v10/applications/{DEGUB_ID}/commands"
    headers = {"Authorization": f"Bot {DEBUG_TOKEN}"}
    r = requests.post(url, headers=headers, json=command)
    pprint.pprint(r.json())


def clear_debug():
    url = f"https://discord.com/api/v10/applications/{DEGUB_ID}/commands"
    headers = {"Authorization": f"Bot {DEBUG_TOKEN}"}
    r = requests.put(url, headers=headers, json=[])
    pprint.pprint(r.json())


if __name__ == '__main__':
    # count()
    # update_prd()
    # add_to_prd()
    # update_debug()
    # add_to_debug()
    clear_debug()
