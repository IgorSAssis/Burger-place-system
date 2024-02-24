package br.com.senior.burger_place.domain.orderItem;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public Page<OrderItem> listOrderItems(
            Pageable pageable,
            @NonNull
            OrderItemStatus status
    ) {
        return this.orderItemRepository.findAllByActiveTrueAndStatusEquals(pageable, status);
    }
}
