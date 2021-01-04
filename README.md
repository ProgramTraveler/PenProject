# ---

## 更新日志

### 2020-11-28

#### 环境的配置

* 今天配完了编译的环境和一些参考代码的阅读
* 配置：32位的eclipse的安装的环境的配置和写字板环境的配置
* 仓库建立

---

### 2020-11-29

#### 使用GUI对初始界面的设计的完成，比较粗糙

* 选择菜单的方向
* 选择完毕的开始按钮
* 界面有待完善...

---

### 2020-11-30

#### 确认笔的数据和文件的写入

* 确认笔的压力，笔的角度，笔的旋转角......
* 能够将笔的一些信息写入创建的csv文件中
* 测试写入的结果是否正确
* 未知信息的考量

---

### 2020-12-05

#### 界面的跟新和一些操作数据测试

* 重写界面，在选择开始按钮后显示写字面板
* 使用cello.tablet.JTabletCursor包来读出笔的各个属性值，目前以笔的压力为测试，后续完善
* 写字板的简单实现，测试为主
* 添加鼠标的监听，在鼠标点击后记录，在鼠标拖动时给出反应
* 后续需要在鼠标拖动时显示线条

---

### 2020-12-06

#### 写字面板的编写和遇到的Bug

* 按下开始按钮后打开写字板
* 打开写字板后拖动鼠标显示线条 **->** **Bug：在写这部分的时候由于没有将初始点进行更新，所以画出的线条是从一个点向外散射的线条,而使用`Graphics`画出的又是一个个小点，所以用`Graphics2D`（已解决）**
* 使用写字板进行一些简单的测试 **->** **Bug：以压力为测试条件，但无法从写字板读出笔的压力值（已解决）**
* 正常获取压力值 **->** **应该是使用`PenValue`里面的`Pressure`里面的函数**
* 能够将每次测试的笔尖压力记录在相应的文件中

---

### 2020-12-07

#### 在写字面板的操作和Bug

* 打开写字板，画一条线 **->** **Bug：当开始画线后会出现开始按钮**（已解决）

---

### 2020-12-09

#### 对与界面新要求

* 在初始界面需要有用户ID和组数Block的数据输入
* 需要有一个选择练习按钮
* 需要在界面中新增选择模式按钮
* 开始按钮 **->** 按照初始设计不变，但需要调整位置

#### 对写字板的新要求

* 写字界面分为传统写字板界面和优化后的写字界面

##### 传统写字板界面要求

* 在传统写字板中加入颜色选择和粗细选择按钮
* 最好能显示测试者当前的颜色和粗细以及应该选择的颜色和粗细
* 在写字板中需要加入两个颜色区域，用于提醒测试者在该区域进行颜色选择和笔的粗细选择
* 记录测试者在做颜色选择和粗细选择中的所花的时间

##### 还有三个与传统界面的做对比的写字界面

* 待讨论......

---

### 2020-12-18

#### 重新对用户初始的登陆页面进行设计

* 新加**用户ID**的输入框
* 新加**实验组数**的输入框
* 新加**练习**选择框
* 但界面的排列还有待改善，目前只考虑方法的实现

#### 完善笔的信息记录

* 获取笔的角度
* 获取笔的旋转角

#### 界面逻辑需要完善

* 当用户选择**开始**后，登录界面需要被写字板覆盖

#### 传统写字板类的创建

* 新添加一个传统写字板的类，用于实现相应功能

---

### 2020-12-19

#### 对登录界面修改

* 解决**2020-12-07**时画线会出现开始按钮的Bug
* 当用户选择开始按钮后，登录界面被写字界面覆盖
* 将四个写字模式的按钮添加到界面

#### 传统写字界面

* 添加两个菜单栏，分别是颜色和像素
* 添加颜色和像素的下拉菜单
* 画线的逻辑需要修改

---

### 2020-12-20

#### 传统写字界面的实现

* 通过用户选择下拉菜单栏，来实现对画笔颜色的选择 **->** **Bug:一旦切换为某个颜色，所有之前的线条都会变成当前选择的颜色**
* 像素中下拉菜单中对画笔的修改暂未实现

#### 点类

* 为了方便对点进行信息处理，建立点类，**也是为了解决切换颜色后所有线条会变为当前颜色的Bug**
* 可以保存当前点的信息
* 通过读取点的位置来画线 **->** **Bug:会出现当重画一条线时，新的线会和旧线连接（已解决）**
* 对点的颜色逻辑处理需要修改，可以做到对线条颜色进行实时处理

