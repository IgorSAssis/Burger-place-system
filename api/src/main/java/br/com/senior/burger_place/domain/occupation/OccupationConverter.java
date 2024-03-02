package br.com.senior.burger_place.domain.occupation;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.occupation.dto.BoardDTO;
import br.com.senior.burger_place.domain.occupation.dto.ListOccupationDTO;
import org.springframework.stereotype.Component;

@Component
public class OccupationConverter {
    public ListOccupationDTO toListOccupationDTO(Occupation occupation) {
        return ListOccupationDTO.builder()
                .id(occupation.getId())
                .beginOccupation(occupation.getBeginOccupation())
                .endOccupation(occupation.getEndOccupation())
                .paymentForm(occupation.getPaymentForm())
                .peopleCount(occupation.getPeopleCount())
                .board(this.toBoardDTO(occupation.getBoard()))
                .build();
    }


    public BoardDTO toBoardDTO(Board board) {
        return BoardDTO.builder()
                .location(board.getLocation())
                .number(board.getNumber())
                .build();
    }
}
