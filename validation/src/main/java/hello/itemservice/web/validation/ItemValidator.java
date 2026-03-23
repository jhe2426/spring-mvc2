package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        /*
            A.isAssignableFrom(B): B 타입 객체를 A 타입 변수에 넣을 수 있는지의 결과를 반환해주는 메서드
            해당 메서드는 B가 A와 같거나 A의 자식 클래스인 B가 들어와도 true를 반환해준다.
        */
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }


        if ((item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) && !errors.hasFieldErrors("price")) {
            errors.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // reject() 메서드는 ObjectError를 생성해주는 코드가 내부에 존재함
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
