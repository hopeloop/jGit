# Java课程项目

此项目为java第24组的课程项，拟用java实现部分git命令的版本控制工具 



## 课程项目要求

- 命令行工具

- 参考git实现原理，实现blob,tree,commit核心存储结构

### 功能点

1. 可以提交commit，可以进行”git log”查看commit历史
2. 可以进行”git reset”回滚到指定commit
3. 可创建多分支，可在分支之间切换
4. 远程仓库
5. 不要求merge功能



## Git实现原理

### key-value存储

- Git仓库实际上是一个key-value存储/对象存储（类似HashSet）
- 一个value即一个对象object
- value是文件内容，key是这段内容的哈希值
- 给定一个key值，能够得到一个value
- 给定一个value，能够判断是否存在这个value，若不存在，可以插入新的value

### 三种类型的object

####  Blob

- 每个用户文件的每个版本都会对应一个blob对象
- Blob对象的key是用户文件的hash值
- Blob对象的value是文件内容
- Blob对象没有文件名信息

####  Tree

- 每个用户文件夹（和子文件夹）都会对应一个Tree对象
- Tree对象的value包含：
  1. 此文件夹内包含的文件名，以及这些文件对应的Blob key
  2. 此文件夹内包含的子文件夹名，以及这些子文件夹对应的Tree key

####  Commit

- 每次提交对应一个Commit对象
- Commit对象的value包含：

1.  项目根目录对应的Tree对象的key
2. 前驱Commit对象的key
3. 代码author
4. 代码commiter
5. commit时间戳
6. commit备注/注释

### 查看所有object

-  git cat-file --batch-check --batch-all-objects

### 查看对应key的value

-  git cat-file –p <key>

### 分支切换与回滚

- 本质：把commit对应的根目录Tree对象恢复成一个文件夹

- 要切换到一个已存在的分支，需要使用 git checkout 命令

- ```java
  git checkout master
  这条命令做了两件事。 一是使 HEAD 指回 master 分支，二是将工作目录恢复成 master 分支所指向的快照内容。
  ```

- 注意：在切换分支时，一定要注意你工作目录里的文件会被改变。 如果是切换到一个较旧的分支，你的工作目录会恢复到该分支最后一次提交时的样子。 如果 Git 不能干净利落地完成这个任务，它将禁止切换分支

### 分支管理

- Git 的分支，其实本质上仅仅是指向提交对象的可变指针。 Git 的默认分支名字是 master。 在多次提交操作之后，你其实已经有一个指向最后那个提交对象的 master分支。 master分支会在每次提交时自动向前移动。
- 保存分支信息
  1. 确定有哪些分支
  2. 每个分支最新的commit值
  3. 当前处于哪个分支

- 命令：
  1. 新建分支—git branch，git checkout
  2. 合并分支—git merge
  3. 删除分支—git branch -d dev(分支名)
  4. 切换分支—git checkout dev

## 每周任务

### 12月3日——12月10日 第一周任务

#### Task1: 实现key-value存储

- 最简单的key-value存储方式
- 支持以下功能：
  1. 给定value，向存储中添加对应的key-value
  2. 给定key，查找得到对应的value值

#### Task2: 将一个文件夹转化成key,value

- 给定一个文件夹目录，将其转化成若干tree和blob
- 深度优先遍历此目录
  1.  遇到子文件就转化成blob并保存
  2. 遇到子文件夹就递归调用其内部的子文件/文件夹最后构造tree并保存

- 使用任务1提供的接口 --- hash表
- 单元测试

### 12月10日——12月17日 第二周任务

#### Task1: 完善、优化已有的代码

- 最终小组内每位同学都需要贡献至少3次commit，可以是完善注释、设计文档和单元测试（使用代码来自动化实现测试用例的生成以及验证测试结果是否正确）

#### Task2: 实现commit

- 给定一个工作区目录，生成对应的blob和tree(上周已完成)以及commit

- 写代码之前先理清思路，写设计文档

- 提示：
  1. 需要存储指向当前最新commit的HEAD指针
  2. 每次新生成一个commit前，需要把根目录的tree key与已有的最新commit的	 tree key进行比较，发现不相同时（即文件发生了变动）才添加这个commit

### 12月23日——12月30日 第三周任务 

#### Task1: 分支管理与切换

- 创建一个名为branch的文件夹，文件夹中记录各个branch的信息
- 每新建一个分支，在branch文件夹中增加一个以该分支名命名的文件，将最新commit的值写入对应的分支文件中
- 找到branch文件夹中要切换到的分支对应的文件，读出其中最新的commit的key
- 把commit对应的根目录tree对象恢复成文件夹
- 修改HEAD的值为最新的commit的key

#### Task2: 回滚

- 根据commit key查询得到commit的value
- 从commit value中解析得到根目录tree的key
- 恢复(path)：
  1. 根据tree的key查询得到value
  2. 解析value中的每一条记录，即这个tree对象所代表的文件夹内的子文件与子文件夹名称以及对应的blob/tree key
  3. 对于blob，在path中创建文件，命名为相应的文件名，写入blob的value
  4. 对于tree，在path中创建文件夹，命名为相应的文件夹名，递归调用恢复(path+文件夹名)
  5. 切换分支/回滚至某个commit后，需要更新HEAD指针

#### Task3: 命令行交互

- Scanner接收用户指令
- 通过main函数命令行参数String[] args接收用户指令