#!/bin/bash


mvn clean install

echo -n "Copying Updater .. "
cp update/target/RemoteTEARS-Update-1.0-SNAPSHOT-jar-with-dependencies.jar ~/jar/remoteTEARS/RemoteTEARS-Update.jar
echo -e -n '\b[OK]\nCopying Launcher .. '
cp launcher/target/RemoteTEARS-Launcher-1.0-SNAPSHOT-jar-with-dependencies.jar ~/jar/remoteTEARS/RemoteTEARS-Launcher.jar
echo -e -n '[OK]\nCopying Client .. '
cp client/target/RemoteTEARS-Client-1.0-SNAPSHOT-jar-with-dependencies.jar ~/jar/remoteTEARS/RemoteTEARS-Client.jar
echo -e -n '[OK]\nCopying Server .. '
cp server/target/RemoteTEARS-Server-1.0-SNAPSHOT-jar-with-dependencies.jar ~/jar/remoteTEARS/RemoteTEARS-Server.jar
echo -e -n '[OK]\n'

cd ~/jar/remoteTEARS
echo 'Executing upload'
./upload.sh