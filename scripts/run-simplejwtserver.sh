#!/usr/bin/bash
export TOKENS_YAML_PATH=${HOME}/tokens
export APICLIENTS_YAML_PATH=${HOME}/api-clients-simplejwtserver.yml
export JAVA_RUN='/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el8_1.x86_64/bin/java'
export APP_HOME='/home/devel1/IdeaProjects/simplejwtserver/target'
$JAVA_RUN -jar $APP_HOME/tomcat-embedded-example-1.0.0.jar
