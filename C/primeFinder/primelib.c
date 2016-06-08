#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include"primelib.h"


int isPrimeInt(int num2check)
{

   if(num2check < 2)
   {
      return 1;
   }

   int root =  (int) sqrt(num2check);

   int i;
   for(i = 2; i < root+1; i++)
   {
      if((num2check % i) == 0)
      {
         return 0;
      }
   }

   return 1;
}

int isPrimeLongLong(long long num2check)
{
   if(num2check < 2)
   {
      return 1;
   }

   long long root = (long long)sqrt(num2check);

   long long i;
   for(i = 2; i < root+1; i++)
   {
      if((num2check % i) == 0)
      {
         return 0;
      }
   }

   return 1;
}
