// This is a generated buildscript. You shouldn't edit it.
// If you want to edit the template, consult the /templates folder,
// as well as the generate_buildscripts.py script.

plugins {
    id("figurafsb.targets.neoforge-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.20.4"
        upstream(":common:1.20.4")
    }
}