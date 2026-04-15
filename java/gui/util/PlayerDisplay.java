package gui.util;

import gui.dto.PlayerDto;

import javax.swing.*;
import java.awt.*;

public class PlayerDisplay extends JPanel {

    JLabel timeLabel;
    JLabel nameLabel;
    JLabel ratingLabel;

    public PlayerDisplay(PlayerDto playerDto, int time) {
        setupUI();
        nameLabel.setText(playerDto.lastname + ", " + playerDto.firstname);
        ratingLabel.setText(playerDto.fide_title.getKey().toUpperCase() +  " (" + playerDto.fide_rating + ")");
        update(time);
    }

    public PlayerDisplay(String name, int time) {
        setupUI();
        nameLabel.setText(name);
        update(time);
    }

    public void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(0, 0, 0, 0));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        timeLabel = new JLabel();
        timeLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(timeLabel, BorderLayout.WEST);

        nameLabel = new JLabel();
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(nameLabel);

        ratingLabel = new JLabel();
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ratingLabel.setForeground(new Color(76, 153, 234));
        textPanel.add(ratingLabel);

        add(textPanel, BorderLayout.CENTER);

    }

    public void update(int time) {
        timeLabel.setText(formatTime(time));
        timeLabel.setFont(timeLabel.getFont().deriveFont((float) (getHeight() - 20)));
        nameLabel.setFont(nameLabel.getFont().deriveFont((float) ((getHeight() - 20) * 0.5)));
        ratingLabel.setFont(ratingLabel.getFont().deriveFont((float) ((getHeight() - 20) * 0.3)));
    }

    public void updateTime(int time) {
        if (time == -1) {
            timeLabel.setText("");
        } else {
            timeLabel.setText(formatTime(time));
        }
    }

    private String formatTime(int seconds) {
        int minutes = (seconds - (seconds % 60)) / 60;
        seconds -= (seconds - (seconds % 60));
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
}