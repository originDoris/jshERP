@echo off
if "%1"=="h" goto begin
start mshta vbscript:createobject("wscript.shell").run("""%~nx0"" h",0)(window.close)&&exit
:begin
title jshERP

java -Xms1000m -Xmx2000m -jar .\lib\jshERP.jar
pause over