#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <sys/time.h>

#define N 40000000

void merge(int *a, int p, int q, int r) {
    int n1 = q - p + 1, n2 = r - q;
    int i, j, k;
    int *L = (int *)malloc(n1 * sizeof(int));
    int *R = (int *)malloc(n2 * sizeof(int));
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

void bottom_up_merge_sort(int *a, int n) {
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

int main()
 {
    int i;
    int *a = (int *)malloc(N * sizeof(int));
    struct timeval start, end;
    double runtime[16];
    int num_threads[16] = {1, 2, 4, 6, 8, 10, 12, 14, 16};

    for (int t = 0; t < 9; t++) {
        omp_set_num_threads(num_threads[t]);
        for (i = 0; i < N; i++) a[i] = rand();
        gettimeofday(&start, NULL);
        bottom_up_merge_sort(a, N);
        gettimeofday(&end, NULL);
        runtime[t] = (end.tv_sec - start.tv_sec) + (end.tv_usec - start.tv_usec) / 1000000.0;
    }

    // Calculate the average runtime of 5 runs for each number of threads
    for (int t = 0; t < 9; t++) {
        double avg_runtime = 0;
        for (i = 0; i < 5; i++) {
            avg_runtime += runtime[t];
       
    avg_runtime /= 5;
    printf("Number of threads: %d, Average runtime: %lf\n", num_threads[t], avg_runtime);
}

free(a);
return 0;
}
}
