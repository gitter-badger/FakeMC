# FakeMC
## Description
FakeMC is a fake minecraft server that shows information in the server list.
You will be able to modify all messages and icons by using a configuration file.

## How does this work?
This server implementation handles specific minecraft packets (handshake, ping etc.) to provide fake information to a client.

## Credits
@michidk implemented a similar version of this project but without Netty. [FakeMCServer](https://github.com/michidk/FakeMCServer)
I took the protocol information from [this](http://wiki.vg/Protocol) wiki.
