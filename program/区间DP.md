区间类动态规划是线性动态规划的扩展，它在分阶段地划分问题时，与阶段中元素出现的顺序和由前一阶段的哪些元素合并而来有很大的关系。
令状态 𝑓(𝑖,𝑗)![](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 "f(i,j)") 表示将下标位置 𝑖![](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 "i") 到 𝑗![](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 "j") 的所有元素合并能获得的价值的最大值，那么 𝑓(𝑖,𝑗) =max{𝑓(𝑖,𝑘) +𝑓(𝑘 +1,𝑗) +𝑐𝑜𝑠𝑡}![](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 "f(i,j)=\max\{f(i,k)+f(k+1,j)+cost\}")，𝑐𝑜𝑠𝑡![](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 "cost") 为将这两组元素合并起来的价值。
区间DP在链状的数据组上进行，对于环状的数据也可以将其进行操作变为链状，但由于时间复杂度过高不推荐使用。
区间 DP 有以下特点：

**合并**：即将两个或多个部分进行整合，当然也可以反过来；

**特征**：能将问题分解为能两两合并的形式；

**求解**：对整个问题设最优值，枚举合并点，将问题分解为左右两个部分，最后合并两个部分的最优值得到原问题的最优值。
常见的区间DP有ST算法（用于寻找任意区间的最大值）
```cpp
Obsidian Vault/算法竞赛/倍增——ST算法.md
```