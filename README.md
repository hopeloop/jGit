# Java课程项目

此项目为java第24组的课程项，拟用java实现部分git命令的版本控制工具

## 课程项目要求

- 命令行工具
- 参考git实现原理，实现blob,tree,commit核心存储结构

### 功能点

1. git commit : 将工作区内容添加到本地仓库中

2. git branch: 查看分支

3. git branch branch_name: 新建分支

4. git checkout branch_name: 切换分支

5. git init: 在指定目录下初始化jGit仓库

6. git reset: 回滚到本分支上一次commit

7. git rest commit Id: 切换到任意commit

8. git log: 查看log

   

## Git实现原理

### key-value存储

- Git仓库实际上是一个key-value存储/对象存储（类似HashSet）
- 一个value即一个对象object
- value是文件内容，key是这段内容的哈希值
- 给定一个key值，能够得到一个value
- 给定一个value，能够判断是否存在这个value，若不存在，可以插入新的value

### 三种类型的object

#### Blob

- 每个用户文件的每个版本都会对应一个blob对象
- Blob对象的key是用户文件的hash值
- Blob对象的value是文件内容
- Blob对象没有文件名信息

#### Tree

- 每个用户文件夹（和子文件夹）都会对应一个Tree对象
- Tree对象的value包含：
  1. 此文件夹内包含的文件名，以及这些文件对应的Blob key
  2. 此文件夹内包含的子文件夹名，以及这些子文件夹对应的Tree key

#### Commit

- 每次提交对应一个Commit对象
- Commit对象的value包含：

1. 项目根目录对应的Tree对象的key
2. 前驱Commit对象的key
3. 代码author
4. 代码commiter
5. commit时间戳
6. commit备注/注释

### 查看所有object

- git cat-file --batch-check --batch-all-objects

### 查看对应key的value

- git cat-file –p

### 分支切换与回滚

- 本质：把commit对应的根目录Tree对象恢复成一个文件夹

- 要切换到一个已存在的分支，需要使用 git checkout 命令

