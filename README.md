## CHATHACK PROJECT

ChatHack allows to send messages and transfer 
files by using a server and clients based on TCP 
protocol.

To use project please refer to "Quick 
installation and usage instructions" section or
check the user manual.


####  User Manual 

User manual is placed in manuals file and contain
all informations about how use this project.


####  Developper Manual 

Developper Manual is placed in manuals file and 
describes architectural choices, difficulties 
encountered and how the project has been done. 


####  Executables 

Executables are placed in sources/jar file after 
using this command in the "sources" repertory:

 - sh build.sh

This project contains 2 executable jar files:
ServerChatHack.jar which allows to load the server.
ClientChatHack.jar which allows to load the client.
Their usage is described in User Manual. 


####  Sources 

Sources are placed in sources/src file.


####  Documentation

Documentation is placed in sources/doc file. 


#### RFC
RFC describe the protocol used for this project. 


—— Quick installation and usage instructions ——
First of all, please check you have installed curl
 on your device before.
Then please use sh build.sh in the "sources" 
directory. It will set:

-documentation
-jar files
-compilated class
-compilated tests
-tests reports

**/!\ IMPORTANT /!\**
Make always sure that the file "censure.txt" is in 
your current directory when you launch the server.

Before launching the server, you have to launch a
registered logins database server. 
A default database server is available, to launch
it, please use:
java -jar ServerMDP.jar

You can use the default passwords file:
passwords.txt

To execute server please use: 
java -jar ServerChatHack.jar

To execute the client please use:
java -jar ClientChatHack.jar

For more informations please refer to User Manual.

#### Developed with
- [Java 11](https://www.oracle.com/fr/java/technologies/javase-jdk11-downloads.html)


*Developed by: Pierre-Jean Besnard & Louis Billaut.*
