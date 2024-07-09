int main() {
  int n1 = 0;
  int n2 = 1;
  int n3;
  int i;
  for (i = 2; i < (7) + 1; ++i) {
    n3 = n1 + n2;
    n1 = n2;
    n2 = n3;
  }
  int *result;
  result = (int *)0x1504; // 结果输出至 0x1004
  *result = n3;
  for (;;) {
  } // Man
  return 0;
}
