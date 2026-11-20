"""Package the editable data pack with pack.mcmeta at the archive root."""
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile, ZipInfo

root = Path(__file__).resolve().parents[1]
pack = root / "datapacks" / "ender_undertow"
archive = root / "dist" / "ender-undertow-0.2.0-beta.1.zip"
archive.parent.mkdir(exist_ok=True)
with ZipFile(archive, "w") as output:
    for path in sorted(pack.rglob("*")):
        if path.is_file():
            entry = ZipInfo(path.relative_to(pack).as_posix())
            output.writestr(entry, path.read_bytes(), compress_type=ZIP_DEFLATED)
print(archive)
