package hello.typeconverter.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Slf4j
/*
    포맷터(Formatter)는 객체를 문자로 변경하고, 문자를 객체로 변경하는 두 가지 기능을 모두 수행
     - String print(T object, Locale locale): 객체를 문자로 변경
     - T parse(String text, Locale locale): 문자를 객체로 변경
*/
public class MyNumberFormatter implements Formatter<Number> {

    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("text={}, locale={}", text, locale);
        // "1,000" -> 1000
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(text);
    }

    @Override
    public String print(Number object, Locale locale) {
        log.info("object={}, locale={}", object, locale);
        return NumberFormat.getInstance().format(object);
    }
}
