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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import edu.cs449.solitaire.SolitaireGame.Move;
import edu.cs449.solitaire.SolitaireGame.Position;

/** Swing interface for Sprint 3 manual, automated, recording and replay features. */
public final class SolitaireApp {
  private static final Color BACKGROUND = new Color(242, 246, 244);
  private static final Color GREEN = new Color(29, 106, 85);
  private static final int AUTOMATED_DELAY_MS = 220;
  private static final int REPLAY_DELAY_MS = 550;

  private final Random random = new Random();
  private GameSession session = new ManualGameSession(new BoardShape.English(), 7, false);
  private SolitaireGame replayGame;
  private GameRecording replayRecording;
  private int replayFrameIndex;
  private Position selected;
  private Timer automatedTimer;
  private Timer replayTimer;
  private final JLabel counts = new JLabel();
  private final JLabel status = new JLabel("Select a peg, then an empty hole two positions away.");
  private final JCheckBox hints = new JCheckBox("Show legal destinations", true);
  private final JCheckBox recordGame = new JCheckBox("Record this game", false);
  private final BoardPanel board = new BoardPanel();

  private void show() {
    JFrame frame = new JFrame("Peg Solitaire BrainVita - CS 449 Sprint 3");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel content = new JPanel(new BorderLayout(20, 16));
    content.setBackground(BACKGROUND);
    content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JPanel header = new JPanel(new GridLayout(0, 1, 0, 6));
    header.setOpaque(false);
    JLabel title = new JLabel("Peg Solitaire BrainVita");
    title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
    header.add(title);
    header.add(new JLabel("Manual play, automated play, randomization, recording and replay"));
    header.add(new JSeparator());
    content.add(header, BorderLayout.NORTH);
    content.add(board, BorderLayout.CENTER);

    JComboBox<BoardShape> types = new JComboBox<>(new BoardShape[] {
        new BoardShape.English(), new BoardShape.Hexagon(), new BoardShape.Diamond()});
    JComboBox<Integer> sizes = new JComboBox<>(new Integer[] {7, 9});
    JComboBox<GameMode> modes = new JComboBox<>(GameMode.values());

    JPanel settings = new JPanel(new GridLayout(0, 1, 0, 8));
    settings.setOpaque(false);
    settings.add(new JLabel("Board type"));
    settings.add(types);
    settings.add(new JLabel("Board size"));
    settings.add(sizes);
    settings.add(new JLabel("Game mode"));
    settings.add(modes);
    recordGame.setOpaque(false);
    settings.add(recordGame);

    JButton newGame = new JButton("New Game");
    newGame.addActionListener(event -> {
      stopTimers();
      BoardShape shape = (BoardShape) types.getSelectedItem();
      int size = (Integer) sizes.getSelectedItem();
      GameMode mode = (GameMode) modes.getSelectedItem();
      session = mode == GameMode.MANUAL
          ? new ManualGameSession(shape, size, recordGame.isSelected())
          : new AutomatedGameSession(shape, size, recordGame.isSelected());
      replayGame = null;
      replayRecording = null;
      selected = null;
      update(mode == GameMode.MANUAL
          ? "Manual game started. Select a peg."
          : "Automated game started. Click Autoplay.");
    });
    settings.add(newGame);

    JButton autoplay = new JButton("Autoplay");
    autoplay.addActionListener(event -> startAutoplay());
    settings.add(autoplay);

    JButton randomize = new JButton("Randomize");
    randomize.addActionListener(event -> randomizeManualGame());
    settings.add(randomize);

    JButton saveRecording = new JButton("Save Recording");
    saveRecording.addActionListener(event -> saveRecording());
    settings.add(saveRecording);

    JButton replay = new JButton("Replay Recording");
    replay.addActionListener(event -> loadReplay());
    settings.add(replay);

    hints.setOpaque(false);
    hints.addActionListener(event -> board.repaint());
    settings.add(hints);
    settings.add(new JLabel("Settings apply on New Game."));

    JPanel sidebar = new JPanel(new BorderLayout());
    sidebar.setOpaque(false);
    sidebar.add(settings, BorderLayout.NORTH);
    content.add(sidebar, BorderLayout.EAST);

    JPanel footer = new JPanel(new GridLayout(0, 1, 0, 6));
    footer.setOpaque(false);
    footer.add(new JSeparator());
    footer.add(counts);
    footer.add(status);
    content.add(footer, BorderLayout.SOUTH);

    frame.setContentPane(content);
    frame.setMinimumSize(new Dimension(980, 790));
    frame.pack();
    frame.setLocationRelativeTo(null);
    update("Select a peg, then an empty hole two positions away.");
    frame.setVisible(true);
  }

