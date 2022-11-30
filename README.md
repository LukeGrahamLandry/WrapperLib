# WrapperLib

A collection of multi-platform implementations of common tasks for developing Minecraft mods. 

- Provides a uniform api across mod loaders and minecraft versions.
- A priority is placed on never manually writing serialization code for nbt or byte buffers.
- Designed to be modular, so you can shadow only the parts you need and have no external dependencies. 

Supported Mod Loaders: Forge, ~~Fabric, Quilt~~  
Supported Versions: 1.19, ~~1.18, 1.16~~  
API Objects: ConfigWrapper, PacketWrapper, DataWrapper  

haven't tested on servers yet. its possible syncing doesn't actually work cause currently objects might be on the same thread

## Installation

```groovy
repositories {
    maven { url "https://maven.lukegrahamlandry.ca" }
}

dependencies {
    shade group: 'ca.lukegrahamlandry.lib', name: 'MODULE-LOADER', version: 'LIB_VER+MC_VER'
}
```

- MODULE: packets, config, data
- LOADER: common, forge, fabric
- MC_VER: 1.19, 1.18, 1.16
- LIB_VER: MAJOR.MINOR.PATCH (see the latest version numbers in [gradle.properties](gradle.properties))

**You must relocate my packages when using the shadow plugin, or you will conflict with other mods. You must also call `mergeServiceFiles()` for my crossplatform stuff to work.**

```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '7.1.2'
}

configurations {
    shade
    implementation.extendsFrom shade
}

shadowJar {
    archiveClassifier = ''
    configurations = [project.configurations.shade]
    relocate 'ca.lukegrahamlandry.lib', "${project.group}.shadow.wrapperlib"
    finalizedBy 'reobfShadowJar'
    mergeServiceFiles()
    exclude 'ca.lukegrahamlandry.lib.mod.WrapperLibForgeModMain'
    append 'META-INF/accesstransformer.cfg'
}

assemble.dependsOn shadowJar

reobf {
    shadowJar {}
}
```

on fabric you need to add my mod and client init classes to your entry points (before yours)

note: 
i cant just append fabric accesswideners cause they have version info at the top. 
they get added to fabric.mod.json but doesnt support giving a list
write my own Transformer and make sure to always use the same aw version/mappings

## Config

- [X] support configs on both client and server sides
- [X] sync server side configs to the client
- [X] define default values in code
- [X] generate default config file with comments
- [X] json format for good support of nested map data structures
- [X] server checks global folder so modpacks can ship defaults
- [X] shadow so no external dependencies
- [ ] cross platform: forge, fabric, quilt
- [X] lightweight: no extra bloat increasing your jar size
- [X] no boilerplate: just write a data class
- [ ] automatic integration with config screen api
- [ ] generate english lang file for screens based on comment annotations
- [ ] min and max values for numbers with annotations
- [ ] validator annotation 
- [ ] watch file for changes and reload config
- [ ] reload configs with reload command
- [ ] comment translations
- [ ] update with default values of new fields 
- [ ] array or map of registry objects that removes missing ones
- [ ] could be wrapped in data.RegistryObjectGroup or data.RegistryObjectMap that can accept tags as well?
- [X] provided json serializers for ResourceLocation, CompoundTag, ItemStack
- [ ] subdirectory support in case you want lots of config files without risk of conflicting with other mods

TODO: chart with comparison to other config libraries
- Forge Config & Fuzss/forgeconfigapiport-fabric (verbose)
- Draylar/omega-config (fabric only)
- wisp-forest/owo-lib (fabric only, bloat)
- shedaniel/cloth-config (not synced)
- Minenash/TinyConfig (fabric only, not synced)
- ZsoltMolnarrr/TinyConfig (fabric only, not synced)
- isXander/YetAnotherConfigLib (client only, fabric only)
- LambdAurora/SpruceUI (verbose, fabric only, not synced)
- TeamMidnightDust/MidnightLib (bloat, not synced)

## Packets

Replaces `SimpleImpl` on Forge or `*PlayNetworking` on Fabric.

