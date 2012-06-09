#!/bin/bash

rm -rf dist
mkdir dist
cp -r lib dist
cp scripts/* dist
cp -r client dist
cp -r data dist
cp -r builds dist
