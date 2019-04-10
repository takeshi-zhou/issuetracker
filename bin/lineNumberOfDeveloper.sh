#!/bin/bash

usage="\nUsage:  lineNumberOfDeveloper.sh repoPath start end author\n"

# if no args specified, show usage
if [ $# -le 0 ]; then
  echo -e ${usage}
  exit 1
fi

cd ${1}

#git log --shortstat --since=2018.10.11 --until=2018.12.11 --author=2461812105@qq.com --pretty=tformat: --numstat | gawk '{ add += $1  } END { printf "%s\n",add }'88
#git log --shortstat --since=${start} --until=${end} --author=${author} --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 + $2 } END { printf "added lines: %s removed lines : %s total lines: %s\n",add,subs,loc }'
#git log --shortstat --since=2018.10.11 --until=2018.12.11 --author=2461812105@qq.com --pretty=tformat: --numstat | gawk '{ add += $1 ; subs += $2 ; loc += $1 + $2 } END { printf "added lines: %s removed lines : %s total lines: %s\n",add,subs,loc }'

#for (( i = 4; i <= $#; i++ ))
#do
#    author = ${i}
#    git log --shortstat --since=${2} --until=${3} --author=${author} --pretty=tformat: --numstat | gawk '{ add += $1  } END { printf "%s\n",add }'
#done
j=1
for i in $@
do  
    if [ ${j} -gt 3 ]; then
        echo ${i} ":"
        git log --shortstat --since=${2} --until=${3} --author=${i} --pretty=tformat: --numstat | gawk '{ add += $1  } END { printf "%s\n",add }'
    fi
    let j=j+1
done

exit 0