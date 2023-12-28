package br.com.senior.burger_place.utils;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.OrderItem;
import br.com.senior.burger_place.domain.occupation.PaymentForm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class OccupationTestFactory {
    public static Occupation openedOccupationFactory(Long id) {
        return OccupationTestFactory.openedOccupationFactory(
                id,
                BoardTestFactory.boardFactory(1L, 1, 2),
                Set.of(),
                List.of()
        );
    }

    public static Occupation openedOccupationFactory(Long id, List<OrderItem> orderItems) {
        return OccupationTestFactory.openedOccupationFactory(
                id,
                BoardTestFactory.boardFactory(1L, 1, 2),
                Set.of(),
                orderItems
        );
    }

    public static Occupation openedOccupationFactory(Long id, Board board, Set<Customer> customers) {
        return OccupationTestFactory.openedOccupationFactory(
                id,
                board,
                customers,
                List.of()
        );
    }

    private static Occupation openedOccupationFactory(Long id, Board board, Set<Customer> customers, List<OrderItem> orderItems) {
        return new Occupation(
                id,
                LocalDateTime.now(),
                null,
                2,
                PaymentForm.DINHEIRO,
                orderItems,
                board,
                customers,
                true
        );
    }

    public static Occupation closedOccupationFactory(Long id) {
        return new Occupation(
                id,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                2,
                PaymentForm.DINHEIRO,
                List.of(),
                BoardTestFactory.boardFactory(1L, 1, 2),
                Set.of(),
                true
        );
    }


}
