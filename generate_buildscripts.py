import os
from pathlib import Path

from jinja2 import Environment, FileSystemLoader, select_autoescape

env = Environment(loader=FileSystemLoader("templates"), autoescape=select_autoescape())


def generateLoomPropertiesFile(platform: str):
    return f"""
# this file is generated! your edits will be lost next time the build setup changes
loom.platform={platform}
    """.strip() + "\n"


def main():
    commonTemplate = env.get_template("common.gradle.kts.jinja2")
    fabricTemplate = env.get_template("fabric.gradle.kts.jinja2")
    forgeTemplate = env.get_template("forge.gradle.kts.jinja2")
    neoForgeTemplate = env.get_template("neoforge.gradle.kts.jinja2")

    sourceRoot = Path("./minecraft")
    commons = sourceRoot / "common"
    fabrics = sourceRoot / "fabric"
    forges = sourceRoot / "forge"
    neoforges = sourceRoot / "neoforge"

    neoForgeAfter = "1.20.2"
    tuples = [
        ("1.16.5", 8),
        ("1.18.2", 17),
        ("1.19.2", 17),
        ("1.19.3", 17),
        ("1.19.4", 17),
        ("1.20.1", 17),
        ("1.20.2", 17),
        ("1.20.4", 17),
        ("1.20.6", 21),
        ("1.21.1", 21),
        ("1.21.3", 21),
        ("1.21.4", 21),
    ]
    versionOnly = [version for version, _ in tuples]
    neoForgeIdx = versionOnly.index(neoForgeAfter)

    for i, (version, java) in enumerate(tuples):
        common = commons / version
        fabric = fabrics / version
        forge = forges / version
        hasNeo = i >= neoForgeIdx
        neoforge = neoforges / version

        os.makedirs(common, exist_ok=True)
        os.makedirs(fabric, exist_ok=True)
        os.makedirs(forge, exist_ok=True)
        with open(common / "build.gradle.kts", "w") as f:
            f.write(commonTemplate.render(version=version, java=java))
        with open(fabric / "build.gradle.kts", "w") as f:
            f.write(fabricTemplate.render(version=version, java=java))
        with open(forge / "build.gradle.kts", "w") as f:
            f.write(forgeTemplate.render(version=version, java=java))
        with open(forge / "gradle.properties", "w") as f:
            f.write(generateLoomPropertiesFile("forge"))

        os.makedirs(common / "src" / "main" / "java", exist_ok=True)
        os.makedirs(fabric / "src" / "main" / "java", exist_ok=True)
        os.makedirs(forge / "src" / "main" / "java", exist_ok=True)

        if hasNeo:
            os.makedirs(neoforge, exist_ok=True)
            with open(neoforge / "build.gradle.kts", "w") as f:
                f.write(neoForgeTemplate.render(version=version, java=java))
            with open(neoforge / "gradle.properties", "w") as f:
                f.write(generateLoomPropertiesFile("neoforge"))
            os.makedirs(neoforge / "src" / "main" / "java", exist_ok=True)


if __name__ == "__main__":
    main()
