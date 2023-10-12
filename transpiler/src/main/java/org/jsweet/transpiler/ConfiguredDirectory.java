package org.jsweet.transpiler;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Cleaner.Cleanable;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * A wrapper for directories that are either simple paths, or temporary ones
 * that should be removed whenever possible.
 * 
 * @author Christian Kohlschütter
 */
public class ConfiguredDirectory implements Closeable {
  private static final String TEMPORARY_PREFIX_DEFAULT = "javatmp";
  private Path path;

  public static final ConfiguredDirectory ofDir(Path p) throws IOException {
    if (p == null) {
      return ofTemporaryDir();
    }
    return new ConfiguredDirectory(p);
  }

  public static final ConfiguredDirectory ofDir(File f) throws IOException {
    if (f == null) {
      return ofTemporaryDir();
    }
    return ofDir(f.toPath());
  }

  public static ConfiguredDirectory ofOrTemporaryDir(ConfiguredDirectory cd) throws IOException {
    return cd == null ? ofTemporaryDir() : cd;
  }

  public static ConfiguredDirectory ofOrTemporaryDir(ConfiguredDirectory cd, String prefix) throws IOException {
    return cd == null ? ofTemporaryDir(prefix) : cd;
  }

  public static ConfiguredDirectory ofTemporaryDir() throws IOException {
    return ofTemporaryDir(TEMPORARY_PREFIX_DEFAULT);
  }

  public static final ConfiguredDirectory ofDirOrNull(Path p) throws IOException {
    if (p == null) {
      return null;
    }
    return new ConfiguredDirectory(p);
  }

  public static final ConfiguredDirectory ofDirOrNull(File f) throws IOException {
    if (f == null) {
      return null;
    }
    return ofDir(f.toPath());
  }

  public static final ConfiguredDirectory ofTemporaryDir(Path p) throws IOException {
    p.toFile().deleteOnExit();
    return new Temporary(p);
  }

  public static final ConfiguredDirectory ofTemporaryDir(File f) throws IOException {
    return ofTemporaryDir(f.toPath());
  }

  public static final ConfiguredDirectory ofTemporaryDir(String prefix) throws IOException {
    return ofTemporaryDir(Files.createTempDirectory(prefix));
  }

  protected ConfiguredDirectory(Path p) {
    this.path = p;
  }

  public Path getPath() {
    return path;
  }

  static final class Temporary extends ConfiguredDirectory {
    private final Cleanable cleanable;

    private Temporary(Path p) throws IOException {
      super(p);

      CleanAction ca = new CleanAction(p);
      this.cleanable = CleanerProvider.getCleaner().register(this, ca);
      ca.setShutdownHook(cleanable::clean);
    }

    @Override
    public void close() throws IOException {
      cleanable.clean();
    }
  }

  @Override
  public void close() throws IOException {
  }

  @Override
  public String toString() {
    return super.toString() + "[path=" + getPath() + "]";
  }

  private static final class CleanAction implements Runnable {
    private static final FileVisitor<Path> VISITOR = new FileVisitor<>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.deleteIfExists(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.deleteIfExists(dir);
        return FileVisitResult.CONTINUE;
      }
    };
    private final Path path;
    private Thread shutdownHook;

    CleanAction(Path p) {
      this.path = Objects.requireNonNull(p);
    }

    void setShutdownHook(Runnable runnable) {
      this.shutdownHook = new Thread(runnable);
      Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    @Override
    public void run() {
      try {
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
      } catch (RuntimeException ignore) {
        // shutdown in progress
      }
      try {
        Files.walkFileTree(Objects.requireNonNull(path), VISITOR);
      } catch (RuntimeException | IOException ignore) {
        // nothing we can do
      }
    }
  }
}
