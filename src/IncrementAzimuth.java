import cello.tablet.JTablet;
import cello.tablet.JTabletException;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
    date:2021-02-08
    author:王久铭
    purpose:方位角增量化界面，主要是通过方位角的不断变化来进行颜色和像素的选择
 */
public class IncrementAzimuth extends JFrame implements ActionListener, MouseInputListener, KeyListener {
    private int time  = 50;
    private Timer timer = new Timer(time,this);

    private int TimeShift = 0; //获取点的时间间隔，在这里判断是600ms
    private boolean ShiftIndex = false; //获取点的时间信号

    private AIExperimentPanel aiExperimentPanel = new AIExperimentPanel();

    private int CurrentAzimuth = -1; //当前的方位角信息

    private boolean MenuFlag = false; //对菜单打开和关闭的控制
    private boolean MenuMove = true; //菜单位置是否随着鼠标移动
    private boolean ChooseFlag = false; //进行菜单选择的标志
    //下面这个两个boolean的值是用来检测颜色和像素切换是否合法
    private boolean ColorChange = false; //在进入到颜色的测试区域后，变为true
    private boolean PixelChange = false; //在进入到像素的测试区域后，变为true

    private int MenuX = 0; //菜单那出现位置的X值
    private int MenuY = 0; //菜单出现位置的Y值
    private int MenuWith = 50; //选择颜色和像素菜单的宽度
    private int MenuHeight = 40; //选择颜色和像素菜单的高度
    private int NumberOfMenuItem = 2; //选择菜单的内容，分别为颜色和像素

    //方位角增量化界面的定义
    private JFrame IncrementAFrame = new JFrame("A-增量化");

    //设置画笔的初始颜色和像素
    int SetColor = 0;
    int SetPixel = 0;

    private CompleteExperiment completeExperiment = new CompleteExperiment(); //为测试提供实验数据
    private JTablet tablet = null; //获得笔的相关信息

    private PenData penData = new PenData(); //记录笔在实验过程中的信息
    private PenValue penValue = new PenValue(); //获取笔的实时数据

    private double x0,y0; //笔尖开始的位置
    private double x1,y1; //笔尖结束的位置'

    /*
        将ActualAzimuth分为两个区域
     */
    private JPanel AIInter = new JPanel(); //提示信息区域
    private JPanel AIDraw = new JPanel(); //画线区域

    /*
       提示标签和提示语句
           当前的颜色和像素
   */
    //做为当前颜色的提示标签
    private JLabel ShowColorL = new JLabel("当前颜色:");
    //设置当前的提示颜色块（默认的初始的颜色为黑色）
    private JPanel ShowColorBlock = new JPanel();
    //设置当前颜色的像素标签
    private JLabel ShowPixelL = new JLabel("当前像素:");
    //展示像素值
    private JLabel ShowPixel = new JLabel();
    //像素语句，用于提示当前的像素值
    private String StringPixel = "1.0";
    /*
    提示标签和语句
        应该切换的颜色和像素
     */
    //切换颜色的提示
    private JLabel ShowColorT = new JLabel("目标颜色");
    //颜色提示块
    private JPanel JPanelRandomC = new JPanel();
    //切换像素显示
    private JLabel ShowPixelT = new JLabel("目标像素");
    //展示目标像素值
    private JLabel JPanelRandomP = new JLabel();
    //像素提示语句
    private String RandomPixel = "";

    private boolean PromptFlag = false;
    private JLabel Prompt = new JLabel("即将进入菜单");


    //用户是否第一次进入颜色测试区域，true表示未进入
    private boolean ColorFlag = true;
    //判断用户是否第一次进入像素测试区域，true表示未进入
    private boolean PixelFlag = true;

    //记录到达压力值过程中的压力值
    private ArrayList<Integer> DotCompare = new ArrayList<Integer>();
    //记录是否打开菜单
    private boolean index = false;

