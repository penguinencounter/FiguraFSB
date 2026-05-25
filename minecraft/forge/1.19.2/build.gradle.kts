// This is a generated buildscript. You shouldn't edit it.
// If you want to edit the template, consult the /templates folder,
// as well as the generate_buildscripts.py script.

plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.19.2"
        upstream(":common:1.19.2")
    }
}