@echo off
setlocal
set MAVEN_VERSION=3.9.16
set MAVEN_ZIP_SHA512=ed41650d42485cfc243fad22158caf9cbb5dc408ce7a09ddb94dd42a019de929ca43065bfa450612cf12bf78b5cafa3884b96c090de326ff590448c933454af3
set ROOT=%~dp0
set CACHE=%ROOT%.mvn\.cache\apache-maven-%MAVEN_VERSION%
if exist "%CACHE%\bin\mvn.cmd" goto run
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $root='%ROOT%'; $v='%MAVEN_VERSION%'; $expected='%MAVEN_ZIP_SHA512%'; $cache=Join-Path $root '.mvn\.cache'; New-Item -ItemType Directory -Force -Path $cache | Out-Null; $zip=Join-Path $cache ('apache-maven-'+$v+'-bin.zip'); Invoke-WebRequest -UseBasicParsing ('https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/'+$v+'/apache-maven-'+$v+'-bin.zip') -OutFile $zip; $actual=(Get-FileHash -Algorithm SHA512 $zip).Hash.ToLowerInvariant(); if ($actual -ne $expected) { Remove-Item -Force $zip; throw 'Maven archive checksum mismatch' }; Expand-Archive -Force $zip $cache"
if errorlevel 1 exit /b %errorlevel%
:run
call "%CACHE%\bin\mvn.cmd" %*
