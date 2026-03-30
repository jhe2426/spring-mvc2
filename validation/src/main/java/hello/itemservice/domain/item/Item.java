package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
/*
    오브젝트 오류(글로벌 오류)의 경우에는 @ScriptAssert를 억지로 사용하는 것보다는 컨트롤러나 서비스에서 오브젝트 오류 관련 부부만 직접 자바 코드로 작성하는 것을
    권장
        왜냐하면 실제로 서비스에서 이 해당 클래스 내부의 필드의 조합으로만 검증을 하는 것이 아닌 다른 객체의 필드 조합으로도 검증이 필요한데
        이렇게 하나의 객체에만 한정해서 검증을 하려고하니 대응이 어렵기 때문이다.
*/
// @ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총합이 10000원 넘게 입력해주세요. ")
public class Item {

    @NotNull(groups = UpdateCheck.class)
    private Long id;

    @NotBlank(message = "공백X", groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9999, groups = {SaveCheck.class})
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
