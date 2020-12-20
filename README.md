## 更新日志
---
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