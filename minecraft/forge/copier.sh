#!/usr/bin/env bash

if [ "$(basename "$(realpath .)")" != "forge" ]; then
  echo "please run from the 'forge' folder. thanks" 1>&2
  exit 1
fi

template="./any/src/main/templates"

for version in *; do
  if ! [ -d "./$version" ]; then continue; fi
  if [ "$version" = "any" ]; then continue; fi
  echo "$version"
  resMeta=./$version/src/main/resources/META-INF
#  init=./$version/src/main/java/org/figuramc/fsb2/forge
  mkdir -p "$resMeta"
#  mkdir -p "$init"
  cp -fT "$template/mods.toml" "$resMeta/mods.toml"
#  cp -fT "$template/FSBForgeInit.java" "$init/FSBForgeInit.java"
done

echo "done" 1>&2
