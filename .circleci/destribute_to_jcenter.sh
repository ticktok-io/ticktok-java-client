#!/usr/bin/env bash

set -e
# more bash-friendly output for jq
JQ="jq --raw-output --exit-status"

distribute(){
    if [ "${CIRCLE_BRANCH}" == "master" ]; then
        mvn -s settings.xml deploy -Dbintray.user=$BINTRAY_USER -Dbintray.key=$BINTRAY_KEY
        echo "ticktok-java-client was deployed to jcenter"
    fi
}

distribute