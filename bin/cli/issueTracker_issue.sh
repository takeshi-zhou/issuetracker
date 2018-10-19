#!/bin/bash
usage="\nUsage: issueTracker_issue （list|filter|add_tag） <issue id>\n"

# if no args specified, show usage
if [ $# -le 0 ]; then
  echo -e ${usage}
  exit 1
fi

function readPageAndSize(){
    read  -p "please enter page: "	page
    read  -p "please enter page: "	size
}

baseDir="$ISSUETRACKER_HOME/bin"
source ${baseDir}/.env

option=$1
case ${option} in

  (list)
    readPageAndSize
   # paging or not ；size is the number of listing in the page
    curl -H "token:${TOKEN}"  --url "http://$IP:$PORT/issue?project-id=${PROJECT_ID}&page=${page}&size=${size}&category=${TOOL}" -s | jq -r
    ;;
   (filter)
    readPageAndSize
    read  -p "please enter page: "	tags
    read  -p "please enter page: "	types
    curl -H "token:${TOKEN}"  -d '{"project_id": "'${PROJECT_ID}'","page": "'${page}'","size": "'${size}'","tags": "'${tags}'","types": "'${types}'"}' -X POST  --url "http://$IP:$PORT/issue/filter" -s | jq -r
    ;;
  (add_tag)
   # need to be done
   read  -p "please enter tag name(Low Urgent): "	tags
   itemId=${2}
   scope="issue"
   curl -H "token:${TOKEN}"  -d '{"name": "'${name}'","scope": "'${scope}'","itemId": "'${itemId}'"}' --url "http://$IP:$PORT/tag" -s | jq -r
    ;;

  (*)
    echo -e "No option $1"
    echo -e ${usage}
    exit 0
    ;;

esac
