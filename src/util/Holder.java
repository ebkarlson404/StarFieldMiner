package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for defining "final" variables that can used to capture values from inside a lambda
 *
 * @param <T> The type of value to be captured
 * @author Eric Karlson
 */
public class Holder<T> {
  private T value;

  public Holder() {
    this.value = null;
  }

  public Holder(T value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return (null == value) ? null : value.toString();
  }

  public void set(@Nullable T value) {
    this.value = value;
  }

  public @Nullable T get() {
    return value;
  }

  public @NotNull T getWithDefault(@NotNull T dflt) {
    return (null != value) ? value : dflt;
  }
}
