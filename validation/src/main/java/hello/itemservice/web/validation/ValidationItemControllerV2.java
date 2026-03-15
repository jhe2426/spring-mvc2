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

    @PostMapping("/add")
    public String addItemV1(
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

