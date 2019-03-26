# Remote "Pen and Paper"

This is a simple gui, to play pen and paper over the network. Since i am german and no internationalization has been implementated, nearly all of the sentences are in german!

## Installation

To install this, you require git, maven and java (i recommend oracle's java). To run the client, java has to be a version less than 11 (so that javafx is within the classpath).

```
sudo apt install git mvn
```

Java will be let out here. First, clone this repository:

```
git clone https://github.com/ThorbenKuck/RemotePNP.git
```

alternatively, you may download this repository as a zip. Either way, you whould have a folder called RemotePNP. Enter this folder and run the following command:

```
mvn clean install
```

This will create the jars. Now we may launch the client/server. We require 2 servers running. The update-server and the main-server. Before starting those, create a correlating folder, which i will name "rpnp":

```
mkdir ~/rpnp
mkdir ~/rpnp/server
mkdir ~/rpnp/client
mv update/target/RemoteTEARS-Update-1.0-SNAPSHOT-jar-with-dependencies.jar ~/rpnp/server/RemotePNP-Updater.jar
mv server/target/RemoteTEARS-Server-1.0-SNAPSHOT-jar-with-dependencies.jar ~/rpnp/server/RemotePNP-Server.jar
mv client/target/RemoteTEARS-Client-1.0-SNAPSHOT-jar-with-dependencies.jar ~/rpnp/client/RemotePNP-Client.jar
mv launcher/target/RemoteTEARS-Launcher-1.0-SNAPSHOT-jar-with-dependencies.jar ~/rpnp/client/RemotePNP-Launcher.jar
```

## The system

We created a client and a server folder. This has a basic reason. We use a launcher to start the Client, which asks the Updater for the newest Client version, downloads it and then launches it. Afterwards, the downloaded client connects to the "not-update" server.

The Client will create multiple files, which are required so that you may change little things. Fot the sake of the argument, let's leave out the Server. An example of this Server is running on thorbenkuck.de:8881. To test this, all you need to to is this:

```
cd ~/rpnp/client
java -jar RemotePNP-Launcher.jar
```

This will ask you, for a Server on which the Update-Server is running. Enter `thorbenkuck.de`. Now, the launcher will download the newest Client.

Afterwards, the client starts. To use it, enter thorbenkuck.de as a Server, a Name (do not use " " signs) and select a character. If you have no character, click on "Charaktere" and "Neu".

All you have to do, is to enter a Name under "Charakter Name" and a 1 under "Leben" and "Mentale Gesundheit". Afterwards click "Speichern". Then select the created character and click on "Anmelden".

## Custom Server

WIP

## Customization options

WIP
