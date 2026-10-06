package edu.cs449.solitaire;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

/** Demonstrates Sprint 0 GUI components; full gameplay is added in later sprints. */
public final class SolitaireSetupDemo {
  private final JLabel status = new JLabel("Choose a board and recording preference.");
  private final JCheckBox recordGame = new JCheckBox("Record game", true);
  private final ButtonGroup boardTypes = new ButtonGroup();

  private void showWindow() {
    JFrame frame = new JFrame("CS 449 - Sprint 0 Peg Solitaire BrainVita");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel content = new JPanel(new BorderLayout(16, 16));
    content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

    JPanel heading = new JPanel(new GridLayout(0, 1, 0, 8));
    JLabel title = new JLabel("Peg Solitaire BrainVita");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
    heading.add(title);
    heading.add(new JLabel("Sprint 0 - GUI programming demonstration"));
    heading.add(new JSeparator());
    content.add(heading, BorderLayout.NORTH);

    JPanel options = new JPanel(new GridLayout(0, 1, 0, 10));
    options.add(new JLabel("Board type"));
    for (String name : new String[] {"English", "Hexagon", "Diamond"}) {
      JRadioButton button = new JRadioButton(name, name.equals("English"));
      button.setActionCommand(name);
      boardTypes.add(button);
      options.add(button);
    }
    options.add(new JSeparator());
    options.add(recordGame);
    options.add(new JLabel("This demo previews settings; it does not start gameplay."));
    content.add(options, BorderLayout.CENTER);

    JPanel footer = new JPanel(new GridLayout(0, 1, 0, 12));
    JButton preview = new JButton("Preview settings");
    preview.addActionListener(event -> updateStatus());
    footer.add(preview);
    footer.add(status);
    content.add(footer, BorderLayout.SOUTH);

    frame.setContentPane(content);
    frame.setMinimumSize(new Dimension(610, 480));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private void updateStatus() {
    String board = boardTypes.getSelection().getActionCommand();
    String recording = recordGame.isSelected() ? "on" : "off";
    status.setText("Selected: " + board + " | Recording: " + recording);
  }

  /** Creates Swing components on the event dispatch thread. */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new SolitaireSetupDemo().showWindow());
  }
}
