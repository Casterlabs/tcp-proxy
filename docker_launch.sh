#!/bin/bash

java \
    -Dfastloggingframework.defaultlevel=INFO \
    -Dtp.bindport=8000 \
    -Dtp.sotimeout=$SO_TIMEOUT \
    -Dtp.targetaddr=$TARGET_ADDR \
    -Dtp.targetport=$TARGET_PORT \
    -jar tcp-proxy.jar