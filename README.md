# logtailer
Tails rotating logs, saves the state, uses tcp to send data

config file: application.conf

override file: override.conf

config keys:

tailer.file.directory - logs location

config-dir - tailer state file location

sender.connection.ip - server ip

sender.connection.port - server port

main class: Application. It has method main which starts "tailing", this class must be executed at client-machine which has log files at specified path
sender.connection configuration contains ip and port where server side appication must be executed.

Protocol: Server waits for client to connect and to send "starting offset" - long value which shows amount of bytes which were sent to server
After it client starts to send bytes. Server receives data and sends acks - long value = global offset + number of bytes received
When client receives ack it stores this ack as following: global offset, inode, local offset - calculated offset for this inode

Because we expect files to rotate some kind of "rotation monitoring" was developed: 
Before start of file choosing process, inode of main file is saved and to count file choose as successful it must be still the same.

Problem that is counted as critical - IOException when reading statefile. If it is found but state can't be read because of IO problems application stops.
