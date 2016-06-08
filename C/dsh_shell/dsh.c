/* Brandon Irizarry  @01396419 */

#include<stdio.h>
#include<stdlib.h>
#include<sys/wait.h>
#include<string.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/time.h>
#include<sys/stat.h>
#include<sys/fcntl.h>
#include <signal.h>

struct cmd
{
  int redirect_in;     /* Any stdin redirection?         */
  int redirect_out;    /* Any stdout redirection?        */
  int redirect_append; /* Append stdout redirection?     */
  int background;      /* Put process in background?     */
  int piping;          /* Pipe prog1 into prog2?         */
  char *infile;        /* Name of stdin redirect file    */
  char *outfile;       /* Name of stdout redirect file   */
  char *argv1[10];     /* First program to execute       */
  char *argv2[10];     /* Second program in pipe         */
};


void execAfterConsideringBackgrounding(struct cmd command, pid_t); /* Sends a pid to be killed */
void execWithBackgrounding(struct cmd command);
int cmdscan(char *cmdbuf, struct cmd *com);

int main() 
{
  char buf[1024];
  struct cmd command;

  while( (gets(buf) != NULL) )
  {
    if(cmdscan(buf, &command))
    {
      printf("Illegal Format: \n");
      continue;
    }

    if(strcmp(command.argv1[0],"exit") == 0){exit(1);}

    if(command.background)
    {
      execWithBackgrounding(command);
    }else{
      execAfterConsideringBackgrounding(command, -1);
    }
  }

  return(0);
}

void execWithBackgrounding(struct cmd command)
{
  pid_t bkgrndPid; 
  switch(bkgrndPid = fork()) //adds an extra insta-killable child so the terminal doesnt have to wait
  {
    case -1:
      perror("fork failed in backgrounding");
    case 0:
      bkgrndPid = getpid();
      execAfterConsideringBackgrounding(command, bkgrndPid);
    default:
	    waitpid(bkgrndPid, NULL, 0);
      return;	   
	}
	return;
}
     
void execAfterConsideringBackgrounding(struct cmd command, pid_t bkgrndPid)
{
  int fdIn; /*file descriptors*/
  int fdOut;

  int pd[2]; /*pipe descriptor*/

  pid_t kidPid;
  switch(kidPid = fork())
  {
    case -1:
      perror("fork failed in execNoBackGround");

    /*The first child gets created*/
    case 0:
      if(bkgrndPid > 0)
      {
        kill(bkgrndPid, SIGTERM);
      }

      /*What to do if piping is included*/
      if(command.piping)
      {
        pipe(pd); /*create a pipe for the children to talk*/
  	    switch(fork())
        {
          case -1:
            perror("fork failed after pipe flag");
  		
          /*The second child gets created*/
          case 0:
            if(command.redirect_in)/*Only prog1 deals with redirected stdin*/
            {
              fdIn = open(command.infile, O_RDONLY);
              dup2(fdIn, STDIN_FILENO);
              close(fdIn);
            }

            dup2(pd[1], STDOUT_FILENO); /*has to put to pipe*/
            close(pd[1]);
            close(pd[0]);
  		      execvp(command.argv1[0], command.argv1);
            perror("exec failed in piped parent/first child");	
  		      exit(-1);

          /*The First child as a parent to the second child*/
          default:
            if(command.redirect_out) /*Only prog2 deals with redirected stdout*/
            {
              if(command.redirect_append)
              {
                fdOut = open(command.outfile, O_WRONLY | O_CREAT | O_APPEND, 0600);
              }else{
                fdOut = open(command.outfile, O_WRONLY | O_CREAT | O_TRUNC, 0600);
              }
              dup2(fdOut, STDOUT_FILENO);
              close(fdOut);
            }
		        dup2(pd[0], STDIN_FILENO); /*has to get from pipe*/
            close(pd[0]);
		        close(pd[1]);
		        execvp(command.argv2[0], command.argv2);
	          perror("exec failed in piped child/second child");
		        exit(-1);
        }
      }
            
      /*What to do no Piping Included*/
	    if(command.redirect_in)
	    {
		    fdIn = open(command.infile, O_RDONLY);
		    dup2(fdIn, STDIN_FILENO);
		    close(fdIn);
	    }

	    if(command.redirect_out)
      {
		    if(command.redirect_append)
        {
		      fdOut = open(command.outfile, O_WRONLY | O_CREAT | O_APPEND, 0600);
		    }else{
		      fdOut = open(command.outfile, O_WRONLY | O_CREAT | O_TRUNC, 0600);
		    }
		    dup2(fdOut, STDOUT_FILENO);
		    close(fdOut);
	    }
	    execvp(command.argv1[0], command.argv1);
	    perror("exec failed in only child, no piping\n");
	    exit(-1);

    /*The Original Process*/		  
    default:
      waitpid(kidPid, NULL, 0);
      return;
  }
}
