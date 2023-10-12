package org.jsweet.transpiler;

import java.lang.ref.Cleaner;

final class CleanerProvider {
  private static final Cleaner CLEANER = Cleaner.create();

  public static Cleaner getCleaner() {
    return CLEANER;
  }
}
