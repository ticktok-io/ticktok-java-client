#!/usr/bin/env bash

# more bash-friendly output for jq
JQ="jq --raw-output --exit-status"

distribute(){
    if [ "${CIRCLE_BRANCH}" == "master" ]; then
        version=$(git tag | sed -e 's/v//g')
        echo $version
#        mvn -s settings.xml deploy -Dbintray.user=$BINTRAY_USER -Dbintray.key=$BINTRAY_KEY -Dversion=$version
        echo "deploy"
    fi
}

distribute