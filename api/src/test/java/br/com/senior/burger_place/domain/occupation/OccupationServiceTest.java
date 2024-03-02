package br.com.senior.burger_place.domain.occupation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static utils.OccupationCreator.createOccupation;

@ExtendWith(MockitoExtension.class)
@DisplayName("OccupationService unit tests")
public class OccupationServiceTest {
    @Mock
    private OccupationRepository occupationRepository;
    @Mock
    private OccupationSpecification occupationSpecification;
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
}
