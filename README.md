# GuildGrabber
A program to download all messages in a Discord server

# How To Use
Download or build, then run the jarfile in a terminal or command prompt.
You will see a notice about a file called token.ini.
Find your Discord API token, then paste it into the file.
Launch the jarfile with the Discord server you want to archive's ID as an argument.
If all goes well, the program will begin to archive the server!

Example (run in cmd or a terminal): `java -jar guild_grabber.jar <server ID>`

# A Note About Memory Usage
This program is known to crash because it goes of the the Java VM default memory limit. To raise this limit, add `-Xms1024m` to the command to raise the limit to 1GB, or `-Xms2048m` for 2GB, etc.

Example: `java -jar -Xms1024m guild_grabber.jar <server ID>`

# Safety
Discord probably doesn't want you doing this, so be careful. Also, if you use your own account, then you have a larger chance of getting banned. Use with caution, and don't blame me for anything that happens to your account.
