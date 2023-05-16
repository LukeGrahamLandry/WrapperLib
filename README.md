# WrapperLib

A collection of multi-platform implementations of common tasks for developing Minecraft mods. 

- Provides a uniform api across mod loaders and minecraft versions.
- A priority is placed on never manually writing serialization code for nbt or byte buffers.
- Designed to be modular, so you can shadow only the parts you need and have no external dependencies. 
- Each api concept is exposed as a Builder like class with no additional setup required.
- Extensive documentation (wiki & javadocs).

Supported Mod Loaders: Forge, Fabric, Quilt  
Supported Versions: 1.19.2, 1.18.2, 1.16.5

## See [wiki](https://github.com/LukeGrahamLandry/WrapperLib/wiki) for more info. 

- [NetworkWrapper](https://github.com/LukeGrahamLandry/WrapperLib/wiki/Network-Usage): send information between the client and the server.
- [ConfigWrapper](https://github.com/LukeGrahamLandry/WrapperLib/wiki/Config-Usage): allow players to configure mod features by editing json files.
- [DataWrapper](https://github.com/LukeGrahamLandry/WrapperLib/wiki/Saved-Data-Usage): save extra information with world data.
- [RegistryWrapper](https://github.com/LukeGrahamLandry/WrapperLib/wiki/Registry): an api like Forge's deferred register that can be called from common code.
- [Resources](https://github.com/LukeGrahamLandry/WrapperLib/wiki/Resources): load information from data packs or resource packs.
- [Keybinds](https://github.com/LukeGrahamLandry/WrapperLib/wiki/Keybinds): react to keyboard input, automatically synced to the server.
- [Helpers](https://github.com/LukeGrahamLandry/WrapperLib/wiki/Helpers): simple utilities for multi-platform mods.

## Official Downloads

- https://www.curseforge.com/minecraft/mc-mods/wrapperlib
- https://modrinth.com/mod/wrapperlib
- https://github.com/LukeGrahamLandry/WrapperLib/releases
- https://maven.lukegrahamlandry.ca
