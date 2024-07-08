int main() {
  // int *n;            // Input
  // n = (int *)0x1500; // Point input to 0x1000
  int n1 = 0;
  int n2 = 1;
  int n3;
  int i;
  for (i = 2; i < (7) + 1; ++i) {
    n3 = n1 + n2;
    n1 = n2;
    n2 = n3;
  }
  int *result;            // Output
  result = (int *)0x1504; // Point output to 0x1004
  *result = n3;
  for (;;) {
  } // Endless loop, to hold on the result
  return 0;
}
