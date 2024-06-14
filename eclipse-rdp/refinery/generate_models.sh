#!/bin/bash

# $1: output directory for models
mkdir -p $1

cd "$(dirname "$0")"
RESPATH=$(pwd)/subprojects/generator-cli/src/main/resources/

cp $RESPATH/railway.problem.seedless $RESPATH/railway.problem
./gradlew "generate Railway"
for file in $(pwd)/subprojects/generator-cli/model*.xmi;
do
        newfilename=$(basename $(echo $file | sed -r "s/model([0-9]+)/seedless\1/g"))
        cp $file $1/$newfilename
        echo SEEDLESS $file $1/$newfilename
		rm $file
done

cp $RESPATH/railway.problem.seeded $RESPATH/railway.problem
./gradlew "generate Railway"
for file in $(pwd)/subprojects/generator-cli/model*.xmi;
do
		newfilename=$(basename $(echo $file | sed -r "s/model([0-9]+)/seeded\1/g"))
        cp $file $1/$newfilename
        echo SEEDED $file $1/$newfilename
		rm $file
done
