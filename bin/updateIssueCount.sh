#!/bin/bash


case ${option} in

  (day)
	curl -H "apikey:$TOKEN" --url ""
	exit 0
	;;
  (week)
   # need Formatted output display
    curl -H "apikey:$TOKEN"  --url ""
    exit 0
    ;;
  (month)
	curl  "apikey:$TOKEN"  --url ""
    ;;
  (*)
    exit 0
    ;;

esac