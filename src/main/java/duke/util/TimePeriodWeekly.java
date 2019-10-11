package duke.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimePeriodWeekly implements TimePeriod {

    private LocalTime begin;
    private LocalTime end;
    private boolean isUntilNextDay;
    private DayOfWeek dayOfWeek;

    /**
     * Constructor for TimePeriodWeekly.
     * @param begin Start time.
     * @param end End time.
     * @param dayOfWeek Day of week where this period applies.
     */
    public TimePeriodWeekly(LocalTime begin, LocalTime end, DayOfWeek dayOfWeek) {
        this.initialize(begin, end, dayOfWeek);
    }

    /**
     * Constructor for TimePeriodWeekly.
     * @param begin Start time.
     * @param duration Duration of the period.
     * @param dayOfWeek Day of week where this period applies.
     */
    public TimePeriodWeekly(LocalTime begin, TimeInterval duration, DayOfWeek dayOfWeek) {
        this.initialize(begin, duration, dayOfWeek);
    }

    /**
     * Constructor for TimePeriodWeekly
     * @param begin Start time.
     * @param isInstantEnd Ends immediately or not.
     * @param dayOfWeek Day of week where this period applies.
     */
    public TimePeriodWeekly(LocalTime begin, boolean isInstantEnd, DayOfWeek dayOfWeek) {
        this(begin, (LocalTime) null, dayOfWeek);
        if (isInstantEnd) {
            this.setEnd(this.begin);
        }
    }

    public TimePeriodWeekly(LocalTime begin, DayOfWeek dayOfWeek) {
        this(begin, true, dayOfWeek);
    }

    public TimePeriodWeekly(DayOfWeek dayOfWeek) {
        this(null, (LocalTime) null, dayOfWeek);
    }

    /**
     * Checker function for clashing time periods.
     * @param localTime Given LocalTime.
     * @param strictBegin Strict clashing for begin or not (begin matches ends).
     * @param strictEnd Strict clashing for end or not (begin matches ends).
     * @param dayOfWeek Given day of week.
     * @return Boolean result if the period clash.
     */
    public boolean isClashing(LocalTime localTime, boolean strictBegin, boolean strictEnd, DayOfWeek dayOfWeek) {
        if (localTime == null) {
            return false;
        }
        boolean beforeBegin = localTime.isBefore(this.begin);
        boolean afterBegin = localTime.isAfter(this.begin);
        boolean beforeEnd = localTime.isBefore(this.end);
        boolean afterEnd = localTime.isAfter(this.end);
        return !this.isUntilNextDay && afterBegin && beforeEnd
                || this.isUntilNextDay
                    && ((dayOfWeek == null || this.dayOfWeek.equals(dayOfWeek)) && afterBegin && afterEnd
                        || (dayOfWeek == null || this.dayOfWeek.plus(1).equals(dayOfWeek)) && beforeBegin && beforeEnd)
                || strictBegin && localTime.equals(this.begin)
                || strictEnd && localTime.equals(this.end);
    }

    public boolean isClashing(LocalDateTime localDateTime, boolean strictBegin, boolean strictEnd) {
        return localDateTime != null
                && this.isClashing(localDateTime.toLocalTime(), strictBegin, strictEnd, localDateTime.getDayOfWeek());
    }

    public boolean isClashing(LocalTime localTime, boolean strictBegin, boolean strictEnd, LocalDate localDate) {
        return this.isClashing(localTime, strictBegin, strictEnd, localDate.getDayOfWeek());
    }

    public boolean isClashing(LocalDateTime localDateTime) {
        return this.isClashing(localDateTime, false, false);
    }

    public boolean isClashing(LocalTime localTime, DayOfWeek dayOfWeek) {
        return this.isClashing(localTime, false, false, dayOfWeek);
    }

    public boolean isClashing(LocalTime localTime) {
        return this.isClashing(localTime, false, false, (DayOfWeek) null);
    }

    public boolean isClashing(LocalDate localDate) {
        return this.isClashing(new TimePeriodWeekly(LocalTime.MIN, LocalTime.MAX, localDate.getDayOfWeek()));
    }

    public boolean isClashing(LocalDateTime begin, LocalDateTime end) {
        return this.isClashing(begin) || this.isClashing(end);
    }

    public boolean isClashing(LocalTime begin, LocalTime end) {
        return this.isClashing(begin) || this.isClashing(end);
    }

    public boolean isClashing(TimePeriodSpanning other) {
        if (other == null) {
            return false;
        }
        long days = other.getInterval().toDuration().toDays();
        if (days > 6) {
            return true;
        }
        else if (days == 0) {
            return this.isClashing(
                    new TimePeriodWeekly(
                            other.getBegin().get().toLocalTime(),
                            other.getEnd().get().toLocalTime(),
                            other.getBegin().get().getDayOfWeek()));
        }
        LocalDateTime otherBeginEndOfDay = LocalDateTime.of(other.getBegin().get().toLocalDate(), LocalTime.MAX);
        LocalDateTime otherEndBeginOfDay = LocalDateTime.of(other.getBegin().get().toLocalDate(), LocalTime.MIN);
        if (this.isClashing(other.getBegin().get(), otherBeginEndOfDay)
                || this.isClashing(otherEndBeginOfDay, other.getEnd().get())) {
            return true;
        }
        LocalDate begin = other.getBegin().get().toLocalDate().plusDays(1);
        LocalDate end = other.getEnd().get().toLocalDate();
        for (; begin != end; begin = begin.plusDays(1)) {
            if (this.isClashing(begin)) {
                return true;
            }
        }
        return false;
    }

    public boolean isClashing(TimePeriodWeekly other) {
        return other != null
                && (this.isClashing(other.begin, other.dayOfWeek)
                    || this.isClashing(other.end, other.dayOfWeek)
                    || other.isClashing(this.begin, this.dayOfWeek)
                    || other.isClashing(this.end, this.dayOfWeek));
    }
    
    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setBegin(LocalTime begin) {
        this.setPeriod(begin, this.end);
    }

    public void setEnd(LocalTime end) {
        this.setPeriod(this.begin, end);
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Set period for this object.
     * @param begin Start time.
     * @param end End time.
     */
    public void setPeriod(LocalTime begin, LocalTime end) {
        if (begin != null && end != null && end.isBefore(begin)) {
            this.isUntilNextDay = true;
        }
        else {
            this.isUntilNextDay = false;
        }
        this.begin = begin;
        this.end = end;
    }

    public void setPeriod(LocalTime begin, TimeInterval duration) {
        this.setPeriod(begin, begin.plus(duration.getTimeDuration()));
    }

    /**
     * Initialize attributes.
     * @param begin Start time.
     * @param end End time.
     * @param dayOfWeek Day of week where this period applies.
     */
    public void initialize(LocalTime begin, LocalTime end, DayOfWeek dayOfWeek) {
        this.setPeriod(begin, end);
        this.setDayOfWeek(dayOfWeek);
    }

    public void initialize(LocalTime begin, TimeInterval duration, DayOfWeek dayOfWeek) {
        this.setPeriod(begin, duration);
        this.setDayOfWeek(dayOfWeek);
    }

    @Override
    public TimeInterval getInterval() {
        return TimeInterval.between(this.begin, this.end);
    }

    @Override
    public String toString() {
        return this.begin + " - " + this.end + " on " + this.dayOfWeek;
    }

    @Override
    public LocalTime getBeginTime() {
        return this.begin;
    }

    @Override
    public LocalTime getEndTime() {
        return this.end;
    }

    @Override
    public DateTime<LocalTime> getBegin() {
        return new DateTime<>(this.begin);
    }

    @Override
    public DateTime<LocalTime> getEnd() {
        return new DateTime<>(this.end);
    }

    @Override
    public List<DayOfWeek> getDaysOfWeek() {
        return new ArrayList<>(Collections.singleton(this.dayOfWeek));
    }

    // TODO: Combine the isClashing of TimePeriods
    @Override
    public boolean isClashing(TimePeriod other) {
        if (other instanceof TimePeriodSpanning) {
            return this.isClashing((TimePeriodSpanning) other);
        }
        else if (other instanceof  TimePeriodWeekly) {
            return this.isClashing((TimePeriodWeekly) other);
        }
        return false;
    }
}
