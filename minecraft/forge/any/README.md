So this subproject is a little bit useless. I can't have the actual `mods.toml`
in here since the dev environment builds a separate jar for each subproject
involved in a run-task. Forge proceeds to crash out because the `forge.any` jar
is invalid because the `mods.toml` isn't in the same jar as `@Mod` `FSBForgeInit.class`.

Instead, it's just used to hold the template files for the `mods.toml`
in all the versioned subprojects. You can run `minecraft/forge/copier.sh` to
clone that file into all the subprojects.