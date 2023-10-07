package com.github.itzhsnu.mc_note_block_to_playsound;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame implements ActionListener {
    private final JButton addDelay = new JButton("Add Delay");
    private final JButton addSound = new JButton("Add Sound");
    private final JButton generate = new JButton("Generate");
    public final JPanel display = new JPanel();
    public final List<Object> list = new ArrayList<>();

    public static void main(String[] args) {
        new Main().setVisible(true);
    }

    public Main() {
        setTitle("MC Note Block to Playsound");
        setBounds(100, 100, 350, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        addDelay.setBounds(5, 5, 100, 40);
        addSound.setBounds(115, 5, 100, 40);
        generate.setBounds(225, 5, 100, 40);

        display.setBorder(new LineBorder(Color.BLACK));
        display.setPreferredSize(new Dimension(312, 100000));
        display.setLayout(null);

        JScrollPane scroll = new JScrollPane(display);
        scroll.setBounds(5, 55, 330, 730);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        addDelay.addActionListener(this);
        addSound.addActionListener(this);
        generate.addActionListener(this);

        getContentPane().add(addDelay);
        getContentPane().add(addSound);
        getContentPane().add(generate);
        getContentPane().add(scroll);
    }

    public void relocate(int pos) {
        list.remove(pos);

        if (list.size() > pos) {
            for (int i = pos; list.size() > i; ++i) {
                Object o = list.get(i);
                if (o instanceof AddSound) {
                    ((AddSound) o).pos -= 1;
                    ((AddSound) o).setPos();
                } else if (o instanceof AddDelay) {
                    ((AddDelay) o).pos -= 1;
                    ((AddDelay) o).setPos();
                }
            }
        }

        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addDelay) {
            new AddDelay(this, list.size());
        } else if (e.getSource() == addSound) {
            new AddSound(this, list.size());
        } else if (e.getSource() == generate) {
            int time = 0;
            StringBuilder sb = new StringBuilder();
            for (Object o : list) {
                if (o instanceof AddSound) {
                    String s = "playsound " + ((AddSound) o).getSoundType() + " master @s[scores={Music=" + time + "}] ~ ~ ~ 1 " + getPitch(((AddSound) o).getNote()) + " 1\n";
                    sb.append(s);
                } else if (o instanceof AddDelay) {
                    time += ((AddDelay) o).getDelay();
                }
            }

            StringSelection selection = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        }
    }

    public float getPitch(int note) {
        switch (note) {
            case 0:
                return 0.5F;
            case 1:
                return 0.529732F;
            case 2:
                return 0.561231F;
            case 3:
                return 0.594604F;
            case 4:
                return 0.629961F;
            case 5:
                return 0.667420F;
            case 6:
                return 0.707107F;
            case 7:
                return 0.749154F;
            case 8:
                return 0.793701F;
            case 9:
                return 0.840896F;
            case 10:
                return 0.890899F;
            case 11:
                return 0.943874F;
            case 12:
                return 1.0F;
            case 13:
                return 1.059463F;
            case 14:
                return 1.122462F;
            case 15:
                return 1.189207F;
            case 16:
                return 1.259921F;
            case 17:
                return 1.334840F;
            case 18:
                return 1.414214F;
            case 19:
                return 1.498307F;
            case 20:
                return 1.587401F;
            case 21:
                return 1.681793F;
            case 22:
                return 1.781797F;
            case 23:
                return 1.887749F;
            case 24:
                return 2.0F;
        }
        return 0.5F;
    }
}
