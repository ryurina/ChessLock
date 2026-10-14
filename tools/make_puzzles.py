"""Build app/src/main/assets/puzzles.txt from the Lichess puzzle database.

1. Download lichess_db_puzzle.csv.zst from https://database.lichess.org/#puzzles
2. pip install zstandard   (only needed for the .zst file)
3. python tools/make_puzzles.py lichess_db_puzzle.csv.zst 3000 600 1600
   args: file, how many puzzles (default 2000), min rating (600), max rating (1600)
"""
import csv, io, random, sys
from pathlib import Path

src = sys.argv[1]
n = int(sys.argv[2]) if len(sys.argv) > 2 else 2000
lo = int(sys.argv[3]) if len(sys.argv) > 3 else 600
hi = int(sys.argv[4]) if len(sys.argv) > 4 else 1600

if src.endswith(".zst"):
    import zstandard
    fh = io.TextIOWrapper(zstandard.ZstdDecompressor().stream_reader(open(src, "rb")), encoding="utf-8")
else:
    fh = open(src, encoding="utf-8")

random.seed(1)
picked, seen = [], 0
for r in csv.DictReader(fh):          # reservoir sampling keeps memory small
    if not lo <= int(r["Rating"]) <= hi:
        continue
    line = f'{r["FEN"]}|{r["Moves"]}|{r["Rating"]}'
    seen += 1
    if len(picked) < n:
        picked.append(line)
    else:
        j = random.randrange(seen)
        if j < n:
            picked[j] = line

out = Path(__file__).resolve().parent.parent / "app/src/main/assets/puzzles.txt"
out.write_text("\n".join(picked) + "\n", encoding="utf-8")
print(f"wrote {len(picked)} puzzles to {out}")
