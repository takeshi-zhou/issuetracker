#!/bin/bash
usage="\nUsage: issueTracker_retrieve_detail (list|code) <issue_id> \n"

# if no args specified, show usage
if [ $# -le 0 ]; then
  echo -e ${usage}
  exit 1
fi

baseDir="$ISSUETRACKER_HOME/bin"
source ${baseDir}/.env

option=$1

case ${option} in

    (list)
    curl -H "token:${TOKEN}"  --url "http://$IP:$PORT/raw-issue?issue_id=${2}" -s | jq -r
    ;;
    (*)
    echo -e "No option $1"
    echo -e ${usage}
    exit 0
    ;;

esac