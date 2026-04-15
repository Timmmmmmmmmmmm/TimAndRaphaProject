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
        styleByTitle(playerDto.fide_title);
        update(time);
    }

    public PlayerDisplay(String name, int time) {
        setupUI();
        nameLabel.setText(name);
        update(time);
    }

    public void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 255, 255, 70));

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

    private void styleByTitle(FideTitle title) {
        switch (title) {
            case GRANDMASTER, WOMAN_GRANDMASTER:
                ratingLabel.setForeground(new Color(197, 32, 32));
                ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.BOLD));
                break;

            case INTERNATIONAL_MASTER, WOMAN_INTERNATIONAL_MASTER:
                ratingLabel.setForeground(new Color(255, 140, 0));
                ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.BOLD));
                break;

            case FIDE_MASTER, WOMAN_FIDE_MASTER:
                ratingLabel.setForeground(new Color(63, 131, 40));
                break;

            case FIDE_CANDIDATE_MASTER, WOMAN_CANDIDATE_MASTER:
                ratingLabel.setForeground(new Color(0, 21, 255));
                break;

            default:
                ratingLabel.setForeground(new Color(0, 0, 0));
        }
    }

    private String formatTime(int seconds) {
        int minutes = (seconds - (seconds % 60)) / 60;
        seconds -= (seconds - (seconds % 60));
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
}