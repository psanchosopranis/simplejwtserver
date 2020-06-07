#!/usr/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.252.b09-2.el8_1.x86_64
set -x
openssl pkcs12 -export \
-out simplejwtserver_example_com.pfx \
-passout pass:changeit \
-inkey simplejwtserver_example_com_PKEY.pem \
-passin pass:changeit \
-in simplejwtserver_example_com.crt \
-CAfile CASubordinateDomains.crt \
-certfile CASubordinateDomains.crt \
-name "simplejwtserver_example_com"

openssl pkcs12 -info \
-in simplejwtserver_example_com.pfx \
-passin pass:changeit

$JAVA_HOME/bin/keytool -importkeystore \
-srckeystore simplejwtserver_example_com.pfx -srcstoretype pkcs12 \
-srcstorepass changeit -srcalias simplejwtserver_example_com \
-destkeystore simplejwtserver_example_com.jks \
-destkeypass changeit -destalias simplejwtserver_example_com \
-deststoretype jks \
-deststorepass changeit

$JAVA_HOME/bin/keytool -list -v -keystore simplejwtserver_example_com.jks \
-storetype jks \
-storepass changeit

set +x


