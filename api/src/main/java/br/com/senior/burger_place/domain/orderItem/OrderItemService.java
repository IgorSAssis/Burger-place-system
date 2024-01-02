package br.com.senior.burger_place.domain.orderItem;

import br.com.senior.burger_place.domain.orderItem.dto.ListOrderItemsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public Page<ListOrderItemsDTO> listPendingOrderItems(
            Pageable pageable,
            OrderItemStatus status
    ) {
        if (status == null) {
            throw new IllegalArgumentException("Obrigatório informar um status");
        }

        return this.orderItemRepository
                .findAllByActiveTrueAndStatusEquals(pageable, status)
                .map(ListOrderItemsDTO::new);
    }
}
