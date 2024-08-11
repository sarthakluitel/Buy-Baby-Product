package np.com.yourname.babybuy.dashboard.recyclerview;

import androidx.annotation.NonNull;

public class SelectionSort {
        public static void selectionSort(@NonNull int[] arr) {
            int n = arr.length;
            for (int i = 0; i < n - 1; i++) {
                int HomeFragment= i;
                for (int j = i + 1; j < n; j++) {
                    if (arr[j] < arr[HomeFragment]) {
                        HomeFragment = j;
                    }
                }
                // Swap arr[i] and arr[minIndex]
                int temp = arr[i];
                arr[i] = arr[HomeFragment];
                arr[HomeFragment] = temp;
            }
        }
    }


