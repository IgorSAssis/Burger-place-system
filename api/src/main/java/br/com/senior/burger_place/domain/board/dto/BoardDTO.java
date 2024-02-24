package br.com.senior.burger_place.domain.board.dto;

import br.com.senior.burger_place.domain.board.BoardLocation;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class BoardDTO {
    private UUID id;
    private Integer boardNumber;
    private Integer boardCapacity;
    private BoardLocation boardLocation;
    @Builder.Default
    private Boolean active = true;
    private Boolean occupied;
}