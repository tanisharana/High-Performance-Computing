#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <omp.h>

#define MATRIX_SIZE 20
#define N_POWERS 4

double **allocate_matrix(int rows, int cols) {
  int i;
  double **matrix = (double**)malloc(rows * sizeof(double*));
  for (i = 0; i < rows; i++) {
    matrix[i] = (double*)malloc(cols * sizeof(double));
  }
  return matrix;
}

void fill_matrix(double **matrix, int rows, int cols) {
  int i, j;
  for (i = 0; i < rows; i++) {
    for (j = 0; j < cols; j++) {
      matrix[i][j] = (double)rand() / RAND_MAX;
    }
  }
}

void multiply_matrices(double **a, double **b, double **result, int a_rows, int a_cols, int b_cols) {
  int i, j, k;
  for(int x=1;x<=4;x=x*2)
  {
  omp_set_num_threads(x);
  
  #pragma omp parallel for private(i, j, k) shared(a, b, result)
  for (i = 0; i < a_rows; i++) {
    for (j = 0; j < b_cols; j++) {
      result[i][j] = 0;
      for (k = 0; k < a_cols; k++) {
        result[i][j] += a[i][k] * b[k][j];
      }
    }
  }
  }
}

double calculate_runtime(struct timeval start, struct timeval end) {
  return (end.tv_sec - start.tv_sec) * 1000.0 + (end.tv_usec - start.tv_usec) / 1000.0;
}

int main(int argc, char** argv) {
  int i, j, k;
  double **a, **b, **temp;
  struct timeval start, end;

  srand(time(NULL));

  a = allocate_matrix(MATRIX_SIZE, MATRIX_SIZE);
  b = allocate_matrix(MATRIX_SIZE, MATRIX_SIZE);
  temp = allocate_matrix(MATRIX_SIZE, MATRIX_SIZE);

  fill_matrix(a, MATRIX_SIZE, MATRIX_SIZE);

  gettimeofday(&start, NULL);
  for (i = 0; i < N_POWERS; i++) {
    if (i == 0) {
      b = a;
    } else {
      multiply_matrices(a, b, temp, MATRIX_SIZE, MATRIX_SIZE, MATRIX_SIZE);
      b = temp;
    }
  
  gettimeofday(&end, NULL);

  double runtime = calculate_runtime(start, end);
  printf("Ordinary Matrix Multiplication (OMM) runtime: %f ms\n", runtime);
  }
for (i = 0; i < MATRIX_SIZE; i++) {
free(a[i]);
free(b[i]);
free(temp[i]);
}
free(a);
free(b);
free(temp);

return 0;
}
