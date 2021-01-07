#!/usr/bin/bash
rm -rf target/ && mvn package && java -jar target/petscii-bbs.jar --bbs StdChoice:23
