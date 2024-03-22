#!/bin/bash

tag=n6.0

curDir=$(pwd)

sourceDir=${curDir}/code

git clone  -b ${tag} https://github.com/FFmpeg/FFmpeg  ${sourceDir}

#cd ${sourceDir}
#
#git checkout


