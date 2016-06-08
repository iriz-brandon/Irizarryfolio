#primeFinder problem description

   ** Write a program, primeFinder, in C that will accept 2-3 values as parameters. The first
      parameter being a starting number, the second parameter being the number of primes to 
      find after the starting number, and the optional third paramter is the number of 
      threads to use to accomplish this. Also keep track of how long this operation takes.
      Output the found primes and the total time it took at the end. 
      
   Testing - the executable 'xprime4' should work. If not, compile the primeFinder.c to make a new executable.
   
   gcc -g -c primelib.c
   gcc -g -c primeFinder.c
   gcc -g -o xprime primeFinder.o primelib.o -lm -lpthread

   ./xprime 10241024 10 4

   Uses - primelib.h library to determine if a number is a prime. Checks a 'long long' or an 'int'


#catsup problem description

   ** Write a program, catsup, in C using only low-level I/O which will accept two or three command-line
      arguments, which should be file names, and will concatenate the first file with the second file
      while writing the result to the third file or stdout.
      
   Testing - the executable xcatsup.c should work, if not compile the catsup.c to make a new executable.
   
      gcc -ansi -g -o xcatsup catsup.c
   
      ./xcatsup data1.txt data2.txt | OR | ./xcatsup data1.txt data2.txt data3.txt 


#simnet problem description 

   ** Write a single program, called netsim.c that will create a total of 9 processes which will identify
      themselves as process0 - process8. The original process is process0.
      
   ** Each process will randomly, 13 times, select one of the other processes and write its process name
      to the selected process. After writing the 13 messages, each process will close its pipes to the 
      other process and then will read all messages written to it, printing out, for each message,
      a route message on stdout using stdio.
      
   ** Each process will terminate when no other process has its read pipe open for write. No process should
      wait for any other process to complete nor should any writing to pipes occur in the fork loop. 
      
   Testing - the executable simnet should work. If not, compile the simnet.c to make a new executable.
   
      gcc -ansi -g -o xsimnet simnet.c
   
      ./simnet
   
   
#dsh_shell problem description

   dsh.c contains the code for the shell, but wont produce a viable executable by itself

   ** This project is to extend a simple shell to handle redirection, piping, and backgrounding
      (i.e. the program will be started executing and the shell will not wait for the program to
      terminate).
   
   ** The shell should terminate on the command "exit".
   
   ** The program should handle stdin, stdout, and append redirection( <, > and >>) but the stderr
      redirection need not be handled.
      
   ** Backgrounding will be designated with an '&' .
      
   ** The program should handled one level of piping with possible redirection, thus the most 
      complicated command would have a form similar to:
      
     prog1 arg1 arg2 | prog2 arg3 < infile > outfile &
     
   ** A cmdscan.c file is included for sending cmd line strings that it will then break up into a
      struct based on the inputs given on the command line.
      
   Testing - to test this project, you will need to unzip the zip folder, then configure, then make.
             After which you can then run the executable.
             
      unzip dsh-1.0.zip
   
      cd dsh-1.0
      
      ./configure
      
      make
      
      ./dsh (or exectuable that gets made)
      
   Then once it is running, act as if you are using a regular shell.
   
   Backspace doesn't work, so just press enter and start again.
   
   "exit" will return you to the original shell. 