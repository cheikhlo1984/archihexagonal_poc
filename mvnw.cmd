@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.
@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.1
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "MVN_CMD=mvn.cmd") ELSE (SET "MVN_CMD=%__MVNW_ARG0_NAME__%")
@SET WRAPPER_PROPERTIES=.mvn\wrapper\maven-wrapper.properties

@FOR /F "usebackq tokens=1,2 delims==" %%a IN ("%WRAPPER_PROPERTIES%") DO (
    @IF "%%a"=="distributionUrl" SET "DISTRIBUTION_URL=%%b"
)

@SET "MAVEN_VERSION="
@FOR /F "tokens=* delims=" %%i IN ('echo %DISTRIBUTION_URL%') DO (
    @SET "MAVEN_DIST_URL=%%i"
)

@SET "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists"

@IF EXIST "%MAVEN_HOME%\apache-maven\bin\mvn.cmd" (
    CALL "%MAVEN_HOME%\apache-maven\bin\mvn.cmd" %*
) ELSE (
    @WHERE mvn >NUL 2>&1
    @IF %ERRORLEVEL% EQU 0 (
        CALL mvn %*
    ) ELSE (
        @ECHO ERROR: Maven not found. Please install Apache Maven and add it to PATH.
        EXIT /B 1
    )
)