    public IncrementAzimuth(int BlockNumber) {
        aiExperimentPanel.setLayout(new BorderLayout());
        aiExperimentPanel.addMouseListener(this);
        aiExperimentPanel.addMouseMotionListener(this);
        aiExperimentPanel.addKeyListener(this);

        completeExperiment.SetRandomNUmber(); //生成测试的随机数
        completeExperiment.SetRandomC(); //生成颜色提示语句
        completeExperiment.SetRandomP(); //生成像素提示语句
        completeExperiment.SetExperimentB(BlockNumber); //设置进行的实验组数

        this.CreateAIFrame();

        aiExperimentPanel.requestFocusInWindow(); //获得焦点

        try {
            tablet = new JTablet();
        } catch (JTabletException e) {
            e.printStackTrace();
        }
        timer.start();
        timer.stop();
    }
    public void CreateAIFrame() {
        this.CreateAIInter();
        this.CreateAIDraw();
        /*
        将界面分割为两部分
         */
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,false,AIInter,AIDraw); //这里第一个参数是控制分割线竖直，第二个参数是当你拖曳切割面版的分隔线时，窗口内的组件是否会随着分隔线的拖曳而动态改变大小，最后两个参数就是我分割完成后分割线两边各添加哪个容器。
        jSplitPane.setDividerLocation(300); //分割线的位置  也就是初始位置
        jSplitPane.setOneTouchExpandable(false); //是否可展开或收起，在这里没用
        jSplitPane.setDividerSize(0);//设置分割线的宽度 像素为单位(这里设为0，择时不显示分割线)
        jSplitPane.setEnabled(false); //设置分割线不可拖动！！
        IncrementAFrame.add(jSplitPane);  //加入到面板中就好了

