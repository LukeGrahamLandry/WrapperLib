
## Config

- [X] support configs on both client and server sides
- [ ] sync server side configs to the client
- [X] define default values in code
- [X] generate default config file with comments
- [X] json format for good support of nested map data structures
- [X] server checks global folder so modpacks can ship defaults
- [X] shadow so no external dependencies
- [X] cross platform: forge, fabric, quilt
- [X] lightweight: no extra bloat increasing your jar size
- [X] no boilerplate: just write a data class
- [ ] automatic integration with cloth config screen
- [ ] min and max values for numbers with annotations
- [ ] validator annotation 
- [ ] watch file for changes and reload config
- [ ] reload configs with reload command

TODO: chart with comparison to other config libraries
- tinyconfig
- forge config
- cloth config / autoconfig
- https://modrinth.com/mod/midnightlib
- https://www.curseforge.com/minecraft/mc-mods/yacl
- https://github.com/LambdAurora/SpruceUI

## Packets

- [ ] automatically serialize your data class (to json to bytebuffer)
- [ ] cross platform: forge, fabric, quilt
- [ ] send options: client -> server, server -> client, server -> all clients
- [ ] handshake system to know that server and client are on the same version

## Player Data

replaces capabilities or cardinal components on players

- [ ] store data per player in a json file (map of uuid to data object)
- [ ] automatically serialize your data class
- [ ] sync data to clients
- [ ] cross platform: forge, fabric, quilt
