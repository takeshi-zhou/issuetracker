#!/bin/sh

dir=$(pwd)/repo/${1}

#if [ ! -f pom.xml ];then
#  exit 1
#fi

find ${dir} -name pom.xml  > .pom

sed -i "s/pom.xml//g" .pom

for pomDir in `cat .pom`
do
	cd ${pomDir}	
	result=`mvn  compile | grep -E  "BUILD FAILURE|ERROR"`
	if  [[ "$result" != ""  ]]
	then
    		exit 1
	fi
done


find $dir -name *.jar -exec rm -f {} \;
exit 0
