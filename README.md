# WrapperLib

A collection of multi-platform implementations of common tasks for developing Minecraft mods. 

- Designed to be modular, so you can jar-in-jar only the parts you need and have no external dependencies. 
- A priority is placed on never manually to writing serialization code for nbt or byte buffers. 
- Provides a uniform api across mod loaders and minecraft versions

API Objects: ConfigWrapper, PacketWrapper, DataWrapper
Supported Mod Loaders: Forge, Fabric, Quilt  
Supported Versions: 1.19, 1.18, 1.16

1. config
2. packets
3. data
4. figure out mixins
5. base
6. example mod
7. tutorials
8. energy
9. geckolib

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

On forge, make sure to call `fg.deobf` on the dependency.  

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
    append 'META-INF/accesstransformer.cfg'
}

assemble.dependsOn shadowJar

reobf {
    shadowJar {}
}
```

note: 
i cant just append fabric accesswideners cause they have version info at the top. 
they get added to fabric.mod.json but doesnt support giving a list
write my own Transformer and make sure to always use the same aw version/mappings
## Config

- [X] support configs on both client and server sides
- [ ] sync server side configs to the client
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

- [ ] automatically serialize your data class (to json to bytebuffer)
- [ ] cross platform: forge, fabric, quilt
- [ ] send options: client -> server, server -> client, server -> all clients
- [ ] handshake system to know that server and client are on the same version

## Saved Data

Replaces `WorldSavedData` and capabilities on Forge or cardinal components on Fabric (although without the focus on mod interoperability).

Data for all players is stored in a json file mapping uuids to data objects. 
World data is just a file with a json object (perhaps multiple files if stored separate per dimension).

- [ ] store data in a json file instead of nbt for easy editing
- [ ] automatically serialize your data class
- [ ] sync data to clients
- [ ] cross platform: forge, fabric, quilt
- [ ] store data linked to each server or world or player
- [ ] system for loading data packs to a Map<ResourceLocation, Object>

global, world, player, entity
synced or server or client
saved or not
global client is just a GenericHolder
config is a global that writes with comments
not synced plus not saved is just a map

DataWrapper data = DataWrapper.world(Data.class).synced().saved().named("modid:data").dir("modid")
Data instance = data.get(world)
data.setDirty()

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

feels like there are a lot of agressivly clever registry helper libs 
but really all i want is to wrap a deferred register so i can use it from common code
then in mod init just RegistryWrapper.init() on forge it can get the mod bus and fabric doesn't need it

## Example Mod

have two versions. showing what you'd have to do without the library
write tutorials based on these 
make a quilt version to make sure everything works 

- track each player's kills of players/mobs and display the numbers on screen
- give a configurable effect when they jump
- give a configurable enchanted sword when they join the game
- spawn a configurable group of mobs when a player dies

## Base Module

- type adapters: nbt, item stack
- fabric: mixin that loads InjectedModInit and InjectedClientModInit services since no annotations
- canFindClass helpers for each module

everything needs base
data and config need packets if you call sync
distribute a fat jar that contains everything
putting it on curseforge makes me part of the problem

should rename it WrapperLib 
i like the pattern of exposing every api as a ThingWrapper
seems to reflect what's actually going on well

implementing config and data on bukkit would give me a good starting place in case someone offered to pay a lot for a plugin 
even packets would work. just only meaningful if theres a client mod to accept them

## Make Shading Work

- figure out how to merge mixins
    - unique mixins.json and refmap.json name
    - forge: add to jar manifest
    - fabric: add to fabric.mod.json
    - does shade remap mixin package name?
- figure out how to merge mod initializers on fabric
- perhaps just do initializers as mixins since those are more important 
- probably have to be a gradle plugin
- tell it which to remap ie. featurelib-config.mixins.json to modid-featurelib-config.mixins.json
- i want each mod to have their own copy of the mixins in case i change them, no weird shared version negotiation 
  - just don't ever overwrite methods; only use as hooks so its fine for there to be multiple 
- have a mixin that loads services for mod init interface. does it become a problem if i use the same name as fabric?

## my mods plan

mod - definitely improvement (might rewrite but effort)
torcherino - config, (packets)
mimic - config
simple xp config - config
cosmetology - packets
staff of travelling - config, (packets)
find my friends - (config, packets) if i do fabric port
