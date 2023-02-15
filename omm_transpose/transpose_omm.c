#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MIN_ORDER 512
#define MAX_ORDER 2048

void fillMatrix(int N, double *matrix) {
  int i, j;
  for (i = 0; i < N; i++) {
    for (j = 0; j < N; j++) {
      matrix[i * N + j] = (double)rand() / RAND_MAX;
    }
  }
}

void transpose(int N, double *matrix) {
  int i, j;
  for (i = 0; i < N; i++) {
    for (j = i + 1; j < N; j++) {
      double temp = matrix[i * N + j];
      matrix[i * N + j] = matrix[j * N + i];
      matrix[j * N + i] = temp;
    }
  }
}

void matrixMultiplication(int N, double *A, double *B, double *C) {
  int i, j, k;
  for (i = 0; i < N; i++) {
    for (j = 0; j < N; j++) {
      for (k = 0; k < N; k++) {
        C[i * N + j] += A[i * N + k] * B[k * N + j];
      }
    }
  }
}

void matrixPowerOMM(int N, double *matrix, int n) {
  double *result = (double *)malloc(N * N * sizeof(double));
  int i, j, k;
  for (k = 0; k < n; k++) {
    for (i = 0; i < N; i++) {
      for (j = 0; j < N; j++) {
        result[i * N + j] = 0;
      }
    }
    matrixMultiplication(N, matrix, matrix, result);
    double *temp = matrix;
    matrix = result;
    result = temp;
  }
  free(result);
}

int main() {
  srand(time(NULL));
  int i, j;
  for (i = MIN_ORDER; i <= MAX_ORDER; i *= 2) {
    double *matrix = (double *)malloc(i * i * sizeof(double));
    fillMatrix(i, matrix);
    transpose(i, matrix);
    for (j = 2; j <= 16; j++) {
      double start = omp_get_wtime();
      matrixPowerOMM(i, matrix, j);
      double end = omp_get_wtime();
      printf("Time taken for matrix of size %d with power %d using OMM is %lf\n", i, j, end - start);
    }
    free(matrix);
  }
  return 0;
}