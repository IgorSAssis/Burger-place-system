package br.com.senior.burger_place.domain.occupation;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardRepository;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.customer.CustomerRepository;
import br.com.senior.burger_place.domain.occupation.dto.*;
import br.com.senior.burger_place.domain.product.Product;
import br.com.senior.burger_place.domain.product.ProductRepository;
import br.com.senior.burger_place.utils.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OccupationServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private OccupationRepository occupationRepository;
    @InjectMocks
    private OccupationService occupationService;

    @Test
    void listOccupations_whenExistProducts_shouldReturnPageWithOccupationsDTO() {
        List<Occupation> someOccupations = Arrays.asList(
                OccupationTestFactory.openedOccupationFactory(1L),
                OccupationTestFactory.closedOccupationFactory(2L)
        );

        Page<Occupation> somePage = new PageImpl<>(someOccupations);

        when(this.occupationRepository.findAllByActiveTrue(any(Pageable.class))).thenReturn(somePage);

        List<ListOccupationDTO> output = this.occupationService.listOccupations(Pageable.ofSize(20)).toList();

        List<ListOccupationDTO> expectedOccupations = Arrays.asList(
                new ListOccupationDTO(someOccupations.get(0)),
                new ListOccupationDTO(someOccupations.get(1))
        );

        assertAll(
                () -> assertEquals(expectedOccupations.size(), output.size()),
                () -> assertEquals(expectedOccupations, output)
        );
    }

    @Test
    void listOccupations_whenProductsDoesNotExist_shouldReturnAnEmptyPage() {
        Page<Occupation> somePage = new PageImpl<>(new ArrayList<>());

        when(this.occupationRepository.findAllByActiveTrue(any(Pageable.class))).thenReturn(somePage);

        List<ListOccupationDTO> output = this.occupationService.listOccupations(Pageable.ofSize(20)).toList();

        assertTrue(output.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void showOccupation_whenIdIsInvalid_shouldThrow(Long id) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.showOccupation(id)
        );

        assertEquals("ID inválida", exception.getMessage());
    }

    @Test
    void showOccupation_whenProductDoesNotExist_shouldReturnEmptyOptional() {
        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(null);

        Optional<OccupationDTO> output = this.occupationService.showOccupation(1L);

        assertTrue(output.isEmpty());
    }

    @Test
    void showOccupation_whenProductExists_shouldReturnAnOptionalWithOccupationDTO() {
        Occupation someOccupation = OccupationTestFactory.openedOccupationFactory(1L);

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someOccupation);

        Optional<OccupationDTO> output = this.occupationService.showOccupation(1L);

        OccupationDTO expectedOccupationDTO = new OccupationDTO(someOccupation);

        assertAll(
                () -> assertFalse(output.isEmpty()),
                () -> assertEquals(expectedOccupationDTO, output.get())
        );

    }

    @Test
    void createOccupation_whenDTOIsNull_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.createOccupation(null)
        );

        assertEquals("DTO inválido", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void createOccupation_whenDTOBoardIdIsInvalid_shouldThrow(Long id) {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().minusMinutes(10),
                2,
                id,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.createOccupation(input)
        );

        assertEquals("ID da mesa é inválida", exception.getMessage());
    }

    @Test
    void createOccupation_whenDTOBeginOccupationIsNull_shouldThrow() {
        CreateOccupationDTO input = new CreateOccupationDTO(
                null,
                2,
                1L,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.createOccupation(input)
        );

        assertEquals("Data de início da ocupação é inválida", exception.getMessage());
    }

    @Test
    void createOccupation_whenDTOBeginOccupationIsAfterNow_shouldThrow() {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().plusMinutes(10),
                2,
                1L,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.createOccupation(input)
        );

        assertEquals("A data de entrada deve ser menor ou igual a atual", exception.getMessage());
    }

    @Test
    void createOccupation_whenBoardDoesNotExist_shouldThrow() {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().minusMinutes(10),
                2,
                1L,
                null
        );

        when(this.boardRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.createOccupation(input)
        );

        assertEquals("Mesa não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void createOccupation_whenBoardIsOccupied_shouldThrow() {
        Board someBoard = BoardTestFactory.boardFactory(1L, 1, 2);
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().minusMinutes(10),
                2,
                someBoard.getId(),
                null
        );

        when(this.boardRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someBoard);
        when(this.boardRepository.isBoardOccupied(anyLong())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.createOccupation(input)
        );

        assertEquals("Mesa já está ocupada", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1, -10})
    void createOccupation_whenDTOPeopleCountIsLessThanOrEqualToZero_shouldThrow(Integer peopleCount) {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().minusMinutes(10),
                peopleCount,
                1L,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.createOccupation(input)
        );

        assertEquals("Quantidade de pessoas é inválida", exception.getMessage());
    }

    @Test
    void createOccupation_whenDTOCustomerIdsIsNotEmpty_shouldSaveAndReturnOccupation() {
        Set<Customer> someCustomers = Set.of(
                CustomerTestFactory.customerFactory(1L),
                CustomerTestFactory.customerFactory(2L)
        );
        Board someBoard = BoardTestFactory.boardFactory(1L, 1, 2);
        Occupation someOccupation = OccupationTestFactory.openedOccupationFactory(1L, someBoard, someCustomers);
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().minusMinutes(10),
                2,
                someBoard.getId(),
                Set.of(1L, 2L)
        );

        when(this.boardRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someBoard);
        when(this.boardRepository.isBoardOccupied(anyLong())).thenReturn(false);
        when(this.occupationRepository.save(any(Occupation.class))).thenReturn(someOccupation);
        when(this.customerRepository.getCustomers(anySet())).thenReturn(someCustomers);

        ArgumentCaptor<Occupation> argumentCaptor = ArgumentCaptor.forClass(Occupation.class);

        OccupationDTO output = this.occupationService.createOccupation(input);

        verify(this.occupationRepository).save(argumentCaptor.capture());
        Occupation capturedOccupation = argumentCaptor.getValue();

        OccupationDTO expectedOccupationDTO = new OccupationDTO(someOccupation);

        assertAll(
                () -> assertEquals(input.beginOccupation(), capturedOccupation.getBeginOccupation()),
                () -> assertEquals(input.peopleCount(), capturedOccupation.getPeopleCount()),
                () -> assertEquals(someBoard, capturedOccupation.getBoard()),

                () -> assertEquals(expectedOccupationDTO, output),
                () -> assertEquals(someCustomers.size(), output.customers().size())
        );
    }

    @Test
    void createOccupation_whenCustomersDoesNotExist_shouldThrow() {
        Board someBoard = BoardTestFactory.boardFactory(1L, 1, 2);
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().minusMinutes(10),
                2,
                someBoard.getId(),
                Set.of(1L, 2L)
        );

        when(this.boardRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someBoard);
        when(this.boardRepository.isBoardOccupied(anyLong())).thenReturn(false);
        when(this.customerRepository.getCustomers(anySet())).thenReturn(new HashSet<>());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.createOccupation(input)
        );

        assertEquals("Clientes não existem ou foram desativados", exception.getMessage());
    }

    @Test
    void createOccupation_whenSomeCustomerDoesNotExist_shouldThrow() {
        Set<Customer> someCustomers = Set.of(CustomerTestFactory.customerFactory(1L));
        Board someBoard = BoardTestFactory.boardFactory(1L, 1, 2);
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().minusMinutes(10),
                2,
                someBoard.getId(),
                Set.of(1L, 2L)
        );

        when(this.boardRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someBoard);
        when(this.boardRepository.isBoardOccupied(anyLong())).thenReturn(false);
        when(this.customerRepository.getCustomers(anySet())).thenReturn(someCustomers);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.createOccupation(input)
        );

        assertEquals("Algum cliente não existe ou foi desativado", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void addOrderItems_whenOccupationIdIsInvalid_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.addOrderItems(occupationId, null)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @Test
    void addOrderItems_whenDTOIsNull_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.addOrderItems(1L, null)
        );

        assertEquals("DTO inválido", exception.getMessage());
    }

    @Test
    void addOrderItems_whenDTOOrderItemsIsEmpty_shouldThrow() {
        AddOrderItemsDTO input = new AddOrderItemsDTO(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.addOrderItems(1L, input)
        );

        assertEquals("Lista de itens do pedido está vazia", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void addOrderItems_whenDTOOrderItemsHasSomeProductIdIsInvalid_shouldThrow(Long productId) {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(productId, 2, null)
                )
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.addOrderItems(1L, input)
        );

        assertEquals("Algum item possui a ID do produto é inválida", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1, -10})
    void addOrderItems_whenDTOOrderItemsHasSomeProductAmountBeenInvalid_shouldThrow(Integer productAmount) {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(2L, productAmount, null)
                )
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.addOrderItems(1L, input)
        );

        assertEquals("Quantidade do produto é inválido", exception.getMessage());
    }

    @Test
    void addOrderItems_whenOccupationDoesNotExist_shouldThrow() {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(2L, 1, null)
                )
        );

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.addOrderItems(1L, input)
        );

        assertEquals("Ocupação não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void addOrderItems_whenOccupationWasFinished_shouldThrow() {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(2L, 1, null)
                )
        );
        Occupation someOccupation = OccupationTestFactory.closedOccupationFactory(1L);

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someOccupation);

        IllegalCallerException exception = assertThrows(
                IllegalCallerException.class,
                () -> this.occupationService.addOrderItems(1L, input)
        );

        assertEquals("A ocupação já foi finalizada", exception.getMessage());
    }

    @Test
    void addOrderItems_whenDTOOrderItemsHasNonExistentProducts_shouldThrow() {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(2L, 1, null)
                )
        );
        Occupation someOccupation = OccupationTestFactory.openedOccupationFactory(1L);
        List<Product> someProducts = List.of(ProductTestFactory.productFactory(1L));

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someOccupation);
        when(this.productRepository.getReferenceByActiveTrueAndIdIn(anyList())).thenReturn(someProducts);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.addOrderItems(1L, input)
        );

        assertEquals("Existem produtos inválidos", exception.getMessage());
    }

    @Test
    void addOrderItems_whenDTOIsValidAndProductsExists_shouldSaveOrderItems() {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(2L, 1, null)
                )
        );
        Occupation someOccupation = OccupationTestFactory.openedOccupationFactory(1L);
        List<Product> someProducts = List.of(
                ProductTestFactory.productFactory(1L),
                ProductTestFactory.productFactory(2L)
        );

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someOccupation);
        when(this.productRepository.getReferenceByActiveTrueAndIdIn(anyList())).thenReturn(someProducts);

        ArgumentCaptor<List<OrderItem>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        this.occupationService.addOrderItems(1L, input);
        verify(this.orderItemRepository, atMostOnce()).saveAll(anyList());

        verify(this.orderItemRepository).saveAll(argumentCaptor.capture());
        List<OrderItem> capturedValue = argumentCaptor.getValue();

        assertAll(
                () -> assertEquals(input.orderItems().get(0).amount(), capturedValue.get(0).getAmount()),
                () -> assertEquals(someProducts.get(0).getPrice(), capturedValue.get(0).getItemValue()),
                () -> assertEquals(someProducts.get(0), capturedValue.get(0).getProduct()),
                () -> assertEquals(someOccupation, capturedValue.get(0).getOccupation()),
                () -> assertEquals(input.orderItems().get(0).observation(), capturedValue.get(0).getObservation()),

                () -> assertEquals(input.orderItems().get(1).amount(), capturedValue.get(1).getAmount()),
                () -> assertEquals(someProducts.get(1).getPrice(), capturedValue.get(1).getItemValue()),
                () -> assertEquals(someProducts.get(1), capturedValue.get(1).getProduct()),
                () -> assertEquals(someOccupation, capturedValue.get(1).getOccupation()),
                () -> assertEquals(input.orderItems().get(1).observation(), capturedValue.get(1).getObservation())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void removeOrderItems_whenOccupationIdIsInvalid_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.removeOrderItems(occupationId, null)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @Test
    void removeOrderItems_whenDTOIsNull_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.removeOrderItems(1L, null)
        );

        assertEquals("DTO inválido", exception.getMessage());
    }

    @Test
    void removeOrderItems_whenDTOOrderItemsIsEmpty_shouldThrow() {
        RemoveOrderItemsDTO input = new RemoveOrderItemsDTO(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.removeOrderItems(1L, input)
        );

        assertEquals("Lista de itens do pedido está vazia", exception.getMessage());
    }

    @Test
    void removeOrderItems_whenOccupationDoesNotExist_shouldThrow() {
        RemoveOrderItemsDTO input = new RemoveOrderItemsDTO(List.of(1L, 2L));

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.removeOrderItems(1L, input)
        );

        assertEquals("Ocupação não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void removeOrderItems_whenAllOrderItemsDoesNotBelongToOccupation_shouldThrow() {
        RemoveOrderItemsDTO input = new RemoveOrderItemsDTO(List.of(1L, 2L));

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByActiveTrueAndOccupationIdAndIdIn(anyLong(), anyList())).thenReturn(new ArrayList<>());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.removeOrderItems(1L, input)
        );

        assertEquals("Nenhum item pertence ao pedido", exception.getMessage());
    }

    @Test
    void removeOrderItems_whenOrderItemsDoesNotBelongToOccupation_shouldThrow() {
        RemoveOrderItemsDTO input = new RemoveOrderItemsDTO(List.of(1L, 2L));
        List<OrderItem> someOrderItems = List.of(
                OrderItemTestFactory.orderItemFactory(1L)
        );

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByActiveTrueAndOccupationIdAndIdIn(anyLong(), anyList())).thenReturn(someOrderItems);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.removeOrderItems(1L, input)
        );

        assertEquals("Existem itens que não pertencem ao pedido", exception.getMessage());
    }

    @Test
    void removeOrderItems_whenOrderItemsBelongsToOccupation_shouldInactivateAllOrderItems() {
        RemoveOrderItemsDTO input = new RemoveOrderItemsDTO(List.of(1L, 2L));
        List<OrderItem> someOrderItems = List.of(
                spy(OrderItemTestFactory.orderItemFactory(1L)),
                spy(OrderItemTestFactory.orderItemFactory(2L))
        );

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByActiveTrueAndOccupationIdAndIdIn(anyLong(), anyList())).thenReturn(someOrderItems);


        this.occupationService.removeOrderItems(1L, input);

        verify(someOrderItems.get(0), atMostOnce()).inactivate();
        verify(someOrderItems.get(1), atMostOnce()).inactivate();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void updateOrderItem_whenOccupationIdIsInvalid_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.updateOrderItem(occupationId, null, null)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void updateOrderItem_whenItemIdIsInvalid_shouldThrow(Long itemId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.updateOrderItem(1L, itemId, null)
        );

        assertEquals("ID do item inválido", exception.getMessage());
    }

    @Test
    void updateOrderItem_whenDTOIsNull_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.updateOrderItem(1L, 1L, null)
        );

        assertEquals("DTO inválido", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1, -10})
    void updateOrderItem_whenDTOAmountIsInvalid_shouldThrow(Integer amount) {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(amount, null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.updateOrderItem(1L, 1L, input)
        );

        assertEquals("Quantidade de itens inválida", exception.getMessage());
    }

    @Test
    void updateOrderItem_whenOccupationDoesNotExist_shouldThrow() {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(2, null);

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.updateOrderItem(1L, 1L, input)
        );

        assertEquals("Ocupação não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void updateOrderItem_whenItemDoesNotBelongToOccupation_shouldThrow() {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(2, null);

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.updateOrderItem(1L, 1L, input)
        );

        assertEquals("Item não existe ou não pertence a essa ocupação", exception.getMessage());
    }

    @Test
    void updateOrderItem_whenItemWasDelivered_shouldThrow() {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(2, null);
        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);
        someOrderItem.deliver();

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItem);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> this.occupationService.updateOrderItem(1L, 1L, input)
        );

        assertEquals("O item já foi entregue e, portanto, não pode ser mais alterado", exception.getMessage());
    }

    @Test
    void updateOrderItem_whenItemIsValid_shouldUpdateOrderItem() {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(2, null);
        OrderItem someOrderItemSpy = spy(OrderItemTestFactory.orderItemFactory(1L, 1));

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItemSpy);

        this.occupationService.updateOrderItem(1L, 1L, input);

        verify(someOrderItemSpy, atMostOnce()).update(any(UpdateOrderItemDTO.class));
        assertEquals(input.amount(), someOrderItemSpy.getAmount());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void inactivateOccupation_whenOccupationIdIsInvalid_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.inactivateOccupation(occupationId)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @Test
    void inactivateOccupation_whenOccupationDoesNotExist_shouldThrow() {
        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.inactivateOccupation(1L)
        );

        assertEquals("Ocupação não existe", exception.getMessage());
    }

    @Test
    void inactivateOccupation_whenOccupationIsValid_shouldInactivateOccupationAndItsOrderItems() {
        List<OrderItem> someOrderItems = Arrays.asList(
                spy(OrderItemTestFactory.orderItemFactory(1L)),
                spy(OrderItemTestFactory.orderItemFactory(2L))
        );
        Occupation someOccupationSpy = spy(OccupationTestFactory.openedOccupationFactory(1L));

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someOccupationSpy);
        when(this.orderItemRepository.findByOccupationId(anyLong())).thenReturn(someOrderItems);

        this.occupationService.inactivateOccupation(1L);

        verify(someOccupationSpy, atMostOnce()).inactivate();
        verify(someOrderItems.get(0), atMostOnce()).inactivate();
        verify(someOrderItems.get(1), atMostOnce()).inactivate();

        assertFalse(someOccupationSpy.isActive());
        assertFalse(someOrderItems.get(0).isActive());
        assertFalse(someOrderItems.get(1).isActive());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void startOrderItemPreparation_whenOccupationIdIsInvalid_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.startOrderItemPreparation(occupationId, null)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void startOrderItemPreparation_whenItemIdIsInvalid_shouldThrow(Long itemId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.startOrderItemPreparation(1L, itemId)
        );

        assertEquals("ID do item inválido", exception.getMessage());
    }

    @Test
    void startOrderItemPreparation_whenOccupationDoesNotExists_shouldThrow() {
        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.startOrderItemPreparation(1L, 1L)
        );

        assertEquals("Ocupação não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void startOrderItemPreparation_whenItemDoesNotExists_shouldThrow() {
        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.startOrderItemPreparation(1L, 1L)
        );

        assertEquals("Item não existe ou não pertence a essa ocupação", exception.getMessage());
    }

    @Test
    void startOrderItemPreparation_whenItemIsInProgress_shouldThrow() {
        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);
        someOrderItem.startPreparation();

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItem);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> this.occupationService.startOrderItemPreparation(1L, 1L)
        );

        assertEquals("O item já está sendo preparado", exception.getMessage());
    }

    @Test
    void startOrderItemPreparation_whenWasReceived_shouldChangeToInProgressState() {
        OrderItem someOrderItemSpy = spy(OrderItemTestFactory.orderItemFactory(1L));

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItemSpy);

        this.occupationService.startOrderItemPreparation(1L, 1L);

        verify(someOrderItemSpy, atMostOnce()).startPreparation();
        assertEquals(OrderItemStatus.EM_ANDAMENTO, someOrderItemSpy.getStatus());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void finishOrderItemPreparation_whenOccupationIdIsInvalid_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.finishOrderItemPreparation(occupationId, null)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void finishOrderItemPreparation_whenItemIdIsLessThanOrEqualToZero_shouldThrow(Long itemId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.finishOrderItemPreparation(1L, itemId)
        );

        assertEquals("ID do item inválido", exception.getMessage());
    }

    @Test
    void finishOrderItemPreparation_whenOccupationDoesNotExists_shouldThrow() {
        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.finishOrderItemPreparation(1L, 1L)
        );

        assertEquals("Ocupação não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void finishOrderItemPreparation_whenItemDoesNotExists_shouldThrow() {
        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.finishOrderItemPreparation(1L, 1L)
        );

        assertEquals("Item não existe ou não pertence a essa ocupação", exception.getMessage());
    }

    @Test
    void finishOrderItemPreparation_whenItemIsInProgress_shouldThrow() {
        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);
        someOrderItem.finishPreparation();

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItem);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> this.occupationService.finishOrderItemPreparation(1L, 1L)
        );

        assertEquals("O preparo do item já foi finalizado", exception.getMessage());
    }

    @Test
    void finishOrderItemPreparation_whenWasReceived_shouldChangeToInProgressState() {
        OrderItem someOrderItemSpy = spy(OrderItemTestFactory.orderItemFactory(1L));

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItemSpy);


        this.occupationService.finishOrderItemPreparation(1L, 1L);

        verify(someOrderItemSpy, atMostOnce()).finishPreparation();
        assertEquals(OrderItemStatus.PRONTO, someOrderItemSpy.getStatus());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void deliverOrderItem_whenOccupationIdIsInvalid_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.deliverOrderItem(occupationId, null)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void deliverOrderItem_whenItemIdIsInvalid_shouldThrow(Long itemId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.deliverOrderItem(1L, itemId)
        );

        assertEquals("ID do item inválido", exception.getMessage());
    }

    @Test
    void deliverOrderItem_whenOccupationDoesNotExists_shouldThrow() {
        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.deliverOrderItem(1L, 1L)
        );

        assertEquals("Ocupação não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void deliverOrderItem_whenItemDoesNotExists_shouldThrow() {
        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.deliverOrderItem(1L, 1L)
        );

        assertEquals("Item não existe ou não pertence a essa ocupação", exception.getMessage());
    }

    @Test
    void deliverOrderItem_whenItemIsInProgress_shouldThrow() {
        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);
        someOrderItem.deliver();

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItem);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> this.occupationService.deliverOrderItem(1L, 1L)
        );

        assertEquals("O item já foi entregue ao cliente", exception.getMessage());
    }

    @Test
    void deliverOrderItem_whenWasReceived_shouldChangeToInProgressState() {
        OrderItem someOrderItemSpy = spy(OrderItemTestFactory.orderItemFactory(1L));

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItemSpy);


        this.occupationService.deliverOrderItem(1L, 1L);

        verify(someOrderItemSpy, atMostOnce()).deliver();
        assertEquals(OrderItemStatus.ENTREGUE, someOrderItemSpy.getStatus());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void cancelOrderItem_whenOccupationIdIsInvalid_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.cancelOrderItem(occupationId, null)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void cancelOrderItem_whenItemIdIsInvalid_shouldThrow(Long itemId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.cancelOrderItem(1L, itemId)
        );

        assertEquals("ID do item inválido", exception.getMessage());
    }

    @Test
    void cancelOrderItem_whenOccupationDoesNotExists_shouldThrow() {
        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.cancelOrderItem(1L, 1L)
        );

        assertEquals("Ocupação não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void cancelOrderItem_whenItemDoesNotExists_shouldThrow() {
        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.cancelOrderItem(1L, 1L)
        );

        assertEquals("Item não existe ou não pertence a essa ocupação", exception.getMessage());
    }

    @Test
    void cancelOrderItem_whenItemIsInProgress_shouldThrow() {
        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);
        someOrderItem.cancel();

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItem);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> this.occupationService.cancelOrderItem(1L, 1L)
        );

        assertEquals("O item já foi cancelado", exception.getMessage());
    }

    @Test
    void cancelOrderItem_whenWasReceived_shouldChangeToInProgressState() {
        OrderItem someOrderItemSpy = spy(OrderItemTestFactory.orderItemFactory(1L));

        when(this.occupationRepository.existsByIdAndActiveTrue(anyLong())).thenReturn(true);
        when(this.orderItemRepository.getReferenceByIdAndOccupationIdAndActiveTrue(anyLong(), anyLong())).thenReturn(someOrderItemSpy);

        this.occupationService.cancelOrderItem(1L, 1L);

        verify(someOrderItemSpy, atMostOnce()).cancel();
        assertEquals(OrderItemStatus.CANCELADO, someOrderItemSpy.getStatus());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void finishOccupation_whenOccupationIdIsLessThanOrEqualToZero_shouldThrow(Long occupationId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.finishOccupation(occupationId, null)
        );

        assertEquals("ID da ocupação inválida", exception.getMessage());
    }

    @Test
    void finishOccupation_whenDTOIsNull_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.finishOccupation(1L, null)
        );

        assertEquals("DTO inválido", exception.getMessage());
    }

    @Test
    void finishOccupation_whenDTOEndOccupationIsNull_shouldThrow() {
        FinishOccupationDTO input = new FinishOccupationDTO(null, null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.finishOccupation(1L, input)
        );

        assertEquals("Término da ocupação inválido", exception.getMessage());
    }

    @Test
    void finishOccupation_whenDTOEndOccupationIsBeforeNow_shouldThrow() {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().minusMinutes(20),
                null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.finishOccupation(1L, input)
        );

        assertEquals("Término da ocupação deve ser igual ou maior que a data atual", exception.getMessage());
    }

    @Test
    void finishOccupation_whenDTOPaymentFormIsNull_shouldThrow() {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().plusMinutes(1),
                null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.occupationService.finishOccupation(1L, input)
        );

        assertEquals("Forma de pagamento inválida", exception.getMessage());
    }

    @Test
    void finishOccupation_whenOccupationDoesNotExist_shouldThrow() {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().plusMinutes(1),
                PaymentForm.CARTAO_CREDITO);

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.occupationService.finishOccupation(1L, input)
        );

        assertEquals("Ocupação não existe ou foi inativada", exception.getMessage());
    }

    @Test
    void finishOccupation_whenOccupationIsFinished_shouldThrow() {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().plusMinutes(1),
                PaymentForm.CARTAO_CREDITO);

        Occupation someOccupation = OccupationTestFactory.closedOccupationFactory(1L);

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someOccupation);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> this.occupationService.finishOccupation(1L, input)
        );

        assertEquals("A ocupação já foi finalizada", exception.getMessage());
    }

    @Test
    void finishOccupation_whenOccupationHasPendingOrderItens_shouldThrow() {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().plusMinutes(1),
                PaymentForm.CARTAO_CREDITO);

        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);
        someOrderItem.startPreparation();

        Occupation someOccupation = OccupationTestFactory.openedOccupationFactory(1L, List.of(someOrderItem));

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someOccupation);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> this.occupationService.finishOccupation(1L, input)
        );

        assertEquals("Não é possível finalizar a ocupação tendo itens pendentes", exception.getMessage());
    }

    @Test
    void finishOccupation_whenOccupationCanBeFinish_shouldShouldFinishOccupation() {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().plusMinutes(1),
                PaymentForm.CARTAO_CREDITO);

        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);
        someOrderItem.deliver();

        Occupation someOccupationSpy = spy(OccupationTestFactory.openedOccupationFactory(1L, List.of(someOrderItem)));

        when(this.occupationRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(someOccupationSpy);

        this.occupationService.finishOccupation(1L, input);

        verify(someOccupationSpy, atMostOnce()).finish(any(FinishOccupationDTO.class));
        assertEquals(input.endOccupation(), someOccupationSpy.getEndOccupation());
        assertEquals(input.paymentForm(), someOccupationSpy.getPaymentForm());

    }
}
