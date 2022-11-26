
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
  - [ ] generate english lang file based on comment annotations
- [ ] min and max values for numbers with annotations
- [ ] validator annotation 
- [ ] watch file for changes and reload config
- [ ] reload configs with reload command
- [ ] comment translations

TODO: chart with comparison to other config libraries
- Forge Config & Fuzss/forgeconfigapiport-fabric (verbose)
- Draylar/omega-config (fabric only)
- wisp-forest/owo-lib (fabric only, bloat)
- shedaniel/cloth-config
- ZsoltMolnarrr/TinyConfig (fabric only, not synced)
- isXander/YetAnotherConfigLib (client only, fabric only)
- LambdAurora/SpruceUI (verbose, fabric only, not synced)
- TeamMidnightDust/MidnightLib (bloat, not synced)

## Packets

- [ ] automatically serialize your data class (to json to bytebuffer)
- [ ] cross platform: forge, fabric, quilt
- [ ] send options: client -> server, server -> client, server -> all clients
- [ ] handshake system to know that server and client are on the same version

## Player Data

replaces capabilities or cardinal components on players

- [ ] store data in a json file (map of uuid to data object) instead of nbt for easy editing
- [ ] automatically serialize your data class
- [ ] sync data to clients
- [ ] cross platform: forge, fabric, quilt

## Geckolib Attack Animations

