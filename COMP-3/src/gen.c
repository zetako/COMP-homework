#! /usr/bin/tcc -run

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>

int main(int argc, char *argv[]) {
    int repeatTime;
    if (argc == 2) {
        repeatTime = atoi(argv[1]);
    } else {
        return -1;
    }
    const char* dig = "0123456789";
    const char* op = "+-";

    srand(time(NULL));
    int i=0;
    while (i<repeatTime) {
        write(STDOUT_FILENO, dig+rand()%10, 1);
        write(STDOUT_FILENO, op+rand()%2, 1);
        write(STDOUT_FILENO, dig+rand()%10, 1);
        i+=1;
    }
    write(STDOUT_FILENO, &"\n", 1);
}