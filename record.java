import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class record extends JFrame {

    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public record() {
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
                Transaction transaction = getTransactionDetails("收入", true); // 获取收入详情
                if (transaction != null) { // 如果用户输入了有效数据
                    transactions.add(transaction); // 添加到事务列表
                    System.out.println("新收入：" + transaction); // 打印收入信息
                }
            }
        });
        centerPanel.add(addIncomeButton); // 将按钮添加到居中面板

        // 添加支出按钮
        JButton addExpenseButton = new JButton("记录支出");
        addExpenseButton.addActionListener(new ActionListener() { // 设置按钮监听器
            @Override
            public void actionPerformed(ActionEvent e) {
                Transaction transaction = getTransactionDetails("支出", false); // 获取支出详情
                if (transaction != null) { // 如果用户输入了有效数据
                    transactions.add(transaction); // 添加到事务列表
                    System.out.println("新支出：" + transaction); // 打印支出信息
                }
            }
        });
        centerPanel.add(addExpenseButton); // 将按钮添加到居中面板

        // 添加查询按钮
        JButton queryButton = new JButton("查询账单");
        queryButton.addActionListener(new ActionListener() { // 设置按钮监听器
            @Override
            public void actionPerformed(ActionEvent e) {
                showQueryDialog(); // 显示查询对话框
            }
        });
        centerPanel.add(queryButton); // 将按钮添加到居中面板

        // 添加展示账单按钮
        JButton displayBillsButton = new JButton("展示所有账单");
        displayBillsButton.addActionListener(new ActionListener() { // 设置按钮监听器
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllBills(); // 显示所有账单记录
            }
        });
        centerPanel.add(displayBillsButton); // 将按钮添加到居中面板

        setVisible(true); // 显示窗口
    }

    // 获取并验证交易详情
    private Transaction getTransactionDetails(String type, boolean isIncome) {
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
            if ((isIncome && amount <= 0) || (!isIncome && amount >= 0)) {
                JOptionPane.showMessageDialog(this, "金额必须为正数!");
                return null; // 金额不符合要求
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "金额输入错误，请输入有效的数字！");
            return null; // 金额输入格式错误
        }

        String category = categoryField.getText(); // 类别
        String remark = remarkField.getText(); // 备注

        return new Transaction(date, amount, category, remark, type); // 返回交易对象
    }

    // 显示查询对话框
    private void showQueryDialog() {
        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new GridLayout(5, 2));

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

        JButton queryButton = new JButton("查询");
        JButton clearButton = new JButton("清除");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(queryButton);
        buttonPanel.add(clearButton);
        queryPanel.add(buttonPanel);

        final JList<String> incomeList = new JList<>(); // 收入列表
        final JList<String> expenseList = new JList<>(); // 支出列表

        // 查询按钮动作
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dateFrom = null, dateTo = null;
                try {
                    dateFrom = sdf.parse(dateFromField.getText());
                    dateTo = sdf.parse(dateToField.getText());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(record.this, "日期格式错误！");
                    return;
                }

                String category = categoryField.getText();

                ArrayList<String> incomeResults = new ArrayList<>();
                ArrayList<String> expenseResults = new ArrayList<>();

                for (Transaction t : transactions) {
                    if (t.getDate().after(dateFrom) && t.getDate().before(dateTo)
                            && (category.isEmpty() || t.getCategory().equals(category))) {
                        if ("收入".equals(t.getType())) { // 使用 getType 方法
                            incomeResults.add(t.toChineseString());
                        } else if ("支出".equals(t.getType())) { // 使用 getType 方法
                            expenseResults.add(t.toChineseString());
                        }
                    }
                }

                incomeList.setListData(incomeResults.toArray(new String[0]));
                expenseList.setListData(expenseResults.toArray(new String[0]));
            }
        });

        // 清除按钮动作
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateFromField.setText("");
                dateToField.setText("");
                categoryField.setText("");
                incomeList.setListData(new String[0]);
                expenseList.setListData(new String[0]);
            }
        });

        // 创建查询结果窗口
        final JFrame queryResultFrame = new JFrame("查询结果");
        queryResultFrame.setSize(800, 600);

        // 设置窗口相对于屏幕居中显示
        queryResultFrame.setLocationRelativeTo(null);

        queryResultFrame.setLayout(new BorderLayout());

        JPanel resultListPanel = new JPanel();
        resultListPanel.setLayout(new GridLayout(2, 1));

        JScrollPane incomeScrollPane = new JScrollPane(incomeList);
        JScrollPane expenseScrollPane = new JScrollPane(expenseList);

        resultListPanel.add(incomeScrollPane);
        resultListPanel.add(expenseScrollPane);

        queryResultFrame.add(queryPanel, BorderLayout.NORTH);
        queryResultFrame.add(resultListPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, queryPanel,
                "查询条件", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            queryResultFrame.setVisible(true);
        }
    }

    // 显示所有账单记录
    private void displayAllBills() {
        final JFrame allBillsFrame = new JFrame("所有账单记录");
        allBillsFrame.setSize(800, 600);

        // 设置窗口相对于屏幕居中显示
        allBillsFrame.setLocationRelativeTo(null);

        allBillsFrame.setLayout(new BorderLayout());

        final JList<String> incomeList = new JList<>(); // 收入列表
        final JList<String> expenseList = new JList<>(); // 支出列表

        ArrayList<String> incomeResults = new ArrayList<>();
        ArrayList<String> expenseResults = new ArrayList<>();

        for (Transaction t : transactions) {
            if ("收入".equals(t.getType())) { // 使用 getType 方法
                incomeResults.add(t.toChineseString());
            } else if ("支出".equals(t.getType())) { // 使用 getType 方法
                expenseResults.add(t.toChineseString());
            }
        }

        incomeList.setListData(incomeResults.toArray(new String[0]));
        expenseList.setListData(expenseResults.toArray(new String[0]));

        JPanel resultListPanel = new JPanel();
        resultListPanel.setLayout(new GridLayout(2, 1));

        JScrollPane incomeScrollPane = new JScrollPane(incomeList);
        JScrollPane expenseScrollPane = new JScrollPane(expenseList);

        resultListPanel.add(incomeScrollPane);
        resultListPanel.add(expenseScrollPane);

        allBillsFrame.add(resultListPanel, BorderLayout.CENTER);

        allBillsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // 使用事件调度线程启动程序
            new record();
        });
    }
}

class Transaction {
    private Date date;
    private double amount;
    private String category;
    private String remark;
    private String type; // 收入 or 支出

    public Transaction(Date date, double amount, String category, String remark, String type) {
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
                ", 金额: " + amount +
                ", 类别: " + category +
                ", 备注: " + remark +
                ", 类型: " + type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", remark='" + remark + '\'' +
                ", type='" + type + '\'' +
                '}'; // 返回交易的字符串表示形式
    }

}