import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class GameUI extends JFrame implements ActionListener {
    private static GameUI frame;
    private JPanel backPanel, centerPanel, bottomPanel;
    private JButton  btnOK,btnNextGeneration, btnStart, btnStop, btnReset,btnExit,btnRandom;
    private JButton[][] btnBlock;
    private JLabel lblRow, lblCol;
    private JComboBox rowList, colList;
    private boolean[][] isSelected;
    private int maxRow, maxCol;
    private Life life;
    private boolean isRunning;
    private Thread thread;
    private boolean isDead;

    public int speed=500;

    public static void main(String arg[]) {
        frame = new GameUI("生命游戏");
    }

    public int getMaxRow() {
        return maxRow;
    }

    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }

    public int getMaxCol() {
        return maxCol;
    }

    public void setMaxCol(int maxCol) {
        this.maxCol = maxCol;
    }

    public void initGUI() {
        /**
         * 设计地图生成器界面 *
         */
        if (maxRow == 0) {
            maxRow = 20;
        }

        if (maxCol == 0) {
            maxCol = 30;
        }

        life = new Life(maxRow, maxCol);

        backPanel = new JPanel(new BorderLayout());
        centerPanel = new JPanel(new GridLayout(maxRow, maxCol));
        bottomPanel = new JPanel();
        rowList = new JComboBox();
        for (int i = 3; i <= 20; i++) {
            rowList.addItem(String.valueOf(i));
        }
        colList = new JComboBox();
        for (int i = 3; i <= 30; i++) {
            colList.addItem(String.valueOf(i));
        }
        rowList.setSelectedIndex(maxRow - 3);
        colList.setSelectedIndex(maxCol - 3);

        btnOK=new JButton("确定");
        btnNextGeneration = new JButton("下一代");
        btnBlock = new JButton[maxRow][maxCol];
        btnStart = new JButton("开始演化");
        btnStop = new JButton("停止演化");
        btnReset = new JButton("重置");
        btnExit = new JButton("退出");
        btnRandom = new JButton("随机生成一个图案");
        isSelected = new boolean[maxRow][maxCol];
        lblRow = new JLabel("设置行数：");
        lblCol = new JLabel("设置列数：");
        this.setContentPane(backPanel);

        backPanel.add(centerPanel, "Center");
        backPanel.add(bottomPanel, "South");

        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                btnBlock[i][j] = new JButton("");
                btnBlock[i][j].setBackground(Color.WHITE);
                centerPanel.add(btnBlock[i][j]);
            }
        }

        bottomPanel.add(lblRow);
        bottomPanel.add(rowList);
        bottomPanel.add(lblCol);
        bottomPanel.add(colList);

        bottomPanel.add(btnOK);
        bottomPanel.add(btnNextGeneration);
        bottomPanel.add(btnStart);
        bottomPanel.add(btnStop);
        bottomPanel.add(btnReset);
        bottomPanel.add(btnExit);
        bottomPanel.add(btnRandom);
        // 设置窗口
        this.setSize(1200, 800);
        this.setResizable(false);
        this.setLocationRelativeTo(null); // 让窗口在屏幕居中

        // 将窗口设置为可见的
        this.setVisible(true);

        // 注册监听器
        this.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        btnOK.addActionListener(this);
        btnNextGeneration.addActionListener(this);
        btnStart.addActionListener(this);
        btnStop.addActionListener(this);
        btnReset.addActionListener(this);
        btnExit.addActionListener(this);
        btnRandom.addActionListener(this);
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                btnBlock[i][j].addActionListener(this);
            }
        }
    }

    public GameUI(String name) {
        super(name);
        initGUI();
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnOK) {
            frame.setMaxRow(rowList.getSelectedIndex() + 3);
            frame.setMaxCol(colList.getSelectedIndex() + 3);
            initGUI();
            life = new Life(getMaxRow(), getMaxCol());
        } else if(e.getSource() == btnNextGeneration) {
            makeNextGeneration();
        } else if (e.getSource() == btnStart) {
            isRunning = true;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        makeNextGeneration();
                        boolean isSame = true;
                        try {
                            Thread.sleep(speed);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        isDead = true;
                        for(int row = 1; row <= maxRow; row++) {
                            for (int col = 1; col <= maxCol; col++) {
                                if (life.getGrid()[row][col] != 0) {
                                    isDead = false;
                                    break;
                                }
                            }
                            if (!isDead) {
                                break;
                            }
                        }
                        if (isDead) {
                            JOptionPane.showMessageDialog(null, "生命灭绝了〒▽〒");
                            isRunning = false;
                            thread = null;
                        }
                    }
                }
            });
            thread.start();
        }else if (e.getSource() == btnStop) {
            isRunning = false;
            thread = null;
        } else if (e.getSource() == btnReset) {
            initGUI();
            life = new Life(getMaxRow(), getMaxCol());
        }else if (e.getSource() == btnExit) {
            System.exit(0);
        }else if(e.getSource() == btnRandom){
            initGUI();
            life = new Life(getMaxRow(), getMaxCol());
            int[][] a = life.getGrid();
            for(int i = 0;i < maxRow * maxCol / 8;i++) {
                int n = (int) (Math.random() * maxRow);
                int m = (int) (Math.random() * maxCol);
                btnBlock[n][m].setBackground(Color.red);
                a[n + 1][m + 1] = 1;
            }
            life.setGrid(a);

        }else {
            int[][] grid = life.getGrid();
            for (int i = 0; i < maxRow; i++) {
                for (int j = 0; j < maxCol; j++) {
                    if (e.getSource() == btnBlock[i][j]) {
                        isSelected[i][j] = !isSelected[i][j];
                        if (isSelected[i][j]) {
                            btnBlock[i][j].setBackground(Color.red);
                            grid[i + 1][j + 1] = 1;
                        } else {
                            btnBlock[i][j].setBackground(Color.white);
                            grid[i + 1][j + 1] = 0;
                        }
                        break;
                    }
                }
            }
            life.setGrid(grid);
        }
    }

    public void makeNextGeneration() {
        life.update();
        int[][] grid = life.getGrid();
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                if (grid[i + 1][j + 1] == 1) {
                    btnBlock[i][j].setBackground(Color.RED);
                } else {
                    btnBlock[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }
}