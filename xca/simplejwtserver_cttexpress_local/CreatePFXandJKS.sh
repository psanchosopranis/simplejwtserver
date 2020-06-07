#!/usr/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el8_1.x86_64
set -x
openssl pkcs12 -export \
-out simplejwtserver_cttexpress_local.pfx \
-passout pass:changeit \
-inkey simplejwtserver_cttexpress_local_PKEY.pem \
-passin pass:changeit \
-in simplejwtserver_cttexpress_local.crt \
-CAfile CALocalSubordinateServers.crt \
-certfile CALocalSubordinateServers.crt \
-name "simplejwtserver_cttexpress_local"

openssl pkcs12 -info \
-in simplejwtserver_cttexpress_local.pfx \
-passin pass:changeit

$JAVA_HOME/bin/keytool -importkeystore \
-srckeystore simplejwtserver_cttexpress_local.pfx -srcstoretype pkcs12 \
-srcstorepass changeit -srcalias simplejwtserver_cttexpress_local \
-destkeystore simplejwtserver_cttexpress_local.jks \
-destkeypass changeit -destalias simplejwtserver_cttexpress_local \
-deststoretype jks \
-deststorepass changeit

$JAVA_HOME/bin/keytool -list -v -keystore simplejwtserver_cttexpress_local.jks \
-storetype jks \
-storepass changeit

set +x


