package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.BoardRegisterDTO;
import br.com.senior.burger_place.domain.board.dto.BoardUpdateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "boards")
@Entity(name = "Board")
public class Board {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private Integer number;
    private Integer capacity;
    @Enumerated(EnumType.STRING)
    private BoardLocation location;
    private boolean active;

    public Board(BoardRegisterDTO data) {
        this.active = true;
        this.number = data.number();
        this.capacity = data.capacity();
        this.location = data.location();
    }

    public void updateInformation(BoardUpdateDTO data) {
        if (data.location() != null){
            this.location = data.location();
        }
        if (data.capacity() != null){
            this.capacity = data.capacity();
        }
        if (data.number() != null){
            this.number = data.number();
        }
    }
    public void inactivate() {
        this.active = false;
    }
}
