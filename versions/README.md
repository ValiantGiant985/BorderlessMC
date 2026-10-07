# Minecraft Version Modules

Each supported Minecraft release belongs in its own module.

Current modules:

- `mc26.3` - Minecraft 26.3
- `mc26.2` - Minecraft 26.2
- `mc26.1.2` - Minecraft 26.1.2
- `mc26.1.1` - Minecraft 26.1.1
- `mc26.1` - Minecraft 26.1

A version module owns the Minecraft, loader, mappings, and compatibility dependencies for that target. Version-specific source can live inside the module when Minecraft changes require different implementations.

The root project is only the umbrella build.
