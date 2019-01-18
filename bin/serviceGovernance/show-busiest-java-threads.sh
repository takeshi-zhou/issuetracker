#!/bin/bash
# @Function
# Find out the most cpu consumed threads of java, and print the stack trace of these threads
#
# @Usage
#  $ ./show-busy-java-threads -h
#
PROG=$(basename ${0})

usage(){
    cat <<EOF
    Usage: ${PROG} [OPTION]...
    Find out the highest cpu consumed threads of java, and print the stack of these threads.
    Example: ${PROG} -c 10

    Options:
EOF
    exit ${1}
}