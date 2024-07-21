package hmy.self.clicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MouseClicker implements NativeKeyListener {

    private static Boolean clicking = Boolean.FALSE;
    private static Robot robot;
    private static JComboBox<String> speedComboBox;
    private static JButton startButton;
    private static JLabel statusLabel;
    private static JLabel tipLabel;
    private static Timer timer;
    private static final List<Speed> speedList = Arrays.asList(Speed.values());

    public static void main(String[] args) {
        init();
    }


    /**
     * 处理键盘原生事件，特别是关注F8键的按下事件。
     * 当F8键被按下时，此方法将检查当前是否正在进行点击操作，并根据情况启动或停止点击。
     * 这种设计允许用户通过F8键快速切换点击状态，而无需与应用程序的其他部分进行交互。
     *
     * @param event NativeKeyEvent，代表发生的键盘事件。
     *              通过此事件，可以获取到具体的按键信息，如按键码。
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        // 检查按下的键是否为F8
        if (event.getKeyCode() == NativeKeyEvent.VC_F8) {
            // 如果当前正在进行点击操作，则停止点击
            if (clicking) {
                stopClicking();
            } else {
                // 如果当前没有进行点击操作，则开始点击
                startClicking();
            }
        }
    }


    /**
     * 初始化程序的主窗口和各种控件。
     * 设置窗口的大小、标题、布局，并添加各个面板。
     * 在控制面板中，包括点击速度选择框和启动/停止点击的按钮。
     * 程序还设置了一个状态面板，用于显示程序当前的状态。
     * 此外，程序还初始化了一个机器人对象，用于模拟鼠标点击。
     * 最后，设置窗口可见并注册全局键盘监听器。
     */
    private static void init() {
        // 创建主框架并设置属性
        JFrame frame = new JFrame("连点器");
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 创建标题面板并添加标题标签
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("连点器");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // 创建控制面板并设置布局
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 添加点击速度标签
        JLabel speedLabel = new JLabel("连点间隔:");
        speedLabel.setFont(new Font("宋体", Font.PLAIN, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(speedLabel, gbc);

        // 创建点击速度选择框
        speedComboBox = new JComboBox<>(speedList.stream().map(Speed::getName).toArray(String[]::new));
        speedComboBox.setSelectedItem(Speed.DEFAULT_SPEED);
        speedComboBox.setFont(new Font("宋体", Font.PLAIN, 24));
        gbc.gridx = 1;
        gbc.gridy = 0;
        controlPanel.add(speedComboBox, gbc);

        // 创建启动/停止点击按钮
        startButton = new JButton("开始点击");
        startButton.setFont(new Font("宋体", Font.PLAIN, 24));
        startButton.addActionListener(e -> {
            if (clicking) {
                stopClicking();
            } else {
                startClicking();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        controlPanel.add(startButton, gbc);

        // 将控制面板添加到主框架
        frame.add(controlPanel, BorderLayout.CENTER);

        // 创建状态面板并添加状态标签
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("按下F8也可以开始连点");
        statusLabel.setFont(new Font("宋体", Font.PLAIN, 24));
        statusPanel.add(statusLabel);

        tipLabel = new JLabel("鼠标箭头拖动到要点击的位置即可");
        tipLabel.setFont(new Font("宋体",Font.PLAIN,24));
        statusPanel.add(tipLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // 尝试创建机器人对象，失败则显示错误并退出程序
        try {
            robot = new Robot();
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(frame, "机器人初始化失败", "报错", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // 设置框架获取焦点
        frame.setFocusable(true);
        frame.requestFocusInWindow();

        // 中心化框架
        frame.setLocationRelativeTo(null);

        // 设置框架图标
        URL imageURL = MouseClicker.class.getResource("/icon.png");
        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            frame.setIconImage(icon.getImage());
        }

        // 注册全局键盘监听器
        registerGlobalKeyBoardListener();

        // 显示框架
        frame.setVisible(true);
    }

    /**
     * 注册全局键盘监听器。
     * 本方法尝试建立一个全局的键盘监听器，以便捕获系统范围内的键盘事件。
     * 如果注册失败，将打印错误信息。
     * 成功注册后，将添加一个特定的键盘事件监听器MouseClicker。
     *
     * @see GlobalScreen#registerNativeHook() 用于注册全局屏幕监听器的函数。
     * @see GlobalScreen#addNativeKeyListener(NativeKeyListener) 用于添加键盘事件监听器的函数。
     */
    private static void registerGlobalKeyBoardListener() {
        try {
            // 尝试注册全局键盘监听器，这允许捕获系统范围内的键盘事件。
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            // 如果注册失败，打印错误信息。这可能发生在某些操作系统或环境下，注册不被支持。
            System.out.println(e.getMessage());
        }
        // 添加一个监听器实例，用于处理键盘事件。这里使用了MouseClicker类，它应实现NativeKeyListener接口。
        GlobalScreen.addNativeKeyListener(new MouseClicker());
    }

    /**
     * 开始自动点击操作。
     * 此方法将启动一个定时器，根据所选的速度，定时模拟鼠标左键的按下和释放动作。
     * 当点击操作应停止时，通过设置clicking变量为false来终止定时器的动作。
     */
    private static void startClicking() {
        // 设置点击状态为true，表示开始点击操作。
        clicking = true;
        // 修改启动按钮的文本为“停止点击”，以反映当前的操作状态。
        startButton.setText("停止点击");
        // 更新状态标签的文本，提示用户如何停止点击操作。
        statusLabel.setText("按下F8停止点击");

        // 根据速度选择框的当前选择，获取选定的速度值。
        int speed = speedList.get(speedComboBox.getSelectedIndex()).getSpeed();
        // 创建一个定时器，间隔为选定的速度，用于执行点击操作。
        // 定时器的回调函数中检查点击状态，如果仍在进行中，则模拟鼠标点击动作。
        timer = new Timer(speed, e -> {
            if (clicking) {
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
            }
        });
        // 启动定时器，开始执行点击操作。
        timer.start();
    }

    /**
     * 停止自动点击操作。
     * 此方法将点击状态设置为false，并更新启动按钮和状态标签的文本，以指示用户需要按F8键来重新启动点击操作。
     * 如果定时器正在运行，它将被停止。
     */
    private static void stopClicking() {
        // 设置点击状态为false，停止点击
        clicking = false;
        // 更新启动按钮的文本，以便用户知道现在需要点击以开始点击操作
        startButton.setText("开始点击");
        // 更新状态标签的文本，提供启动点击操作的指示
        statusLabel.setText("按下F8开始点击");
        // 如果定时器正在运行，停止它
        if (timer != null) {
            timer.stop();
        }
    }
}
