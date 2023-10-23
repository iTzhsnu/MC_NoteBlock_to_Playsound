package com.github.itzhsnu.mc_note_block_to_playsound;

import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectNotePane implements ActionListener {
    private final Main main;
    private final JLayeredPane pane = new JLayeredPane();
    private final JComboBox<String> soundType = new JComboBox<>();

    private SourceDataLine sdl = null;

    public final JTextField posF = new JTextField();
    public final List<JButton> buttons = new ArrayList<>();

    public SelectNotePane(Main p) {
        main = p;

        posF.setBounds(400, 257, 40, 20);

        soundType.setBounds(450, 257, 200, 20);
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

        pane.setLayout(null);
        pane.setBounds(400, 282, 360, 85);

        JLabel l = new JLabel(new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/noteblock_reference.png"))).getImage().getScaledInstance(360, 85, Image.SCALE_SMOOTH)));
        l.setBounds(0, 0, 360, 85);
        pane.add(l);

        addButton(0, 1); //F#
        addButton(1, 0); //G
        addButton(1, 1); //G#
        addButton(2, 0); //A
        addButton(2, 1); //A#
        addButton(3, 0); //B
        addButton(4, 0); //C
        addButton(4, 1); //C#
        addButton(5, 0); //D
        addButton(5, 1); //D#
        addButton(6, 0); //E
        addButton(7, 0); //F
        addButton(7, 1); //F#
        addButton(8, 0); //G
        addButton(8, 1); //G#
        addButton(9, 0); //A
        addButton(9, 1); //A#
        addButton(10, 0); //B
        addButton(11, 0); //C
        addButton(11, 1); //C#
        addButton(12, 0); //D
        addButton(12, 1); //D#
        addButton(13, 0); //E
        addButton(14, 0); //F
        addButton(14, 1); //F#


        p.getContentPane().add(pane);
        p.getContentPane().add(posF);
        p.getContentPane().add(soundType);
    }

    public void addButton(int posX, int posY) {
        JButton b = new JButton();
        if (posY == 1) {
            b.setBounds(12 + 20 * posX, 0, 16, 50);
        } else if (posY == 0) {
            b.setBounds(1 + 20 * posX, 0, 19, 85);
        }
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setContentAreaFilled(false);

        b.addActionListener(this);
        pane.add(b);
        pane.setLayer(b, posY + 1);
        buttons.add(b);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; buttons.size() > i; ++i) {
            JButton b = buttons.get(i);
            if (e.getSource() == b) {
                new AudioManage(i).start();
                if (!posF.getText().isEmpty()) {
                    int pos = Integer.parseInt(posF.getText());
                    if (pos == -1) {
                        new AddSound(main, main.list.size(), i).soundType.setSelectedItem(soundType.getItemAt(soundType.getSelectedIndex()));
                    } else if (pos > -1) {
                        Object o = main.list.get(pos);
                        if (o instanceof AddSound) {
                            ((AddSound) o).setNote(i);
                        }
                    }
                }
            }
        }
    }

    public class AudioManage extends Thread {
        private final int i;
        private final List<SourceDataLine> sdls = new ArrayList<>();

        public AudioManage(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            try {
                new Main.PlayAudioThread(soundType.getItemAt(soundType.getSelectedIndex()), Main.getPitch(i), main.volume.getValue(), sdls).start();
                Thread.sleep(1000);
                for (SourceDataLine sdl : sdls) {
                    sdl.stop();
                    sdl.close();
                }
                this.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
