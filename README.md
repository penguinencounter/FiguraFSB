# Figura Server Backend, standalone

## Development

### Sync performance & resources

This project has a _LOT_ of dependencies. By my estimates,
you'll need around 7 gigabytes of disk space for cache (yes, really!!)

The main culprit for this is the fact that you're downloading at least 30 copies
of Minecraft in various forms.

The initial sync might take a long time due to all the downloading that needs
to happen. Subsequent Gradle operations should be significantly quicker, especially
if you keep using the same Gradle daemon throughout.

Some statistics that may help you decide what to do:
* This project has CI. The CI process caches only the global Gradle cache (`~/.gradle`).
  * Running a build from no cache takes upwards of **30 minutes** (!)
  * With the cache loaded (but not including the project cache), a build takes **5 minutes**
* On my personal computer (which is pretty powerful to be fair), I can run a full build in **50 seconds** with
  the global and project cache primed (`gradlew clean build --rerun-tasks`)

### Running the game

This project **does not create run configurations for you**. There would be
60 of them and you'd probably lose a bunch of time clicking the wrong configurations.

Also, the run configurations wouldn't work anyway due to this project's resource processing system.
Instead, to run the game, use the `runClient` or `runServer` tasks in conjunction with the `-PoverrideMC=<mc version>`
command line flag. For example, to run a 1.20.1 Fabric Dedicated Server, it would be

```sh
./gradlew -PoverrideMC=1.20.1 :minecraft:fabric:1.20.1:runServer
```

(psst: you should learn shorthand for the tasks you use frequently. for example that's `./gradlew -PoverrideMC=1.20.1 :m:fab:1.20.1:ruS` :P)

Not providing `overrideMC` will result in broken dependency info in the built mod, which
will cause mod loading to inevitably fail.

> [!note]  
> If any Gradle witches/wizards/sorcerers want to help
out and make this situation less bad, we need to somehow transform the resources from a common
module according to which module is depending on the common module.
> 
> We're accomplishing this in release builds for now by transforming the final product in `shadowJar`.
> If there were a way to transform the incoming resources before that on the dependent project's side
> before it gets sent to the classpath, that would be perfect!

