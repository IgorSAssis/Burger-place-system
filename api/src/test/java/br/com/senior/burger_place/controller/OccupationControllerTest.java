package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.occupation.OccupationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {OccupationController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("OccupationController integration tests")
public class OccupationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OccupationController occupationController;
    @MockBean
    private OccupationService occupationService;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("list tests")
    class ListTest {

    }
}
