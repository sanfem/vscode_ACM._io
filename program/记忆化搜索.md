一种通过将复杂问题分解为重叠子问题来求解的算法思想。
特征：
**最优子结构**：问题的最优解包含子问题的最优解
    
**重叠子问题**：子问题会被重复计算，需要记忆化存储
    
**状态转移**：通过状态定义和转移方程建立子问题间的关系

例题：
1.
跳台阶
要求：一次可以跳一个台阶或两个台阶，问有多少种跳法。
思路：对输入台阶数进行dfs，为了避免溢出使用记忆化搜索。
```c++
#include<bits/stdc++.h>
using namespace std;
const int N = 1001;
int mem[N], sum;
int dfs(int x)
{
    if(mem[x]) return mem[x];
    
    if(x == 1) sum = 1;
    else if(x == 2) sum = 2;
    else sum = dfs(x - 1) + dfs(x - 2);
    
    mem[x] = sum;
    return sum;
}

int main()
{   
    int n;
    cin >> n;
    int res = dfs(n);
    cout << res << endl;
    
    return 0;
}

2.
Vlad and a Sum of Sum of Digits
要求：求出从1到n的数字之和(大于9之后的数要拆开加)。
思路：把从1到n的数进行预处理，然后输出第n位的值。
```c++
#include<bits/stdc++.h>
using namespace std;
typedef long long ll;
const int N = 2e5 + 10;
ll a[N];
void solve()
{
	int n;
	cin >> n;
	cout << a[n] << "\n";  
}

int main()
{
	ios::sync_with_stdio(false);
	cin.tie(NULL);
	int t;
	cin >> t;
	int m;
	a[0] = 0;
	
	for(int i = 1; i <= N; i++){
	m = i;
	int sum = 0, temp;
	while(m > 0){
		temp = m % 10;
		sum += temp;
		m /= 10;	
	}
	a[i] = a[i - 1] + sum;
	}
	
	while(t--) solve();
	return 0;
}