  private SolitaireGame activeGame() {
    return replayGame == null ? session.game() : replayGame;
  }

  private String activeMode() {
    return replayRecording == null ? session.mode().toString()
        : replayRecording.mode() + " replay";
  }

  private void update(String message) {
    SolitaireGame game = activeGame();
    counts.setText(game.shape() + " • Size " + game.size() + " • " + activeMode()
        + " • Pegs: " + game.pegCount() + " • Moves: " + game.moveCount());
    if (replayGame != null) {
      status.setText(message);
    } else {
      status.setText(game.isOver()
          ? "Game over — " + game.pegCount() + " pegs remaining. " + game.result()
              + ". Save the recording or click New Game."
          : message);
    }
    board.repaint();
  }

  private void select(int row, int column) {
    if (replayGame != null) {
      update("Replay is read-only. Start a New Game to play.");
      return;
    }
    if (!(session instanceof ManualGameSession manual)) {
      update("The computer controls an automated game. Click Autoplay.");
      return;
    }
    SolitaireGame game = session.game();
    if (game.isOver()) {
      return;
    }
    Position clicked = new Position(row, column);
    if (game.hasPeg(row, column)) {
      selected = clicked.equals(selected) ? null : clicked;
      update(selected == null ? "Selection cleared." : "Peg selected. Choose an empty destination.");
    } else if (selected == null) {
      update("Choose a peg first.");
    } else if (manual.move(new Move(selected, clicked))) {
      selected = null;
      update("Good move. Select your next peg.");
    } else {
      update("Invalid move: jump over one peg into an empty hole two positions away.");
    }
  }

  private void startAutoplay() {
    if (replayGame != null) {
      update("Finish the replay or click New Game before autoplay.");
      return;
    }
    if (!(session instanceof AutomatedGameSession automated)) {
      update("Choose Automated mode and click New Game before Autoplay.");
      return;
    }
    if (automatedTimer != null && automatedTimer.isRunning()) {
      update("Autoplay is already running.");
      return;
    }
    automatedTimer = new Timer(AUTOMATED_DELAY_MS, event -> {
      if (automated.game().isOver()) {
        automatedTimer.stop();
        update("Automated game finished.");
      } else {
        automated.moveNext(random);
        update("Computer made move " + automated.game().moveCount() + ".");
      }
    });
    automatedTimer.start();
    update("Autoplay started.");
  }

  private void randomizeManualGame() {
    if (replayGame != null) {
      update("Replay is read-only. Start a New Game to randomize.");
      return;
    }
    if (!(session instanceof ManualGameSession manual)) {
      update("Randomize is available during a manual game.");
      return;
    }
    selected = null;
    if (manual.randomize(random)) {
      update("The current pegs were randomized; the peg and move counts were preserved.");
    } else {
      update("A finished game cannot be randomized.");
    }
  }

