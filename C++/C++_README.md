#C++ README

**Meterpreter Killer

This is a program written to find meterpreter code injected into windows processes
and then kill those processes. It does this by examining permissions on the stack
while a process is running. Upon finding part of the stack to be executable, it checks
the allocated size of that executable space. It compares this size to the size of the most 
often used variant of meterpreter(with some slight margin for error) and then kills that 
process.