#!/bin/bash

repoPath=${1}
fileName=${2}


find ${repoPath} -name ${fileName}

exit 0