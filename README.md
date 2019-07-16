# Chat-Application

### Introduction
- This is a multi-threaded chat server and client implementation in Java
- Multiple clients can connect to the server simultaneously (a new service thread handles each new client at server side)
- Server assigns a unique id to each client
- Following functionalities are available to each client:
	1. `Client X: <message>` delivers _message_ to client with id _X_
	2. `All: <message>` delivers _message_ to all present clients (aka broadcast)
	3. `Client X,Y: <message>` delivers _message_ to clients with ids _X,Y_ only
	4. `Server: List All` requests the server to reply with the list of all currently connected clients


### Repository structure
-  `src/` contains the source code
	- `src/Server.java` and `src/Client.java` contain the server and client, respectively
    - `src/ClientServiceThread.java` is the `Runnable` (thread) used by `Server` (one per client)
- `compile.sh` bash shell-script can be used to compile both server and client from source

### Build
- First compile by `cd`-ing into the root of this repository and then running command  `./compile.sh`
- To start the server,
	- Execute `java -classpath bin Server <port_no>` where `port_no` is the port number at which you want to start the server
	- For example, execute `java -classpath bin Server 1024` if server is to be started at port number _1024_
- To start a client (you can start and connect multiple clients),
	- Execute `java -classpath bin Client <ip_addr> <port_no>` where `ip_addr`, `port_no` are IP address of server and port number, respectively
	- For example, execute `java -classpath bin Client 192.168.1.15 1024` is server's IP is _192.168.1.15_ and port number is _1024_

### Sample run
| Hello | World |
| Okay | This |

### References
1. [Reading from and Writing to a Socket](https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html)
2. [Writing the Server Side of a Socker](https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html)