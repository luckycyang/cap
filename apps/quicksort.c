static void quicksort(int *arr, int l, int r)
{
    if (l >= r)
        return;

    int pivot = arr[l];
    int i = l, j = r;
    while (i < j) {
        while (arr[j] >= pivot && i < j)
            --j;
        arr[i] = arr[j];
        while (arr[i] < pivot && i < j)
            ++i;
        arr[j] = arr[i];
    }
    arr[i] = pivot;
    quicksort(arr, l, i - 1);
    quicksort(arr, i + 1, r);
}

int main()
{
    int nums[10] = {6, 2, 4};

    quicksort(nums, 0, 2);

    int *result;
    result =  (int *)0x1600;
    for (int i = 0; i < 2; ++i)
        *(result + i * 4) = nums[i];

    return 0;
}
