// 交换两个元素的值
void swap(int *a, int *b) {
  int temp = *a;
  *a = *b;
  *b = temp;
}

// 分区函数
int partition(int arr[], int low, int high) {
  int pivot = arr[high]; // 选择最右边的元素作为枢轴
  int i = (low - 1);     // 将较小元素的索引初始化为低索引减一

  for (int j = low; j <= high - 1; j++) {
    if (arr[j] < pivot) {
      i++;                    // 增加较小元素的索引
      swap(&arr[i], &arr[j]); // 交换当前元素和较小元素
    }
  }
  swap(&arr[i + 1], &arr[high]); // 将枢轴元素放到正确位置
  return (i + 1);
}

// 快速排序函数
void quickSort(int arr[], int low, int high) {
  if (low < high) {
    int pi = partition(arr, low, high); // 找到分区点

    quickSort(arr, low, pi - 1);  // 递归对左子数组排序
    quickSort(arr, pi + 1, high); // 递归对右子数组排序
  }
}

int main() {
  int arr[] = {10, 7, 8, 9, 1, 5, 6, 11, 17, 19};
  int n = sizeof(arr) / sizeof(arr[0]);
  int *result = (int *)0x1600;
  // 先放 内存
  for (int i = 0; i < n; i++) {
    *(result + i * 4) = *(arr + i);
  }
  quickSort(arr, 0, n - 1);
  // 后放内存
  for (int i = 0; i < n; i++) {
    *(result + i * 4) = *(arr + i);
  }
  return 0;
}
