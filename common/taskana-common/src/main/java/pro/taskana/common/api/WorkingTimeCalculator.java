package pro.taskana.common.api;

import java.time.Duration;
import java.time.Instant;

public interface WorkingTimeCalculator {

  /**
   * Subtracts <code>workingTime</code> from <code>instant</code>. Respects the configured working
   * time schedule and Holidays.
   *
   * <p>E.g can be used for planned date calculation.
   *
   * @param workStart The Instant <code>workingTime</code> is subtracted from.
   * @param workingTime The Duration to subtract from <code>instant</code>. May have any resolution
   *     Duration supports, e.g. minutes or seconds.
   * @return A new Instant which represents the subtraction of working time (rounded to
   *     milliseconds).
   * @throws IllegalArgumentException If <code>workingTime</code> is negative.
   */
  Instant subtractWorkingTime(Instant workStart, Duration workingTime)
      throws IllegalArgumentException;

  /**
   * Adds <code>workingTime</code> from <code>workStart</code> Respects the configured working time
   * schedule and Holidays.
   *
   * <p>E.g can be used for due date calculation.
   *
   * @param workStart The Instant <code>workingTime</code> is added to.
   * @param workingTime The Duration to add to <code>workStart</code>. May have any resolution *
   *     Duration supports, e.g. minutes or seconds.
   * @return A new Instant which represents the addition of working time (rounded to milliseconds).
   * @throws IllegalArgumentException If <code>workingTime</code> is negative.
   */
  Instant addWorkingTime(Instant workStart, Duration workingTime) throws IllegalArgumentException;

  /**
   * Calculates the working time between <code>from</code> and <code>to</code> according to the
   * configured working time schedule. The returned Duration is precise to microseconds.
   *
   * @param from The Instant which denotes the beginn of the considered time frame. May not be
   *     <code>null</code>.
   * @param to The Instant which denotes the end of the considered time frame. May not be <code>null
   *             </code>.
   * @return The Duration representing the working time between <code>from</code> and <code>to
   *     </code>.
   * @throws IllegalArgumentException If either <code>from</code> or <code>to</code> is <code>null
   *                                  </code>. If <code>from</code> is after <code>to</code>.
   */
  Duration workingTimeBetween(Instant from, Instant to) throws IllegalArgumentException;

  /**
   * Decides whether there is any working time between <code>first</code> and <code>second</code>.
   *
   * <p><code>first</code> may be after <code>second</code>.
   *
   * @param first The first Instant to check. May not be <code>null</code>.
   * @param second The second Instant to check. May not be <code>null</code>.
   * @return <code>true</code> if there is working time between <code>first</code> and <code>second
   *     </code>. <code>false</code> otherwise.
   */
  boolean isWorkingTimeBetween(Instant first, Instant second);

  /**
   * Decides whether <code>instant</code> is a working day.
   *
   * @param instant The Instant to check. May not be <code>null</code>.
   * @return <code>true</code> if <code>instant</code> is a working day. <code>false</code>
   *     otherwise.
   */
  boolean isWorkingDay(Instant instant);

  /**
   * Decides whether <code>instant</code> is a weekend day.
   *
   * @param instant The Instant to check. May not be <code>null</code>.
   * @return <code>true</code> if <code>instant</code> is a weekend day. <code>false</code>
   *     otherwise.
   */
  boolean isWeekend(Instant instant);

  /**
   * Decides whether <code>instant</code> is a holiday.
   *
   * @param instant The Instant to check. May not be <code>null</code>.
   * @return <code>true</code> if <code>instant</code> is a holiday. <code>false</code> otherwise.
   */
  boolean isHoliday(Instant instant);

  /**
   * Decides whether <code>instant</code> is a holiday in Germany.
   *
   * @param instant The Instant to check. May not be <code>null</code>.
   * @return <code>true</code> if <code>instant</code> is a holiday in Germany. <code>false</code>
   *     otherwise.
   */
  boolean isGermanHoliday(Instant instant);
}
