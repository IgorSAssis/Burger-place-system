package br.com.senior.burger_place.domain.occupation;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardRepository;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.customer.CustomerRepository;
import br.com.senior.burger_place.domain.occupation.dto.*;
import br.com.senior.burger_place.domain.orderItem.OrderItem;
import br.com.senior.burger_place.domain.orderItem.OrderItemRepository;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import br.com.senior.burger_place.domain.product.Product;
import br.com.senior.burger_place.domain.product.ProductRepository;
import br.com.senior.burger_place.domain.validation.InvalidDTOValidation;
import br.com.senior.burger_place.domain.validation.InvalidIdValidation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class OccupationService {
    @Autowired
    private OccupationRepository occupationRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public Page<ListOccupationDTO> listOccupations(Pageable pageable) {
        return this.occupationRepository.findAllByActiveTrue(pageable).map(ListOccupationDTO::new);
    }

    public Optional<OccupationDTO> showOccupation(Long id) {
        InvalidIdValidation.validate(id);

        Occupation occupation = this.occupationRepository.getReferenceByIdAndActiveTrue(id);

        if (occupation == null) {
            return Optional.empty();
        }

        OccupationDTO occupationDTO = new OccupationDTO(occupation);

        return Optional.of(occupationDTO);
    }

    public OccupationDTO createOccupation(CreateOccupationDTO orderData) {
        InvalidDTOValidation.validate(orderData);

        if (orderData.boardId() == null || orderData.boardId() <= 0) {
            throw new IllegalArgumentException("ID da mesa é inválida");
        }

        if (orderData.beginOccupation() == null) {
            throw new IllegalArgumentException("Data de início da ocupação é inválida");
        }

        if (orderData.beginOccupation().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data de entrada deve ser menor ou igual a atual");
        }

        if (orderData.peopleCount() == null || orderData.peopleCount() <= 0) {
            throw new IllegalArgumentException("Quantidade de pessoas é inválida");
        }

        Board board = this.boardRepository.getReferenceByIdAndActiveTrue(orderData.boardId());

        if (board == null) {
            throw new EntityNotFoundException("Mesa não existe ou foi inativada");
        }

        if (this.boardRepository.isBoardOccupied(orderData.boardId())) {
            throw new IllegalArgumentException("Mesa já está ocupada");
        }

        Occupation occupation = new Occupation(
                orderData.beginOccupation(),
                orderData.peopleCount(),
                board
        );

        if (orderData.customerIds() != null && !orderData.customerIds().isEmpty()) {
            Set<Customer> customers = this.customerRepository.getCustomers(orderData.customerIds());

            if (customers.isEmpty()) {
                throw new EntityNotFoundException("Clientes não existem ou foram desativados");
            }

            if (orderData.customerIds().size() != customers.size()) {
                throw new IllegalArgumentException("Algum cliente não existe ou foi desativado");
            }

            occupation.setCustomers(customers);
        }

        Occupation savedOccupation = this.occupationRepository.save(occupation);

        return new OccupationDTO(savedOccupation);
    }

    public void addOrderItems(Long occupationId, AddOrderItemsDTO itemsDTO) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");
        InvalidDTOValidation.validate(itemsDTO);

        if (itemsDTO.orderItems().isEmpty()) {
            throw new IllegalArgumentException("Lista de itens do pedido está vazia");
        }

        itemsDTO.orderItems().forEach(item -> {
            InvalidIdValidation.validate(item.productId(), "Algum item possui a ID do produto é inválida");

            if (item.amount() == null || item.amount() <= 0) {
                throw new IllegalArgumentException("Quantidade do produto é inválido");
            }
        });

        Occupation occupation = this.occupationRepository.getReferenceByIdAndActiveTrue(occupationId);

        if (occupation == null) {
            throw new EntityNotFoundException("Ocupação não existe ou foi inativada");
        }

        if (occupation.getEndOccupation() != null) {
            throw new IllegalCallerException("A ocupação já foi finalizada");
        }

        List<Product> products = this.productRepository.getReferenceByActiveTrueAndIdIn(
                itemsDTO.orderItems()
                        .stream()
                        .map(CreateOrderItemDTO::productId)
                        .toList()
        );

        if (products.size() != itemsDTO.orderItems().size()) {
            throw new EntityNotFoundException("Existem produtos inválidos");
        }

        List<OrderItem> orderItems = itemsDTO.orderItems().stream().map(item -> {
            Product product = products.stream()
                    .filter(p -> Objects.equals(p.getId(), item.productId()))
                    .findFirst()
                    .get();

            return new OrderItem(
                    item.amount(),
                    product.getPrice(),
                    product,
                    occupation,
                    item.observation()
            );
        }).toList();

        this.orderItemRepository.saveAll(orderItems);
    }

    public void removeOrderItems(Long occupationId, RemoveOrderItemsDTO itemsDTO) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");
        InvalidDTOValidation.validate(itemsDTO);

        if (itemsDTO.orderItems().isEmpty()) {
            throw new IllegalArgumentException("Lista de itens do pedido está vazia");
        }

        if (!this.occupationRepository.existsByIdAndActiveTrue(occupationId)) {
            throw new EntityNotFoundException("Ocupação não existe ou foi inativada");
        }

        List<OrderItem> orderItems = this.orderItemRepository.getReferenceByActiveTrueAndOccupationIdAndIdIn(occupationId, itemsDTO.orderItems());

        if (orderItems.isEmpty()) {
            throw new EntityNotFoundException("Nenhum item pertence ao pedido");
        }

        if (itemsDTO.orderItems().size() > orderItems.size()) {
            throw new IllegalArgumentException("Existem itens que não pertencem ao pedido");
        }

        itemsDTO.orderItems()
                .forEach(orderItemId -> {
                    OrderItem orderItem = orderItems.stream()
                            .filter(item -> item.getId().equals(orderItemId))
                            .findFirst()
                            .get();

                    orderItem.inactivate();
                });
    }

    public void updateOrderItem(Long occupationId, Long itemId, UpdateOrderItemDTO itemDTO) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");
        InvalidIdValidation.validate(itemId, "ID do item inválido");
        InvalidDTOValidation.validate(itemDTO);

        if (itemDTO.amount() == null || itemDTO.amount() <= 0) {
            throw new IllegalArgumentException("Quantidade de itens inválida");
        }

        if (!this.occupationRepository.existsByIdAndActiveTrue(occupationId)) {
            throw new EntityNotFoundException("Ocupação não existe ou foi inativada");
        }

        OrderItem item = this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(itemId, occupationId);

        if (item == null) {
            throw new EntityNotFoundException("Item não existe ou não pertence a essa ocupação");
        }

        if (item.getStatus() == OrderItemStatus.ENTREGUE) {
            throw new IllegalStateException("O item já foi entregue e, portanto, não pode ser mais alterado");
        }

        item.update(itemDTO);
    }

    public void inactivateOccupation(Long occupationId) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");

        Occupation occupation = this.occupationRepository.getReferenceByIdAndActiveTrue(occupationId);

        if (occupation == null) {
            throw new EntityNotFoundException("Ocupação não existe");
        }
        occupation.inactivate();

        List<OrderItem> orderItems = this.orderItemRepository.findByOccupationId(occupationId);

        if (!orderItems.isEmpty()) {
            orderItems.forEach(OrderItem::inactivate);
        }
    }

    public void startOrderItemPreparation(Long occupationId, Long itemId) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");
        InvalidIdValidation.validate(itemId, "ID do item inválido");

        if (!this.occupationRepository.existsByIdAndActiveTrue(occupationId)) {
            throw new EntityNotFoundException("Ocupação não existe ou foi inativada");
        }

        OrderItem item = this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(itemId, occupationId);

        if (item == null) {
            throw new EntityNotFoundException("Item não existe ou não pertence a essa ocupação");
        }

        if (item.getStatus() == OrderItemStatus.EM_ANDAMENTO) {
            throw new IllegalStateException("O item já está sendo preparado");
        }

        item.startPreparation();
    }

    public void finishOrderItemPreparation(Long occupationId, Long itemId) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");
        InvalidIdValidation.validate(itemId, "ID do item inválido");

        if (!this.occupationRepository.existsByIdAndActiveTrue(occupationId)) {
            throw new EntityNotFoundException("Ocupação não existe ou foi inativada");
        }

        OrderItem item = this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(itemId, occupationId);

        if (item == null) {
            throw new EntityNotFoundException("Item não existe ou não pertence a essa ocupação");
        }

        if (item.getStatus() == OrderItemStatus.PRONTO) {
            throw new IllegalStateException("O preparo do item já foi finalizado");
        }

        item.finishPreparation();
    }

    public void deliverOrderItem(Long occupationId, Long itemId) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");
        InvalidIdValidation.validate(itemId, "ID do item inválido");

        if (!this.occupationRepository.existsByIdAndActiveTrue(occupationId)) {
            throw new EntityNotFoundException("Ocupação não existe ou foi inativada");
        }

        OrderItem item = this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(itemId, occupationId);

        if (item == null) {
            throw new EntityNotFoundException("Item não existe ou não pertence a essa ocupação");
        }

        if (item.getStatus() == OrderItemStatus.ENTREGUE) {
            throw new IllegalStateException("O item já foi entregue ao cliente");
        }

        item.deliver();
    }

    public void cancelOrderItem(Long occupationId, Long itemId) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");
        InvalidIdValidation.validate(itemId, "ID do item inválido");

        if (!this.occupationRepository.existsByIdAndActiveTrue(occupationId)) {
            throw new EntityNotFoundException("Ocupação não existe ou foi inativada");
        }

        OrderItem item = this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(itemId, occupationId);

        if (item == null) {
            throw new EntityNotFoundException("Item não existe ou não pertence a essa ocupação");
        }

        if (item.getStatus() == OrderItemStatus.CANCELADO) {
            throw new IllegalStateException("O item já foi cancelado");
        }

        item.cancel();
    }

    public void finishOccupation(Long occupationId, FinishOccupationDTO occupationDTO) {
        InvalidIdValidation.validate(occupationId, "ID da ocupação inválida");
        InvalidDTOValidation.validate(occupationDTO);

        if (occupationDTO.endOccupation() == null) {
            throw new IllegalArgumentException("Término da ocupação inválido");
        }

        if (occupationDTO.endOccupation().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Término da ocupação deve ser igual ou maior que a data atual");
        }

        if (occupationDTO.paymentForm() == null) {
            throw new IllegalArgumentException("Forma de pagamento inválida");
        }

        Occupation occupation = this.occupationRepository.getReferenceByIdAndActiveTrue(occupationId);

        if (occupation == null) {
            throw new EntityNotFoundException("Ocupação não existe ou foi inativada");
        }

        if (occupation.getEndOccupation() != null) {
            throw new IllegalStateException("A ocupação já foi finalizada");
        }

        occupation.getOrderItems().forEach(item -> {
            if (item.getStatus() != OrderItemStatus.ENTREGUE && item.getStatus() != OrderItemStatus.CANCELADO) {
                throw new IllegalStateException("Não é possível finalizar a ocupação tendo itens pendentes");
            }
        });

        occupation.finish(occupationDTO);

    }
}
