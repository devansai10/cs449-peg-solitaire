#!/bin/zsh
cd -- "$(dirname -- "$0")" || exit 1
mvn -q package || exit 1
java -jar target/peg-solitaire-brainvita-1.0-SNAPSHOT.jar
