package br.com.burger_place_app.domain.order;

import br.com.burger_place_app.domain.customer.Customer;
import br.com.burger_place_app.domain.customer.CustomerRepository;
import br.com.burger_place_app.domain.order.dto.*;
import br.com.burger_place_app.domain.product.Product;
import br.com.burger_place_app.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    public Page<ListOrderData> getOrders(Pageable pageable, OrderStatus orderStatusParam) {
        Specification<Order> orderSpecification = Specification.where(null);

        if (orderStatusParam != null) {
            orderSpecification = orderSpecification.and(OrderSpecification.filterByOrderStatus(orderStatusParam));
        }

        return this.orderRepository
                .findAll(orderSpecification, pageable)
                .map(ListOrderData::new);
    }

    public Optional<OrderData> showOrder(Long id) {
        Order order = this.orderRepository.getReferenceByIdAndActiveTrue(id);

        if (order == null) {
            return Optional.empty();
        }

        return Optional.of(new OrderData(order));
    }

    public OrderData createOrder(CreateOrderData orderData) {
        Customer customer = this.customerRepository.getReferenceByIdAndActiveTrue(orderData.clientId());
        if (customer == null) {
            throw new EntityNotFoundException("Cliente não existe ou está inativado");
        }

        List<Product> products = this.productRepository.getProductPriceById(
                orderData.orderItems()
                        .stream()
                        .map(CreateOrderItemData::productId)
                        .toList()
        );

        if (products.size() != orderData.orderItems().size()) {
            throw new EntityNotFoundException("Existem produtos inválidos!");
        }

        List<OrderItem> orderItems = orderData.orderItems().stream().map(item -> {
            Product prod = products.stream()
                    .filter(p -> Objects.equals(p.getId(), item.productId()))
                    .findFirst()
                    .get();

            return new OrderItem(item.amount(), prod.getPrice(), prod);
        }).toList();

        Order order = new Order(
                LocalDateTime.now(),
                null,
                orderData.paymentForm(),
                OrderStatus.RECEBIDO,
                customer,
                orderItems
        );

        this.orderRepository.save(order);
        return new OrderData(order);
    }

    public void addOrderItemToOrder(Long orderId, AddNewOrderItemsData orderItemsData) {
        if (!this.orderRepository.existsByIdAndActiveTrue(orderId)) {
            throw new EntityNotFoundException("Pedido não existe");
        }

        if (orderItemsData.orderItems().isEmpty()) {
            throw new IllegalArgumentException("Lista de itens do pedido está vazia");
        }

        List<Product> products = this.productRepository.getProductPriceById(
                orderItemsData.orderItems()
                        .stream()
                        .map(CreateOrderItemData::productId)
                        .toList()
        );

        if (products.size() != orderItemsData.orderItems().size()) {
            throw new EntityNotFoundException("Existem produtos inválidos!");
        }

        List<OrderItem> orderItems = orderItemsData.orderItems().stream().map(item -> {
            Product prod = products.stream()
                    .filter(p -> Objects.equals(p.getId(), item.productId()))
                    .findFirst()
                    .get();

            return new OrderItem(item.amount(), prod.getPrice(), prod);
        }).toList();

        this.orderItemRepository.saveAll(orderItems);
    }

    public void removeOrderItems(Long orderId, RemoveOrderItemsData orderItemsData) {
        if (!this.orderRepository.existsByIdAndActiveTrue(orderId)) {
            throw new EntityNotFoundException("Pedido não existe");
        }

        if (orderItemsData.orderItems().isEmpty()) {
            throw new IllegalArgumentException("Lista de itens do pedido está vazia");
        }

        List<OrderItem> orderItems = this.orderItemRepository.findOrderItems(orderId, orderItemsData.orderItems());

        if (orderItems.isEmpty()) {
            throw new EntityNotFoundException("Nenhum item pertence ao pedido");
        }

        if (orderItemsData.orderItems().size() > orderItems.size()) {
            throw new IllegalArgumentException("Existem itens que não pertencem ao pedido");
        }

        orderItemsData.orderItems()
                .forEach(orderItemId -> {
                    OrderItem orderItem = orderItems.stream()
                            .filter(item -> item.getId().equals(orderItemId))
                            .findFirst()
                            .get();

                    orderItem.inactivate();
                });
    }

    public void removeOrderItemFromOrder(Long orderId, Long orderItemId) {
        if (!this.orderRepository.existsByIdAndActiveTrue(orderId)) {
            throw new EntityNotFoundException("Pedido não existe");
        }

        OrderItem orderItem = this.orderItemRepository.getReferenceByIdAndOrderId(orderItemId, orderId);

        if (orderItem == null) {
            throw new EntityNotFoundException("Item não existe ou não pertence a esse pedido");
        }

        orderItem.inactivate();
    }

    public void updateOrderItem(Long orderId, Long orderItemId, UpdateOrderItemData orderItemData) {
        if (!this.orderRepository.existsByIdAndActiveTrue(orderId)) {
            throw new EntityNotFoundException("Pedido não existe");
        }

        OrderItem orderItem = this.orderItemRepository.getReferenceByIdAndOrderId(orderItemId, orderId);

        if (orderItem == null) {
            throw new EntityNotFoundException("Item não existe ou não pertence a esse pedido");
        }

        orderItem.update(orderItemData);
    }

    public void inactivateOrder(Long orderId) {
        Order order = this.orderRepository.getReferenceByIdAndActiveTrue(orderId);

        if (order == null) {
            throw new EntityNotFoundException("Pedido não existe");
        }
        order.inactivate();

        List<OrderItem> orderItems = this.orderItemRepository.findByOrderIdAndActiveTrue(orderId);

        if (!orderItems.isEmpty()) {
            orderItems.forEach(OrderItem::inactivate);
        }
    }
}
