#!/bin/bash
protoc -I=proto --java_out=java proto/account_proto.proto proto/login_proto.proto
