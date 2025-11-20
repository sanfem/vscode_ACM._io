kruskal算法

是一种用于求解最小生成树的贪心算法

核心思想

按边权从小到大排序，逐个尝试加入边，确保不形成环


算法步骤

1. 排序：将所有边按权值从小到大排序
    
2. 初始化：每个顶点自成一个连通分量
    
3. 选边：从最小权值边开始，如果该边的两个顶点不在同一连通分量中，则加入生成树
    
4. 判环：使用并查集高效判断是否形成环
    
5. 终止：当选中 n-1 条边时结束（n为顶点数）


 时间复杂度

- 排序：O(E log E)
    
- 并查集操作：O(E α(V))
    
- 总复杂度：O(E log E)


特点

- 适合稀疏图（边少）
    
- 代码实现简单
    
- 需要预先对所有边排序

例题：

#include<bits/stdc++.h>
using namespace std;

struct clody{
	int u, v, w;
}cl[10000];
int fa[1005];
int n, m ,k, p = 0;
void add(int u, int v, int w)
{
	p++;
	cl[p].u = u;
	cl[p].v = v;
	cl[p].w = w;
	
} 

bool cmp(clody a, clody b)
{
	return a.w < b.w;
}

int find(int x)
{
	return fa[x] == x ? x : fa[x] = find(fa[x]);
}

void unity(int a, int b)
{
	
	int aa = find(a);
	int bb = find(b);
	fa[aa] = bb;
}

void kruskal()
{
	int edge = 0, tot = 0;
	for(int i = 1; i <= m; i++){
		int xu = cl[i].u, xv = cl[i].v;
		
		if(find(xu) != find(xv)){
			unity(xu, xv);
			edge++;
			tot += cl[i].w;
		}
		
		if(edge == n - k){
		cout << tot << "\n";
		return;
		}
		
	}
	cout << "No Answer" << "\n";	
}
	


int main()
{
	
	cin >> n >> m >> k;
	if(n == k){
		cout << "0" << "\n";
		return 0;
	}
	
	for(int i = 1; i <= n; i++)
		fa[i] = i;
	
	int x, y, l;
	for(int i = 1; i <= m; i++){
		cin >> x >> y >> l;
		add(x, y, l);
	}
	
	sort(cl + 1, cl + m + 1, cmp);
	
	kruskal();
	return 0;
}