package com.mq.pc.pic;

import com.mq.pc.PCUtil;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * @author mq
 * @description: TODO
 * @title: pic
 * @projectName pic
 * @date 2021/1/416:22
 */
//class Listen extends Component implements ActionListener {
//
//    private String path = null;
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        String name = e.getActionCommand();
//        String s = e.paramString();
//        System.out.println(s);
//        if ("选择保存位置".equals(name)) {
//            JFileChooser jfc = new JFileChooser();
//            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            //开始选择路径
//            if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(Listen.this)) {
//                path = jfc.getSelectedFile().getAbsolutePath();
//                System.out.println(path);
//            }
//        } else if ("开始下载".equals(name)) {
//            if (path == null) {
//                return;
//            }
//
//            try {
//                PCUtil.download(100, path);
//
//                System.exit(0);
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//
//    }
//}

public class pic extends Component {
    private static JTextField t2 = new JTextField(12);
    private static JTextField t1 = new JTextField(12);
    private static JFrame jf = new JFrame("下载图片");
    private static JLabel jl = new JLabel("数量(默认100)");
    private static JLabel wz = new JLabel("保存位置");
    private static JButton b1 = new JButton("选择保存位置");
    private static JButton b2 = new JButton("开始下载");
    private static String path = null;
    private static JLabel zt = new JLabel("状态");
    private static JTextField t3 = new JTextField(26);
    private int i = 100;

    public pic() {
        init();
    }

    public void init() {

        JPanel jp = new JPanel();
        jp.add(wz);
        jp.add(t1);
        jp.add(jl);
        jp.add(t2);
        t2.setText("100");
        jp.add(zt);
        jp.add(t3);

        jp.add(b1);
        jp.add(b2);
        jf.add(jp);
        jf.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jf.setSize(600, 120);
        jf.setLocation(700, 300);
        //显示按钮
        jf.setVisible(true);
//        Listen l = new Listen();
        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void listerner() {
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(pic.this)) {
                    path = jfc.getSelectedFile().getAbsolutePath();
                    t1.setText(path);
                }
            }
        });
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                path = t1.getText();
                String text = t2.getText();
                i = Integer.parseInt(text);
                t3.setText("下载中，请稍等！");
                if (i > 0 && path != null && !"".equals(path)) {
                    try {
                        PCUtil.download(i, path);
                        t3.setText("下载完成，请到<" + path + ">下查看");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请选择路径!", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        pic pic = new pic();
        pic.listerner();

    }
}
