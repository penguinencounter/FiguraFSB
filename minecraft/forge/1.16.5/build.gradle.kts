// This is a generated buildscript. You shouldn't edit it.
// If you want to edit the template, consult the /templates folder,
// as well as the generate_buildscripts.py script.

plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java8()

    minecraft {
        version = "1.16.5"
        upstream(":common:1.16.5")
    }
}