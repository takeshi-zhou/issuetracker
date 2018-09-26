#!/bin/sh

dir=/home/fdse/issueTracker/repo/${1}

cd ${dir}

if [ ! -f pom.xml ];then
  exit 1
fi

result=$(mvn  compile | grep "BUILD FAILURE")
if  [[ "$result" != ""  ]]
then
    exit 1
fi

find ${dir} -name *.jar -exec rm -f {} \;
exit 0
