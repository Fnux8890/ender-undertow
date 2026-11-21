"""Build both Java editions against the official installed Minecraft and Forge launcher libraries."""
import argparse
import json
import os
from pathlib import Path
import subprocess
import tempfile
from zipfile import ZipFile, ZIP_DEFLATED

root = Path(__file__).resolve().parent
parser = argparse.ArgumentParser()
parser.add_argument("--game", type=Path, required=True, help="Launcher directory with vanilla 26.3, Forge 66.1.8 and Fabric Loader 0.20.1")
parser.add_argument("--javac", default="javac")
args = parser.parse_args()
game = args.game.resolve()
libraries = game / "libraries"
version = game / "versions/26.3"
classpath = [version / "26.3.jar"]
for lib in json.loads((version / "26.3.json").read_text())["libraries"]:
    entry = lib.get("downloads", {}).get("artifact")
    if entry and (libraries / entry["path"]).is_file():
        classpath.append(libraries / entry["path"])
mixin = libraries / "net/fabricmc/sponge-mixin/0.17.5+mixin.0.8.7/sponge-mixin-0.17.5+mixin.0.8.7.jar"
classpath.append(mixin)
destination = root.parent / "dist"
destination.mkdir(exist_ok=True)
pack = root.parent / "datapacks/ender_undertow"
for loader in ("fabric", "forge"):
    sources = sorted((root / "src/main/java").rglob("*.java"))
    deps = list(classpath)
    if loader == "forge":
        sources += sorted((root / "src/forge/java").rglob("*.java"))
        deps.extend(libraries.glob("net/minecraftforge/javafmllanguage/26.3-66.1.8/*.jar"))
    with tempfile.TemporaryDirectory() as tmp:
        out = Path(tmp)
        subprocess.run([args.javac, "--release", "21", "-proc:none", "-classpath", os.pathsep.join(map(str, deps)), "-d", str(out), *sources], check=True)
        jar = destination / f"ender-undertow-{loader}-26.3-0.2.0-beta.10.2.jar"
        with ZipFile(jar, "w", ZIP_DEFLATED) as archive:
            for folder in [out, pack]:
                for path in sorted(folder.rglob("*")):
                    if path.is_file() and path.name != "README.md":
                        name = path.relative_to(folder).as_posix()
                        if name == "data/undertow/function/home/warm.mcfunction":
                            archive.writestr(name, "return run undertow_prepare_home\n")
                        elif not (root / "resources" / name).is_file():
                            archive.write(path, name)
            # The 26.3 post-chain API accepts static uniform blocks. Share one shader
            # and generate its small progression of focus distances for either loader.
            chain = json.loads((root / "focus_chain.json").read_text())
            for step in range(1, 33):
                for focus_pass in chain["passes"]:
                    focus_pass["uniforms"]["FocusConfig"][1]["value"] = step / 32
                archive.writestr(f"assets/ender_undertow/post_effect/focus_{step}.json", json.dumps(chain))
            for namespace in ("assets", "data"):
                for asset in sorted((root / "resources" / namespace).rglob("*")):
                    if asset.is_file():
                        archive.write(asset, asset.relative_to(root / "resources").as_posix())
            archive.write(root / "VOICE-CREDITS.md", "VOICE-CREDITS.md")
            archive.write(root.parent / "LICENSE", "LICENSE")
            archive.write(root.parent / "art/ender_undertow.png", "pack.png")
            archive.write(root / "resources/undertow.mixins.json", "undertow.mixins.json")
            if loader == "forge":
                archive.write(root / "resources/mods.toml", "META-INF/mods.toml")
                archive.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\r\nMixinConfigs: undertow.mixins.json\r\n\r\n")
            else:
                archive.write(root / "resources/fabric.mod.json", "fabric.mod.json")
        print(jar, flush=True)
