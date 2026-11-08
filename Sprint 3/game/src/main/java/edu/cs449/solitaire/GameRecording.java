package edu.cs449.solitaire;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import edu.cs449.solitaire.SolitaireGame.State;

/** Human-readable text recording containing every visible board state. */
public final class GameRecording {
  private static final String HEADER = "PEG_SOLITAIRE_BRAINVITA_RECORDING_V1";

  public record Frame(String action, State state) {
    public Frame {
      Objects.requireNonNull(action);
      Objects.requireNonNull(state);
      if (action.isBlank() || action.contains("|") || action.contains("\n")) {
        throw new IllegalArgumentException("Recording action must be one line without '|'.");
      }
    }
  }

  private final String shapeName;
  private final int size;
  private final GameMode mode;
  private final List<Frame> frames = new ArrayList<>();

  public GameRecording(BoardShape shape, int size, GameMode mode, SolitaireGame initialGame) {
    this(shape.toString(), size, mode);
    addFrame("START", initialGame);
  }

  private GameRecording(String shapeName, int size, GameMode mode) {
    this.shapeName = Objects.requireNonNull(shapeName);
    this.mode = Objects.requireNonNull(mode);
    BoardShape.fromName(shapeName);
    if (size != 7 && size != 9) {
      throw new IllegalArgumentException("Recorded board size must be 7 or 9.");
    }
    this.size = size;
  }

  public String shapeName() {
    return shapeName;
  }

  public int size() {
    return size;
  }

  public GameMode mode() {
    return mode;
  }

  public List<Frame> frames() {
    return List.copyOf(frames);
  }

  public void addFrame(String action, SolitaireGame game) {
    Objects.requireNonNull(game);
    if (game.size() != size || !game.shape().toString().equals(shapeName)) {
      throw new IllegalArgumentException("Recorded frame does not match the selected board.");
    }
    frames.add(new Frame(action, game.state()));
  }

  public SolitaireGame gameAt(int frameIndex) {
    Frame frame = frames.get(frameIndex);
    SolitaireGame replay = new SolitaireGame(BoardShape.fromName(shapeName), size);
    replay.restore(frame.state());
    return replay;
  }

  public void save(Path path) throws IOException {
    Objects.requireNonNull(path);
    List<String> lines = new ArrayList<>();
    lines.add(HEADER);
    lines.add("shape=" + shapeName);
    lines.add("size=" + size);
    lines.add("mode=" + mode.name());
    for (Frame frame : frames) {
      lines.add("FRAME|" + frame.action() + "|" + frame.state().moveCount() + "|"
          + String.join("/", frame.state().rows()));
    }
    Files.write(path, lines, StandardCharsets.UTF_8);
  }

  public static GameRecording load(Path path) throws IOException {
    Objects.requireNonNull(path);
    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
    if (lines.size() < 5 || !HEADER.equals(lines.get(0))) {
      throw new IOException("This is not a Sprint 3 Peg Solitaire recording.");
    }
    try {
      String shape = value(lines.get(1), "shape=");
      int size = Integer.parseInt(value(lines.get(2), "size="));
      GameMode mode = GameMode.valueOf(value(lines.get(3), "mode="));
      GameRecording recording = new GameRecording(shape, size, mode);
      for (int index = 4; index < lines.size(); index++) {
        String[] parts = lines.get(index).split("\\|", 4);
        if (parts.length != 4 || !"FRAME".equals(parts[0])) {
          throw new IllegalArgumentException("Invalid frame line.");
        }
        int moves = Integer.parseInt(parts[2]);
        List<String> rows = List.of(parts[3].split("/", -1));
        State state = new State(rows, moves);
        SolitaireGame validation = new SolitaireGame(BoardShape.fromName(shape), size);
        validation.restore(state);
        recording.frames.add(new Frame(parts[1], state));
      }
      if (recording.frames.isEmpty()) {
        throw new IllegalArgumentException("Recording contains no frames.");
      }
      return recording;
    } catch (IllegalArgumentException exception) {
      throw new IOException("Invalid Peg Solitaire recording: " + exception.getMessage(), exception);
    }
  }

  private static String value(String line, String prefix) {
    if (!line.startsWith(prefix) || line.length() == prefix.length()) {
      throw new IllegalArgumentException("Missing " + prefix.substring(0, prefix.length() - 1));
    }
    return line.substring(prefix.length());
  }
}
