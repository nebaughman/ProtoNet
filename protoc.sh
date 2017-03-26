#!/bin/bash
protoc -I=src/proto/ --java_out=src/main/java/ src/proto/account_proto.proto src/proto/login_proto.proto
