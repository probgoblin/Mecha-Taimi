# Riz GW2 Event Bot
This bot is meant to be used to help organize teams for events (raids, fractals, dungeons, etc.) in discord servers for GuildWars 2.
It attempts to create a streamlined user experience so that creating and joining are easy.

## Discord.com and Gateway Intents
As you may know, the Discord API was updated over time and we used to make use of an older version.
The new changes requiring us to upgrade our API connector also require the the new Gateway Intents.
We have updated the bot using the new Gateway Intents but to run the bot, you will have to enable the `Server Members Intent` for the bot from your Developer Portal's bot page. It is located in the `Bot` tab in the `Privileged Gateway Intents` section.
If you don't enable this the bot will not work, it will just log off again as soon as the first action is called.

## How does it work?
Once the bot is running, you need to set the event leader role (using the !setEventLeaderRole \[role\] command) to a server user role.
Any user who should be able to create events needs this role.

To create an event, use the !createEvent command. This will prompt the bot to ask you some questions about your event, and once you are done, it will create the embedded message
announcing the event in the channel that you specified.
For people to join the event, they simply need to click a reaction on the embedded message and then answer the bot on what role they want to play.
After this do this, they can click another specialization reaction to set themselves as a flex role (other roles they can play if necessary).

Event leaders can also remove people from events via !removeFromEvent \[event id\] \[name\]

## How to install the bot
First, the bot requires that Java 8 or higher be installed. Then, the best way to install the bot is to pull this repository
and compile it with maven.

Then, you need to put the resulting jar file where-ever you want to run the bot from.

Next, you need to unzip the icon pack included in this repository and put all of the icons in discord
with the default names discord gives them. Each icon **must** be named after the specialization it represents.

Finally, you need to create a bot via the discord developer application creation page and copy the bot token and put it in a file named 'token'
in the same directory as the jar file.

Finally, you can run the bot and invite it to your discord server and start using it for events!

## Credits
- Christopher Bitler: original bot development
- Tyler "Inverness": original bot idea

- Jeremy D. Thralls, Franziska Mueller: extensions to the original bot

## License
GPLv3
