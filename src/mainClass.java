import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class mainClass extends JFrame {

    private JPanel MainPanel;
    private JButton addExpenseButton;
    private JTextField DateField;
    private JTable table1;
    private JTextField ExpenseField;
    private JTextField AmountField;
    private JTextField IBalanceField;
    private JButton confirmButton;
    private JButton saveButton;
    private JButton loadButton;
    private JTextField RowField;
    static String[] columnNames = {"Row", "Date", "Expense", "Amount", "Balance"};
    static Object[][] data = {{"Row", "Date", "Expense", "Amount", "Balance"}};
    static DefaultTableModel model = new DefaultTableModel(columnNames, 0);
    double iBal = 0;

    public static void addRow(String[] row) {
        model.addRow(row);
    }

    public static void removeRow(int row) {
        model.removeRow(row);
    }

    public static void main(String[] args) {
        new mainClass();

        balance myBalance = new balance(20);
        myBalance.addMoney(100);

        for (int i = 0; i < 41; i++) {
            addRow(new String[]{String.valueOf(model.getRowCount()), "", "", ""});
        }
        //model.setValueAt("rowTest",1,0);
    }

    public mainClass() {
        table1.setEnabled(false);
        table1.setModel(model);
        setContentPane(MainPanel);
        setTitle("MainGUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setVisible(true);


        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int rowNum = Integer.parseInt(RowField.getText());
                    String date = DateField.getText();
                    String expense = ExpenseField.getText();
                    String amount = AmountField.getText();

                    if (rowNum <= 0 || rowNum >= model.getRowCount()) return;

                    model.setValueAt(date, rowNum, 1);
                    model.setValueAt(expense, rowNum, 2);
                    model.setValueAt(amount, rowNum, 3);

                    updateBalanceFromRow(rowNum);
                } catch (NumberFormatException ex) {
                }
            }

        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    iBal = Double.parseDouble(IBalanceField.getText().trim());
                    model.setValueAt(iBal, 0, 4);
                    updateBalanceFromRow(1);
                } catch (NumberFormatException t) {
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("expenses.csv"))) {
                    bw.write("InitialBalance," + iBal);
                    bw.newLine();

                    for (int i = 0; i < model.getRowCount(); i++) {
                        StringBuilder row = new StringBuilder();
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            Object value = model.getValueAt(i, j);
                            row.append(value == null ? "" : value.toString());
                            if (j < model.getColumnCount() - 1) row.append(",");
                        }
                        bw.write(row.toString());
                        bw.newLine();
                    }
                } catch (IOException exception1) {
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (BufferedReader br = new BufferedReader(new FileReader("expenses.csv"))) {
                    model.setRowCount(0);

                    String line = br.readLine();
                    if (line != null && line.startsWith("InitialBalance,")) {
                        iBal = Double.parseDouble(line.split(",")[1]);
                        IBalanceField.setText(String.valueOf(iBal));
                    }

                    while ((line = br.readLine()) != null) {
                        String[] rowData = line.split(",", -1);
                        if (rowData.length == columnNames.length) {
                            model.addRow(rowData);
                        }
                    }

                    if (model.getRowCount() > 0) {
                        model.setValueAt(iBal, 0, 4);
                        updateBalanceFromRow(1);
                    }
                } catch (FileNotFoundException exception1) {

                } catch (IOException exception2) {

                }
            }
        });
    }

    public void action(ActionEvent e) {
        if (e.getActionCommand().equals("addExpense")) {
            model.setValueAt(DateField.getText(), Integer.parseInt(RowField.getText()), 1);
        }
    }

    private void updateBalanceFromRow(int row) {
        Object balanceData = model.getValueAt(0, 4);
        if (balanceData == null || balanceData.toString().isBlank()) return;

        int rowNum = model.getRowCount();

        for (int i = row; i < rowNum; i++) {
            Object balancePrev = model.getValueAt(i - 1, 4);
            Object amountOb = model.getValueAt(i, 3);

            if (balancePrev == null || balancePrev.toString().isBlank()) continue;

            if (amountOb == null || amountOb.toString().isBlank()) continue;

            try {
                double previousBalance = Double.parseDouble(balancePrev.toString());
                double amountCurrent = Double.parseDouble(amountOb.toString());

                model.setValueAt(previousBalance - amountCurrent, i, 4);
                continue;
            } catch (NumberFormatException e) {
                model.setValueAt(null, i, 4);
            }

        }
    }
}

