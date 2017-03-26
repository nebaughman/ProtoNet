# ProtoNet

A small [ProtoBuf](https://developers.google.com/protocol-buffers/) + [Netty](https://netty.io/) client-server framework.

> This was developed as a weekend experiment. It's not a production quality library. I provide it here in hopes it may be useful for reference, and to learn a thing or two.

_Share and enjoy._ This project is licensed under the terms of the MIT license.

# Description

Google [Protocol Buffers](https://developers.google.com/protocol-buffers/) are excellent for defining protocol message objects, which need to be packed efficiently for network transport. 

[Netty](https://netty.io/) is an excellent library for socket handling in Java.

Their powers combined (ala ProtoNet) make an excellent framework for client-server (or p2p) communication. This ProtoNet implementation is still very primitive, yet provides a convenient framework for passing messages between nodes in a network.

# How it Works

Protocol Buffer message objects are defined in (language-neutral) `.proto` files. These are compiled with `protoc` to generate `.java` files with classes representing the messages.

For example, `account_proto.proto` contains a protobuf *message* named `Account`. Compiling this with `protoc` produces a class called `AccountProto` containing an inner class called `Account`, which provides builder and accessor methods to use in Java code.

Protobuf objects can be serialized to (efficient) byte arrays. ProtoNet adds a message ID and packet _framing_ bytes before sending the bytes over the network. On the receiving end, protobuf message classes include a `parseFrom` method, which is used to decode bytes into concrete `Message` objects. Messages are dispatched to registered handlers, which implement application-specific logic.

> Notice that Protocol Buffers can be compiled into other languages than Java. ProtoNet could be used strictly as the server platform, so long as clients are sending messages defined by the same protobuf definitions, and using the same framing and ID technique.

# Project Structure

The code is structured in typical [Gradle](https://gradle.org/) fashion. See `build.gradle` for library dependencies.

Notice that an example client-server application is included in the `main` source package. The `main/proto/*.proto` files go along with this example app. You must install Google's Protocol Buffer development kit in order to compile `.proto` files.

The included example `.proto` files are **not** automatically compiled (see Help Wanted below). `protoc.sh` shows how to compile them into Java source. The generated Java files are included in the `net.nyhm.protonet.example.proto` package.

> The generated Java files probably shouldn't be part of the committed repository code (see below.)

# Help Wanted

I'm a novice with gradle...

I would like to put the example code (and associated proto files) into a separate gradle _sourceSet_ named `example` (but couldn't get it to work).

I would also like to use the [gradle-protobuf-plugin](https://github.com/google/protobuf-gradle-plugin):
- The proto files should be compiled (into Java) on build
- The generated Java files should not themselves be in the repository
- No external dependency on `protoc` (the plugin can compile the `proto` files itself)

**Any advice is most welcome!**
