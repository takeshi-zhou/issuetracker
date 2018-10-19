#!/bin/bash



option=$1
IP="10.141.221.80"
PORT="8005"


case ${option} in

  (day)
        curl -H "apikey:vzPh6QXeja2sjtxg0O0X5e2v9JqLELVO" --url "http://${IP}:${port}/inner/issue/dashboard?time=day"
        exit 0
        ;;
  (week)
   # need Formatted output display
    curl -H "apikey:vzPh6QXeja2sjtxg0O0X5e2v9JqLELVO"  --url "http://${IP}:${port}/inner/issue/dashboard?time=week"
    exit 0
    ;;
  (month)
        curl  "apikey:vzPh6QXeja2sjtxg0O0X5e2v9JqLELVO"  --url "http://${IP}:${port}/inner/issue/dashboard?time=month"
    ;;
  (*)
    exit 0
    ;;

esac
