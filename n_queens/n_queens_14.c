#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

#define N 14

int count = 0;
int board[N][N];

void print_board()
{
    int i, j;
    for (i = 0; i < N; i++) {
        for (j = 0; j < N; j++)
            printf("%d\t", board[i][j]);
        printf("\n");
    }
    printf("\n");
}

int is_attack(int row, int col)
{
    int i, j;

    for (i = 0; i < col; i++)
        if (board[row][i])
            return 1;

    for (i = row, j = col; i >= 0 && j >= 0; i--, j--)
        if (board[i][j])
            return 1;

    for (i = row, j = col; j >= 0 && i < N; i++, j--)
        if (board[i][j])
            return 1;

    return 0;
}

int nqueens(int col)
{
    int i;

    if (col == N) {
#pragma omp critical
        count++;
        return 1;
    }

    for (i = 0; i < N; i++) {
        if (!is_attack(i, col)) {
            board[i][col] = 1;
            nqueens(col + 1);
            board[i][col] = 0;
        }
    }
    return 0;
}

int main()
{
    int i, j;
    double start, end;
    FILE *fp;

    fp = fopen("nqueens_time.dat", "w");
    if (fp == NULL) {
        printf("Unable to open file\n");
        return 1;
    }

    for (i = 1; i <= 16; i++) {
        count = 0;
        omp_set_num_threads(i);
        start = omp_get_wtime();

        #pragma omp parallel
        {
            #pragma omp single
            nqueens(0);
        }

        end = omp_get_wtime();

        printf("Number of threads: %d\n", i);
        printf("Number of solutions: %d\n", count);
        printf("Time taken: %g\n", end - start);
        fprintf(fp, "%d %g\n", i, end - start);
    }

    fclose(fp);
    return 0;
}