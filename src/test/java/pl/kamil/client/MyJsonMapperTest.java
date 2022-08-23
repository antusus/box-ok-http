package pl.kamil.client;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import static java.util.Calendar.APRIL;
import static org.assertj.core.api.Assertions.assertThat;

class MyJsonMapperTest {
  private static final MyJsonMapper mapper = MyJsonMapper.getInstance();

  @Nested
  class DeserializeDate {
    @Test
    void dateWithTimeZone() {
      var withDate = Map.of("date", "2019-04-06T15:57:01-07:00");
      var jsonString = mapper.toJsonString(withDate);
      var deserialized = mapper.deserialize(jsonString, ObjectWithDate.class);

      var expectedDate = dateWithTime(2019, APRIL, 6, 15, 57, 1, TimeZone.getTimeZone("GMT-7:00"));
      assertThat(deserialized.getDate()).isCloseTo(expectedDate, 0L);
    }

    @Test
    void dateWithGMTTimeZone() {
      var withDate = Map.of("date", "2019-04-06T15:57:01Z");
      var jsonString = mapper.toJsonString(withDate);
      var deserialized = mapper.deserialize(jsonString, ObjectWithDate.class);

      var expectedDate = dateWithTime(2019, APRIL, 6, 15, 57, 1, TimeZone.getTimeZone("GMT"));
      assertThat(deserialized.getDate()).isCloseTo(expectedDate, 0L);
    }

    @Test
    void dateWithPlusZeroTimeZone() {
      var withDate = Map.of("date", "2019-04-06T15:57:01+00:00");
      var jsonString = mapper.toJsonString(withDate);
      var deserialized = mapper.deserialize(jsonString, ObjectWithDate.class);

      var expectedDate = dateWithTime(2019, APRIL, 6, 15, 57, 1, TimeZone.getTimeZone("GMT"));
      assertThat(deserialized.getDate()).isCloseTo(expectedDate, 0L);
    }

    @Test
    void dateWithMinusZeroTimeZone() {
      var withDate = Map.of("date", "2019-04-06T15:57:01-00:00");
      var jsonString = mapper.toJsonString(withDate);
      var deserialized = mapper.deserialize(jsonString, ObjectWithDate.class);

      var expectedDate = dateWithTime(2019, APRIL, 6, 15, 57, 1, TimeZone.getTimeZone("GMT"));
      assertThat(deserialized.getDate()).isCloseTo(expectedDate, 0L);
    }

    @Test
    void dateWithRFC822ZeroOffsetTimezone() {
      var withDate = Map.of("date", "2019-04-06T15:57:01+0000");
      var jsonString = mapper.toJsonString(withDate);
      var deserialized = mapper.deserialize(jsonString, ObjectWithDate.class);

      var expectedDate = dateWithTime(2019, APRIL, 6, 15, 57, 1, TimeZone.getTimeZone("GMT"));
      assertThat(deserialized.getDate()).isCloseTo(expectedDate, 0L);
    }

    @Test
    void dateWithoutTime() {
      var withDate = Map.of("date", "2019-04-06");
      var jsonString = mapper.toJsonString(withDate);
      var deserialized = mapper.deserialize(jsonString, ObjectWithDate.class);

      var expectedDate = dateOnly(2019, APRIL, 6);
      assertThat(deserialized.getDate()).isCloseTo(expectedDate, 0L);
    }

    @Test
    void deserializeWithCustomDeserializer() {
      mapper.registerDeserializer(Date.class, new MyDateDeserializer(), "MyDateDeserializer");
      var withDate = Map.of("date", "2019-04-06");
      var jsonString = mapper.toJsonString(withDate);
      var deserialized = mapper.deserialize(jsonString, ObjectWithDate.class);

      // our deserializer always returns current date
      assertThat(deserialized.getDate()).isCloseTo(new Date(), 50L);

      mapper.removeModules();
    }
  }

  @Nested
  class SerializeDate {
    @Test
    void dateWithTimeZone() {
      var calendar = new GregorianCalendar(2019, APRIL, 6, 15, 57, 1);
      calendar.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
      // SerializeFullDate is using date format which is OK when we expect one type of date format
      // in returned data
      var withDate = new SerializeFullDate();
      var date = calendar.getTime();
      withDate.setDate(date);
      assertThat(new String(mapper.serailize(withDate)))
          .isEqualTo("""
                              {"date":"2019-04-06T22:57:01Z"}""");
    }

    @Test
    void dateOnly() {
      var calendar = new GregorianCalendar(2019, APRIL, 6, 15, 57, 1);
      calendar.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
      // SerializeDateOnly is using date format which is OK when we expect one type of date format
      // in returned data
      var withDate = new SerializeDateOnly();
      var date = calendar.getTime();
      withDate.setDate(date);
      assertThat(new String(mapper.serailize(withDate)))
          .isEqualTo("""
                              {"date":"2019-04-06"}""");
    }
  }

  private Date dateWithTime(
      int year,
      int month,
      int dayOfMonth,
      int hourOfDay,
      int minute,
      int second,
      TimeZone timeZone) {
    var calendar = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
    calendar.setTimeZone(timeZone);
    return calendar.getTime();
  }

  private Date dateOnly(int year, int month, int dayOfMonth) {
    var calendar = new GregorianCalendar(year, month, dayOfMonth);
    calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    return calendar.getTime();
  }
}
