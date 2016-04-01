#!/usr/bin/env bash

jmeter -s -q ./injector.properties -Djava.rmi.server-hostname=192.168.1.11
