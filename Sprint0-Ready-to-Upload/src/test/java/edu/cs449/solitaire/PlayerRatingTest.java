package edu.cs449.solitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PlayerRatingTest {
  private final PlayerRating rating = new PlayerRating();

  @Test
  void onePegReceivesOutstanding() {
    assertEquals("Outstanding", rating.rate(1));
  }

  @Test
  void twoPegsReceiveVeryGood() {
    assertEquals("Very Good", rating.rate(2));
  }

  @Test
  void threePegsReceiveGood() {
    assertEquals("Good", rating.rate(3));
  }

  @Test
  void fourOrMorePegsReceiveAverage() {
    assertEquals("Average", rating.rate(4));
    assertEquals("Average", rating.rate(32));
  }

  @Test
  void zeroPegsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> rating.rate(0));
  }

  @Test
  void negativePegsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> rating.rate(-1));
  }
}