- [X] automatically serialize your data class (to json to bytebuffer)
- [ ] cross platform: forge, fabric, quilt
- [X] send options: client -> server, server -> client, server -> all clients
- [ ] handshake system to know that server and client are on the same version

CompressedPacketWrapper that does reflection whatever to just write field values in order because the other side already knows what their names are

## Saved Data

Replaces `WorldSavedData` and capabilities on Forge or cardinal components on Fabric (although without the focus on mod interoperability).

Data for all players is stored in a json file mapping uuids to data objects. 
World data is just a file with a json object (perhaps multiple files if stored separate per dimension).

- [X] store data in a json file instead of nbt for easy editing
- [X] automatically serialize your data class
- [X] sync data to clients
- [ ] cross platform: forge, fabric, quilt
- [X] store data linked to each server or world or player
- [ ] system for loading data packs to a Map<ResourceLocation, Object>
- [ ] ItemStackDataWrapper since you cant have fields on an item, dont even need the json file stuff, just save to nbt. i think it syncs automatically 
- [ ] store data linked to any entity or tile entity. maybe just go to nbt there as well 


## Geckolib Animation Managers

- [ ] use geckolib in common code 1.16/1.18 (fix official mappings fabric)
- [ ] looped animations with conditions for triggering (ie idle, walk, swim, fly)
- [ ] jump animations
- [ ] attack animations
- [ ] death animations

look at how 4.0 does it? i think they have some helpers

## Energy

- uniform api for making items / tile entities consume energy
- let each machine choose what types of energy it accepts
- let each machine choose conversion rates for each type of energy with reasonable defaults 
- on forge wrap an RF capability 
- botania mana: items absorb from or give to pools, tiles can be selected with living wand
- compat with whatever fabric tech mods have

## Registries

feels like there are a lot of aggressively clever registry helper libs 
but really all i want is to wrap a deferred register so i can use it from common code
then in mod init just RegistryWrapper.init(), on forge it can get the mod bus and fabric doesn't need it

## Example Mod

have two versions. showing what you'd have to do without the library
write tutorials based on these 
make a quilt version to make sure everything works 

- track each player's kills of players/mobs and display the numbers on screen
- give a configurable effect when they jump
- give a configurable enchanted sword when they join the game
- spawn a configurable group of mobs when a player dies

## Base Module

- [X] type adapters: nbt, item stack
- [X] system for event listeners so i dont need that code many times
- [ ] annotated forge event classes for those event listeners 
- [X] canFindClass helpers for each module

distribute a fat jar that contains everything
i could have just done everything in a normal multiloader project and used shade to exclude the modules that arent being used
i think i'll do that, it just makes it so much less overhead to keep track of everything
they're still split into packages to its easy to see which parts are where
if you shadow it you have to exlude the mods.toml and the services of the modules that are excluded
i could write a gradle plugin that handles it

putting it on curseforge makes me part of the problem

implementing config and data on bukkit would give me a good starting place in case someone offered to pay a lot for a plugin 
even packets would work. just only meaningful if theres a client mod to accept them

name adapter map in case they want to change the name of config files or data files
register(context, oldname, newname, old dir, new dir, transformer)
transformer is a function JsonElement -> JsonElement that maps the old data format to the new data format
system for data_format_version numbers in the json that trigger adapters in the same way
context is like config or PlayerDataWrapper or whatever. make an enum

## Make Shading Work

- figure out how to merge mixins
    - unique mixins.json and refmap.json name
    - forge: add to jar manifest
    - fabric: add to fabric.mod.json
    - does shade remap mixin package name?
- probably have to be a gradle plugin
- tell it which to remap ie. featurelib-config.mixins.json to modid-featurelib-config.mixins.json
- i want each mod to have their own copy of the mixins in case i change them, no weird shared version negotiation 
  - just don't ever overwrite methods; only use as hooks so its fine for there to be multiple 

## my mods plan

mod - definitely improvement (might rewrite but effort)
torcherino - config, (packets)
mimic - config
simple xp config - config
cosmetology - packets
staff of travelling - config, (packets)
find my friends - (config, packets) if i do fabric port
rain events - data, datapacks
