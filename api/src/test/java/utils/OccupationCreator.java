package utils;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.PaymentForm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static utils.BoardCreator.createBoard;

public class OccupationCreator {
    public static Occupation createOccupation() {
        return Occupation.builder()
                .id(UUID.randomUUID())
                .beginOccupation(LocalDateTime.now())
                .endOccupation(LocalDateTime.now().plusMinutes(20))
                .board(createBoard())
                .peopleCount(2)
                .customers(Set.of())
                .orderItems(List.of())
                .paymentForm(PaymentForm.CARTAO_CREDITO)
                .active(true)
                .build();
    }
}
