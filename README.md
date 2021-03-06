# InterServerPorts
This is a proof of concept for enabling plugins that don't natively support cross server teleports via the BungeeCord proxy.

It uses a map of world aliases to teleport players between servers.

If you want to test and play around with it you can get builds from the [Minebench.de Jenkins](http://ci.minebench.de/job/InterServerPorts/)

### Some Thoughts about Compatibility

There are two major problems with the concept behind this plugin:
* The world that is mapped to another server needs to exist
* Teleport destinations are cached by most plugins

So if you would want it to work proberly you would have to make sure that Bukkit knows about all your mapped worlds, one could generate them for example while loading the plugin or at least make sure they exist.

For the caching issue one would have to link the data of the different servers together and maybe reload the config on every server everytime one server changes something. This concept might therefor only be suited for simple warps that are configured one time only.

If you don't want to setup some form of syncing and wont change the locations often (for example for warps or spawns) it might also be practical if the plugins you want to use this with allow to directly set the location rather than having to execute a command at the destination's position, that way you don't have to figure out the exact location by constantly switching between two servers. Maybe directly editing the location data works even better if the plugins have an easy editable config and a reload function build in. (I'm thinking of Essentials' warps here)

### License
```
InterServerPorts
Copyright (C) 2016 Max Lee (https://github.com/Phoenix616/)

This program is free software: you can redistribute it and/or modify
it under the terms of the Mozilla Public License as published by
the Mozilla Foundation, version 2.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
Mozilla Public License v2.0 for more details.

You should have received a copy of the Mozilla Public License v2.0
along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
```
