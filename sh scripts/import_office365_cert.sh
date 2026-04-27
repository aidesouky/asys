#!/bin/bash

HOST="smtp.office365.com"
PORT="587"
ALIAS="office365-smtp"
CRT_FILE="office365-smtp.crt"

# Default Java cacerts path (Linux)
JAVA_CACERTS="/u01/install/APPS/fs1/EBSapps/comn/util/jdk64/jre/lib/security/cacerts"

# If your system uses another path, change it
# Example:
# JAVA_CACERTS="/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts"

STOREPASS="changeit"

echo "===> Extracting certificate from ${HOST}:${PORT} ..."

openssl s_client -connect ${HOST}:${PORT} -starttls smtp -showcerts </dev/null 2>/dev/null | sed -n '/-----BEGIN CERTIFICATE-----/,/-----END CERTIFICATE-----/p' > ${CRT_FILE}

if [ ! -s "${CRT_FILE}" ]; then
    echo "ERROR: Failed to extract certificate!"
    exit 1
fi

echo "===> Certificate saved to ${CRT_FILE}"

echo "===> Importing certificate into Java keystore: ${JAVA_CACERTS}"

keytool -delete -alias ${ALIAS} -keystore ${JAVA_CACERTS} -storepass ${STOREPASS} 2>/dev/null

keytool -importcert -trustcacerts -alias ${ALIAS} -file ${CRT_FILE} -keystore ${JAVA_CACERTS} -storepass ${STOREPASS} -noprompt

if [ $? -eq 0 ]; then
    echo "===> Certificate imported successfully!"
else
    echo "ERROR: Failed to import certificate!"
    exit 2
fi

echo "===> Verifying imported certificate:"
keytool -list -keystore ${JAVA_CACERTS} -storepass ${STOREPASS} | grep ${ALIAS}

echo "DONE."