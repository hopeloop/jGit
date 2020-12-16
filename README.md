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
2.  前驱Commit对象的key
3.  代码author
4.  代码commiter
5.  commit时间戳
6.  commit备注/注释

### 查看所有object

-  git cat-file --batch-check --batch-all-objects

### 查看对应key的value

-  git cat-file –p <key>

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
  2.  遇到子文件夹就递归调用其内部的子文件/文件夹最后构造tree并保存

- 使用任务1提供的接口 --- hash表
- 单元测试

### 12月10日——12月17日 第二周任务

#### Task1: 完善、优化已有的代码

- 最终小组内每位同学都需要贡献至少3次commit，可以是完善注释、设计文档和单元	 测试（使用代码来自动化实现测试用例的生成以及验证测试结果是否正确）

#### Task2: 实现commit

- 给定一个工作区目录，生成对应的blob和tree(上周已完成)以及commit

- 写代码之前先理清思路，写设计文档

- 提示：
  1. 需要存储指向当前最新commit的HEAD指针
  2. 每次新生成一个commit前，需要把根目录的tree key与已有的最新commit的	 tree key进行比较，发现不相同时（即文件发生了变动）才添加这个commit
------

## 实现：

### 类：

#### **1.ObjectStore**


#### **2.Hash**


#### **3.Blob**


#### **4.Tree**


#### **5.Commit**

   - 想法：
      1. value包括根目录对应的Tree对象的key，前驱Commit对象的key，commit时间戳和注释。
  		2. 要构建commit链条，需要有一个文件（Head）存储指向最新commit的Head指针(最新commit的key)。
  		3. 生成新commit前，先判断文件是否变动，即根目录tree key是否改变。
          变动了才能成功生成新的commit，并将新产生的commit key覆盖Head中原有内容。
      4. 以上三点可以写成3个方法解决。

   
   - 主要方法：

		1. isCommitable: 判断Head文件是否存在，存在则取出保存在其中的commit key（lastCommitKey），进而取出上一次的tree key。
    调用Tree方法对工作区根目录（除jGit文件夹外）生成新的treekey (latestTreeKey)。比较两个tree key是否相同。
  		2. createCommit：生成新Commit的Value,并写入文件。
  		3. updateHead：如果Head文件不存在，则在jGit文件夹生成HEAD文件，如果已存在，将新生成的commit key存入（覆盖）。

     
    -msg:String
    -timeStamp:String
    -lastTreeKey:String
    -latestTreeKey:String
    -lastCommitKey:String
    -latestCommitKey:String
    
    Commit()
    -doTimeStamp():void 生成时间戳
    -updateHead():void 更新Head,如果没有则创建
    -isCommitable：boolean 判断是否能Commit
    -createCommit:void 

​	
