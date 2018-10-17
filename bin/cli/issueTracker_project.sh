#!/bin/bash

usage="ERROR\nUsage: issueTracker_project.sh (add|list|delete|load) <url | project-Id> \nERROR"

# if no args specified, show usage
if [ $# -le 0 ]; then
  echo -e ${usage}
  exit 1
fi

baseDir="$ISSUETRACKER_HOME/bin"
source ${baseDir}/.env

option=$1

case ${option} in

  (add)
	read  -p "please enter the tool name you want to use: "	tool
	read  -p "please enter project name: " name
    if [ ${tool} == "bug" -o ${tool} == "clone" ]; then
          echo "export  TOOL=\""${tool}"\"" >> ${baseDir}/.env
          curl -H "token:$TOKEN" -H "Content-Type:application/json"  -X POST -d '{"url": "'${url}'" , "type":"'${tool}'" ,"name":"'${name}'"}' http://$IP:$PORT/proj    ect -s | grep -Po 'msg[":]+\K[^"]+'
    else
        echo "Error"
    fi
	exit 0
	;;
  (list)
	read  -p "please enter the tool name you want to use: "	tool
    if [ ${tool} == "bug" -o ${tool} == "clone" ]; then
          echo "export  TOOL=\""${tool}"\"" >> ${baseDir}/.env
          curl -H "token:$TOKEN" http://$IP:$PORT/project?type=${tool} -s  | jq -r
    else
        echo "Error"
    fi
    ;;
  (delete)
	curl -v -X DELETE -H "token:$TOKEN" http://$IP:$PORT/project/$2 -s | grep -Po 'msg[":]+\K[^"]+'
    ;;
  (load)
   ### display detail of project
	# echo $2 > ${baseDir}/project
	PROJECT_ID=$2
	echo "export  PROJECT_ID=\""${PROJECT_ID}"\"" >> ${baseDir}/.env

	echo -e "successful loaded"
    ;;
  (*)
	echo -e "No option $1"
    echo -e ${usage}
    exit 0
    ;;

esac