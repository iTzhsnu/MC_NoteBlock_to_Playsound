package com.github.itzhsnu.mc_note_block_to_playsound;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddDelay implements ActionListener {
    private final Main p;
    public int pos;

    private final JTextField delay = new JTextField();
    private final JButton delete;
    private final JButton up;
    private final JButton down;

    public AddDelay(Main p, int pos, int delay) {
        this(p, pos);

        this.delay.setText(String.valueOf(delay));
    }

    public AddDelay(Main p, int pos) {
        this.p = p;
        this.pos = pos;

        up = Buttons.up(pos, this);
        down = Buttons.down(pos, this);
        delete = Buttons.delete(pos, this);

        up.addActionListener(this);
        down.addActionListener(this);
        delete.addActionListener(this);

        setPos();

        p.display.add(delay);
        p.display.add(delete);
        p.display.add(up);
        p.display.add(down);

        p.list.add(this);
        SwingUtilities.updateComponentTreeUI(p);
    }

    public void setPos() {
        delay.setBounds(180, 5 + pos * 30, 40, 20);
        up.setBounds(255, 5 + 30 * pos, 20, 20);
        down.setBounds(280, 5 + 30 * pos, 20, 20);
        delete.setBounds(305, 5 + 30 * pos, 20, 20);
    }

    public int getDelay() {
        if (delay.getText().matches("[+-]?\\d*(\\.\\d+)?")) {
            return Integer.parseInt(delay.getText());
        } else {
            return 0;
        }
    }

    public void removeAll() {
        p.display.remove(delay);
        p.display.remove(delete);
        p.display.remove(up);
        p.display.remove(down);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == up) {
            p.up(pos);
        } else if (e.getSource() == down) {
            p.down(pos);
        } else if (e.getSource() == delete) {
            removeAll();
            p.relocate(pos);
        }
    }
}
