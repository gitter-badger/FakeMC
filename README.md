# FakeMC
## Description
FakeMC is a fake minecraft server that shows false information in the server list. You can configure specific information using a `.json` configuration file.

This is what a fake ping response looks like:
![Example Entry](/example.png)

## How does this work?
This server implementation handles specific minecraft packets (handshake, ping etc.) to provide fake information to a client. It does handle the following minecraft packets:
* Handshake
* Status Request
* Ping
* Login

## Credits
* [@michidk](https://github.com/michidk) implemented a similar version of this project but without Netty. [FakeMCServer](https://github.com/michidk/FakeMCServer)
* I took the protocol information from [this](http://wiki.vg/Protocol) wiki.
