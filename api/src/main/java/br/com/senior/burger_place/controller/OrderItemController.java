package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.orderItem.OrderItemConverter;
import br.com.senior.burger_place.domain.orderItem.OrderItemService;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import br.com.senior.burger_place.domain.orderItem.dto.OrderItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order-items")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderItemConverter orderItemConverter;

    @GetMapping
    public ResponseEntity<Page<OrderItemDTO>> listByStatus(
            @PageableDefault(size = 5)
            Pageable pageable,
            @RequestParam(name = "status", required = true)
            OrderItemStatus status
    ) {
        return ResponseEntity.ok(
                this.orderItemService.listOrderItems(pageable, status).map(this.orderItemConverter::toOrderItemsDTO)
        );
    }
}
