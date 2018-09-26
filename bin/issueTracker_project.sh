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
	curl -H "token:$TOKEN" -X POST -d '{"url": "$2"}' http://$IP:$PORT/project -s | grep -Po 'msg[":]+\K[^"]+'
	exit 0
	;;
  (list)
   # need Formatted output display
    curl -H "token:$TOKEN" http://$IP:$PORT/project -s  | jq -r
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