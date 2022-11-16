package pro.taskana.common.api;

import java.time.LocalTime;
import java.util.Objects;

/**
 * LocalTimeInterval provides a closed interval using {@link LocalTime}.
 *
 * <p>That means both begin and end must not be <code>null</code>.
 *
 * <p>Note: this class has a natural ordering that is inconsistent with equals.
 */
public class LocalTimeInterval implements Comparable<LocalTimeInterval> {

  private final LocalTime begin;
  private final LocalTime end;

  public LocalTimeInterval(LocalTime begin, LocalTime end) {
    this.begin = Objects.requireNonNull(begin);
    this.end = Objects.requireNonNull(end);
  }

  public LocalTime getBegin() {
    return begin;
  }

  public LocalTime getEnd() {
    return end;
  }

  /**
   * Compares two LocalTimeInterval objects in regard to their {@link #getBegin() begin}.
   *
   * @param o the LocalTimeInterval to be compared.
   * @return a negative value if <code>o</code> begins before <code>this</code>, 0 if both have the
   *     same begin and a positive value if <code>o</code> begins after <code>this</code>.
   */
  @Override
  public int compareTo(LocalTimeInterval o) {
    return begin.compareTo(o.getBegin());
  }

  @Override
  public String toString() {
    return "LocalTimeInterval [begin=" + begin + ", end=" + end + "]";
  }
}
