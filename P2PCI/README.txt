This is a peer to peer file transfer program with a server contains list of available files
RFC.java is used to store information of a rfc file
UPort.java is used to store information of a peer upload port
LinkedList.java is used to store multiple RFCs or UPorts
Server.java is used to run the main thread of server
ServerThread.java is used to communicate with any connected peer
Peer.java is used to run the main thread of peer
PeerToPeerThread.java is used to transfer file to another peer by request
PeerToServerThread.java is used to communicate with server to send and receive information
SecretKey.java is used to generate a key for Peer to encrypt and Server to decrypt login password

To run the system, start the server by run Server.java on one machine, it will wait for any peer to connect
For peers, copy some rfc files from folder "rfcs" into folder "peer", it is best to make both peers have some different and same rfcs
then run Peer.java on second and third machine, the console will pop out and ask for ip address of Server
check server machine's ip, and type in peer's console and hit enter to start the connection
the server will then ask for user to login, use either one of the login in file "logins.txt" inside "Test" folder, "username password" to login
once user logged in, peer will automatically start to ADD rfc information to the server, response message will show whether the ADD function perform correctly
peers now can use "lookup rfc" to LOOKUP specific rfc, "listall" to LIST all the peers connected and rfcs available, 
"get rfc hostname port" to GET rfc from another peer, "quit" to disconnect from the server
after each action is performed, the response message will show if the action is valid or not by different status codes mentioned in project requirements

in this project we have our own linked list and encrypted password for extra credit