package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.board.BoardLocation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardDTO {
    private Integer number;
    private BoardLocation location;
}
