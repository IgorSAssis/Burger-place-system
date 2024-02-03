package br.com.senior.burger_place.domain.board.dto;

import br.com.senior.burger_place.domain.board.BoardLocation;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateBoardDTO {
    @Positive(message = "Board number cannot be zero or negative")
    private Integer boardNumber;
    @Positive(message = "Board capacity cannot be zero or negative")
    private Integer capacity;
    private BoardLocation boardLocation;
}
