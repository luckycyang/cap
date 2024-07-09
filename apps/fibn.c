int fib(int n) {
  if (n == 1 || n == 2) {
    return 1;
  } else {
    return fib(n - 1) + fib(n - 2);
  }
}

/**
 * 递归版本斐波那契, 计算 5 的结果
 */
int main() {
  int *result;
  result = (int *)0x1600; // 结果输出 0X1600
  *result = fib(5);
  for (;;) {
  } // Man
  return 0;
}
