package leetcode.editor.cn;
//给定两个大小分别为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。请你找出并返回这两个正序数组的 中位数 。 
//
// 算法的时间复杂度应该为 O(log (m+n)) 。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums1 = [1,3], nums2 = [2]
//输出：2.00000
//解释：合并数组 = [1,2,3] ，中位数 2
// 
//
// 示例 2： 
//
// 
//输入：nums1 = [1,2], nums2 = [3,4]
//输出：2.50000
//解释：合并数组 = [1,2,3,4] ，中位数 (2 + 3) / 2 = 2.5
// 
//
// 
//
// 
//
// 提示： 
//
// 
// nums1.length == m 
// nums2.length == n 
// 0 <= m <= 1000 
// 0 <= n <= 1000 
// 1 <= m + n <= 2000 
// -10⁶ <= nums1[i], nums2[i] <= 10⁶ 
// 
// Related Topics 数组 二分查找 分治 👍 5148 👎 0

//Java：寻找两个正序数组的中位数
public class MedianOfTwoSortedArrays
{
    public static void main(String[] args)
    {
        Solution solution = new MedianOfTwoSortedArrays().new Solution();
        int[] arrayA = new int[]{1, 2, 3, 4, 5, 54, 78, 99};
        int[] arrayB = new int[]{2, 4, 6, 8, 10, 11, 23, 45, 67};
        System.out.println(solution.findMedianSortedArrays(arrayA, arrayB));
    }

    //leetcode submit region begin(Prohibit modification and deletion)
    class Solution
    {
        public double findMedianSortedArrays(int[] nums1, int[] nums2)
        {
            if (nums1.length > nums2.length)
            {
                return findMedianSortedArrays(nums2, nums1);
            }

            int m = nums1.length;
            int n = nums2.length;
            int left = 0, right = m;
            // median1：前一部分的最大值
            // median2：后一部分的最小值
            int median1 = 0, median2 = 0;

            while (left <= right)
            {
                // 前一部分包含 nums1[0 .. i-1] 和 nums2[0 .. j-1]
                // 后一部分包含 nums1[i .. m-1] 和 nums2[j .. n-1]
                int i = (left + right) / 2;
                int j = (m + n + 1) / 2 - i;

                // nums_im1, nums_i, nums_jm1, nums_j 分别表示 nums1[i-1], nums1[i], nums2[j-1], nums2[j]
                int nums_im1 = (i == 0 ? Integer.MIN_VALUE : nums1[i - 1]);
                int nums_i = (i == m ? Integer.MAX_VALUE : nums1[i]);
                int nums_jm1 = (j == 0 ? Integer.MIN_VALUE : nums2[j - 1]);
                int nums_j = (j == n ? Integer.MAX_VALUE : nums2[j]);

                if (nums_im1 <= nums_j)
                {
                    median1 = Math.max(nums_im1, nums_jm1);
                    median2 = Math.min(nums_i, nums_j);
                    left = i + 1;
                }
                else
                {
                    right = i - 1;
                }
            }
            return (m + n) % 2 == 0 ? (median1 + median2) / 2.0 : median1;
        }
    }
    //leetcode submit region end(Prohibit modification and deletion)
}