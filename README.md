# chef-eclipse-plugin
Amaze your friends, manage chef cookbooks with Eclipse!

### Some notes so far.
- It's VERY early code right now.
- I wrote it in Scala, because that's the kind of person I am. No, really... I wrote it in scala because among many other things, scala is just a much better java.
- The plugin makes extensive use of chef tools (the chef executable, kitchen, berks, etc), because I could get it up and running much faster this way. 
- You'll need to add the scala plugin to eclipse in order to work on it (and I'm guessing, running it)
- You'll need the ruby plugin in order for the plugin to work perfectly (not a requirement, but if you use chef you're also using ruby, right?)
- Having a yaml plugin is nice too. 
- I don't yet have a global configuration page
- I'm making some assumptions about your environment, e.g. I'm assuming your chef executable is in /usr/bin/chef things like this will be in the config file
