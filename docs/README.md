# 


### 安装 graalvm 21.0.2

> https://download.oracle.com/graalvm/21/latest/graalvm-jdk-21_windows-x64_bin.zip

```bash
# powershell
# $env:path

# cmd
Start-Process cmd -Verb runAs
echo %PATH%
setx /M JAVA_HOME "C:\Program Files\Java\graalvm-jdk-21.0.3+7.1"
setx /M PATH "%PATH%;C:\Program Files\Java\graalvm-jdk-21.0.3+7.1\bin;"
echo %PATH%
echo %JAVA_HOME%

```