- [ ] quilt example mod
- [ ] config array / map comments on fields brought to top if list empty
- [ ] split synced data pack into multiple packets if it gets too big 
- [X] world configs check ./config (as well as ./defaultconfigs) before generating default

## 1.1.0

- ConfigWrapper can load a List or Map as the top level object
- KeybindWrapper
- PlatformHelper: isModLoaded
- MapDataWrapper optional lazy loading
- server side configs reload on /reload command 
- ResourcesWrapper: load data/resource packs
- ItemStackDataWrapper

## 1.0.0

Initial Release!

- NetworkWrapper
- RegistryWrapper
- ConfigWrapper
- DataWrapper: global, level, player
- EntityHelper: attributes, renderer, getSpawnEggConstructor
- PlatformHelper: isDevelopmentEnvironment, isDedicatedServer
