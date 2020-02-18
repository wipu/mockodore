#!/bin/bash

set -euo pipefail

HERE=$(dirname "$0")
CACHED=$HERE/cached
mkdir -p "$CACHED"

CP=$("$HERE"/../../iwant/help.sh target/classpath-of-all-modules/as-path | xargs -r cat)

OUT=$CACHED/sidex.prg
java -cp "$CP" org.fluentjava.mockodore.app.sidex.SysexPlayer > "$OUT"

echo "$OUT"
