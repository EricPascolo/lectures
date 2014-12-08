#include <stdio.h>
#include <time.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/time.h>

double cclock_()
/*
 * Return the second elapsed since Epoch (00:00:00 UTC, January 1, 1970)
 * It can be used to measure elapsed time within a code. For example:
 * 
 * tstart = cclock()
 * ....
 * tstop = cclock()
 * print("Elapsed time in seconds: %.3g", tstop - tstart);
 */
{
    struct timeval tmp;
    double sec;
    gettimeofday( &tmp, (struct timezone *)0 );
    sec = tmp.tv_sec + ((double)tmp.tv_usec)/1000000.0;
    //printf("sec = %lf",sec);
    return sec;
}
