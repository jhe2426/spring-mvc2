package hello.login.web.argumentresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 이 애노테이션을 어디에 붙일  수 있는지 제한을 설정, ElementType.PARAMETER: 메서드 파라미터에서만 사용 가능
@Retention(RetentionPolicy.RUNTIME) // 애노테이션이 언제까지 유지되는지 설정
public @interface Login {
}
