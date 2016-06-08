#include<stdio.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>
#include<unistd.h>

int main(int argc, char* argv[])
{
	/*Very simple argument checking*/
	if(argc < 3 || argc > 4){
		write(STDOUT_FILENO,"ERROR: Wrong number of Arguments!\n", 34); 
		return -1;
	}

	int permissions = 0600;
	int fp1, fp2, fp3, n, x;

	/*Very simple argument checking*/
	if(argv[1][0] == '-' && argv[2][0] != '-')
	{
        fp1 = STDIN_FILENO;
        fp2 = open(argv[2], O_RDONLY, permissions);

    }else if(argv[1][0] != '-' && argv[2][0] == '-'){
        fp1 = open(argv[1], O_RDONLY, permissions);
        fp2 = STDIN_FILENO;

    } else if(argv[1][0] == '-' && argv[2][0] == '-'){
    	write(STDOUT_FILENO,"ERROR! Two Dashes Printed\n",26);
		return(-1);

    } else {
		fp1 = open(argv[1], O_RDONLY,permissions);
		fp2 = open(argv[2], O_RDONLY,permissions);
	}

	if(fp1 == -1)
	{
		perror(argv[1]);
		close(fp1);
		close(fp2);
		close(fp3);
		return -1;
	}
	if(fp2 == -1)
	{
		perror(argv[2]);
		close(fp1);
		close(fp2);
		close(fp3); 
		return -1;
	}

	if(argc == 4)
	{
		fp3 = open(argv[3], O_WRONLY|O_CREAT|O_TRUNC, permissions);
		if(fp3 == -1)
		{
			perror(argv[3]);
			close(fp1);
			close(fp2);
			close(fp3);
			return -1;
		}
	}else{
		fp3 = STDOUT_FILENO;
	}

	char tempLine[1024];
	while((n = read(fp1, tempLine, 1024)) > 0)
	{
		if((x = write(fp3, tempLine, n)) != n)
		{
			write(STDOUT_FILENO, "THERE WAS AN ERROR WRITING FP1 TO FP3!\n",38);
			close(fp1);
			close(fp2);
			close(fp3);
			return -1;
		}
	}

	if(n < 0)
	{
		write(STDOUT_FILENO, "THERE WAS AN ERROR READING FP1 WHEN WRITING IT TO FP2!\n",55);
		close(fp1);
		close(fp2);
		close(fp3);
		return -1;
	}

	while((n=read(fp2, tempLine, 1024))>0)
	{
		if((x = write(fp3, tempLine, n)) != n)
		{
			write(STDOUT_FILENO, "THERE WAS AN ERROR WRITING FP2 TO FP3\n",38);
			close(fp1);
			close(fp2);
			close(fp3);
			return -1;
		}
	}

	if(n < 0)
	{
		write(STDOUT_FILENO, "THERE WAS AN ERROR READING FP2 WHEN WRITING IT TO FP3!\n",55);
		close(fp1);
		close(fp2);
		close(fp3);
		return -1;
	}

	close(fp1);
	close(fp2);
	close(fp3);

	return 0;
}	
