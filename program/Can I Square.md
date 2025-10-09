![[Pasted image 20251008135326.png]]
要求：判断某个数的数量和是否是某个数的平方(大于0)。
思路：对该数的数量相加后进行开方，在平方后看是否能得到开方前数字。
```c++
#include<bits/stdc++.h>
using namespace std;
typedef long long ll;
const int N = 2e5;

void solve()
{
	int n;
	cin >> n;
	vector<ll> a(n);
	ll c = 0;
	for(int i = 0; i < n; i++){
		cin >> a[i];
		c += a[i];
	} 
	
	ll b = sqrt(c);
	if(b * b == c)
	cout << "YES" << "\n";
	else cout << "NO" << "\n";
}

int main()
{
	int t;
	cin >> t;
	
	while(t--) solve();
	return 0;
}