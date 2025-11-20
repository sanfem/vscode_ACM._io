prim算法

是一种用于求解加权无向连通图的最小生成树的贪心算法

核心思想

从任意一个顶点开始，逐步扩展生成树，每次选择与当前生成树相连的权重最小的边，并将该边连接的顶点加入生成树


 算法步骤

1. 初始化：选择任意顶点作为起始点，加入生成树
    
2. 循环直到包含所有顶点：
    
    - 找出连接生成树内顶点与生成树外顶点的所有边
        
    - 选择其中权重最小的边
        
    - 将该边和对应的新顶点加入生成树
        
3. 结束：当所有顶点都加入生成树时停


 关键特点

- 贪心策略：每次选择当前最优的边
    
- 时间复杂度：
    
    - 邻接矩阵：O(V²)
        
    - 二叉堆+邻接表：O(E log V)
        
- 适用性：适用于稠密图


例子：

#include<bits/stdc++.h>
using namespace std;
const int N = 5050, M = 2e5 + 10, INF = 0x3f3f3f3f;
int head[M] = {0}, cnt = 0;

struct T{
	int v, w, x;
}te[2 * M];

void add(int u, int v, int w)
{
	cnt++;
	te[cnt].v = v;
	te[cnt].w = w;
	te[cnt].x = head[u];
	head[u] = cnt;
	
}

struct S{
	int u, w;
};

bool operator<(const S &a, const S &b)
{
	return a.w > b.w;
}

int n, m;
int dis[N];
bool vis[N];
int ans = 0, ct = 0;
void prim()
{
	
	priority_queue<S> q;
	memset(dis, INF, sizeof(dis));
	memset(vis, false, sizeof(vis));
	
	q.push({1, 0});
	dis[1] = 0;
	
	while(!q.empty()){
	if(ct >= n) break;
	
	int a = q.top().u, b = q.top().w;
	q.pop();
	
	if(vis[a]) continue;
	vis[a] = true;
	ans += b;
	ct++;
	
	for(int i = head[a] ;i ;i = te[i].x){
		int vv = te[i].v, ww = te[i].w;
		if(ww < dis[vv]){
			dis[vv] = ww;
			q.push({vv, ww});
		}
	}
	
	}
}

int main()
{
	ios::sync_with_stdio(false);
	cin.tie(NULL);
	
	int u, v, w;
	cin >> n >> m;
	for(int i = 1; i <= m; i++){
		cin >> u >> v >> w;
		add(u, v, w);
		add(v, u, w);
	}
	
	prim();
	if(ct == n)
	cout << ans << "\n";
	else cout << "No MST" << "\n";
	
	return 0;
}
