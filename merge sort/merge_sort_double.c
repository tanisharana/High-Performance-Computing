#include <stdio.h> 
#include <stdlib.h>
#include <omp.h>
#include <sys/time.h>
#include<stdint.h>

#define N 40000000

void merge(double *a, int p, int q, int r) {
    int n1 = q - p + 1, n2 = r - q;
    int i, j, k;
    double *L = (double *)malloc(n1 * sizeof(double));
    double *R = (double *)malloc(n2 * sizeof(double));
    for (i = 0; i < n1; i++) L[i] = a[p + i];
    for (j = 0; j < n2; j++) R[j] = a[q + j + 1];
    i = j = 0;
    k = p;
    while (i < n1 && j < n2) {
        if (L[i] <= R[j]) a[k++] = L[i++];
        else a[k++] = R[j++];
    }
    while (i < n1) a[k++] = L[i++];
    while (j < n2) a[k++] = R[j++];
    free(L);
    free(R);
}

void bottom_up_merge_sort(double *a, int n) {
    int i, width;
    for (width = 1; width < n; width = 2 * width) {
        #pragma omp parallel for
        for (i = 0; i < n; i = i + 2 * width) {
            int p = i, q = i + width - 1, r = i + 2 * width - 1;
            if (q >= n) q = n - 1;
            if (r >= n) r = n - 1;
            merge(a, p, q, r);
        }
    }
}
double rd() {
    uint64_t r53 = ((uint64_t)(rand()) << 21) ^ (rand() >> 2);
    return (double)r53 / 9007199254740991.0; // 2^53 - 1
}

int main()
 {
    int i;
    double *a = (double *)malloc(N * sizeof(double));
    struct timeval start, end;
    double runtime[16];
    int num_threads[16] = {1, 2, 4, 6, 8, 10, 12, 14, 16};

    for (int t = 0; t < 9; t++) {
        omp_set_num_threads(num_threads[t]);
        //printf("%d\n", num_threads[t]);
        for (i = 0; i < N; i++) a[i] = rd();
        gettimeofday(&start, NULL);
        bottom_up_merge_sort(a, N);
        gettimeofday(&end, NULL);
        runtime[t] = (end.tv_sec - start.tv_sec) + (end.tv_usec - start.tv_usec) / 1000000.0;
        printf("Number of threads: %d, Average runtime: %lf\n", num_threads[t], runtime[t]);
    }

    // Calculate the average runtime of 5 runs for each number of threads
   


free(a);
return 0;

}
