package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.*;

public class MessageCodesResolverTest {

    /*
        MessageCodesResolver: 검증 오류 코드로 메시지 코드를을 생성
            MessageCodesResolver는 인터페이스이고 DefaultMessageCodesResolver는 기본 구현체이다.
            주로 ObjectError, FieldError와 함께 사용함,
            bindingResult.rejectValue()를 사용하면 내부적으로 MessageCodesResolver가 메시지 코드를 생성을 해서 rejectValue는 FieldError의
            생성자의 매개변수 codes 자리에 MessageCodesResolver로 생성된 메시지 코드 배열을 넣어서 생성을 해준다.
            reject는 ObjectError의 생성자의 매개변수 codes자리에 MessageCodesResolver로 생성된 메시지 코드 배열을 넣어서 생성을 해준다.
            DefaultMessageCodesResolver의 기본 메시지 생성 규칙
                - 객체 오류의 경우 다음 순서로 2가지 생성
                    1.: code + "." + object name
                    2.: code
                    ex) 오류 코드: required, object name: item
                       1.: required.item
                       2.: required

                - 필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
                    1.: code + "." + object name + "." + field
                    2.: code + "." + field
                    3.: code + "." + field type
                    4.: code
                    ex) 오류 코드: typeMismatch, object name: user, field: age, field type: int
                    1.: "typeMismatch.user.age"
                    2.: "typeMismatch.age"
                    3.: "typeMismatch.int"
                    4.: "typeMismatch"

    */
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        /*
            bindingResult.rejectValue("itemName", "required");
            위의 코드를 실행하면 내부적으로 codesResolver를 호출하여 messageCodes를 뽑아오고 new FieldError()를 만든다.
            이때 messageCodes의 값은 required.item.itemName, required.itemName, required.java.lang.String,
            required 들이 순서대로 배열로 만들어지고 이 배열을 codes 매개변수 자리에 자동으로 아래와 같이 넣어서 FieldError를 생성을 해준다.
            new FieldError()를 만들 때 new FieldError("item", "itemName", null, false, messageCodes, null, null, null);
            이때 messageCodes 배열의 순서는 상세하게 작성된 우선순위가 높은 에러 코드순서로 작성된다.
        */
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
