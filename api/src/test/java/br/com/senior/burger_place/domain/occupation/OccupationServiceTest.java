package br.com.senior.burger_place.domain.occupation;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardService;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.customer.CustomerService;
import br.com.senior.burger_place.domain.occupation.dto.CreateOccupationDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static utils.BoardCreator.createBoard;
import static utils.CustomerCreator.createCustomer;
import static utils.OccupationCreator.createCreateOccupationDTO;
import static utils.OccupationCreator.createOccupation;

@ExtendWith(MockitoExtension.class)
@DisplayName("OccupationService unit tests")
public class OccupationServiceTest {
    @Mock
    private OccupationRepository occupationRepository;
    @Mock
    private OccupationSpecification occupationSpecification;
    @Mock
    private BoardService boardService;
    @Mock
    private CustomerService customerService;
    @InjectMocks
    private OccupationService occupationService;

    @Nested
    @DisplayName("listOccupation tests")
    class ListOccupationsTest {
        @Test
        void listOccupation_whenOccupationsExist_shouldReturnPageWithOccupations() {
            List<Occupation> occupations = List.of(createOccupation());
            Page<Occupation> occupationPage = new PageImpl<>(occupations, Pageable.ofSize(20), occupations.size());

            mockApplyFilters(Specification.where(null));
            mockFindAll(occupationPage);

            Page<Occupation> output = occupationService.listOccupations(
                    occupationPage.getPageable(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(occupations.size(), output.getContent().size()),
                    () -> assertEquals(occupations.get(0), output.getContent().get(0))
            );
        }

        @Test
        void listOccupation_whenOccupationsDoNotExist_shouldReturnEmptyPage() {
            Page<Occupation> occupationPage = new PageImpl<>(List.of(), Pageable.ofSize(20), 0);

            mockApplyFilters(Specification.where(null));
            mockFindAll(occupationPage);

            Page<Occupation> output = occupationService.listOccupations(
                    occupationPage.getPageable(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertTrue(output.getContent().isEmpty())
            );
        }

        private void mockApplyFilters(Specification<Occupation> expectedReturn) {
            when(occupationSpecification.applyFilters(
                    null, null, null, null, null, null)
            ).thenReturn(expectedReturn);
        }

        private void mockFindAll(Page<Occupation> expectedPage) {
            when(occupationRepository.findAll(eq(Specification.where(null)), any(Pageable.class)))
                    .thenReturn(expectedPage);
        }
    }

    @Nested
    @DisplayName("showOccupation tests")
    class ShowOccupationTest {
        @Test
        void showOccupation_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> occupationService.showOccupation(null)
            );
        }

        @Test
        void showOccupation_whenOccupationDoesNotExist_shouldThrowEntityNotFound() {
            String expectedErrorMessage = "Occupation does not exist";

            mockFindById(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> occupationService.showOccupation(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void showOccupation_whenOccupationExists_shouldReturnOccupation() {
            Occupation occupation = createOccupation();

            mockFindById(occupation);

            Occupation output = occupationService.showOccupation(UUID.randomUUID());

            assertNotNull(output);
            assertEquals(occupation, output);
        }

        private void mockFindById(Occupation expectedReturn) {
            when(occupationRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(expectedReturn));
        }
    }

    @Nested
    @DisplayName("createOccupation tests")
    class CreateOccupationTest {
        @Captor
        private ArgumentCaptor<Occupation> occupationArgumentCaptor;

        @Test
        void createOccupation_whenDTOIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> occupationService.createOccupation(null)
            );
        }

        @Test
        void createOccupation_whenBoardIsInactive_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "Board is inactive";
            CreateOccupationDTO createOccupationDTO = createCreateOccupationDTO();
            Board board = createBoard();
            board.inactivate();

            mockShowBoard(board);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> occupationService.createOccupation(createOccupationDTO)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void createOccupation_whenBoardIsOccupied_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "Board already occupied";
            CreateOccupationDTO createOccupationDTO = createCreateOccupationDTO();
            Board board = createBoard();
            board.occupy();

            mockShowBoard(board);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> occupationService.createOccupation(createOccupationDTO)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void createOccupation_whenDTOPeopleCountExceedsBoardCapacity_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "People count exceeds capacity of the board";
            CreateOccupationDTO createOccupationDTO = createCreateOccupationDTO();
            createOccupationDTO.setPeopleCount(3);

            Board board = createBoard();
            board.setCapacity(2);

            mockShowBoard(board);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> occupationService.createOccupation(createOccupationDTO)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void createOccupation_whenDTOIsValid_shouldCreateAndReturnOccupation() {
            CreateOccupationDTO createOccupationDTO = createCreateOccupationDTO();
            createOccupationDTO.setPeopleCount(2);

            Board board = createBoard();
            board.setCapacity(2);

            Set<Customer> customers = Set.of(createCustomer(), createCustomer());

            mockShowBoard(board);
            mockFindCustomersById(customers);

            occupationService.createOccupation(createOccupationDTO);
            verify(occupationRepository, times(1)).save(this.occupationArgumentCaptor.capture());
            Occupation output = this.occupationArgumentCaptor.getValue();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(createOccupationDTO.getBeginOccupation(), output.getBeginOccupation()),
                    () -> assertEquals(createOccupationDTO.getPeopleCount(), output.getPeopleCount()),
                    () -> assertEquals(createOccupationDTO.getCustomerIds().size(), output.getCustomers().size()),
                    () -> assertEquals(customers, output.getCustomers())
            );
        }

        private void mockShowBoard(Board expectedReturn) {
            when(boardService.showBoard(any(UUID.class))).thenReturn(expectedReturn);
        }

        private void mockFindCustomersById(Set<Customer> expectedReturn) {
            when(customerService.findCustomersById(anySet())).thenReturn(expectedReturn);
        }
    }
}
