![[Pasted image 20250921163215.png]]
```c++
#include<bits/stdc++.h>
using namespace std;

void solve()
{
	int n;
	cin >> n;
	vector<vector<int> >edge(n + 1);
	for(int i = 1; i < n; i++){
	 	int u, v;
	 	cin >> u >> v;
		edge[u].push_back(v);
		edge[v].push_back(u); // 建立一颗树，相应节点联系起来
	}
	
	 int cnt = 0;
	 map<int, int> mp; 
	
	if(n == 2){  
	 cout << "0" << "\n"; // 只有两个点的树不需要进行操作
	 return;
	}
	
	for(int i = 1; i <= n; i++){
		if(edge[i].size() == 1){
			mp[edge[i][0]]++; // 找到叶子节点，并将对应点其存入mp中
			cnt++; // 计算叶子节点的数量
		}
	}
	int MAX = 0;
	for(int i = 1; i <= n; i++){
		if(MAX < mp[i]) 
		MAX = mp[i]; // 寻找存入节点中最多的叶子数
	}
	
	int temp = cnt - MAX; // 总叶子数-某节点中拥有的最多的叶子数
	cout << temp << "\n";
	
}

int main()
{
	int t;
	cin >> t;
	while(t--) solve();
	return 0;
}