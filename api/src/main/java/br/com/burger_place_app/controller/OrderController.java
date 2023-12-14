package br.com.burger_place_app.controller;

import br.com.burger_place_app.domain.order.OrderService;
import br.com.burger_place_app.domain.order.OrderStatus;
import br.com.burger_place_app.domain.order.dto.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity listOrders(
            Pageable pageable,
            @RequestParam(name = "status", required = false) OrderStatus orderStatus
    ) {
        return ResponseEntity.ok(this.orderService.getOrders(pageable, orderStatus));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity showOrder(
            @PathVariable
            Long orderId
    ) {
        Optional<OrderData> orderOptional = this.orderService.showOrder(orderId);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(orderOptional.get());
    }

    @PostMapping
    @Transactional
    public ResponseEntity createOrder(
            @RequestBody
            @Valid
            CreateOrderData orderData,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        OrderData order = this.orderService.createOrder(orderData);

        URI uri = uriComponentsBuilder
                .path("/orders/{orderId}")
                .buildAndExpand(order.id())
                .toUri();

        return ResponseEntity.created(uri).body(order);
    }

    @PostMapping("/{orderId}/items")
    @Transactional
    public ResponseEntity addOrderItems(
            @PathVariable
            Long orderId,
            @RequestBody
            @Valid
            AddNewOrderItemsData orderItemData
    ) {
        this.orderService.addOrderItemToOrder(orderId, orderItemData);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}/items")
    @Transactional
    public ResponseEntity removeOrderItems(
            @PathVariable
            Long orderId,
            @RequestBody
            @Valid
            RemoveOrderItemsData orderItemData
    ) {
        this.orderService.removeOrderItems(orderId, orderItemData);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    @Transactional
    public ResponseEntity removeOrderItems(
            @PathVariable
            Long orderId,
            @PathVariable
            Long itemId
    ) {
        this.orderService.removeOrderItemFromOrder(orderId, itemId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}/items/{itemId}")
    @Transactional
    public ResponseEntity updateOrder(
            @PathVariable
            Long orderId,
            @PathVariable
            Long itemId,
            @RequestBody
            UpdateOrderItemData orderItemData
    ) {
        this.orderService.updateOrderItem(orderId, itemId, orderItemData);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}")
    @Transactional
    public ResponseEntity inactivateOrder(
            @PathVariable
            Long orderId
    ) {
        this.orderService.inactivateOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}
