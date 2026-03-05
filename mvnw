#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.1
# ----------------------------------------------------------------------------

set -euf

[ "${MVNW_VERBOSE-}" != true ] || set -x

# OS specific support; same as `uname -o` but works on macOS too.
native_os() { uname -s 2>/dev/null || true; }
is_windows() { case "$(native_os)" in CYGWIN* | MINGW*) true ;; *) false ;; esac; }
native_path() { is_windows && cygpath -w "$1" || echo "$1"; }

# set JAVACMD and JAVA_HOME
if [ -n "${JAVA_HOME-}" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ]; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD="$(command -v java)" || die "ERROR: Cannot find 'java' command. Please set JAVA_HOME."
fi

MAVEN_WRAPPER_PROPERTIES_PATH=".mvn/wrapper/maven-wrapper.properties"
MAVEN_HOME="${MAVEN_USER_HOME:-${HOME}/.m2/wrapper}"

if [ -r "$MAVEN_WRAPPER_PROPERTIES_PATH" ]; then
    DISTRIBUTION_URL="$(grep -E "^distributionUrl=" "$MAVEN_WRAPPER_PROPERTIES_PATH" | sed 's/distributionUrl=//' | tr -d '\r')"
    DISTRIBUTION_URL="${DISTRIBUTION_URL//\\/}"
fi

MAVEN_VERSION="$(echo "$DISTRIBUTION_URL" | grep -oE 'apache-maven-[0-9]+\.[0-9]+\.[0-9]+' | sed 's/apache-maven-//')"
MAVEN_DIST_DIR="$MAVEN_HOME/dists/apache-maven-${MAVEN_VERSION}-bin"
MAVEN_EXEC="$MAVEN_DIST_DIR/apache-maven-${MAVEN_VERSION}/bin/mvn"

if [ ! -x "$MAVEN_EXEC" ]; then
    echo "Downloading Apache Maven ${MAVEN_VERSION}..."
    mkdir -p "$MAVEN_DIST_DIR"
    TEMP_FILE="$(mktemp)"
    if command -v curl > /dev/null 2>&1; then
        curl -fsSL -o "$TEMP_FILE" "$DISTRIBUTION_URL"
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$TEMP_FILE" "$DISTRIBUTION_URL"
    else
        echo "ERROR: Neither curl nor wget found. Please install Maven manually."
        exit 1
    fi
    unzip -q "$TEMP_FILE" -d "$MAVEN_DIST_DIR"
    rm -f "$TEMP_FILE"
fi

exec "$(native_path "$MAVEN_EXEC")" "$@"
