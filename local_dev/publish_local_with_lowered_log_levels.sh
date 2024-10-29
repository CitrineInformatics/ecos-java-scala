#!/usr/bin/env bash
set -o errexit   # abort on nonzero exitstatus
set -o nounset   # abort on unbound variable
set -o pipefail  # don't hide errors within pipes

git submodule init
git submodule update

## This is a hack to set the global logging levels down to a reasonable level
## Note that this differs from the commands in the Dockerfile due
## to the differences in GNU and BSD sed. See https://stackoverflow.com/questions/4247068/sed-command-with-i-option-failing-on-mac-but-works-on-linux
sed -i '' 's/PRINTLEVEL (2)/PRINTLEVEL (0)/g' ecos/include/glblopts.h
sed -i '' 's/PROFILING (1)/PROFILING (0)/g' ecos/include/glblopts.h

sbt publishLocal
