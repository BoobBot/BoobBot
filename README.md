![](https://cdn.discordapp.com/attachments/1020903723133829210/1020915800493793400/OpenSauce.svg) ![](https://cdn.discordapp.com/attachments/330777295952543744/478325842188042241/license.svg) [![CircleCI](https://circleci.com/gh/BoobBot/BoobBot.svg?style=svg)](https://circleci.com/gh/BoobBot/BoobBot) [![Patreon](https://img.shields.io/badge/patreon-donate-green.svg)](https://www.patreon.com/OfficialBoobBot) [![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg)](https://paypal.me/boobbot)  

# BoobBot

This repository is intended for educational purposes, and to allow people to contribute towards/improve the codebase of the bot.
While we don't encourage self-hosting, if you insist on doing so you will need to do several things to get it in a usable state.
See [prerequisites](#prerequisites) for more information.

## Prerequisites

* You will need to create a `bb.env` file in [Resources](src/main/resources), or supply your env variables some other way on boot.
  * Refer to [Constants](src/main/kotlin/bot/boobbot/entities/internals/Config.kt) for the variable names.
* You will need a BB API key which you can purchase [here](https://www.patreon.com/OfficialBoobBot).
* You will need an instance of [Imgen](https://github.com/DankMemer/imgen) for image generation.
* You will need several proxies for large-scale support Which can be found in [Utils](src/main/kotlin/bot/boobbot/utils/Utils.kt).


## Built With

* [JDA](https://github.com/DV8FromTheWorld/JDA) - A Java Discord API Wrapper.
* [Gradle](https://gradle.org/) - Dependency Management.
* [Kotlin](https://kotlinlang.org/) - A powerful JVM-based language with support for coroutines and more.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License

This project is licensed under the AGPL-3.0 License - see the [LICENSE](LICENSE) file for details

## Projects Powering BoobBot

[![Yourkit](https://www.yourkit.com/images/yklogo.png)](https://www.yourkit.com/java/profiler)  
BoobBot uses YourKit to profile the application and provide statistics for the performance of the bot.

[![JetBrains](https://cdn.discordapp.com/attachments/440683853364068381/570687889634099238/untitled.svg)](https://www.jetbrains.com/?from=BoobBot)
