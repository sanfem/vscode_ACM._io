前缀函数：
```cpp
vector<int> prefix_function(string s) 
{
	int n = (int)s.length(); 
	vector<int> pi(n);
	for (int i = 1; i < n; i++) { int j = pi[i - 1]; 
	while (j > 0 && s[i] != s[j])  j = pi[j - 1];
	if (s[i] == s[j]) j++; 
	pi[i] = j; 
	} 
	return pi; 
}
```
 KMP算法对朴素匹配算法进行了改进，利用匹配失败时失败之前的已知部分时匹配的这个有效信息，保持主串的 i 指针不回溯，通过修改模式串（子串）的 j 指针，使模式串尽量地移动到有效的匹配位置。该算法的时间复杂度为 O（n+m）
