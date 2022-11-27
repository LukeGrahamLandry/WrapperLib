# FeatureLib

A collection of multi-platform implementations of common tasks for developing Minecraft mods. 
Designed to be modular, so you can jar-in-jar only the parts you need and have no external dependencies. 
A priority is placed on never manually to writing serialization code for nbt or byte buffers. 

Features: Config, Packets, Saved Data  
Supported Mod Loaders: Forge, Fabric, Quilt  
Supported Versions: 1.19, 1.18, 1.16  

## Installation

```groovy
repositories {
    maven { url "https://maven.lukegrahamlandry.ca" }
}

dependencies {
    shade group: 'ca.lukegrahamlandry.lib', name: 'MODULE-LOADER', version: 'LIB_VER+MC_VER'
}
```

- MODULE: packets, config
- LOADER: forge, fabric
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
    relocate 'ca.lukegrahamlandry.lib', "${project.group}.shadow.featurelib"
    finalizedBy 'reobfShadowJar'
    mergeServiceFiles()
}

assemble.dependsOn shadowJar

reobf {
    shadowJar {}
}
```

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

Replaces `SimpleChannel` on Forge or `ClientPlayNetworking` and `ServerPlayNetworking` on Fabric.

- [ ] automatically serialize your data class (to json to bytebuffer)
- [ ] cross platform: forge, fabric, quilt
- [ ] send options: client -> server, server -> client, server -> all clients
- [ ] handshake system to know that server and client are on the same version

## Saved Data

Replaces `WorldSavedData` and capabilities on Forge or cardinal components on Fabric.

Data for all players is stored in a json file mapping uuids to data objects. 
World data is just a file with a json object (perhaps multiple files if stored separate per dimension).

- [ ] store data in a json file instead of nbt for easy editing
- [ ] automatically serialize your data class
- [ ] sync data to clients
- [ ] cross platform: forge, fabric, quilt

## Geckolib Animation Managers

