import os
from pathlib import Path

from jinja2 import Environment, FileSystemLoader, select_autoescape

env = Environment(loader=FileSystemLoader("templates"), autoescape=select_autoescape())


def main():
    commonTemplate = env.get_template("common.gradle.kts.jinja2")
    fabricTemplate = env.get_template("fabric.gradle.kts.jinja2")

    sourceRoot = Path("./minecraft")
    commons = sourceRoot / "common"
    fabrics = sourceRoot / "fabric"

    for version, java in [
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
    ]:
        common = commons / version
        fabric = fabrics / version
        os.makedirs(common, exist_ok=True)
        os.makedirs(fabric, exist_ok=True)
        with open(common / "build.gradle.kts", "w") as f:
            f.write(commonTemplate.render(version=version, java=java))
        with open(fabric / "build.gradle.kts", "w") as f:
            f.write(fabricTemplate.render(version=version, java=java))
        os.makedirs(common / "src" / "main" / "java", exist_ok=True)
        os.makedirs(fabric / "src" / "main" / "java", exist_ok=True)


if __name__ == "__main__":
    main()
