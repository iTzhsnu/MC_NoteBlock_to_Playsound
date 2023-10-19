package com.github.itzhsnu.mc_note_block_to_playsound;

import javax.swing.*;

public class Buttons {

    public static JButton up(Object o) {
        JButton b = new JButton();
        b.setIcon(new ImageIcon(o.getClass().getResource("/up.png")));
        return b;
    }

    public static JButton down(Object o) {
        JButton b = new JButton();
        b.setIcon(new ImageIcon(o.getClass().getResource("/down.png")));
        return b;
    }

    public static JButton delete(Object o) {
        JButton b = new JButton();
        b.setIcon(new ImageIcon(o.getClass().getResource("/delete.png")));
        return b;
    }

    public static JButton select(Object o) {
        JButton b = new JButton();
        b.setIcon(new ImageIcon(o.getClass().getResource("/select.png")));
        return b;
    }
}
