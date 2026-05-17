# Figura Server Backend (standalone)

## Development

This project uses a fork of the _cloche_ Minecraft Gradle plugin to build.
All Minecraft versions & loaders are on the same branch.

> [!warning]  
> before you clone the project, read the "IDE sync resource usage"
> section below.

### 45 source sets!

- `src/common/main`: all
    - `src/common/<version>`: common code by Minecraft version
    - `src/fabric/any`: common code for Fabric
        - `src/fabric/<version>`: Fabric-specific code by Minecraft version
    - `src/forge/any`: common code for Forge
        - `src/forge/<version>`: Forge-specific code by Minecraft version
    - `src/neoforge/any`: common code for NeoForge
        - `src/neoforge/<version>`: NeoForge-specific code by Minecraft version
        - 1.20.2 NeoForge is not supported.

### IDE sync resource usage

- initial sync time (very dependent on network throughput & cpu): 59m 48s :concern:
- repository: +4.47G
- `.gradle/caches`: +8.6G
