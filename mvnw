#!/bin/sh
set -eu
MAVEN_VERSION=3.9.16
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
CACHE="$ROOT/.mvn/.cache/apache-maven-$MAVEN_VERSION"
ARCHIVE="$ROOT/.mvn/.cache/apache-maven-$MAVEN_VERSION-bin.tar.gz"
URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz"
SHA512="831a8591fe20c8243b1dbe7d71e3244f31d1665b0804b2e825e38cbbe5ce0cafb8338851f90780735568773e0a6cd07bbec107cda0b896b008b861075358b6f6"

if [ ! -x "$CACHE/bin/mvn" ]; then
  mkdir -p "$ROOT/.mvn/.cache"
  if [ ! -f "$ARCHIVE" ]; then
    if command -v curl >/dev/null 2>&1; then
      curl -fL "$URL" -o "$ARCHIVE"
    elif command -v wget >/dev/null 2>&1; then
      wget -O "$ARCHIVE" "$URL"
    else
      echo "curl or wget is required to bootstrap Maven $MAVEN_VERSION" >&2
      exit 1
    fi
  fi
  if command -v sha512sum >/dev/null 2>&1; then
    ACTUAL=$(sha512sum "$ARCHIVE" | awk '{print $1}')
  elif command -v shasum >/dev/null 2>&1; then
    ACTUAL=$(shasum -a 512 "$ARCHIVE" | awk '{print $1}')
  else
    echo "sha512sum or shasum is required to verify Maven" >&2
    exit 1
  fi
  if [ "$ACTUAL" != "$SHA512" ]; then
    echo "Maven archive checksum mismatch" >&2
    rm -f "$ARCHIVE"
    exit 1
  fi
  tar -xzf "$ARCHIVE" -C "$ROOT/.mvn/.cache"
fi
exec "$CACHE/bin/mvn" "$@"
