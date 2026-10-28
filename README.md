# Cobblemon RIze Tweaks — Fabric 1.20.1 Backport

An unofficial community backport of [Cobblemon RIze Tweaks](https://github.com/RIze2kNight/cobblemonRIzetweaks-1.21.1) for Minecraft 1.20.1 and Fabric. The original project targets Minecraft 1.21.1.

## Features

- Jump to a numbered PC box. In the PC, press the configured **Enable PC Box Jump** key (Left Alt by default), type the one-based box number, then press Enter. Press Escape to cancel. This replaces the 1.21.1 title-click interaction because the 1.20.1 PC screen does not expose the same title widget.
- Highlight Pokémon with Hidden Abilities in the PC and Summary screens.
- Show move effectiveness hints during battles, including multi-target battles and supported type changes.
- Scroll through Summary tabs and configure the move helper style.
- Configure features through Cloth Config; Mod Menu can provide the config screen when installed.

## Requirements

- Minecraft 1.20.1
- Fabric Loader 0.15.11 or newer
- Fabric API
- Fabric Language Kotlin
- Cobblemon 1.5.2 for Minecraft 1.20.1 or newer compatible 1.20.1 release
- Cloth Config 11.1.118 or newer

Install the mod and its required dependencies in the client `mods` folder. Mod Menu and Cobblemon UI Tweaks are optional integrations.

## Source and attribution

- Original project: [RIze2kNight/cobblemonRIzetweaks-1.21.1](https://github.com/RIze2kNight/cobblemonRIzetweaks-1.21.1)
- Backport source: [JustHari01/Cobblemon-RIze-Tweaks-1.20.1-Fabric-Backport](https://github.com/JustHari01/Cobblemon-RIze-Tweaks-1.20.1-Fabric-Backport)
- License: MIT; the upstream copyright and license notice are retained in `LICENSE`.

This is an unofficial port and is not affiliated with or endorsed by the original author. The port changes game-version compatibility and adapts PC box jumping for the 1.20.1 interface; some behavior may differ from the 1.21.1 release.

AI tools assisted with the port and compatibility changes; the resulting code was reviewed and build-checked by the port maintainer.

