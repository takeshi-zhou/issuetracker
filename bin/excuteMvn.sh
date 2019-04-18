#!/bin/bash

#dir=/home/fdse/issueTracker/repo/${1}
dir=${1}

cd ${dir}

find ${dir}  -type d -name target -exec rm -rf  {} \;

find ${dir} -name pom.xml  > .pom

sed -i "s/pom.xml//g" .pom

#for pomDir in `cat .pom`
for pomDir in $(tail -n 1 .pom)
do
        cd ${pomDir}
          echo ${pomDir} >>  /home/fdse/issueTracker/maven
        result=`mvn  compile -Dmaven.test.skip=true | grep -E  "BUILD FAILURE|ERROR"`
        if  [[ "$result" != ""  ]]
        then
                  echo ${result} >>  /home/fdse/issueTracker/maven
                exit 1
        fi
done

echo 'success' >  /home/fdse/issueTracker/maven
find $dir -name *.jar -exec rm -f {} \;
exit 0