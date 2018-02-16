package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class test extends JPanel{
    private JPanel panel;
    private JTable table1;

    public test() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        System.out.println(model.getRowCount());
        table1 = new JTable(model);

    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Library Project");
        frame.setContentPane(new test().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }



}
