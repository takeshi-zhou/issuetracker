#!/usr/bin/env bash
mvn package -Dmaven.test.skip=true
sshpass -p cloudfdse scp ./target/clone-service-0.0.3-SNAPSHOT.jar fdse@10.141.221.85:~/user/issueTracker/saga-cpu/test