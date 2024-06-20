#!/bin/bash


cd /config/eclipse-workspace/se.liu.ida.sas.pelab.vqlsyntaxcheck
java -cp "/opt/eclipse/plugins/*:jdbc/*" org.eclipse.xtend.core.compiler.batch.Main -d xtend-gen -useCurrentClassLoader src
eclipse-vnc ant clean build

for file in $INDIR/*.csv; do
	shortname=$(basename -- $file)
	name=${shortname%.csv}
	header=$(head -n 1 $file)
	IFS=','
	HEADER=()
	read -ra HEADER <<< $header
	export AI=$(echo ${HEADER[6]} | sed -r 's/0/<idx>/g')
	export INPUT=$file
    export OUTPUT=$OUTDIR/$name$()_eval.csv
    ant CSVBasedEvaluation
done