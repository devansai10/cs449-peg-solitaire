package edu.cs449.solitaire;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import edu.cs449.solitaire.SolitaireGame.Move;
import edu.cs449.solitaire.SolitaireGame.Position;

/** Swing interface for the manual game. */
public final class SolitaireApp {
  private static final Color BACKGROUND = new Color(242, 246, 244);
  private static final Color GREEN = new Color(29, 106, 85);
  private SolitaireGame game = new SolitaireGame(new BoardShape.English(), 7);
  private Position selected;
  private final JLabel counts = new JLabel();
  private final JLabel status = new JLabel("Select a peg, then an empty hole two positions away.");
  private final JCheckBox hints = new JCheckBox("Show legal destinations", true);
  private final BoardPanel board = new BoardPanel();

  private void show() {
    JFrame frame = new JFrame("Peg Solitaire BrainVita");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel content = new JPanel(new BorderLayout(20, 16));
    content.setBackground(BACKGROUND);
    content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    JPanel header = new JPanel(new GridLayout(0, 1, 0, 8));
    header.setOpaque(false);
    JLabel title = new JLabel("Peg Solitaire BrainVita");
    title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
    header.add(title);
    header.add(new JLabel("Jump over a peg. Clear the board. Aim for one remaining."));
    header.add(new JSeparator());
    content.add(header, BorderLayout.NORTH);
    content.add(board, BorderLayout.CENTER);

    JPanel settings = new JPanel(new GridLayout(0, 1, 0, 10));
    settings.setOpaque(false);
    JComboBox<BoardShape> types = new JComboBox<>(new BoardShape[] {
        new BoardShape.English(), new BoardShape.Hexagon(), new BoardShape.Diamond()});
    JComboBox<Integer> sizes = new JComboBox<>(new Integer[] {7, 9});
    settings.add(new JLabel("Board type"));
    settings.add(types);
    settings.add(new JLabel("Board size"));
    settings.add(sizes);
    JButton newGame = new JButton("New Game");
    newGame.addActionListener(event -> {
      game = new SolitaireGame((BoardShape) types.getSelectedItem(),
          (Integer) sizes.getSelectedItem());
      selected = null;
      update("New game started. Select a peg.");
    });
    settings.add(newGame);
    hints.setOpaque(false);
    hints.addActionListener(event -> board.repaint());
    settings.add(hints);
    settings.add(new JLabel("Settings apply on New Game."));
    settings.add(new JLabel("Horizontal, vertical and diagonal"));
    settings.add(new JLabel("jumps are allowed."));
    JPanel sidebar = new JPanel(new BorderLayout());
    sidebar.setOpaque(false);
    sidebar.add(settings, BorderLayout.NORTH);
    content.add(sidebar, BorderLayout.EAST);
    JPanel footer = new JPanel(new GridLayout(0, 1, 0, 8));
    footer.setOpaque(false);
    footer.add(new JSeparator());
    footer.add(counts);
    footer.add(status);
    content.add(footer, BorderLayout.SOUTH);
    frame.setContentPane(content);
    frame.setMinimumSize(new Dimension(900, 760));
    frame.pack();
    frame.setLocationRelativeTo(null);
    update("Select a peg, then an empty hole two positions away.");
    frame.setVisible(true);
  }

  private void update(String message) {
    counts.setText(game.shape() + " • Size " + game.size() + " • Pegs: " + game.pegCount()
        + " • Moves: " + game.moveCount());
    status.setText(game.isOver()
        ? "Game over — " + game.pegCount() + " pegs remaining. " + game.result()
            + ". Click New Game to play again."
        : message);
    board.repaint();
  }

  private void select(int row, int column) {
    if (game.isOver()) {
      return;
    }
    Position clicked = new Position(row, column);
    if (game.hasPeg(row, column)) {
      selected = clicked.equals(selected) ? null : clicked;
      update(selected == null ? "Selection cleared." : "Peg selected. Choose an empty destination.");
    } else if (selected == null) {
      update("Choose a peg first.");
    } else if (game.move(new Move(selected, clicked))) {
      selected = null;
      update("Good move. Select your next peg.");
    } else {
      update("Invalid move: jump over one peg into an empty hole two positions away.");
    }
  }

  private final class BoardPanel extends JPanel {
    BoardPanel() {
      setPreferredSize(new Dimension(560, 540));
      setBackground(new Color(225, 235, 227));
      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
          for (int row = 0; row < game.size(); row++) {
            for (int column = 0; column < game.size(); column++) {
              if (game.isPlayable(row, column)
                  && Math.hypot(event.getX() - x(row, column), event.getY() - y(row))
                      <= spacing() * .36) {
                select(row, column);
                return;
              }
            }
          }
        }
      });
    }

    private double spacing() {
      return Math.min(getWidth(), getHeight()) / (double) (game.size() + 1);
    }

    private double x(int row, int column) {
      double offset = game.shape() instanceof BoardShape.Hexagon
          ? (row - game.size() / 2) * .5 : 0;
      return getWidth() / 2.0 + (column - game.size() / 2 + offset) * spacing();
    }

    private double y(int row) {
      double scale = game.shape() instanceof BoardShape.Hexagon ? .866 : 1;
      return getHeight() / 2.0 + (row - game.size() / 2) * spacing() * scale;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);
      Graphics2D g = (Graphics2D) graphics.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      int diameter = (int) (spacing() * .68);
      for (int row = 0; row < game.size(); row++) {
        for (int column = 0; column < game.size(); column++) {
          if (!game.isPlayable(row, column)) {
            continue;
          }
          int left = (int) x(row, column) - diameter / 2;
          int top = (int) y(row) - diameter / 2;
          g.setColor(game.hasPeg(row, column) ? GREEN : new Color(190, 206, 193));
          g.fillOval(left, top, diameter, diameter);
          Position position = new Position(row, column);
          boolean target = selected != null && hints.isSelected()
              && game.isLegal(new Move(selected, position));
          if (position.equals(selected) || target) {
            g.setStroke(new BasicStroke(3));
            g.setColor(position.equals(selected) ? new Color(233, 165, 39) : GREEN);
            g.drawOval(left - 3, top - 3, diameter + 6, diameter + 6);
          }
        }
      }
      g.dispose();
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new SolitaireApp().show());
  }
}
