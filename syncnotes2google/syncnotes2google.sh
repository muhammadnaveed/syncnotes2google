#!/bin/bash

NOTES_HOME=/Applications/Notes.app/Contents/MacOS

NOTES_JAR=${NOTES_HOME}/jvm/lib/ext/Notes.jar
CLASS_PATH="./syncnotes2google.jar: \
    ${NOTES_JAR}: \
    ./lib/domingo-1.5.1.jar: \
    ./lib/gdata-calendar-2.0.jar: \
    ./lib/gdata-calendar-meta-2.0.jar: \
    ./lib/gdata-client-1.0.jar: \
    ./lib/gdata-client-meta-1.0.jar: \
    ./lib/gdata-core-1.0.jar: \
    ./lib/gdata-media-1.0.jar: \
    ./lib/google-collect-1.0-rc1.jar"

CLASS_PATH=`echo ${CLASS_PATH} | sed 's/ //g'`

export PATH=${PATH}:${NOTES_HOME}
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${NOTES_HOME}
export DYLD_LIBRARY_PATH=${DYLD_LIBRARY_PATH}:${NOTES_HOME}

java -d32 \
     -Djava.library.path=${NOTES_HOME} \
     -classpath ${CLASS_PATH} \
     com.googlecode.syncnotes2google.SyncNotes2Google

