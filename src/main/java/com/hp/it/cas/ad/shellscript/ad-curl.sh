#!/bin/bash

# Scan the PRO logs for a given day.
# Construct curl commands and execute it against ITG environment.
# Log the number of successful transactions and the number of transactions that crashed address doctor.

# $1:PRO_LOG_FILE
# $2:RESULT_LOG_FILE
# $3:ITG_SERVER_NAME
# $4:SERVER_PORT
# usage
function printUsageAndExit(){
    echo -e "\nUsage: sh $0 PRO_LOG_FILE RESULT_LOG_FILE ITG_SERVER_NAME SERVER_PORT\n";
    exit 1;
}

# check parameters
if [ $# -lt 3 -o $# -gt 4 ]; then
    printUsageAndExit
fi

# check PRO_LOG_FILE if it exists
if [ ! -f "$1" ]; then
    echo -e "$1 does not exist \n";
    exit 1;
elif [ ! -r "$1" ]; then
    echo -e "$1 can not read \n";
    exit 1;
fi

# make sure the RESULT_LOG_FILE is empty
if [ -f "$2" ]; then
    if [ -s "$2" ]; then
        if [ -w "$2" ]; then
            cat /dev/null > $2;
        else
            rm -rf $2;
        fi
    fi
fi

# check SERVER_PORT
if [ -z "$4" ]; then
    # default server port 80
    service="http://"$3":80";
elif [[ "$4" =~ ^[0-9]+$ ]]; then
    service="http://"$3":"$4;
else
    printUsageAndExit;
fi

# Put the shell script and the jar file of this project in the same specified directory
# eg: /opt/casfw/address-doctor/automatedShellScriptTest
# Change to the specified directory
cd /opt/casfw/address-doctor/automatedShellScriptTest

# Run the java command to call the java class
/opt/casfw/software/oracle-java-1.7.0_21/bin/java -classpath ad-tool-2016.02.jar com.hp.it.cas.ad.shellscript.AutomatedReSTServiceTestWithHttp $1 $2 $service