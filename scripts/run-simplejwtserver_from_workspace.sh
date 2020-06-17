#!/usr/bin/bash
export TOKENS_YAML_PATH=${HOME}/simplejwtserverpersistence/tokens
export APICLIENTS_YAML_PATH=${HOME}/simplejwtserverpersistence/apiclients/api-clients.yml
export JAVA_RUN='/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el8_1.x86_64/bin/java'
export APP_HOME=${HOME}/IdeaProjects/simplejwtserver
export HTTP_SCHEME=HTTP
export PORT=8080
$JAVA_RUN -jar $APP_HOME/binaries/tomcat-embedded-example-1.0.0.jar
