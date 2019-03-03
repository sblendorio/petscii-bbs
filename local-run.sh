#!/bin/bash
rm -rf target/ && mvn package && java -jar target/petscii-bbs-1.0-SNAPSHOT.jar -b MenuRetroacademy

