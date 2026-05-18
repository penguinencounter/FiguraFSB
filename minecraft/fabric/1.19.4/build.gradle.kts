// This is a generated buildscript. You shouldn't edit it.
// If you want to edit the template, consult the /templates folder,
// as well as the generate_buildscripts.py script.

// template parameters: version=1.19.4, java=17

plugins {
    id("figurafsb.targets.fabric-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.19.4"
        upstream(":common:1.19.4")
    }
}