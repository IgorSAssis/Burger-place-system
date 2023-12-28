package br.com.senior.burger_place.domain.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
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

    public Board(BoardRegisterData data) {
        this.active = true;
        this.number = data.number();
        this.capacity = data.capacity();
        this.location = data.location();
    }

    public void updateInformation(BoardUpdateData data) {
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
