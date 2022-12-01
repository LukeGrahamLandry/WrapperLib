# WrapperLib

A collection of multi-platform implementations of common tasks for developing Minecraft mods. 

- Provides a uniform api across mod loaders and minecraft versions.
- A priority is placed on never manually writing serialization code for nbt or byte buffers.
- Designed to be modular, so you can shadow only the parts you need and have no external dependencies. 
- Each api concept is exposed as a Builder like class with no additional setup required.
- Extensive documentation (wiki & javadocs).

Supported Mod Loaders: Forge, Fabric, ~~Quilt~~  
Supported Versions: 1.19, ~~1.18, 1.16, 1.12~~  

API Objects: ConfigWrapper, NetworkWrapper, DataWrapper, RegistryWrapper, EntityHelper  


haven't tested on servers yet. its possible syncing doesn't actually work cause currently objects might be on the same thread

- test servers
- fabric impl & test mod
- quilt test mod
- config reloading
- data for item stacks, entities, tile entities
- test RegistryWrapper & EntityHelper
- add maven to publish.gradle
- wiki
- packet version handshake
- config / data version adapters

the sources platform jar doesn't include common classes. might be a problem for maven finding it on single loader projects

## Official Sources

- curseforge
- modrinth
- https://github.com/LukeGrahamLandry/WrapperLib/releases
- https://maven.lukegrahamlandry.ca

## Installation

```groovy
repositories {
    maven { url "https://maven.lukegrahamlandry.ca" }
}

dependencies {
    shade group: 'ca.lukegrahamlandry.lib', name: 'WrapperLib-LOADER-MC_VER', version: 'LIB_VER'
}
```

- LOADER: Common, Forge, Fabric
- MC_VER: 1.19, 1.18, 1.16
- LIB_VER: MAJOR.MINOR.PATCH (see the latest version numbers in [gradle.properties](gradle.properties))

## Shadowing 

- **You must relocate my packages when using the shadow plugin, or you will conflict with other mods.**
- On Fabric, you need to add my mod and client init classes to your entry points.
- On Forge, you need to call `EventWrapper.triggerInit()` from your mod constructor. 

```groovy
shadowJar {
    relocate 'ca.lukegrahamlandry.lib', "${project.group}.shadow.wrapperlib"
}
```

note: 
i cant just append fabric accesswideners cause they have version info at the top. 
they get added to fabric.mod.json but doesnt support giving a list
write my own Transformer and make sure to always use the same aw version/mappings
look at architectury's thing, they seem to have some sort of combiner code

## Config

- [X] support configs on both client and server sides
- [X] sync server side configs to the client
- [X] define default values in code
- [X] generate default config file with comments
- [X] json format for good support of nested map data structures
- [X] server checks global folder so modpacks can ship defaults
- [X] no boilerplate: just write a data class
- [X] provided json serializers for ResourceLocation, CompoundTag, ItemStack
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

## Network

Replaces `SimpleImpl` on Forge or `*PlayNetworking` on Fabric.

- [X] automatically serialize your data class (to json to bytebuffer)
- [X] send options: client -> server, server -> client, server -> all clients
- [ ] handshake system to know that server and client are on the same version

CompressedNetworkWrapper that does reflection whatever to just write field values in order because the other side already knows what their names are

## Saved Data

Replaces `WorldSavedData` and capabilities on Forge or cardinal components on Fabric (although without the focus on mod interoperability).

Data for all players is stored in a json file mapping uuids to data objects. 
World data is just a file with a json object (perhaps multiple files if stored separate per dimension).

- [X] store data in a json file instead of nbt for easy editing
- [X] automatically serialize your data class
- [X] sync data to clients
- [X] store data linked to each server or world or player
- [ ] system for loading data packs to a Map<ResourceLocation, Object>
- [ ] ItemStackDataWrapper since you cant have fields on an item, dont even need the json file stuff, just save to nbt. i think it syncs automatically 
- [ ] store data linked to any entity or tile entity. maybe just go to nbt there as well 

## Registries

- [X] provide an implementation like Forge's DeferredRegister that can be used from common code
- [ ] handle block items automatically

Alternatives:

https://github.com/architectury/architectury-api
They provide a `Registrar` with almost the same api as my `RegistryWrapper` but a much more complex implementation (compare their 780 line [forge RegistriesImpl](https://github.com/architectury/architectury-api/blob/1.19.2/forge/src/main/java/dev/architectury/registry/registries/forge/RegistriesImpl.java) to my 50 line [forge RegistryPlatformImpl](https://github.com/LukeGrahamLandry/WrapperLib/blob/1.19/Forge/src/main/java/ca/lukegrahamlandry/lib/registry/forge/RegistryPlatformImpl.java))
They also do a lot of work properly reimplementing deferred registers in common code for fabric projects to use (see [thier package](https://github.com/architectury/architectury-api/tree/1.19.2/common/src/main/java/dev/architectury/registry/registries) vs [my class](https://github.com/LukeGrahamLandry/WrapperLib/blob/1.19/Common/src/main/java/ca/lukegrahamlandry/lib/registry/RegistryWrapper.java)).
My approach is to notice as early as possible if you're running on fabric and just pass it on to vanilla's code.
My registry stuff is tiny enough that it can reasonably be shadowed.


https://github.com/VazkiiMods/AutoRegLib
- they also provide basic versions of a lot of registry objects you might want to extend for your own. Mine just has the minimum required for handling the actual registration. 
- more complex

https://github.com/lukebemish/mcdevutils
- gradle plugin that automatically generates platform specific registry code at build time based on magic annotations 
- i have much respect for the cleverness of this but am personally intimidated by the prospect of adding complexity that would take a long time to understand if it broke. 
- WrapperLib focuses on providing the simplest possible cross-platform registry api so even if something breaks, you have only two of my classes to look through to find the problem. 

## Entity

- `EntityHelper#attributes`: register an entity's attributes from common code
- replacement for NetworkHooks#getEntitySpawningPacket

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
- [X] system for event listeners so i don't need that code many times
- [X] annotated forge event classes for those event listeners 
- [X] canFindClass helpers for each module

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

## wiki

Case Studies
Torcherino 1.2.3 had x lines (x kb) of config and packets. By implementing WrapperLib Torcherino 1.2.4 reduced that to just y lines (y kb)!
Jar size went from x kb to y kb (z% increase)

About
  - Case Studies
  - Serialization
Installation: loom, forge gradle, shadowing
Each Wrapper: the features checklist
  - Alternatives: explain what it replaces on each loader and other libs that do the same thing
  - Usage
Future Ideas
Problems
Contributing 

## Licensing Info

- WrapperLib is available under the MPL 2.0 License as seen in the LICENSE file. This summary is not a substitute for the full license text.
- You may distribute a Larger Work containing official versions of WrapperLib (ie by shading it into your mods). 
  - You must prominently link to WrapperLib's Source Code Form (this repo).
  - Your Larger Work may be under any license you like.
- You may create and distribute modified versions of WrapperLib. 
  - You must prominently link to the Source Code Form of any modified versions of WrapperLib files.
  - Modified versions of WrapperLib files must be available under the MPL 2.0.
  - The rest of your modified version may be under any license you like.
- You may not remove license notices from any WrapperLib files. 
