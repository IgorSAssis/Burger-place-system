package br.com.senior.burger_place.domain.board.dto;

import br.com.senior.burger_place.domain.board.BoardLocation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateBoardDTO {
    @NotNull(message = "Board number cannot be null")
    @Positive(message = "Board number cannot be zero or negative")
    private Integer boardNumber;

    @NotNull(message = "Board capacity cannot be null")
    @Positive(message = "Board capacity cannot be zero or negative")
    private Integer capacity;

    @NotNull(message = "Board location cannot be null")
    private BoardLocation boardLocation;
}