- ```
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

- 主要功能：

  - **复制文件内容**

    描述：将一个文件的所有内容复制到另一个文件

    方法：copyFile(File source, File target)

    入参：源文件对象，目标文件对象

    实现：

    1. 创建源文件的文件输入流，创建目标文件的文件输出流
    2. 循环将字节从输入流读出，并写入输出流，直到文件内容读完
    3. 判断目标文件是否存在，不存在则创建文件

  - **生成object文件**

    描述：在objects文件夹下创建blob和tree对应的key-value文件

    方法：createFile(String targetSubPath)

    入参：要生成blob的文件的相对路径 or 要生成tree的文件夹的相对路径

    实现：

    1. 判断当前ObjectStore对象的类型是blob还是tree
    2. 创建文件
       - type为blob
         - 获取要记录的文件的内容hash值，作为blob文件的文件名
         - 调用copyFile()方法将要记录文件的内容复制到blob文件，并创建blob文件
       - type为tree
         - 深度遍历仓库根目录，将根目录记录为blob、tree
           - 对于每一个File对象，是文件则调用Blob类的构造函数将其生成为blob对象，是文件夹则调用Tree类的构造函数将其生成为tree对象
           - 获得生成对象的type、key、对应的文件或文件夹名称，整理为一条记录写为tree文件的一行
         - 计算tree文件内容的hash值，将其记录为当前tree对象的key，并将文件重新命名为key

- 数据域

  ```java
  repoPath:String //仓库
  objectsSubPath:String // object存放目录相对路径
  logsPath:String //log文件存放的绝对地址
  headsPath:String //branch head存放的绝对地址
  key:String // object的key值
  type:String // object的类型
  name:String // object源文件(夹)的名称
  ```

  

- 方法

  ```java
  ObjectStore() //构造函数
  setType(String t): void
  getType(): String
  CreateKey(File file): String //创建源文件哈希值 
  getKey(): String
  createFile(String targetSubPath): void //创建源文件(夹)的File对象
  existOrNot(String key): boolean //传入文件名key（哈希值），判断待创建的blob文件是否已存在
  getValue(String key): File
  getValue_inLines(String filePath): String //传入文件地址，获取文件Value
  getValue_inLines_from_File(File file): String ////传入文件，获取文件Value
  writeIn(String FileName,StringBuilder value,String savePath,boolean append): void // //传入文件名，value,存储地址，和是否append,生成文件
  copyFile(File source, File target): void //将source文件内容全部复制到target文件
  ```

  

#### **2.Hash**

- 数据域

  ```java
  Hashcode:String //哈希值
  ```

  

- 方法

  ```java
  Hash(String resourceFilePath) //构造对象，参数：源文件地址
  Hash(File file) //构造对象，参数：输入源文件
  Hash(StringBuilder value)
  SHA1Checksum() //获取源文件hash值
  getHashcode() //获取源文件hash值
  ```

  

#### **3.Blob**

- 方法

  ```java
  Blob(String blobPath) //构造Blob
  toString()
  ```

  

#### **4.Tree**

- 方法

  ```java
  Tree(String treepath) //构造tree
  +toString()
  ```

  

#### 5.Branch

- 主要功能：

  - **新建分支**

    描述：以当前分支为基础生成一个新分支，不自动切换到该分支，新分支获得和当前分支一样的commit和log

    方法：newBranch(String branchName)

    入参：新分支名称

    实现：

    1. 先判断新生成分支是否已经存在，存在则不新建，否则开始建新分支；
    2. 建新分支
       - 生成新分支的log文件（复制当前分支的log文件）
       - 生成新分支的head文件（复制当前分支的head文件）

  - **切换版本**

    描述：将仓库状态还原为指定commit记录

    方法：changeWareHouse(String commitId)

    入参：要回到的commitId

    实现：

    1. 根据commitId获得对应的treeId，并找到tree文件，如果成功找到tree文件则开始切换
    2. 切换版本
       - 清空当前仓库（jGit文件夹除外）
       - 读取tree文件记录，将其中的blob记录还原为文件，tree记录还原为文件夹
       - 修改当前分支的head文件，指向切换到的commit

  - **回滚**

    描述：将仓库状态还原为当前分支的head的parent提交记录

    方法：rollBack()

    入参：无

    实现：

    1. 获得当前分支head的parent的commitId
    2. 调用切换版本的changeWareHouse()方法，让仓库回到前一步得到的commitId的状态

  - **切换分支**

    描述：以当前分支为基础生成一个新分支，不自动切换到该分支，新分支获得和当前分支一样的commit和log

    方法：switchBranch(String theBranch)

    入参：要切换到的分支名称

    实现：

    1. 判断要切换到的分支是否存在，存在则开始切换
    2. 修改分支相关的文件和参数
       - 修改当前分支变量
       - 修改HEAD文件指向当前分支的head文件
    3. 获得切换到的分支的head commit
    4. 调用切换版本的changeWareHouse()方法，让仓库变为切换到的分支的head commit的记录

  - **显示分支**

    描述：命令行输出本地的所有分支，当前分支标*

    方法：showBranches()

    入参：无

    实现：

    1. 遍历heads文件夹，打印文件名（即分支名）
    2. 判断文件名和当前分支名是否相同，相同则在文件名后打印*

- 数据域

  ```java
  repoPath:String // 仓库路径(绝对)
  headPath:String // heads文件夹路径(绝对)
  logPath:String // logs文件夹路径(绝对)
  currBranch:String // 当前分支名
  objStore:ObjectStore // 用该对象调用getValue()
  ```

  

- 方法

  ```java
  Branch(String repoPath, String currBranch) //构造Branch
  getBranch(): String // 获得当前分支名
  getHeadPath(): String //获得heads文件夹路径(绝对)
  newBranch(String branchName): boolean // 生成一个新分支
  showBranches(): void // 显示本地所有分支
  rollBack(): Boolean // 当前分支回滚为上一个提交
  switchBranch(): boolean // 切换分支，若分支没有commit则仓库保持原样，入参为要切换到的分支名
  getCommit(): String  // 获取分支最新的commitId
  getParent(String commitId): String // 获得commit对应的parent
  getTree(String commitId): String // 获得commit对应的tree
  clearOldFiles(): void  // 清空仓库中原有文件(夹)
  -deleteFiles(): void // 删除文件夹操作
  -editBranchHead(String lastCommitId): void // 修改分支文件指向上一次commit
  -editHEAD(String theBranch): void // 修改HEAD文件指向另一个分支
  changeWareHouse(String commitId): boolean // 仓库状态回到指定的commit
  recoverWithTree(String treeId, String dirPath): void // 恢复tree对应的仓库状态，入参为tree的id和tree对应的目录路径
  ```

  

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

- 数据域

  ```java
  msg:String //commit message
  timeStamp:String //时间戳
  lastTreeKey:String //上一次的tree key
  latestTreeKey:String //当前根目录的tree key
  lastCommitKey:String //上一次的commit id
  latestCommitKey:String  //本次的commit id
  lcommitter:String
  curr_branch:String
  ```

  

- 方法

  ```java
  Commit(String message) //构造Commit类，需要传入参数 commit message
  doTimeStamp(): void //生成TimeStamp
  updateCommitKey(): void //更新当前分支的head
  isCommitable(): boolean //判断根目录与上一次commit的根目录相对比有无变化
  generateCommitKV(): StringBuilder //生成本次commit的Key-Value,返回value
  addToLog(): StringBuilder
  ```

  

#### 7.Interact

使用Scanner从控制台获得输入，用if语句配合正则表达式判断并调用jGit中相对应的方法。某些方法（如新建分支`git branch branch_name`）需要提取部分字符，同样使用正则获取。

- 方法

  ```java
  main(String[] args): void //主函数判断输入指令
  ```

  

#### 8.jGit

- 主要功能

  - **初始化仓库**

    方法：init()

    入参：无

    实现：

    1. 判断jGit仓库是否存在，不存在则开始初始化仓库
    2. 创建jGit仓库目录，包括jGit、heads、logs文件夹等
    3. 新建master分支，并生成该分支的head文件
    4. 生成HEAD文件，保存master分支的head文件地址

  - **新建分支**

    方法：newBranch(String newBranch)

    入参：新分支名称

    实现：调用Branch类的newBranch()方法

  - **切换分支**

    方法：switchBranch(String theBranch)

    入参：要切换到的分支名称

    实现：调用Branch类的switchBranch()方法

  - **显示分支**

    方法：showBranches()

    入参：无

    实现：调用Branch类的showBranches()方法

  - **分支回滚**

    方法：rollBack()

    入参：无

    实现：调用Branch类的rollBack()方法

  - **回到分支上的指定commit**

    方法：reset()

    入参：无

    实现：调用Branch类的changeWareHouse()方法

  - **读取log文件，并格式化输出**

    方法：viewLog( )

    入参：无

    实现：读取当前分支对应的log文件，以一行（一次Commit）为一个元素存放在ArrayList中，从后往前读数组（最近的Commit先显示）

- 数据域

  ```java
  repoPath:String // 待管理项目的路径
  branch:Branch  // 分支对象
  committer:String // 提交者
  ```

  

- 方法

  ```java
  jGit(String repoPath) //构造jGit
  init(): void // 初始化仓库
  commit(String message): void
  newBranch(String newBranch): boolean //新建分支
  switchBranch(String theBranch): boolean //切换分支
  showBranches(): void //显示所有分支
  rollBack(): void //回滚到上一分支
  reset(String commitId): void
  viewLog(): void
  ```



### git add：

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

 1.将path传入ObjectStore类createFile()方法，生成对应的blob

 2.修改index文件内容，形式如下（hash 文件名/文件相对地址）

```
9d4305a9f73e82554b2a855065b2eb07e4ff5719 src/jGit.java
```

`git commit`:

 1.根据index内容生成树，获取treekey

 2.按原commit类方法生成commit，更新branch head

