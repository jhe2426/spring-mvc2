package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(
            /*
                @ModelAttribute에 바인딩 시 타입 오류가 발생하면?
                    'BindingResult'가 없으면 -> 400 오류가 발생하면서 컨트롤러가 호출되지 않고, 오류 페이지로 이동
                    'BindingResult'가 있으면 -> 오류 정보('FieldError를 생성해서)를
                        'BindingResult'에 담아서 컨트롤러를 정상 호출한다.
            */
            // 주의 BindingResult bindingResult  파라미터의 위치는 @ModelAttribute Item item 다음에 와야 한다.
                // bindingResult는 검증 결과의 대상이 되는 객체가 필요한데 bindingResult의 바로 앞에 위치한 @ModelAttribute 객체를
                // 검증 결과로 인식하기 때문이다.
                // 아래와 같이 매개변수 순서로 작성을 하게 되면 자동적으로 bindingResult는 검증의 대상으로 Item을 바라보게 됨
            @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model
    ) {

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            // 위의 매개변수 순서만 @ModelAttribute가 BindingResult에 앞에 존재한다면 해당 객체를 바인딩한다는데
            // 왜 아래와 같이 필드를 지정할 때 객체 이름또한 왜 작성해야 할까?
                // 이 해당 메서드에서는 매개변수를 통해서 어떤 객체가 바인딩 되어있는지를 알 수 있지만, 뷰에도 아래의 적은 값만을 넘겨서 에러 메시지를 사용하는데
                // 이때 bindingResult.addError(new FieldError("itemName", "상품 이름은 필수입니다.")); 이렇게만 어떤 객체의 필드인지는 이름을 넘겨주지
                // 않는다면 뷰에서는 도대체 itemName 필드가 어떤 객체의 필드인지를 전혀 알 수 없기 때문에 꼭 필드의 객체 이름또한 꼭 작성을 하는 것임
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            // bindingResult는 뷰로 자동으로 넘어가기 때문에 errors처럼 모델에 값을 넣어주지 않아도 됨
            // model.addAttribute("errors", errors);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV2(
            /*
                사용자의 입력 데이터가 컨트롤러의 @ModelAttribute에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자 입력 값을 유지하기 어렵다.
                예를 들어서 가격에 숫자가 아닌 문자가 입력된다면 가격은 Integer 타입이므로 문자를 보관할 수 있는 방법이 없다.
                그래서 화면에 표시하기가 어렵기 때문에 오류가 발생한 경우 사용자 입력 값을 보관하는 별도의 방법이 필요함
                그래서 FieldError는 오류 발생 시 사용자 입력 값을 저장하는 기능을 제공함
                만약 가격에 숫자가 아닌 qqq를 입력하게 되면 일단 Item 객체를 new Item() 기본 생성자로 생성을 한 후 필드를 바인딩 하는 과정에서 타입 에러가 발생하는데 이를
                bindingResult에서 매핑할 때 price에 타입 에러가 발생한 것을 감지하여 아래와 같은 FieldError 자동으로 생성해서 bindingResult에 추가를 해준다.
                또한 타입 에러가 발생하더라도 예외가 발생하지 않고, 해당 오류는 BindingResult에 저장되기 때문에 컨트롤러 내부에 작성된 코드는 정상적으로 실행된다.
                bindingResult.addError(new FieldError("item", "price", "qqq", true, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
                rejectedValue 파라미터 부분에 사용자가 입력한 값을 넣어주고, bindingFailure 파라미터 부분에 타입 오류 같은 바인딩 실패인지를 표시하기 위해 true를 넣어준다.

                th:field="*{price}" 여기서 타임리프의 th:field는 정상 상황에서는 모델 객체의 값을 사용하지만,
                오류가 발생하면 FieldError에서 보관한 값을 사용해서 값을 출력해줌
            */
            @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model
    ) {

        // 검증 로직
        /*
            FieldError는 두가지 생성자가 존재
            1. public FieldError(String objectName, String field, String defaultMessage);
            2. public FieldError(String objectName, String field, @Nullable Object
                rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage)
                    objectName: 오류가 발생한 객체 이름
                    field: 오류가 발생한 필드
                    rejectedValue: 사용자가 입력한 값(사용자가 정상적으로 입력하지 않아 거절된 값)
                    bindingFailure: 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값(true: 타입 오류 같은 바인딩 실패, false: 검증 실패)
                    codes: 메시지 코드
                    arguments: 메시지에서 사용하는 인자
                    defaultMessage: 기본 오류 메시지
        */
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            /*
                왜 V1 코드에서는 이 구문을 실행하면 왜 사용자가 입력한 값이 안 보였나?
                bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
                V1일 때 검증 실패가 일어나면 위와 같은 코드가 살행되게 되는데 위의 코드에는 rejectedValue 즉, 사용자가 입력한 값을 지정해주는
                매개변수에 값을 넣지 않고 있고, 타임리프는 에러가 발생하면 모델에서 전달한 객체 필드의 값을 출력해주는 것이 아닌
                FieldError로 생성된 rejectedValue 값을 출력해주게 되는데 rejectedValue 값이 비워있으므로 V1일 때 검증 에러가 발생하면
                해당 입력 값은 화면에 보여지지 않게 된 것이다. 타입 오류는 V1도 화면에 출력이 되었는데 이것은 bindingResult가 자동적으로
                생성해주는 FieldError를 사용했기 때문이다.
            */
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

