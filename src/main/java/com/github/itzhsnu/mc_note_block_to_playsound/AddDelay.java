package com.github.itzhsnu.mc_note_block_to_playsound;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddDelay implements ActionListener {
    private final Main p;
    public int pos;

    private final JTextField delay = new JTextField();
    private final JButton delete = new JButton("-");

    public AddDelay(Main p, int pos) {
        this.p = p;
        this.pos = pos;

        setPos();

        delete.addActionListener(this);

        p.display.add(delay);
        p.display.add(delete);

        p.list.add(this);
        SwingUtilities.updateComponentTreeUI(p);
    }

    public void setPos() {
        delay.setBounds(180, 5 + pos * 30, 40, 20);
        delete.setBounds(255, 5 + pos * 30, 40, 20);
    }

    public int getDelay() {
        if (delay.getText().matches("[+-]?\\d*(\\.\\d+)?")) {
            return Integer.parseInt(delay.getText());
        } else {
            return 0;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        p.display.remove(delay);
        p.display.remove(delete);
        p.relocate(pos);
    }
}