        //界面全屏设置
        IncrementAFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //IncrementAFrame.setBounds(500,300,500,500);
        IncrementAFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        IncrementAFrame.setVisible(true);
    }
    //上半部分，主要是用来进行信息提示
    public void CreateAIInter() {
        AIInter.setLayout(null);
        //当前颜色提示标签
        ShowColorL.setBounds(520,200,100,20);
        ShowColorL.setFont(new Font("楷体",Font.BOLD,20));
        AIInter.add(ShowColorL);
        //当前颜色（颜色块）
        ShowColorBlock.setBounds(620,200,60,20);
        ShowColorBlock.setBackground(Color.BLACK);
        AIInter.add(ShowColorBlock);

        //当前像素提示标签
        ShowPixelL.setBounds(903,200,100,20);
        ShowPixelL.setFont(new Font("楷体",Font.BOLD,20));
        AIInter.add(ShowPixelL);
        //当前像素
        ShowPixel.setBounds(1003,200,100,20);
        ShowPixel.setText(StringPixel);
        ShowPixel.setHorizontalAlignment(ShowPixel.LEFT);
        ShowPixel.setFont(new Font("黑体",Font.BOLD,20));
        AIInter.add(ShowPixel);

        Prompt.setFont(new Font("楷体",Font.BOLD,20));
        Prompt.setBounds(1130,250,500,20);
    }
    //下半部分，主要是用来进行绘制区域的展示
    public void CreateAIDraw() {
        AIDraw.setLayout(new BorderLayout());
        AIDraw.setBackground(Color.WHITE);
        AIDraw.add(aiExperimentPanel,BorderLayout.CENTER);
    }
    //对AIInter界面进行重绘，目的是移除提示的目标信息
    public void RemoveRandom() {
        AIInter.removeAll();
        AIInter.repaint();
        //将颜色提示语句和颜色块隐藏
        ShowColorT.setBounds(0,0,0,0);
        JPanelRandomC.setBounds(0,0,0,0);
        //将像素提示语句和像素值隐藏
        ShowPixelT.setBounds(0,0,0,0);
        JPanelRandomP.setBounds(0,0,0,0);
        //将笔的当前的提示颜色变为黑色
        ShowColorBlock.setBackground(Color.BLACK);
        //将笔的当前提示像素变为1.0
        StringPixel = "1.0";
        ShowPixel.setText(StringPixel);
        //当前颜色和像素的展示
        AIInter.add(ShowColorL);
        AIInter.add(ShowColorBlock);
        AIInter.add(ShowPixelL);
        AIInter.add(ShowPixel);
        AIInter.revalidate();
        //将笔的颜色变为黑色
        aiExperimentPanel.DefineColor();
        //将笔的像素变为1.0
        aiExperimentPanel.DefinePixel();
    }
    //重绘AIInter界面
    public void RepaintAIInter() {
        AIInter.removeAll();
        AIInter.repaint();

        //当前颜色和像素的展示
        AIInter.add(ShowColorL);
        AIInter.add(ShowColorBlock);
        AIInter.add(ShowPixelL);
        AIInter.add(ShowPixel);
        //目标颜色和像素的展示
        AIInter.add(ShowColorT);
        AIInter.add(JPanelRandomC);
        AIInter.add(ShowPixelT);
        AIInter.add(JPanelRandomP);

        if (PromptFlag)
            AIInter.add(Prompt);

        AIInter.revalidate();
    }
    //当满足倾斜角时弹出菜单
    public void ProcessTriggerSwitch() {
        MenuFlag = true; //选择打开菜单
        aiExperimentPanel.SetOpenMenu(MenuFlag); //打开颜色和像素选择菜单
        aiExperimentPanel.repaint();
    }
    //根据用户的当前的鼠标位置来计算出用户选择的是菜单中的哪块区域
    public int CheckSelectMenuItem(int x,int y) {
        int MenuItem = -1;
        int tempY = MenuY;
        for (int i = 0; i < NumberOfMenuItem; i ++) {
            if ((MenuX + MenuWith) >=  x && (MenuX < x)) {
                if ((MenuY <= y) && (tempY + MenuHeight) >= y) {
                    MenuItem = i;
                    break;
                }
            }
            tempY += MenuHeight;
        }
        //如果选择的是颜色菜单，打开颜色的分支菜单显示，关闭像素分支菜单显示
        if (MenuItem == 0) {
            aiExperimentPanel.SetShowColorMenu(true);
            aiExperimentPanel.SetShowPixelMenu(false);
        }
        //如果选择的像素菜单，打开像素分支菜单显示，关闭颜色分支菜单显示
        else if (MenuItem == 1) {
            aiExperimentPanel.SetShowPixelMenu(true);
            aiExperimentPanel.SetShowColorMenu(false);
        }else {
            //什么也不做
        }
        return MenuItem;
    }
    //判断用户选择的是哪个颜色
    public int CheckColorItem(int x, int y) {
        int ColorItem = -1;
        int tempY = MenuY;
        for (int i = 0; i < 3; i ++) {
            if ((MenuX + MenuWith * 2) >= x && (MenuX + MenuWith <= x)) {
                if ((MenuY <= y) && (tempY + MenuHeight) >= y) {
                    ColorItem = i;
                    break;
                }
            }
            tempY += MenuHeight;
        }
        return ColorItem;
    }
    //判断用户选择的是哪个像素
    public int CheckPixelItem(int x, int y) {
        int PixelItem = -1;
        int tempY = MenuY;
        for (int i = 0; i < 3; i ++) {
            if ((MenuX + MenuWith * 2) >= x && (MenuX + MenuWith <= x)) {
                if ((MenuY + MenuHeight<= y) && (tempY + MenuHeight * 2) >= y) {
                    PixelItem = i;
                    break;
                }
            }
            tempY += MenuHeight;
        }
        return PixelItem;

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        CurrentAzimuth = penValue.Azimuth(); //获得当前的方位角

        if (CurrentAzimuth < 110 || CurrentAzimuth > 154) DotCompare.add(CurrentAzimuth);
        else {
            DotCompare.clear();
            index = false;
        }

        if (DotCompare.size() <= 7) {
            for (int i = 0; i < DotCompare.size(); i ++) {
                if (Math.abs(DotCompare.get(i) - DotCompare.get(0)) >= 60) {
                    index = true;
                }else {
                    index = false;
                }
            }
        }else {
            int temp = DotCompare.size() - 7;
            for (int i = DotCompare.size() - 7; i < DotCompare.size(); i ++) {
                if (Math.abs(DotCompare.get(i) - DotCompare.get(temp)) >= 60) {
                    index = true;
                }else {
                    index = false;
                }
            }
        }

        //如果到达了预设的方位角范围，就打开颜色和像素选择菜单
        if (index) {
            if (ColorChange == false && PixelChange == false) {
                penData.AddTouchE(); //误触发总数加一
            }
            timer.stop(); //停止触发actionPerFormed
            aiExperimentPanel.SetShowBack(false); //将方位角动态显示界面关闭
            MenuMove = false; //固定菜单出现的位置
            this.ProcessTriggerSwitch(); //弹出颜色和像素选择菜单
            //penData.SetAzimuth(CurrentAzimuth);

        }else {
            //没达到指定的方位角区域就继续显示方位角的动态信息
            aiExperimentPanel.SetCurrentAzimuth(CurrentAzimuth);
            aiExperimentPanel.repaint();
        }
        if ((CurrentAzimuth <= 110 && CurrentAzimuth > 50) || (CurrentAzimuth >= 154 && CurrentAzimuth < 214)) {
            PromptFlag = true;
        }else {
            PromptFlag = false;
            AIInter.removeAll();
        }
        this.RepaintAIInter();

        //对获取点的时间间隔进行判断
        if (TimeShift == 600) {
            ShiftIndex = true;
            TimeShift = 0;
        }else {
            TimeShift += 50;
            ShiftIndex = false;
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //如果用户按下ALT键，说明要开始切换
        /*if (e.getKeyCode() == KeyEvent.VK_ALT) {
            ChooseFlag = true;
        }*/
        //当一次实验完成，用户按下回车键
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            //更新颜色和像素条件
            ColorFlag = true;
            PixelFlag = true;
            //清空集合中的点的信息
            aiExperimentPanel.arrayListSpot.clear();
            //重绘
            aiExperimentPanel.repaint();
            //将检查是否进入颜色和像素测试区域的变量设为false
            ColorChange = false;
            PixelChange = false;
            //将提示语句移除
            this.RemoveRandom();
            //在一组中做完一次实验
            penData.AddTrialN();
            //模式切换出现的错误数
            penData.AddModeE();
            //将笔的压力保存在指定文件中
            try {
                penData.AllocateTime();
                penData.AllocateTimeString();
                penData.SaveInformation();
                penData.InitShift();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            //判断一组实验是否做完
            if (completeExperiment.GetList() == true) {

                //打开一个新的登录界面
                Login login = new Login();
                login.SetInputId("");
                login.SetSelectBlock(1);
                login.SetSelectTechnique("");

                login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                login.pack();
                login.setLocationRelativeTo(login);
                login.setResizable(false);
                login.setVisible(true);

                //关闭当前的界面
                IncrementAFrame.dispose();

            }
            penData.SetColorTouchE(0); //初始化颜色误触发数
            penData.SetPixelTouchE(0); //初始化像素误触发数

            penData.SetColorModeE(0); //初始化颜色切换错误数
            penData.SetPixelModeE(0); //初始化像素切换错误数
            penData.SetTouchE(0); //初始化误触发总数

            penData.SetModeE(0); //初始化模式切换错误
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            //如果要显是动态压力图像
            /*if (ChooseFlag) {
                timer.restart();
                aiExperimentPanel.SetShowBack(true);
            }*/

            timer.restart();
            aiExperimentPanel.SetShowBack(true);

            //获得开始时鼠标的位置
            x0 = e.getX();
            y0 = e.getY();
            /*
            获得落笔的时间
             */
            //获得落笔的时间戳
            penData.AddTime(System.currentTimeMillis());
            if (ColorFlag && PixelFlag) {
                //获得落笔的文字格式
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SS");
                penData.AddTimeString(dateFormat.format(new Date()));
            }
            //如果菜单位置可以随着鼠标位置改变，那么就实时跟新菜单的出现位置
            if (MenuMove) {
                MenuX = e.getX();
                MenuY = e.getY();
                //记录菜单出现的位置
                aiExperimentPanel.SetMenuX_Y(MenuX,MenuY);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //当颜色和像素都已经做过了，此时抬笔，说明已经是最后一次抬笔了
        if (ColorFlag == false && PixelFlag == false) {
            penData.AddTime(System.currentTimeMillis());
            //获得抬笔的文字格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SS");
            penData.AddTimeString(dateFormat.format(new Date()));
        }
        aiExperimentPanel.repaint();
        //当抬笔后说明已经选择完成
        MenuFlag = false; //此时关闭显示菜单
        aiExperimentPanel.SetOpenMenu(false); //在显示界面关闭界面显示
        ChooseFlag = false; //不显示压力动态图像
        aiExperimentPanel.SetShowBack(false); //打开显示压力的动态显示
        MenuMove = true; //菜单位置跟随鼠标变化
        aiExperimentPanel.RemoveAllJLabel(); //清除颜色和像素提示标签
        aiExperimentPanel.SetSelectPixelItem(-1); //初始化像素分支选择
        aiExperimentPanel.SetSelectColorItem(-1); //初始化颜色分支选择
        aiExperimentPanel.SetShowColorMenu(false);
        aiExperimentPanel.SetShowPixelMenu(false);
        //对压力值重新获取
        try {
            tablet.poll();
        } catch (JTabletException e1) {
            e1.printStackTrace();
        }
        timer.stop();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        /*
        应该每次拖动时都会产生一个点对象
         */
        //获得笔在拖动时的坐标
        x1 = e.getX();
        y1 = e.getY();
        //点的位置，用来为压力的显示提供位置信息
        aiExperimentPanel.SetShowPoint(new Point((int)x0,(int)y0));
        //获得颜色切换的颜色值
        SetColor = aiExperimentPanel.GetSetColor();
        //获得像素切换的像素值
        SetPixel = aiExperimentPanel.GetSetPixel();
        //点的位置，是用来为选择菜单的显示提供位置信息
        if (MenuMove) {
            MenuX = e.getX();
            MenuY = e.getY();
            //记录菜单出现的位置
            aiExperimentPanel.SetMenuX_Y(MenuX,MenuY);
        }
        //如果要求打开选择菜单，此时不记录笔的轨迹信息
        if (MenuFlag) {
            //在每次移动的时候对颜色分支和像素分支进行移除和重组
            aiExperimentPanel.RemoveItemJLabel();
            //通过当前点的位置来计算用户选择的是菜单栏中的哪个区域
            aiExperimentPanel.SetSelectMenuItem(this.CheckSelectMenuItem(e.getX(),e.getY()));
            //如果颜色的分支菜单被打开
            if (aiExperimentPanel.GetShowColorMenu()) {
                //传入具体是哪个颜色被选择
                aiExperimentPanel.SetSelectColorItem(this.CheckColorItem(e.getX(), e.getY()));
                //如果颜色提示还没有出现就调用，颜色误触发加一
                /*if (ColorChange == false && aiExperimentPanel.GetSelectColorItem() != -1) {
                    penData.AddColorTouchE(); //颜色误触发加一
                    penData.AddTouchE(); //误触发总数加一
                }*/
                int tempC = aiExperimentPanel.GetSelectColorItem();
                if (tempC == 0) {
                    penData.SetResultC("蓝色");
                    ShowColorBlock.setBackground(Color.BLUE);
                    penData.SetEndColorMode(System.currentTimeMillis());
                }
                if (tempC == 1) {
                    penData.SetResultC("虹色");
                    ShowColorBlock.setBackground(Color.RED);
                    penData.SetEndColorMode(System.currentTimeMillis());
                }
                if (tempC == 2) {
                    penData.SetResultC("黄色");
                    ShowColorBlock.setBackground(Color.ORANGE);
                    penData.SetEndColorMode(System.currentTimeMillis());
                }
                this.RepaintAIInter();

            }
            //如果像素的分支菜单被打开
            if (aiExperimentPanel.GetShowPixelMenu()) {
                //传入具体的哪个像素被选择
                aiExperimentPanel.SetSelectPixelItem(this.CheckPixelItem(e.getX(), e.getY()));
                //如果像素提示还未出现就切换，像素误触发加一
                /*if (PixelChange == false && aiExperimentPanel.GetSelectPixelItem() != -1) {
                    penData.AddPixelTouchE(); //像素误触发加一
                    penData.AddTouchE(); //误触发总数加一
                }*/
                int tempP = aiExperimentPanel.GetSelectPixelItem();
                if (tempP == 0) {
                    penData.SetResultP("2.0");
                    ShowPixel.setText("2.0");
                    penData.SetEndPixelMode(System.currentTimeMillis());
                }
                if (tempP == 1) {
                    penData.SetResultP("3.0");
                    ShowPixel.setText("3.0");
                    penData.SetEndPixelMode(System.currentTimeMillis());
                }
                if (tempP == 2) {
                    ShowPixel.setText("4.0");
                    penData.SetResultP("4.0");
                    penData.SetEndPixelMode(System.currentTimeMillis());
                }
                this.RepaintAIInter();
            }
            aiExperimentPanel.repaint();

        }else if (ChooseFlag == false && MenuFlag == false){
            //当不进行功能切换和菜单选择时，才会进行画线操作
            Dot dot = new Dot();
            dot.SetStarDot(x0,y0);
            dot.SetEndDot(x1,y1);
            dot.SetColor(SetColor); //点的颜色
            dot.SetPixel(SetPixel); //点的像素

            double x = dot.DotStarX();
            double y = dot.DotStarY();

            if (x >= 583 && x < 966 && y >= 5 && y <= 105 && ColorFlag == true) {
                penData.SetAzimuthC(penValue.Azimuth());
                penData.AddTime(System.currentTimeMillis()); //线条绘制结束
                ColorChange = true; //当进入到颜色测试区域时，颜色测换才合法
                penData.SetStartColorMode(System.currentTimeMillis());
                int indexC = completeExperiment.GetRandomNumberC();
                String StringRandomC = completeExperiment.GetRandomC(indexC);
                //System.out.println("进入颜色区域");
                //按照系统的提示颜色存入相应的目标颜色
                if (StringRandomC == "请切换颜色为蓝色") {
                    penData.SetTargetColor("蓝色");
                    JPanelRandomC.setBackground(Color.BLUE);
                }
                else if (StringRandomC == "请切换颜色为红色") {
                    penData.SetTargetColor("红色");
                    JPanelRandomC.setBackground(Color.RED);
                }
                else if (StringRandomC == "请切换颜色为黄色") {
                    penData.SetTargetColor("黄色");
                    JPanelRandomC.setBackground(Color.ORANGE);
                }
                //如果没有提示且按下了空格，就记为空
                else penData.SetTargetColor(null);

                //设置插件位置
                ShowColorT.setBounds(520,250,100,20);
                ShowColorT.setFont(new Font("楷体",Font.BOLD,20));

                JPanelRandomC.setBounds(620,250,60,20);

                AIInter.add(ShowColorT);
                AIInter.add(JPanelRandomC);
                this.RepaintAIInter();
                ColorFlag = false;
            } else if (x0 >= 583 && x0 < 966 && y0 >= 50 && y0 <= 150 && ColorFlag == false){

            }else {
                //ColorFlag = true;
            }

            if (x0 >= 966 && x0 <= 1350 && y0 >= 5 && y0 <= 105 && PixelFlag == true) {
                penData.SetAzimuthP(penValue.Azimuth());
                penData.AddTime(System.currentTimeMillis()); //线条绘制结束
                PixelChange = true; //当进入到像素测试区域时，此时的像素测换才合法
                penData.SetStartPixelMode(System.currentTimeMillis());
                int indexP = completeExperiment.GetRandomNumberP();
                String StringRandomP = completeExperiment.GetRandomP(indexP);
                //System.out.println(StringRandomP);
                //按照系统提示的像素存入目标像素
                if (StringRandomP == "请切换像素为2.0") {
                    //System.out.println("-");
                    RandomPixel = "2.0";
                    penData.SetTargetLine("2.0");
                }
                else if (StringRandomP == "请切换像素为3.0") {
                    //System.out.println("--");
                    RandomPixel = "3.0";
                    penData.SetTargetLine("3.0");
                }
                else if (StringRandomP == "请切换像素为4.0") {
                    //System.out.println("---");
                    RandomPixel = "4.0";
                    penData.SetTargetLine("4.0");
                }
                //如果没有提示就按下了空格，就记为空
                else penData.SetTargetLine(null);

                //设置插件位置
                ShowPixelT.setBounds(903,250,100,20);
                ShowPixelT.setFont(new Font("楷体",Font.BOLD,20));

                //System.out.println(RandomPixel);
                JPanelRandomP.setBounds(1003,250,100,20);
                JPanelRandomP.setText(RandomPixel);
                JPanelRandomP.setHorizontalAlignment(JPanelRandomP.LEFT);
                JPanelRandomP.setFont(new Font("黑体",Font.BOLD,20));

                AIInter.add(ShowPixelT);
                AIInter.add(JPanelRandomP);

                this.RepaintAIInter();
                PixelFlag = false;
            }else if (x0 >= 966 && x0 <= 1350 && y0 >= 5 && y0 <= 105 && PixelFlag == false) {

            }else {
                //PixelFlag = true;
            }
            if (ShiftIndex) {
                //将点的偏移量存入容器中
                penData.SetShift(Math.abs((int) (y0 - 52)));
            }
            //将点的信息记录在容器中
            aiExperimentPanel.arrayListSpot.add(dot);
            aiExperimentPanel.repaint();
            //更新点的起始坐标（下一个点的开始为上一个点的结束）
            x0 = x1;
            y0 = y1;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
