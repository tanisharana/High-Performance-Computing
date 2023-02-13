#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <omp.h>

#define MATRIX_SIZE 2048


void multiply_matrices_block(double **a, double **b, double **result, int n, int block_size) 
{

  int i, j, k, i1, j1, k1;
  for(int x=1;x<=4;x=x*2)
  {
  omp_set_num_threads(x);
  
  for (i = 0; i < n; i += block_size) {
    for (j = 0; j < n; j += block_size) {
      for (k = 0; k < n; k += block_size) {
        for (i1 = i; i1 < i + block_size; i1++) {
          for (j1 = j; j1 < j + block_size; j1++) {
            register double r = result[i1][j1];
            for (k1 = k; k1 < k + block_size; k1++) {
              r += a[i1][k1] * b[k1][j1];
            }
            result[i1][j1] = r;
          }
        }
      }
    }
  }
  }
}

int main() {
  int i, j, k, n = MATRIX_SIZE, p = n, q = n;
  double **a = (double **)malloc(p * sizeof(double *));
  for (i = 0; i < p; i++) {
    a[i] = (double *)malloc(q * sizeof(double));
  }

  double **b = (double **)malloc(p * sizeof(double *));
  for (i = 0; i < p; i++) {
    b[i] = (double *)malloc(q * sizeof(double));
  }

  double **result = (double **)malloc(p * sizeof(double *));
  for (i = 0; i < p; i++) {
    result[i] = (double *)malloc(q * sizeof(double));
  }

  // Initialize matrices with random values
  for (i = 0; i < n; i++) {
    for (j = 0; j < n; j++) {
      a[i][j] = (double)rand() / (double)RAND_MAX;
      b[i][j] = (double)rand() / (double)RAND_MAX;
      result[i][j] = 0.0;
    }
  }

  // Timing start
  struct timeval start, end;
  gettimeofday(&start, NULL);
  int BLOCK_SIZE;
  // Multiply matrices using BMM
  for(BLOCK_SIZE=4;BLOCK_SIZE<=64;BLOCK_SIZE*=2)
  {
  #pragma omp parallel
  multiply_matrices_block(a, b, result, n, BLOCK_SIZE);

  // Timing end
  gettimeofday(&end, NULL);
  double runtime =((end.tv_sec - start.tv_sec) * 1000000 + (end.tv_usec - start.tv_usec)) / 1000000.0;
printf("Block Matrix Multiplication runtime: %lf\n", runtime);
}
// Clean up memory
for (i = 0; i < p; i++) {
free(a[i]);
free(b[i]);
free(result[i]);
}
free(a);
free(b);
free(result);

return 0;
}
