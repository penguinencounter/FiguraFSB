package figurafsb.configurator

val options = extensions.create<OptionsExt>("fsbOptions")

project.afterEvaluate {
    options.postConfigure()
}
