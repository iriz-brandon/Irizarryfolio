#include<stdio.h>
#include<stdlib.h>
#include<sys/time.h>
#include<pthread.h>
#include"primelib.h"

#define MILLION 1000000L

pthread_t tid[4];
pthread_mutex_t lock;
pthread_mutex_t lock2;
int counter;
double timedif;
struct timeval tpend;
struct timeval tpstart;
long long nextPrime;
long long *primesFound;
int primesToFind;
int primeFoundCount;
int check;

void sortPrimes(long long);
void quickSort(long long[], int, int);

void* primeFinder(void *arg)
{
   long long temp;

   while(primeFoundCount < primesToFind)
   {
      pthread_mutex_lock(&lock);
      temp = nextPrime;
      nextPrime++;
      pthread_mutex_unlock(&lock);

      check = isPrimeLongLong(temp);
      if(check == 1)
      {
         pthread_mutex_lock(&lock2);

         if(primeFoundCount > primesToFind - 1) 
         {
            if(primesFound[primesToFind - 1] > temp)
            {
               primesFound[primesToFind - 1] = temp;
            }
         }else{
            primesFound[primeFoundCount] = temp;
            sortPrimes(temp);
         }
         primeFoundCount++;

         pthread_mutex_unlock(&lock2);
      }
   }
}

void sortPrimes(long long primeChecked){
   int i, j;
   long long temp;

   if(primesFound[primesToFind - 1] != -1)
   {
      if(primesFound[primesToFind - 1] > primeChecked)
      {
         primesFound[primesToFind - 1] = primeChecked;
      }
   }else {

      for(i = 0; i < primesToFind; i++)
      {
         if(i > primeFoundCount){break;}

         if(primesFound[i] == -1)
         {
            for(j = i; j < primesToFind; j++)
            {
               if(primesFound[j] != -1)
               {
                  temp = primesFound[i];
                  primesFound[i] = primesFound[j];
                  primesFound[j] = temp;
                  break;
               }
            }
         }
      }
      quickSort(primesFound, 0, i-1);
   }
}

void quickSort( long long a[], int l, int r)
{
   int j;

   if( l < r )
   {
       j = partition( a, l, r);
       quickSort( a, l, j-1);
       quickSort( a, j+1, r);
   }

}
int partition( long long a[], int l, int r) 
{
   int i, j;
   long long pivot = a[l];
   long long t;
   i = l; j = r+1;

   while(1)
   {
        do ++i; while( a[i] <= pivot && i <= r );
        do --j; while( a[j] > pivot );
        if( i >= j ) break;
        t = a[i]; a[i] = a[j]; a[j] = t;
   }

   t = a[l]; a[l] = a[j]; a[j] = t;
   return j;
}

int main(int nargs, char* args[])
{
   int i, err, threadCount, check;
   long long passedNum;
   pthread_t *tids;

   check = 0;
   primeFoundCount = 0;

   if(nargs == 4)
   {
      threadCount = atoi(args[3]);
   }else if(nargs != 3){
      printf("Only 2 arguements needed. Exiting\n");
      exit(-1);
   }

   passedNum = atoll(args[1]);
   primesToFind = atoi(args[2]);

   nextPrime = passedNum;

   primesFound = malloc(sizeof(long long) * primesToFind);

   for(i = 0; i < primesToFind; i++)
   {
      primesFound[i] = -1;
   }

   if (pthread_mutex_init(&lock, NULL) != 0) 
   {
      printf("\n mutex1 init failed\n");
      return 1;
   }

   if (pthread_mutex_init(&lock2, NULL) != 0) 
   {
      printf("\n mutex2 init failed\n");
      return 1;
   }

   if (gettimeofday(&tpstart, NULL)) 
   {
      fprintf(stderr, "Failed to get start time\n");
      return 1;
   }

   tids = (pthread_t *)malloc(threadCount*sizeof(pthread_t *));
   if (tids == NULL) 
   {
      fprintf(stderr,"Error allocating space for %d thread ids\n",threadCount);
      return 1;
   }

   for (i = 0; i < threadCount; i++)
   {
     if (pthread_create(tids+i,NULL,primeFinder,NULL)) 
     {
        fprintf(stderr,"Error creating thread %d\n",i+1);
        return 1;
     }
   }
   
   for (i = 0; i < threadCount; i++)
   {
     if (pthread_join(tids[i],NULL)) 
     {
        fprintf(stderr,"Error joining thread %d\n",i+1);
        return 1;
     }
   }

   pthread_mutex_destroy(&lock);
   pthread_mutex_destroy(&lock2);

   quickSort(primesFound, 0, primesToFind-1);

   if (gettimeofday(&tpend, NULL))
   {
      fprintf(stderr, "Failed to get end time\n");
      return 1;
   }

   timedif = (double)(tpend.tv_sec - tpstart.tv_sec) +
             (double)(tpend.tv_usec - tpstart.tv_usec)/MILLION;

   for(i = 0; i < primesToFind; i++)
   {
      printf("%llu\n", primesFound[i]);
   }
   fprintf(stderr, "%.5f\n", timedif);

   return 0;
}