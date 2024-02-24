package br.com.senior.burger_place.domain.board;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "boards")
@Entity(name = "Board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Integer number;
    private Integer capacity;
    @Enumerated(EnumType.STRING)
    private BoardLocation location;
    @Builder.Default
    private Boolean active = true;
    @Builder.Default
    private Boolean occupied = false;

    public void update(
            BoardLocation newLocation,
            Integer newCapacity,
            Integer newBoardNumber
    ) {
        if (newLocation != null) {
            this.location = newLocation;
        }
        if (newCapacity != null) {
            this.capacity = newCapacity;
        }
        if (newBoardNumber != null) {
            this.number = newBoardNumber;
        }
    }

    public void inactivate() {
        if (!this.active) {
            throw new IllegalStateException("Board already inactive");
        }

        if (this.occupied) {
            throw new IllegalStateException("Cannot inactivate a occupied board");
        }

        this.active = false;
    }

    public void occupy() {
        if (this.occupied) {
            throw new IllegalStateException("Board already occupied");
        }

        this.occupied = true;
    }

    public void vacate() {
        if (!this.occupied) {
            throw new IllegalStateException("Board is not occupied");
        }

        this.occupied = false;
    }
}
