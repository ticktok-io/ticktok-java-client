#!/usr/bin/env bash

# more bash-friendly output for jq
JQ="jq --raw-output --exit-status"

distribute(){
    if [ "${CIRCLE_BRANCH}" == "master" ]; then
#        mvn -s settings.xml deploy -Dbintray.user=$BINTRAY_USER -Dbintray.key=$BINTRAY_KEY -Dversion=$VERSION
        echo "deploy"
    fi
}

distribute