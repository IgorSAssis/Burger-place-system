package br.com.senior.burger_place.domain.occupation;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardRepository;
import br.com.senior.burger_place.domain.board.BoardService;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.customer.CustomerRepository;
import br.com.senior.burger_place.domain.customer.CustomerService;
import br.com.senior.burger_place.domain.occupation.dto.*;
import br.com.senior.burger_place.domain.orderItem.OrderItem;
import br.com.senior.burger_place.domain.orderItem.OrderItemRepository;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import br.com.senior.burger_place.domain.product.Product;
import br.com.senior.burger_place.domain.product.ProductRepository;
import br.com.senior.burger_place.domain.validation.InvalidDTOValidation;
import br.com.senior.burger_place.domain.validation.InvalidIdValidation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    @Autowired
    private OccupationSpecification occupationSpecification;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CustomerService customerService;

    public Page<Occupation> listOccupations(
            Pageable pageable,
            LocalDateTime beginOccupation,
            LocalDateTime endOccupation,
            PaymentForm paymentForm,
            Integer peopleCount,
            Integer boardNumber,
            Boolean active
    ) {
        Specification<Occupation> specification = this.occupationSpecification.applyFilters(
                beginOccupation, endOccupation, paymentForm, peopleCount, boardNumber, active
        );

        return this.occupationRepository.findAll(specification, pageable);
    }

    public Occupation showOccupation(
            @NonNull
            UUID id
    ) {
        return this.occupationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Occupation does not exist"));
    }

    public Occupation createOccupation(
            @NonNull
            @Valid
            CreateOccupationDTO createOccupationDTO
    ) {
        Board board = this.boardService.showBoard(createOccupationDTO.getBoardId());

        if (!board.getActive()) {
            throw new IllegalArgumentException("Board is inactive");
        }

        if (board.getOccupied()) {
            throw new IllegalArgumentException("Board already occupied");
        }

        if (board.getCapacity() < createOccupationDTO.getPeopleCount()) {
            throw new IllegalArgumentException("People count exceeds capacity of the board");
        }

        Occupation occupation = Occupation.builder()
                .beginOccupation(createOccupationDTO.getBeginOccupation())
                .peopleCount(createOccupationDTO.getPeopleCount())
                .board(board)
                .build();

        if (createOccupationDTO.getCustomerIds() != null && !createOccupationDTO.getCustomerIds().isEmpty()) {
            Set<Customer> customers = this.customerService.findCustomersById(createOccupationDTO.getCustomerIds());
            occupation.setCustomers(customers);
        }

        return this.occupationRepository.save(occupation);
    }

    public void addOrderItems(UUID occupationId, AddOrderItemsDTO itemsDTO) {
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

//        List<OrderItem> orderItems = itemsDTO.orderItems().stream().map(item -> {
//            Product product = products.stream()
//                    .filter(p -> Objects.equals(p.getId(), item.productId()))
//                    .findFirst()
//                    .get();
//
//            return new OrderItem(
//                    item.amount(),
//                    product.getPrice(),
//                    product,
//                    occupation,
//                    item.observation()
//            );
//        }).toList();

        this.orderItemRepository.saveAll(null);
    }

    public void removeOrderItems(UUID occupationId, RemoveOrderItemsDTO itemsDTO) {
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

    public void updateOrderItem(UUID occupationId, UUID itemId, UpdateOrderItemDTO itemDTO) {
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

//        item.update(itemDTO);
    }

    public void inactivateOccupation(UUID occupationId) {
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

    public void startOrderItemPreparation(UUID occupationId, UUID itemId) {
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
            throw new IllegalStateException("O item já está cancelado");
        }

        if (item.getStatus() == OrderItemStatus.EM_ANDAMENTO) {
            throw new IllegalStateException("O item já está sendo preparado");
        }

        item.startPreparation();
    }

    public void finishOrderItemPreparation(UUID occupationId, UUID itemId) {
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
            throw new IllegalStateException("O item já está cancelado");
        }

        if (item.getStatus() == OrderItemStatus.PRONTO) {
            throw new IllegalStateException("O preparo do item já foi finalizado");
        }

        item.finishPreparation();
    }

    public void deliverOrderItem(UUID occupationId, UUID itemId) {
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
            throw new IllegalStateException("O item já está cancelado");
        }

        if (item.getStatus() == OrderItemStatus.ENTREGUE) {
            throw new IllegalStateException("O item já foi entregue ao cliente");
        }

        item.deliver();
    }

    public void cancelOrderItem(UUID occupationId, UUID itemId) {
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

    public void finishOccupation(UUID occupationId, FinishOccupationDTO occupationDTO) {
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

        occupation.finish(occupationDTO.endOccupation(), occupation.getPaymentForm());

    }
}
