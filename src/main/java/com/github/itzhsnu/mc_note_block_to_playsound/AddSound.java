package com.github.itzhsnu.mc_note_block_to_playsound;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddSound implements ActionListener {
    private final Main p;
    public int pos;

    private final JComboBox<String> soundType = new JComboBox<>();
    private final JComboBox<String> note = new JComboBox<>();
    private final JButton delete;
    private final JButton up;
    private final JButton down;

    public AddSound(Main p, int pos, String soundType, int note) {
        this(p, pos);

        ((JTextField) this.soundType.getEditor().getEditorComponent()).setText(soundType);
        this.note.setSelectedIndex(note);
    }

    public AddSound(Main p, int pos) {
        this.p = p;
        this.pos = pos;

        soundType.addItem("block.note_block.bass");
        soundType.addItem("block.note_block.snare");
        soundType.addItem("block.note_block.hat");
        soundType.addItem("block.note_block.basedrum");
        soundType.addItem("block.note_block.bell");
        soundType.addItem("block.note_block.flute");
        soundType.addItem("block.note_block.chime");
        soundType.addItem("block.note_block.guitar");
        soundType.addItem("block.note_block.xylophone");
        soundType.addItem("block.note_block.iron_xylophone");
        soundType.addItem("block.note_block.cow_bell");
        soundType.addItem("block.note_block.didgeridoo");
        soundType.addItem("block.note_block.bit");
        soundType.addItem("block.note_block.banjo");
        soundType.addItem("block.note_block.pling");
        soundType.addItem("block.note_block.harp");

        soundType.setEditable(true);

        for (int i = 0; 24 >= i; ++i) {
            note.addItem(String.valueOf(i));
        }

        up = Buttons.up(pos, this);
        down = Buttons.down(pos, this);
        delete = Buttons.delete(pos, this);

        up.addActionListener(this);
        down.addActionListener(this);
        delete.addActionListener(this);

        setPos();

        p.display.add(soundType);
        p.display.add(note);
        p.display.add(delete);
        p.display.add(up);
        p.display.add(down);

        p.list.add(this);
        SwingUtilities.updateComponentTreeUI(p);
    }

    public void setPos() {
        soundType.setBounds(5, 5 + pos * 30, 200, 20);
        note.setBounds(210, 5 + pos * 30, 40, 20);
        up.setBounds(255, 5 + 30 * pos, 20, 20);
        down.setBounds(280, 5 + 30 * pos, 20, 20);
        delete.setBounds(305, 5 + 30 * pos, 20, 20);
    }

    public String getSoundType() {
        return soundType.getItemAt(soundType.getSelectedIndex());
    }

    public int getNote() {
        return note.getSelectedIndex();
    }

    public void removeAll() {
        p.display.remove(soundType);
        p.display.remove(note);
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
