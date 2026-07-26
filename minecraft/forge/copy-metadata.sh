#!/usr/bin/env bash

if [ "$(basename "$(realpath .)")" != "forge" ]; then
  echo "please run from the 'forge' folder. thanks" 1>&2
  exit 1
fi

for version in *; do
  if ! [ -d "./$version" ]; then continue; fi
  if [ "$version" = "any" ]; then continue; fi
  echo "$version"
  mkdir -p "./$version/src/main/resources/META-INF"
  cp -fT "./any/src/main/resources/META-INF/.template-mods.toml" "./$version/src/main/resources/META-INF/mods.toml"
done

echo "done" 1>&2
