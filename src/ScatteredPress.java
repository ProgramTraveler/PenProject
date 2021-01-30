import cello.tablet.JTablet;
import cello.tablet.JTabletException;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
    date:2021-01-27
    author:王久铭
    purpose:压力离散化界面，是通过压力的变化来实现在压力不同区域中选择颜色和像素，不需要弹出菜单
 */
public class ScatteredPress extends JFrame implements MouseInputListener, KeyListener, ActionListener {
    private int time = 50; //更新时间为50毫秒一次
    private Timer timer = new Timer(time, this); //以每50毫秒一次触发actionPerformed触发器
    private PSExperimentPanel psExperimentPanel = new PSExperimentPanel();
    private boolean ChooseFlag = false; //是否显示压力动态图像
    private int CurrentPress = -1; //获取当前压力值

    //下面这个两个boolean的值是用来检测颜色和像素切换是否合法
    private boolean ColorChange = false; //在进入到颜色的测试区域后，变为true
    private boolean PixelChange = false; //在进入到像素的测试区域后，变为true

    //压力离散化界面的定义
    private JFrame ScatteredPFrame = new JFrame("P-离散化界面");

    //设置画笔的初始颜色
    private int SetColor = 0;
    //设置画笔的初始像素
    private int SetPixel = 0;

    private CompleteExperiment completeExperiment = new CompleteExperiment();
    private JTablet tablet = null;

    //储存实验信息
    private PenData penData = new PenData();
    //获取笔的实时数据
    private PenValue penValue = new PenValue();

    private double x0, y0; //每一次笔尖开始的位置
    private double x1, y1; //每一次笔尖结束的位置

    /*
        将ActualPress分为两个区域
     */
    private JPanel SPInter = new JPanel(); //提示信息区域
    private JPanel SPDraw = new JPanel(); //画线区域

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

    //用户是否第一次进入颜色测试区域，true表示未进入
    private boolean ColorFlag = true;
    //判断用户是否第一次进入像素测试区域，true表示未进入
    private boolean PixelFlag = true;


    public ScatteredPress(int BlockNumber) {
        psExperimentPanel.setLayout(new BorderLayout());
        psExperimentPanel.addMouseListener(this);
        psExperimentPanel.addMouseMotionListener(this);
        psExperimentPanel.addKeyListener(this);

        completeExperiment.SetRandomNUmber(); //生成测试随机数
        completeExperiment.SetRandomC(); //生成颜色提示语句
        completeExperiment.SetRandomP(); //生成像素提示语句
        completeExperiment.SetExperimentB(BlockNumber);

        this.CreateSPFrame(); //生成压力离散化测试界面

        psExperimentPanel.requestFocusInWindow(); //获得焦点，是键盘可以正常监听


        try {
            tablet = new JTablet();
        } catch (JTabletException e) {
            e.printStackTrace();
        }

        timer.start();
        timer.stop();
    }

    public void CreateSPFrame() {
        this.CreateSPInter();
        this.CreateSPDraw();

         /*
        将界面分割为两部分
         */
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, SPInter, SPDraw); //这里第一个参数是控制分割线竖直，第二个参数是当你拖曳切割面版的分隔线时，窗口内的组件是否会随着分隔线的拖曳而动态改变大小，最后两个参数就是我分割完成后分割线两边各添加哪个容器。
        jSplitPane.setDividerLocation(300); //分割线的位置  也就是初始位置
        jSplitPane.setOneTouchExpandable(false); //是否可展开或收起，在这里没用
        jSplitPane.setDividerSize(0);//设置分割线的宽度 像素为单位(这里设为0，择时不显示分割线)
        jSplitPane.setEnabled(false); //设置分割线不可拖动！！
        ScatteredPFrame.add(jSplitPane);  //加入到面板中就好了

