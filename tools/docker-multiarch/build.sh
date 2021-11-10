#!/bin/bash
set -e

PREFIX="registry.scc-easy-res.lancs.ac.uk/"
#PREFIX=""

PLATFORMS="linux/amd64,linux/arm64"
PUSH="--load"

function build () {
	echo "Building $TARGET for $PLATFORMS"
	docker buildx build \
		-t "${PREFIX}openems/multiarch/$TARGET:latest" \
		-f "tools/docker-multiarch/Dockerfile.$TARGET" \
		--platform "$PLATFORMS" \
		$PUSH \
		.
}

while [[ $# -gt 1 ]]; do
	opt="$1"
	shift
	case "$opt" in
        "-t"|"--target" )
			PLATFORMS="$1"
			shift;;

		"-p"|"--push" )
			PUSH="--push"
			;;
		
		"--prefix" )
			PREFIX="$1"
			;;

		* )
			echo "Unknown argument: $opt" >&2
			exit 1;;
	esac
done
TARGETS="$1"

for v in ${TARGETS//,/ }; do
	TARGET="$v"
	build
done
