#!/bin/bash

usage="\nUsage: issueTracker_login (sighin|sighup|sighout)  \n"

baseDir="$ISSUETRACKER_HOME/bin"

source ${baseDir}/.env

# if no args specified, show usage
if [ $# -le 0 ]; then
  echo -e ${usage}
  exit 1
fi


option=$1
case ${option} in

  (sighin)
    # before sigh in clear data
  	/bin/cp ${baseDir}/.env ${baseDir}/.envCopy
	sed  '5,$d' ${baseDir}/.envCopy > ${baseDir}/.env

	#echo -e "please enter your password"
	#password=$3
	read -p "please enter username: "	username
	read -s -p "please enter your password: "	password
	echo
	url="http://$IP:$PORT/user/login?username=$username&password=$password"
	curl $url -s > ${baseDir}/authentication1
	grep -Po 'msg[":]+\K[^"]+' ${baseDir}/authentication1
	#grep -Po 'token[":]+\K[^"]+' $baseDir/authentication1 > $baseDir/authentication
	TOKEN=$(grep -Po 'token[":]+\K[^"]+' ${baseDir}/authentication1)
	echo "export  TOKEN=\""${TOKEN}"\"" >> ${baseDir}/.env
	rm -f ${baseDir}/authentication1
	;;
  (sighup)
    # need to be done

    ;;
  (sighout)
    if [ ! -f   "${baseDir}/.envCopy" ];then
        cp ${baseDir}/.env ${baseDir}/.envCopy
    fi
	sed  '5,$d' ${baseDir}/.envCopy > ${baseDir}/.env
	echo -e "successful sigh out"
    ;;

  (*)
	echo -e "No option $1"
    echo -e ${usage}
    exit 1
    ;;

esac