        //界面全屏设置
        ScatteredPFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //ScatteredPFrame.setBounds(500,200,300,800);
        ScatteredPFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ScatteredPFrame.setVisible(true);
    }

    public void CreateSPInter() {
        //上半部分的界面,背景颜色为默认颜色
        SPInter.setLayout(null); //不采用布局管理器,由坐标定位

        //当前颜色提示标签
        ShowColorL.setBounds(500, 250, 100, 20);
        ShowColorL.setFont(new Font("楷体", Font.BOLD, 20));
        SPInter.add(ShowColorL);
        //当前颜色（颜色块）
        ShowColorBlock.setBounds(600, 250, 60, 20);
        ShowColorBlock.setBackground(Color.BLACK);
        SPInter.add(ShowColorBlock);

        //当前像素提示标签
        ShowPixelL.setBounds(700, 250, 100, 20);
        ShowPixelL.setFont(new Font("楷体", Font.BOLD, 20));
        SPInter.add(ShowPixelL);
        //当前像素
        ShowPixel.setBounds(800, 250, 100, 20);
        ShowPixel.setText(StringPixel);
        ShowPixel.setHorizontalAlignment(ShowPixel.LEFT);
        ShowPixel.setFont(new Font("黑体", Font.BOLD, 20));
        SPInter.add(ShowPixel);
    }

    public void CreateSPDraw() {
        SPDraw.setLayout(new BorderLayout());
        SPDraw.setBackground(Color.WHITE);
        SPDraw.add(psExperimentPanel, BorderLayout.CENTER);
    }

    //重绘SPInter界面(将提示的移除掉)
    public void RemoveRandom() {
        SPInter.removeAll();
        SPInter.repaint();
        //将颜色提示语句和颜色块隐藏
        ShowColorT.setBounds(0, 0, 0, 0);
        JPanelRandomC.setBounds(0, 0, 0, 0);
        //将像素提示语句和像素值隐藏
        ShowPixelT.setBounds(0, 0, 0, 0);
        JPanelRandomP.setBounds(0, 0, 0, 0);
        //将笔的当前的提示颜色变为黑色
        ShowColorBlock.setBackground(Color.BLACK);
        //将笔的当前提示像素变为1.0
        StringPixel = "1.0";
        ShowPixel.setText(StringPixel);
        //当前颜色和像素的展示
        SPInter.add(ShowColorL);
        SPInter.add(ShowColorBlock);
        SPInter.add(ShowPixelL);
        SPInter.add(ShowPixel);
        SPInter.revalidate();
        //将笔的颜色变为黑色
        psExperimentPanel.DefineColor(0);
        //将笔的像素变为1.0
        psExperimentPanel.DefinePixel(1);
    }

    //重绘SPInter界面
    public void RepaintSPInter() {
        SPInter.removeAll();
        SPInter.repaint();

        //当前颜色和像素的展示
        SPInter.add(ShowColorL);
        SPInter.add(ShowColorBlock);
        SPInter.add(ShowPixelL);
        SPInter.add(ShowPixel);
        //目标颜色和像素的展示
        SPInter.add(ShowColorT);
        SPInter.add(JPanelRandomC);
        SPInter.add(ShowPixelT);
        SPInter.add(JPanelRandomP);

        SPInter.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CurrentPress = penValue.Pressure();
        //System.out.println(CurrentPress);
        //如果选择进行
        psExperimentPanel.SetCurrentPress(CurrentPress);
        psExperimentPanel.repaint();
        //对像素标签进行移除
        psExperimentPanel.RemoveItemJLabel();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //如果用户按下ALT键，说明选择该区域
        if (e.getKeyCode() == KeyEvent.VK_ALT) {
            if (CurrentPress >= 702 && CurrentPress < 755) {
                psExperimentPanel.DefinePixel(4);
            }
            if (CurrentPress >= 755 && CurrentPress < 808) {
                psExperimentPanel.DefinePixel(3);
            }
            if (CurrentPress >= 808 && CurrentPress < 863) {
                psExperimentPanel.DefinePixel(2);
            }
            if (CurrentPress >= 863 && CurrentPress < 916) {
                psExperimentPanel.DefineColor(1);
            }
            if (CurrentPress >= 916 && CurrentPress < 969) {
                psExperimentPanel.DefineColor(2);
            }
            if (CurrentPress >= 969 && CurrentPress <= 1023) {
               psExperimentPanel.DefineColor(3);
            }
        }
        //当一次实验完成，用户按下空格键
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //清空集合中的点的信息
            psExperimentPanel.arrayListSpot.clear();
            //重绘
            psExperimentPanel.repaint();
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
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            //判断一组实验是否做完
            if (completeExperiment.GetList() == true) {
                if (completeExperiment.GetExperimentB() - 1 >= 1) {
                    int temp = completeExperiment.GetExperimentB() - 1;
                    completeExperiment.SetRandomC();
                    completeExperiment.SetRandomP();
                    completeExperiment.SetExperimentB(temp);
                } else {
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

                    penData.SetColorTouchE(0); //初始化颜色误触发数
                    penData.SetPixelTouchE(0); //初始化像素误触发数

                    penData.SetColorModeE(0); //初始化颜色切换错误数
                    penData.SetPixelModeE(0); //初始化像素切换错误数

                    //关闭当前的界面
                    ScatteredPFrame.dispose();
                }
            }
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
            timer.restart();
            psExperimentPanel.SetShowBack(true);
            //获得开始时鼠标的位置
            x0 = e.getX();
            y0 = e.getY();
            /*
            获得落笔的时间
             */
            //获得落笔的时间戳
            penData.AddTime(System.currentTimeMillis());
            //获得落笔的文字格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
            penData.AddTimeString(dateFormat.format(new Date()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //获得抬笔的时间戳
        penData.AddTime(System.currentTimeMillis());
        //获得抬笔的文字格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        penData.AddTimeString(dateFormat.format(new Date()));
        psExperimentPanel.repaint();
        //当抬笔后说明已经选择完成
        ChooseFlag = false; //不显示压力动态图像
        psExperimentPanel.SetShowBack(false); //不打开显示压力的动态显示

        psExperimentPanel.RemoveAllJLabel(); //清除颜色和像素标签
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
        //获得笔在拖动时的坐标
        x1 = e.getX();
        y1 = e.getY();
        //点的位置，用来为压力的显示提供位置信息
        psExperimentPanel.SetShowPoint(new Point((int) x0, (int) y0));
        //获得颜色切换的颜色值
        SetColor = psExperimentPanel.GetSetColor();
        //获得像素切换的像素值
        SetPixel = psExperimentPanel.GetSetPixel();

        //当不进行功能切换和菜单选择时，才会进行画线操作
        Dot dot = new Dot();
        dot.SetStarDot(x0, y0);
        dot.SetEndDot(x1, y1);
        dot.SetColor(SetColor); //点的颜色
        dot.SetPixel(SetPixel); //点的像素

        double x = dot.DotStarX();
        double y = dot.DotStarY();

        if (x >= 350 && x <= 850 && y >= 50 && y <= 150 && ColorFlag == true) {
            ColorChange = true; //当进入到颜色测试区域时，颜色测换才合法
            penData.SetStartColorMode(System.currentTimeMillis());
            int indexC = completeExperiment.GetRandomNumberC();
            String StringRandomC = completeExperiment.GetRandomC(indexC);
            //System.out.println("进入颜色区域");
            //按照系统的提示颜色存入相应的目标颜色
            if (StringRandomC == "请切换颜色为蓝色") {
                penData.SetTargetColor("蓝色");
                JPanelRandomC.setBackground(Color.BLUE);
            } else if (StringRandomC == "请切换颜色为红色") {
                penData.SetTargetColor("红色");
                JPanelRandomC.setBackground(Color.RED);
            } else if (StringRandomC == "请切换颜色为黄色") {
                penData.SetTargetColor("黄色");
                JPanelRandomC.setBackground(Color.ORANGE);
            }
            //如果没有提示且按下了空格，就记为空
            else penData.SetTargetColor(null);

            //设置插件位置
            ShowColorT.setBounds(880, 250, 100, 20);
            ShowColorT.setFont(new Font("楷体", Font.BOLD, 20));

            JPanelRandomC.setBounds(980, 250, 60, 20);
            //将插件添加到TFInter中
            SPInter.add(ShowColorT);
            SPInter.add(JPanelRandomC);
            //重绘TFInter界面
            this.RepaintSPInter();
            ColorFlag = false;
        } else if (x0 >= 350 && x0 <= 850 && y0 >= 50 && y0 <= 150 && ColorFlag == false) {

        } else {
            ColorFlag = true;
        }

        if (x0 >= 900 && x0 <= 1400 && y0 >= 50 && y0 <= 150 && PixelFlag == true) {
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
            } else if (StringRandomP == "请切换像素为3.0") {
                //System.out.println("--");
                RandomPixel = "3.0";
                penData.SetTargetLine("3.0");
            } else if (StringRandomP == "请切换像素为4.0") {
                //System.out.println("---");
                RandomPixel = "4.0";
                penData.SetTargetLine("4.0");
            }
            //如果没有提示就按下了空格，就记为空
            else penData.SetTargetLine(null);

            //设置插件位置
            ShowPixelT.setBounds(1080, 250, 100, 20);
            ShowPixelT.setFont(new Font("楷体", Font.BOLD, 20));

            //System.out.println(RandomPixel);
            JPanelRandomP.setBounds(1180, 250, 100, 20);
            JPanelRandomP.setText(RandomPixel);
            JPanelRandomP.setHorizontalAlignment(JPanelRandomP.LEFT);
            JPanelRandomP.setFont(new Font("黑体", Font.BOLD, 20));

            //把插件添加到TFInter中
            SPInter.add(ShowPixelT);
            SPInter.add(JPanelRandomP);

            //重绘SPInter界面
            this.RepaintSPInter();
            PixelFlag = false;
        } else if (x0 >= 900 && x0 <= 1400 && y0 >= 50 && y0 <= 150 && PixelFlag == false) {

        } else {
            PixelFlag = true;
        }
        //将点的信息记录在容器中
        psExperimentPanel.arrayListSpot.add(dot);
        psExperimentPanel.repaint();
        //更新点的起始坐标（下一个点的开始为上一个点的结束）
        x0 = x1;
        y0 = y1;
    }
    @Override
    public void mouseMoved(MouseEvent e) {}
}