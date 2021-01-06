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

## 实现

### 类：

#### **1.ObjectStore**

+ 主要功能：

  + **复制文件内容**

    描述：将一个文件的所有内容复制到另一个文件

    方法：copyFile(File source, File target)

    入参：源文件对象，目标文件对象

    实现：

    1. 创建源文件的文件输入流，创建目标文件的文件输出流
    2. 循环将字节从输入流读出，并写入输出流，直到文件内容读完
    3. 判断目标文件是否存在，不存在则创建文件

  + **生成object文件**

    描述：在objects文件夹下创建blob和tree对应的key-value文件

    方法：createFile(String targetSubPath)

    入参：要生成blob的文件的相对路径 or 要生成tree的文件夹的相对路径

    实现：

    1. 判断当前ObjectStore对象的类型是blob还是tree

    2. 创建文件

       + type为blob

         + 获取要记录的文件的内容hash值，作为blob文件的文件名
         + 调用copyFile()方法将要记录文件的内容复制到blob文件，并创建blob文件

       + type为tree

         + 深度遍历仓库根目录，将根目录记录为blob、tree
           + 对于每一个File对象，是文件则调用Blob类的构造函数将其生成为blob对象，是文件夹则调用Tree类的构造函数将其生成为tree对象
           + 获得生成对象的type、key、对应的文件或文件夹名称，整理为一条记录写为tree文件的一行

         + 计算tree文件内容的hash值，将其记录为当前tree对象的key，并将文件重新命名为key

#### **2.Hash**



#### **3.Blob**



#### **4.Tree**



#### 5.Branch

+ 主要功能：

  + **新建分支**

    描述：以当前分支为基础生成一个新分支，不自动切换到该分支，新分支获得和当前分支一样的commit和log

    方法：newBranch(String branchName)

    入参：新分支名称

    实现：

    1. 先判断新生成分支是否已经存在，存在则不新建，否则开始建新分支；

    2. 建新分支
       + 生成新分支的log文件（复制当前分支的log文件）
       + 生成新分支的head文件（复制当前分支的head文件）

  + **切换版本**

    描述：将仓库状态还原为指定commit记录

    方法：changeWareHouse(String commitId)

    入参：要回到的commitId

    实现：

    1. 根据commitId获得对应的treeId，并找到tree文件，如果成功找到tree文件则开始切换
    2. 切换版本
       + 清空当前仓库（jGit文件夹除外）
       + 读取tree文件记录，将其中的blob记录还原为文件，tree记录还原为文件夹
       + 修改当前分支的head文件，指向切换到的commit

  + **回滚**

    描述：将仓库状态还原为当前分支的head的parent提交记录

    方法：rollBack()

    入参：无

    实现：

    1. 获得当前分支head的parent的commitId
    2. 调用切换版本的changeWareHouse()方法，让仓库回到前一步得到的commitId的状态

  + **切换分支**

    描述：以当前分支为基础生成一个新分支，不自动切换到该分支，新分支获得和当前分支一样的commit和log

    方法：switchBranch(String theBranch) 

    入参：要切换到的分支名称

    实现：

    1. 判断要切换到的分支是否存在，存在则开始切换
    2. 修改分支相关的文件和参数
       + 修改当前分支变量
       + 修改HEAD文件指向当前分支的head文件

    3. 获得切换到的分支的head commit
    4. 调用切换版本的changeWareHouse()方法，让仓库变为切换到的分支的head commit的记录

  + **显示分支**

    描述：命令行输出本地的所有分支，当前分支标*

    方法：showBranches()

    入参：无

    实现：

    1. 遍历heads文件夹，打印文件名（即分支名）
    2. 判断文件名和当前分支名是否相同，相同则在文件名后打印*

