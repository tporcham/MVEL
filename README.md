# MVEL 1.3
MVEL (MVFLEX Expression Language) Version 1.3  
Fork of MVEL Version 1.3.14 which is still in user by certain projects.  
Documentation can be found in the [Wiki](../../wiki).

The more recent 2.* releases of MVEL can be [found here](https://github.com/mvel/mvel)

## Getting Started
Download the mvel .jar file from the target directory.
MVEL comes with a simple shell. You can start the shell with the following command:
```bash
$ java -jar mvel-1.3.14.jar
Welcome to MVEL!
```
The shell command `help;` gives some hints on how to use the shell. Shell commands must end with a ';'.
```
mvel$ help;
Commands
--------
echo            -   toggles output echo on/off
template        -   use the template parser shell
stacktrace      -   toggles stacktraces on/off
benchmark       -   toggles benchmark mode on/off
exectime        -   toggles execution time display on/off
clear           -   clears all variables
quit            -   exits the shell
```
The Shell allows to try out the various features of MVEL:
```
mvel$ 3 + 5
OUT: 8
mvel$ map = ['key':'value', 'color':'green']
OUT: {color=green, key=value}
mvel$ map.class
OUT: class java.util.HashMap
mvel$
```
The shell will store variables entered while the shell runs:
```
mvel$ x = 5
OUT: 5
mvel$ y = x + 7
OUT: 12
```
When you enter multiple statements on a single line the statements have to be seperated by a semi-colon. The result of the expression is the result of the last statement:
```
mvel$ firstName = "John"; lastName = "Doe"
OUT: Doe
```
You can use `clear;` to clear all variables stored at runtime of the shell.
```
mvel$ clear;
CLEARED VARIABLES.
```
