#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/time.h>
#include<sys/stat.h>
#include<sys/fcntl.h>

#define RAND_MAX  2147483647
#define PROCESSES 9


int main(int argc, char* argv[]){

	int pipes[PROCESSES][2];
	char buf1[1024], buf2[1024];
	int n, parent, i, j, myndx, num, rng, len, len2;

	for(i = 0; i < PROCESSES; i++)
	{
		if(pipe(pipes[i]))
		{
			perror("Pipe Error");
			exit(-1);
		}
	}

	myndx = 0;
	for(i = 1; i < PROCESSES; i++)
	{
		switch(parent = fork())
		{
			case -1:
				perror("Fork Error");
				exit(-1);

			case 0:
				myndx = i;
				break;

			default:
				break;
		}
		if(parent){break;}
	}

	for(i = 0; i < PROCESSES; i++)
	{
		if(i != myndx)
		{
			close(pipes[i][0]);
		}
	}
	
	srand(RAND_MAX * myndx);
	rng = RAND_MAX / PROCESSES;
	rng *= PROCESSES;
	for(n = 0; n < 13; n++)
	{
		do
		{
			num = rand();
			j = num%PROCESSES;
		}

		while(num >= rng || i == j);

		sprintf(buf1, "process%d", myndx);

		if(write(pipes[j][1], &buf1, 8) != 8)
		{
			perror("Write Error");
			exit(-1);
		}
	}

	for(i = 0; i < PROCESSES; i++)
	{
		close(pipes[i][1]);
	}

	while((n = read(pipes[myndx][0], &buf1, 8)) > 0)
	{
		sprintf(buf2, "process%d has received a message from %s\n", myndx, buf1);
		write(STDOUT_FILENO,buf2,strlen(buf2));
	}
}	 