#### **6.Commit**

   - 主要方法：

        - 构造方法需要传入commit message，构造时通过isCommitable方法判断根目录是否发生改变。
             - 如果没有改变，提示仓库无变化。
             - 如果改变
                  - generateCommitKV方法生成commit的Key-Value，使用父类ObjectStore的writeIn方法写入文件。
                  - addToLog方法生成log内容，用writeIn追加到log文件
                  - updateCommitKey更新该分支head中存储的CommitKey。

     - isCommitable，返回bool值

       - 生成根目录的树，获latest tree key
       - 读取branch head中commit key, 进而从commit文件取出 last tree key，并与latest tree key比较是否相等，相等则返回false，否则提示仓库无变化。如果branch head中为空，直接返回true。

     - generateCommitKV，生成Key-Value,并返回StringBuilder value

       - 生成value内容，形式如下

         ```
         tree a51f4efcced167950d39d4082369240cdfcb4
         parent 8ac3ff9a86d0a8c475bc85267d3da76fd6b29c96
         Time: 2021-01-06 08:24:06
         Committer:
         commit message
         ```

       - 通过Value new一个Hash对象，并获取key存入latestCommitKey

     - addToLog，生成log内容以StringBuilder形式返回
       ​

    Commit(String message)
    -doTimeStamp():void 生成时间戳
    -updateCommitKey():void 更新branch head内容
    -isCommitable()：boolean 判断是否能Commit
    -generateCommitKV():StringBuilder
    -addToLog():StringBuilder

#### 7.Interact

使用Scanner从控制台获得输入，用if语句配合正则表达式判断并调用jGit中相对应的方法。某些方法（如新建分支`git branch branch_name`）需要提取部分字符，同样使用正则获取。

#### 8.jGit

- 主要功能

  + **初始化仓库**

    方法：init()

    入参：无

    实现：

    1. 判断jGit仓库是否存在，不存在则开始初始化仓库
    2. 创建jGit仓库目录，包括jGit、heads、logs文件夹等
    3. 新建master分支，并生成该分支的head文件
    4. 生成HEAD文件，保存master分支的head文件地址

  + **新建分支**

    方法：newBranch(String newBranch)

    入参：新分支名称

    实现：调用Branch类的newBranch()方法

  + **切换分支**

    方法：switchBranch(String theBranch)

    入参：要切换到的分支名称

    实现：调用Branch类的switchBranch()方法

  + **显示分支**

    方法：showBranches()

    入参：无

    实现：调用Branch类的showBranches()方法

  + **分支回滚**

    方法：rollBack()

    入参：无

    实现：调用Branch类的rollBack()方法

  + **回到分支上的指定commit**

    方法：reset()

    入参：无

    实现：调用Branch类的changeWareHouse()方法

- viewLog，读取当前分支对应的log文件，以一行（一次Commit）为一个元素存放在ArrayList中，从后往前读数组（最近的Commit先显示），并进行格式化输出。

### 后续关于git add和diff的想法：
#### git add：

git add，将文件生成Blob，并添加到暂存区(Staging Area)，即更新index文件内容。git中的index是位于`<baseOfRepo>/.git/index`的二进制文件。通过`git ls -files -s` 可发现index文件内容如下例：

```
100644 a9ec3a6e2aac41be1ec1f7e62c9c5871d256c97c 0       src/jGit.java
100644 9d4305a9f73e82554b2a855065b2eb07e4ff5719 0       test
```

列出来当前分支所有的文件，存储了时间戳，哈希值以及文件名（文件相对地址）。

此时修改例子中的test文件，重新add，index文件中test对应的hash改变，指向新生成的blob。

本项目目前commit命令是直接对工作区根目录生成tree，如果添加add方法，那么新的commit命令应该将暂存区的tree提交到本地库。

##### 思路：

`git add path` :

​	1.将path传入ObjectStore类createFile()方法，生成对应的blob

​	2.修改index文件内容，形式如下（hash 文件名/文件相对地址）

```
9d4305a9f73e82554b2a855065b2eb07e4ff5719 src/jGit.java
```

`git commit`:

​	1.根据index内容生成树，获取treekey

​	2.按原commit类方法生成commit，更新branch head

#### git diff：




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