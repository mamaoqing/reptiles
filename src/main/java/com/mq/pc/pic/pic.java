package com.mq.pc.pic;

import com.mq.pc.PCUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author mq
 * @description: TODO
 * @title: pic
 * @projectName pic
 * @date 2021/1/416:22
 */
class Listen extends Component implements ActionListener {

    private String path = null;
    private static JTextField t2 = new JTextField(12);
    @Override
    public void actionPerformed(ActionEvent e) {
        String name=e.getActionCommand();
        String s = e.paramString();
        System.out.println(s);
        if("选择保存位置".equals(name)){
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //开始选择路径
            if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(Listen.this)) {
                path = jfc.getSelectedFile().getAbsolutePath();
            }
        } else if("开始下载".equals(name)){
            if(path == null){
                return;
            }

            try {
                PCUtil.download(100,path);

                System.exit(0);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
}
public class pic extends Listen {
    public static void main(String[] args) {
        JFrame jf=new JFrame("下载图片");
        JLabel jl=new JLabel("数量");
        JTextField t2 = new JTextField(12);
        JButton b1 =new JButton("选择保存位置");
        JButton b2 =new JButton("开始下载");
        JPanel jp=new JPanel();
        jp.add(jl);
        jp.add(t2);
        jp.add(b1);
        jp.add(b2);
        jf.add(jp);
        jf.setSize(400, 80);
        jf.setLocation(700, 300);
        //显示按钮
        jf.setVisible(true);
        Listen l=new Listen();
        b1.addActionListener(l);
        //按钮登录，退出共享绑定事件监控器
        b2.addActionListener(l);

    }

    private void choose() {
        JFileChooser jfc = new JFileChooser();  //新建一个选择文件夹的类型
        if (jfc.showOpenDialog(pic.this) == JFileChooser.APPROVE_OPTION) { //开始选择路径
            System.out.println(jfc.getSelectedFile().getAbsolutePath());//打印出所选择的图片的路径

            //---------------------------把选择的图片显示在面板上----------------------//
            ImageIcon imageIcon = new ImageIcon(jfc.getSelectedFile()
                    .getAbsolutePath()); //新建一个ImageIcon，这个ImageIcon等于上面所取路径上的图片
            JLabel jLabel = new JLabel(imageIcon);//新建一个JLabel，把图片放进去
            jLabel.setBounds(0, 0, imageIcon.getIconWidth(), imageIcon
                    .getIconHeight());//设置JLabel的大小

//            panel1.add(jLabel);//把JLabel加进panel1中，显示出来
        }
    }

}
