package edu.cs449.solitaire;

/** Player modes required by Sprint 3. */
public enum GameMode {
  MANUAL("Manual"),
  AUTOMATED("Automated");

  private final String displayName;

  GameMode(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
