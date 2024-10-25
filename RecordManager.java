import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordManager extends JFrame {

    private final ArrayList<Record> records = new ArrayList<>();

    public RecordManager() {
        setTitle("收支记录");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // 计算主窗口的位置使其居中
        int posX = (screenSize.width - getWidth()) / 2;
        int posY = (screenSize.height - getHeight()) / 2;

        setLocation(posX, posY); // 设置主窗口位置使其居中

        JPanel mainPanel = new JPanel(); // 主面板
        mainPanel.setLayout(new BorderLayout()); // 使用边界布局管理器
        setContentPane(mainPanel);

        // 创建一个居中的面板来放置按钮
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // 按钮居中
        mainPanel.add(centerPanel, BorderLayout.CENTER); // 将居中面板添加到主面板的中心

        // 添加收入按钮
        JButton addIncomeButton = new JButton("记录收入");
        addIncomeButton.addActionListener(new ActionListener() { // 设置按钮监听器
            @Override
            public void actionPerformed(ActionEvent e) {
                Record record = getRecordDetails("收入", true); // 获取收入详情
                if (record != null) { // 如果用户输入了有效数据
                    records.add(record); // 添加到记录列表
                    System.out.println("新收入：" + record); // 打印收入信息
                }
            }
        });
        centerPanel.add(addIncomeButton); // 将按钮添加到居中面板

        // 添加支出按钮
        JButton addExpenseButton = new JButton("记录支出");
        addExpenseButton.addActionListener(new ActionListener() { // 设置按钮监听器
            @Override
            public void actionPerformed(ActionEvent e) {
                Record record = getRecordDetails("支出", false); // 获取支出详情
                if (record != null) { // 如果用户输入了有效数据
                    records.add(record); // 添加到记录列表
                    System.out.println("新支出：" + record); // 打印支出信息
                }
            }
        });
        centerPanel.add(addExpenseButton); // 将按钮添加到居中面板

        // 添加展示账单按钮
        JButton displayBillsButton = new JButton("展示所有账单");
        displayBillsButton.addActionListener(new ActionListener() { // 设置按钮监听器
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllRecords(); // 显示所有账单记录
            }
        });
        centerPanel.add(displayBillsButton); // 将按钮添加到居中面板

        // 添加查询账单按钮
        JButton queryButton = new JButton("查询账单");
        queryButton.addActionListener(new ActionListener() { // 设置按钮监听器
            @Override
            public void actionPerformed(ActionEvent e) {
                showQueryDialog(); // 显示查询对话框
            }
        });
        centerPanel.add(queryButton); // 将按钮添加到居中面板

        setVisible(true); // 显示窗口
    }

    // 获取并验证记录详情
    private Record getRecordDetails(String type, boolean isIncome) {
        JPanel inputPanel = new JPanel(); // 创建输入面板
        inputPanel.setLayout(new GridLayout(4, 2)); // 设置网格布局

        // 输入字段和标签
        JLabel dateLabel = new JLabel("日期 (yyyy-MM-dd):");
        JTextField dateField = new JTextField();
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);

        JLabel amountLabel = new JLabel("金额:");
        JTextField amountField = new JTextField();
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        JLabel categoryLabel = new JLabel("类别:");
        JTextField categoryField = new JTextField();
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryField);

        JLabel remarkLabel = new JLabel("备注:");
        JTextField remarkField = new JTextField();
        inputPanel.add(remarkLabel);
        inputPanel.add(remarkField);

        // 显示输入对话框
        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                "请输入" + type + "详情", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION) {
            return null; // 用户取消操作
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 日期格式化工具
        Date date = null;
        try {
            date = sdf.parse(dateField.getText()); // 解析日期
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "日期格式错误！");
            return null; // 用户输入的日期格式错误
        }

        double amount = 0.0;
        try {
            amount = Double.parseDouble(amountField.getText()); // 解析金额
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "金额必须为正数!");
                return null; // 金额不符合要求
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "金额输入错误，请输入有效的数字！");
            return null; // 金额输入格式错误
        }//

        String category = categoryField.getText(); // 类别
        String remark = remarkField.getText(); // 备注

        // 根据是否为收入决定金额的正负
        if (!isIncome) {
            amount = -amount; // 支出时金额取负值
        }

        return new Record(date, amount, category, remark, type); // 返回记录对象
    }

    // 显示查询账单界面
    private void showQueryDialog() {
        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new GridLayout(7, 2));

        JLabel dateFromLabel = new JLabel("开始日期 (yyyy-MM-dd):");
        JTextField dateFromField = new JTextField();
        queryPanel.add(dateFromLabel);
        queryPanel.add(dateFromField);

        JLabel dateToLabel = new JLabel("结束日期 (yyyy-MM-dd):");
        JTextField dateToField = new JTextField();
        queryPanel.add(dateToLabel);
        queryPanel.add(dateToField);

        JLabel categoryLabel = new JLabel("类别:");
        JTextField categoryField = new JTextField();
        queryPanel.add(categoryLabel);
        queryPanel.add(categoryField);

        // 添加查询方式选择
        JLabel methodLabel = new JLabel("查询方式:");
        String[] methods = {"按日期", "按日期范围", "按类别"};
        JComboBox<String> methodComboBox = new JComboBox<>(methods);
        queryPanel.add(methodLabel);
        queryPanel.add(methodComboBox);

        JButton confirmButton = new JButton("确定");
        JButton clearButton = new JButton("清除");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        buttonPanel.add(clearButton);
        queryPanel.add(buttonPanel);

        final JList<String> resultList = new JList<>(); // 结果列表

        // 确认按钮动作
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String method = (String) methodComboBox.getSelectedItem();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dateFrom = null, dateTo = null;
                try {
                    if (!dateFromField.getText().isEmpty()) {
                        dateFrom = sdf.parse(dateFromField.getText());
                    }
                    if (!dateToField.getText().isEmpty()) {
                        dateTo = sdf.parse(dateToField.getText());
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(RecordManager.this, "日期格式错误！");
                    return;
                }

                String category = categoryField.getText();

                ArrayList<String> results = new ArrayList<>();

                switch (method) {
                    case "按日期":
                        if (dateFrom != null) {
                            for (Record r : records) {
                                if (r.getDate().equals(dateFrom) && (category.isEmpty() || r.getCategory().equals(category))) {
                                    results.add(r.toChineseString());
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(RecordManager.this, "请选择查询方式并输入相应信息！");
                        }
                        break;
                    case "按日期范围":
                        if (dateFrom != null && dateTo != null) {
                            for (Record r : records) {
                                if (r.getDate().after(dateFrom) && r.getDate().before(dateTo)
                                        && (category.isEmpty() || r.getCategory().equals(category))) {
                                    results.add(r.toChineseString());
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(RecordManager.this, "请选择查询方式并输入相应信息！");
                        }
                        break;
                    case "按类别":
                        if (!category.isEmpty()) {
                            for (Record r : records) {
                                if (r.getCategory().equals(category)) {
                                    results.add(r.toChineseString());
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(RecordManager.this, "请选择查询方式并输入相应信息！");
                        }
                        break;
                }

                resultList.setListData(results.toArray(new String[0]));
            }
        });

        // 清除按钮动作
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateFromField.setText("");
                dateToField.setText("");
                categoryField.setText("");
                resultList.setListData(new String[0]);
            }
        });

        // 创建查询结果窗口
        final JFrame queryResultFrame = new JFrame("查询结果");
        queryResultFrame.setSize(800, 600);
        queryResultFrame.setLocationRelativeTo(null); // 居中显示
        queryResultFrame.setLayout(new BorderLayout());

        JScrollPane resultScrollPane = new JScrollPane(resultList);

        queryResultFrame.add(queryPanel, BorderLayout.NORTH);
        queryResultFrame.add(resultScrollPane, BorderLayout.CENTER);

        queryResultFrame.setVisible(true);
    }

    // 显示所有账单记录
    private void displayAllRecords() {
        final JFrame allRecordsFrame = new JFrame("所有账单记录");
        allRecordsFrame.setSize(800, 600);

        // 设置窗口相对于屏幕居中显示
        allRecordsFrame.setLocationRelativeTo(null);

        allRecordsFrame.setLayout(new BorderLayout());

        final JList<String> resultList = new JList<>(); // 结果列表

        ArrayList<String> results = new ArrayList<>();

        for (Record r : records) {
            results.add(r.toChineseString());
        }

        resultList.setListData(results.toArray(new String[0]));

        JScrollPane resultScrollPane = new JScrollPane(resultList);

        allRecordsFrame.add(resultScrollPane, BorderLayout.CENTER);

        allRecordsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // 使用事件调度线程启动程序
            new RecordManager();
        });
    }
}

class Record {
    private Date date;
    private double amount;
    private String category;
    private String remark;
    private String type; // 收入 or 支出

    public Record(Date date, double amount, String category, String remark, String type) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.remark = remark;
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getType() { // 新增方法
        return type;
    }

    // 返回中文格式的字符串表示形式
    public String toChineseString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return "日期: " + sdf.format(date) +
                ", 金额: " + Math.abs(amount) +
                ", 类别: " + category +
                ", 备注: " + remark +
                ", 类型: " + type;
    }

    @Override
    public String toString() {
        return "Record{" +
                "date=" + date +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", remark='" + remark + '\'' +
                ", type='" + type + '\'' +
                '}'; // 返回记录的字符串表示形式
    }
}