---

### 2020-12-21

#### 传统写字界面部分实现

* 通过下拉菜单的形式来对画笔的颜色和粗细进行选择的功能正常实现
* 在写字界面加入两个实验区域，一个为颜色测试区域，一个为像素测试区域
* 当线条进入颜色测试区域时，会提示待选颜色
* 像素测试区域暂未实现
* 颜色测试区域 **->** **Bug:当画笔进入到测试区域的时候会出现提示颜色乱跳的情况** **->** **分析原因：可能是判断逻辑里有问题，到时候再测试一下** **->** **问题确认：随机数生成位置不对**
* 提示颜色乱跳问题 **->** **解决办法：将随机数作为属性放入到点类中，当发现点进入到颜色测试区域，会获得该点当时被赋得随机数，作为颜色提示的依据**

---

### 2020-12-22

#### 传统写字界面更新

* 开始对像素测试区域进行实验，发现原始逻辑存在问题，初始的解决办法不太适用，开始尝试使用手写一个对测试区域的监听来实现对进入测试区域的对应操作，同时，也为了提示方法的优化
* **问题：** 当界面重绘时，发现有些组件不能在重绘界面出现 **->** **解决办法，将原来的界面分割为两个区域，分别进行不同的操作**

---

### 2020-12-23

#### 传统写字界面完成

* 设置在左边界面的中提示标签位置
* 写监听，按照触发事件提示相应动作
* 监听用户当前的笔的颜色和像素，当用户切换颜色和像素时，也跟着变化
* 后面就该写进入测试区域的监听了 **->** **当用户进入颜色测试区域时，在左边的提示区域会显示应该切换的颜色，同理，当进入像素测试区域时，会在左边提示应该切换的像素**
* 对测试区域的监听不能使用现成的监听，还是得自己写一个对应的监听，这样的话，传统写字界面的功能就大致写完了
* 不用监听，直接可以使用鼠标滑动的那个监听器
* 传统界面大致完成

#### 新增要求--->登陆界面标签修改

* 将 **四个写字板** 改为 **四个模式**，细化 **除传统写字板外另外三个模式，比如实列化 -> 实列化模式,PAT实列化**

#### 新增要求--->传统写字界面修改

* 将测试区域修改为长条形
* 去掉默认的颜色和像素在选择菜单中，保证三个颜色对应三个像素
* 优化颜色提示和像素提示
* 数值记录

---

### 2020-12-31

#### 修改传统写字界面

* 修改便签的名称
* 修改测试区域 **->** 将测试区域修改为长条形
* 修改颜色初始的选择菜单，将选择的黑色去掉，将画笔的颜色默认设为黑色
* 修改像素初始的选择菜单，将画笔的初始像素设为1个像素
* 明确模式中所要记录的数据
* 在传统界面的提示区域优化工作未完成

---

### 2021-01-01

#### 左边的界面提示信息美化

* 左边区域中提示信息以文本框的形式出现，而不是纯文字
* 左边区域的显示情况待讨论，暂时先以这样显示
* 开始尝试将数据保存在`csv`文件中，可能会花的时间有点久，因为要记录的数据比较多

---

### 2021-01-02

### 传统写字界面的修改

* 对于用户的提示信息需要修改，到时候确定后再进行修改，不然重复修改太麻烦了

### 信息的记录

* 将信息记录，但要保存的信息需要重新选择
* 按照要求记录信息 **出现的问题->** **文字记录在文件中变成乱码** **->** **解决办法：修改格式，将其改为`GBK`**
* 但文字写入的格式需要修改，在每个列标题中加入`","`作为`csv`文档的分隔符
* **Bug:** **->** **最后一个方位角无法记录在文件中** **->** **解决办法：把这个数据记录两次**

---

### 2021-01-03

#### 信息记录

* 能够直接获取的量已经可以正常记录
* 但其他的实验数据的保存还要在代码中添加
* 当用户进入到任意区域内表示开始测试，这时候开始记录实验数据

#### 写字界面测试区域修改

* 减小两个颜色测试区域之间的间隔

#### 写字界面的键盘监听

* 当按下一个键后，清空界面，开始下一次测试
* **Bug：** **->** **键盘监听无响应** **解决办法：除了要使用`addKeyListener`，还要让其成为`焦点`**

---

### 2021-01-04

#### 绘画时间信息记录

* 信息记录存在问题，笔的间隔太短时会出现0秒的情况，而且时间上的算数运算也存在问腿