  private void saveRecording() {
    if (replayGame != null) {
      update("Start a New Game before saving a new recording.");
      return;
    }
    GameRecording recording = session.recording().orElse(null);
    if (recording == null) {
      update("Select Record this game, then click New Game before playing.");
      return;
    }
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Save game recording");
    chooser.setSelectedFile(new java.io.File("peg-solitaire-recording.txt"));
    if (chooser.showSaveDialog(board) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    Path path = chooser.getSelectedFile().toPath();
    if (!path.getFileName().toString().toLowerCase().endsWith(".txt")) {
      path = Path.of(path.toString() + ".txt");
    }
    try {
      recording.save(path);
      update("Recording saved to " + path.getFileName() + ".");
    } catch (IOException exception) {
      update("Could not save recording: " + exception.getMessage());
    }
  }

  private void loadReplay() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Open game recording");
    if (chooser.showOpenDialog(board) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    try {
      stopTimers();
      replayRecording = GameRecording.load(chooser.getSelectedFile().toPath());
      replayFrameIndex = 0;
      replayGame = replayRecording.gameAt(0);
      selected = null;
      showReplayFrame();
      replayTimer = new Timer(REPLAY_DELAY_MS, event -> {
        if (replayFrameIndex + 1 >= replayRecording.frames().size()) {
          replayTimer.stop();
          update("Replay finished — " + replayGame.pegCount() + " pegs remain.");
          return;
        }
        replayFrameIndex++;
        replayGame = replayRecording.gameAt(replayFrameIndex);
        showReplayFrame();
      });
      replayTimer.start();
    } catch (IOException | RuntimeException exception) {
      replayGame = null;
      replayRecording = null;
      update("Could not replay file: " + exception.getMessage());
    }
  }

  private void showReplayFrame() {
    GameRecording.Frame frame = replayRecording.frames().get(replayFrameIndex);
    update("Replay " + (replayFrameIndex + 1) + "/" + replayRecording.frames().size()
        + ": " + frame.action());
  }

  private void stopTimers() {
    if (automatedTimer != null) {
      automatedTimer.stop();
    }
    if (replayTimer != null) {
      replayTimer.stop();
    }
  }

  private final class BoardPanel extends JPanel {
    BoardPanel() {
      setPreferredSize(new Dimension(600, 560));
      setBackground(new Color(225, 235, 227));
      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent event) {
          SolitaireGame game = activeGame();
          for (int row = 0; row < game.size(); row++) {
            for (int column = 0; column < game.size(); column++) {
              if (game.isPlayable(row, column)
                  && Math.hypot(event.getX() - x(game, row, column),
                      event.getY() - y(game, row)) <= spacing(game) * .36) {
                select(row, column);
                return;
              }
            }
          }
        }
      });
    }

    private double spacing(SolitaireGame game) {
      return Math.min(getWidth(), getHeight()) / (double) (game.size() + 1);
    }

    private double x(SolitaireGame game, int row, int column) {
      double offset = game.shape() instanceof BoardShape.Hexagon
          ? (row - game.size() / 2) * .5 : 0;
      return getWidth() / 2.0 + (column - game.size() / 2 + offset) * spacing(game);
    }

    private double y(SolitaireGame game, int row) {
      double scale = game.shape() instanceof BoardShape.Hexagon ? .866 : 1;
      return getHeight() / 2.0 + (row - game.size() / 2) * spacing(game) * scale;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);
      SolitaireGame game = activeGame();
      Graphics2D g = (Graphics2D) graphics.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      int diameter = (int) (spacing(game) * .68);
      for (int row = 0; row < game.size(); row++) {
        for (int column = 0; column < game.size(); column++) {
          if (!game.isPlayable(row, column)) {
            continue;
          }
          int left = (int) x(game, row, column) - diameter / 2;
          int top = (int) y(game, row) - diameter / 2;
          g.setColor(game.hasPeg(row, column) ? GREEN : new Color(190, 206, 193));
          g.fillOval(left, top, diameter, diameter);
          Position position = new Position(row, column);
          boolean target = replayGame == null && session instanceof ManualGameSession
              && selected != null && hints.isSelected()
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
