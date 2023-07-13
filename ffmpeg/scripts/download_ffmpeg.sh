#!/bin/bash

tag=n6.0

curDir=$(pwd)

sourceDir=${curDir}/code

git clone  https://github.com/FFmpeg/FFmpeg  ${sourceDir}

cd ${sourceDir}

git checkout -b ${tag}


