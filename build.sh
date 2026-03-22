#!/bin/sh -eux
cd "$(dirname "${0}")"
./mvnw clean package
