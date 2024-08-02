# YAKVS
Yet Another Key Value Store 

Key-Value store database server
* Java 21
* SSLSocket
* Virtual Threads
* Protocol buffers

## server.yml
* name = add the name for the proto file saved on disk
* port = specify bound port

## Keywords
* SET = Set a new key
* GET = Get a key
* DELETE = Delete a key
* COUNT = Size of entries
* KEYS = Iterate all the keys
* FLUSH = Flush the database
* SAVE = Save to disk

## SSL-WIP
Properties at Runtime.
* `-Djavax.net.ssl.keyStore=$JDKPath$/lib/security/cacerts`
* `-Djavax.net.ssl.keyStorePassword=changeit`
