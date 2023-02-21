#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <time.h>
#include <omp.h>

#define MIN_DIM 1000
#define MAX_DIM 2000
#define MIN_NUM_MATRICES 10

int **m; 
int **s; 
int *p; 
int n; 

void matrix_chain_multiplication(int i, int j, int num_threads) {
    if (i == j) {
        return;
    }

    int k, q;
    for (k = i; k < j; k++) {
        #pragma omp task shared(m, s, p) firstprivate(i, k) num_threads(num_threads)
        matrix_chain_multiplication(i, k, num_threads);
        #pragma omp task shared(m, s, p) firstprivate(k, j) num_threads(num_threads)
        matrix_chain_multiplication(k+1, j, num_threads);
        #pragma omp taskwait

        q = m[i][k] + m[k+1][j] + p[i-1]*p[k]*p[j];
        if (q < m[i][j]) {
            m[i][j] = q;
            s[i][j] = k;
        }
    }
}

void matrix_chain_order(int num_threads) {
    int i, j, k;

    for (i = 1; i <= n; i++) {
        m[i][i] = 0;
    }

    #pragma omp parallel shared(m, s, p) num_threads(num_threads)
    #pragma omp single
    matrix_chain_multiplication(1, n, num_threads);
}

int main() {
    int i, j, num_threads;

    printf("Enter the number of threads (1-16): ");
    scanf("%d", &num_threads);
    omp_set_num_threads(num_threads);

    srand(time(NULL));
    n = rand() % (MAX_DIM - MIN_DIM + 1) + MIN_DIM;
    p = (int*) malloc((n+1) * sizeof(int));
    for (i = 0; i <= n; i++) {
        p[i] = rand() % (MAX_DIM - MIN_DIM + 1) + MIN_DIM;
    }

    m = (int**) malloc((n+1) * sizeof(int*));
    s = (int**) malloc((n+1) * sizeof(int*));
    for (i = 0; i <= n; i++) {
        m[i] = (int*) malloc((n+1) * sizeof(int));
        s[i] = (int*) malloc((n+1) * sizeof(int));
    }


    for (i = 1; i <= n; i++) {
        for (j = 1; j <= n; j++) {
            m[i][j] = INT_MAX;
            s[i][j] = 0;
        }
    }

    matrix_chain_order(num_threads);

printf("Dimensions of matrices:\n");
for (i = 0; i < n; i++) {
printf("A%d: %d x %d\n", i+1, p[i], p[i+1]);
}
printf("\nCost matrix:\n");
for (i = 1; i <= n; i++) {
    for (j = 1; j <= n; j++) {
        if (m[i][j] == INT_MAX) {
            printf("   - ");
        } else {
            printf("%4d ", m[i][j]);
        }
    }
    printf("\n");
}

printf("\nSplit position matrix:\n");
for (i = 1; i <= n; i++) {
    for (j = 1; j <= n; j++) {
        printf("%4d ", s[i][j]);
    }
    printf("\n");
}

free(p);
for (i = 0; i <= n; i++) {
    free(m[i]);
    free(s[i]);
}
free(m);
free(s);

return 0;
}