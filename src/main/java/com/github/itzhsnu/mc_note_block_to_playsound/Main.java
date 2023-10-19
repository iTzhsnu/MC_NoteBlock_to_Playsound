package com.github.itzhsnu.mc_note_block_to_playsound;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame implements ActionListener, ChangeListener {
    private final JButton addDelay = new JButton("Add Delay");
    private final JButton addSound = new JButton("Add Sound");
    private final JButton generate = new JButton("Generate");
    private final JButton load = new JButton("Load");
    private final JLabel volumeText = new JLabel("Volume 100%");
    private final JButton play = new JButton("Play");

    public final JSlider volume = new JSlider(0, 100, 100);
    public final SelectNotePane snp;

    public final JPanel display = new JPanel();
    public final List<Object> list = new ArrayList<>();

    public static void main(String[] args) {
        new Main().setVisible(true);
    }

    public Main() {
        setTitle("MC Note Block to Playsound");
        setBounds(100, 100, 800, 800); //W450
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        addDelay.setBounds(5, 5, 100, 40);
        addSound.setBounds(115, 5, 100, 40);
        generate.setBounds(225, 5, 100, 40);
        load.setBounds(335, 5, 100, 40);
        play.setBounds(445, 5, 100, 40);

        display.setBorder(new LineBorder(Color.BLACK));
        display.setPreferredSize(new Dimension(355, 700));
        display.setLayout(null);

        JScrollPane scroll = new JScrollPane(display);
        scroll.setBounds(5, 55, 373, 700);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        addDelay.addActionListener(this);
        addSound.addActionListener(this);
        generate.addActionListener(this);
        load.addActionListener(this);
        play.addActionListener(this);

        volumeText.setBounds(400, 175, 200, 20);
        volume.setBounds(400, 200, 200, 20);
        volume.addChangeListener(this);

        getContentPane().add(addDelay);
        getContentPane().add(addSound);
        getContentPane().add(generate);
        getContentPane().add(load);
        getContentPane().add(scroll);
        getContentPane().add(volume);
        getContentPane().add(volumeText);
        getContentPane().add(play);

        snp = new SelectNotePane(this);
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

        display.setPreferredSize(new Dimension(330, 700 + 30 * list.size()));

        SwingUtilities.updateComponentTreeUI(this);
    }

    public void up(int pos) {
        if (pos < 1) return;
        List<Object> l = new ArrayList<>(list.subList(pos - 1, pos + 1)); //0 = Up, 1 = This

        list.set(pos - 1, l.get(1)); //Move to Up
        list.set(pos, l.get(0)); //Move to Down

        relocate(pos - 1, pos + 1);
    }

    public void down(int pos) {
        if (list.size() < pos + 2) return;
        List<Object> l = new ArrayList<>(list.subList(pos, pos + 2)); //0 = This, 1 = Down

        list.set(pos, l.get(1)); //Move to Up
        list.set(pos + 1, l.get(0)); //Move to Down

        relocate(pos, pos + 2);
    }

    //from Index inclusive, to Index exclusive
    public void relocate(int fromIndex, int toIndex) {
        for (int i = fromIndex; toIndex > i; ++i) {
            Object o = list.get(i);
            if (o instanceof AddSound) {
                ((AddSound) o).pos = i;
                ((AddSound) o).setPos();
            } else if (o instanceof AddDelay) {
                ((AddDelay) o).pos = i;
                ((AddDelay) o).setPos();
            }
        }

        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addDelay) { //Add Delay
            new AddDelay(this, list.size());
            display.setPreferredSize(new Dimension(330, 700 + 30 * list.size()));
        } else if (e.getSource() == addSound) { //Add Sound
            new AddSound(this, list.size());
            display.setPreferredSize(new Dimension(330, 700 + 30 * list.size()));
        } else if (e.getSource() == generate) { //Generate
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
        } else if (e.getSource() == load) { //Load
            loadData();
        } else if (e.getSource() == play) { //Play
            new PlayMusicThread().start();
        }
    }

    public void loadData() {
        for (Object o : list) {
            if (o instanceof AddSound) {
                removeAll();
            } else if (o instanceof AddDelay) {
                removeAll();
            }
        }

        try {
            int time = 0;
            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            String[] ss = ((String) t.getTransferData(DataFlavor.stringFlavor)).split("\r?\n|\r");
            List<String> soundNames = new ArrayList<>();

            for (String s : ss) {
                String[] command = s.split(" ");
                int thisTime = Integer.parseInt(command[3].split("Music=")[1].split("}]")[0]);
                if (thisTime > time) {
                    new AddDelay(this, list.size(), thisTime - time);
                    time = thisTime;
                    soundNames.add("");
                }

                new AddSound(this, list.size(), getNote(Float.parseFloat(command[8])));
                soundNames.add(command[1]);
            }
            display.setPreferredSize(new Dimension(330, 700 + 30 * list.size()));

            for (int i = 0; list.size() > i; ++i) {
                Object o = list.get(i);
                if (o instanceof AddSound) {
                    ((JTextField) ((AddSound) o).soundType.getEditor().getEditorComponent()).setText(soundNames.get(i));
                }
            }

        } catch (IOException | UnsupportedFlavorException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        volumeText.setText("Volume " + volume.getValue() + "%");
    }

    public static float getPitch(int note) {
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

    public static int getNote(float pitch) {
        int iPitch = (int) Math.floor(pitch * 100);
        switch (iPitch) {
            case 50:
                return 0;
            case 52:
                return 1;
            case 56:
                return 2;
            case 59:
                return 3;
            case 62:
                return 4;
            case 66:
                return 5;
            case 70:
                return 6;
            case 74:
                return 7;
            case 79:
                return 8;
            case 84:
                return 9;
            case 89:
                return 10;
            case 94:
                return 11;
            case 100:
                return 12;
            case 105:
                return 13;
            case 112:
                return 14;
            case 118:
                return 15;
            case 125:
                return 16;
            case 133:
                return 17;
            case 141:
                return 18;
            case 149:
                return 19;
            case 158:
                return 20;
            case 168:
                return 21;
            case 178:
                return 22;
            case 188:
                return 23;
            case 200:
                return 24;
        }
        return 0;
    }

    private final List<SourceDataLine> sdls = new ArrayList<>();
    public class PlayMusicThread extends Thread {
        @Override
        public void run() {
            try {
                System.out.println("running");
                for (Object o : list) {
                    if (o instanceof AddSound) {
                        new PlayAudioThread(((AddSound) o).getSoundType(), getPitch(((AddSound) o).getNote()), volume.getValue(), sdls).start();
                    } else if (o instanceof AddDelay) {
                        Thread.sleep(50L * ((AddDelay) o).getDelay());
                    }
                }

                Thread.sleep(1000);

                for (SourceDataLine sdl : sdls) {
                    sdl.stop();
                    sdl.close();
                }
                System.out.println("end");
                this.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class PlayAudioThread extends Thread {
        private final String audio;
        private final float pitch;
        private final int volume;
        private final List<SourceDataLine> sdls;

        public PlayAudioThread(String audio, float pitch, int volume, List<SourceDataLine> sdls) {
            this.audio = audio;
            this.pitch = pitch;
            this.volume = volume;
            this.sdls = sdls;
        }

        @Override
        public void run() {
            try {
                playAudio();
                Thread.sleep(1000);
                this.interrupt();
            } catch (InterruptedException | UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }

        public void playAudio() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("/" + audio + ".wav")));

            AudioFormat audio = audioStream.getFormat();
            AudioFormat editedAudio = new AudioFormat(audio.getEncoding(), audio.getSampleRate() * pitch, audio.getSampleSizeInBits(), audio.getChannels(), audio.getFrameSize(), audio.getFrameRate() * pitch, audio.isBigEndian());

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, editedAudio);
            SourceDataLine sdl = (SourceDataLine) AudioSystem.getLine(info);

            sdl.open(editedAudio);

            FloatControl volumeControl = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue((float)Math.log10(volume / 100F) * 20);

            int bufferSize = sdl.getBufferSize();
            int bytesRead;
            byte[] data = new byte[bufferSize];

            sdl.start();

            while ((bytesRead = audioStream.read(data, 0, data.length)) != -1) {
                sdl.write(data, 0, bytesRead);
            }

            //sdl.stop();
            sdl.drain();
            //sdl.close();
            if (sdls != null) sdls.add(sdl);
        }
    }
}
