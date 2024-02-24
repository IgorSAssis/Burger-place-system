package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.occupation.OccupationService;
import br.com.senior.burger_place.domain.occupation.dto.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("occupations")
public class OccupationController {
    @Autowired
    private OccupationService occupationService;

    @GetMapping
    public ResponseEntity<Page<ListOccupationDTO>> listOccupations(Pageable pageable) {
        return ResponseEntity.ok(this.occupationService.listOccupations(pageable));
    }

    @GetMapping("/{occupationId}")
    public ResponseEntity<OccupationDTO> showOccupation(
            @PathVariable
            UUID occupationId
    ) {
        Optional<OccupationDTO> orderOptional = this.occupationService.showOccupation(occupationId);

        return orderOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping
    @Transactional
    public ResponseEntity<OccupationDTO> createOccupation(
            @RequestBody
            @Valid
            CreateOccupationDTO orderDTO,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        OccupationDTO occupationDTO = this.occupationService.createOccupation(orderDTO);

        URI uri = uriComponentsBuilder
                .path("/occupations/{occupationId}")
                .buildAndExpand(occupationDTO.id())
                .toUri();

        return ResponseEntity.created(uri).body(occupationDTO);
    }

    @PostMapping("/{occupationId}/items")
    @Transactional
    public ResponseEntity<Void> addOrderItems(
            @PathVariable
            UUID occupationId,
            @RequestBody
            @Valid
            AddOrderItemsDTO itemsDTO
    ) {
        this.occupationService.addOrderItems(occupationId, itemsDTO);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{occupationId}/items")
    @Transactional
    public ResponseEntity<Void> removeOrderItems(
            @PathVariable
            UUID occupationId,
            @RequestBody
            @Valid
            RemoveOrderItemsDTO itemsDTO
    ) {
        this.occupationService.removeOrderItems(occupationId, itemsDTO);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{occupationId}/items/{itemId}")
    @Transactional
    public ResponseEntity updateOrder(
            @PathVariable
            UUID occupationId,
            @PathVariable
            UUID itemId,
            @RequestBody
            @Valid
            UpdateOrderItemDTO itemDTO
    ) {
        this.occupationService.updateOrderItem(occupationId, itemId, itemDTO);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{occupationId}")
    @Transactional
    public ResponseEntity inactivateOrder(
            @PathVariable
            UUID occupationId
    ) {
        this.occupationService.inactivateOccupation(occupationId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{occupationId}/items/{itemId}/start-preparation")
    @Transactional
    public ResponseEntity startOrderItemPreparation(
            @PathVariable
            UUID occupationId,
            @PathVariable
            UUID itemId
    ) {
        this.occupationService.startOrderItemPreparation(occupationId, itemId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{occupationId}/items/{itemId}/finish-preparation")
    @Transactional
    public ResponseEntity finishOrderItemPreparation(
            @PathVariable
            UUID occupationId,
            @PathVariable
            UUID itemId
    ) {
        this.occupationService.finishOrderItemPreparation(occupationId, itemId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{occupationId}/items/{itemId}/deliver")
    @Transactional
    public ResponseEntity deliverOrderItemPreparation(
            @PathVariable
            UUID occupationId,
            @PathVariable
            UUID itemId
    ) {
        this.occupationService.deliverOrderItem(occupationId, itemId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{occupationId}/items/{itemId}/cancel")
    @Transactional
    public ResponseEntity cancelOrderItem(
            @PathVariable
            UUID occupationId,
            @PathVariable
            UUID itemId
    ) {
        this.occupationService.cancelOrderItem(occupationId, itemId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{occupationId}/finish")
    @Transactional
    public ResponseEntity finishOccupation(
            @PathVariable
            UUID occupationId,
            @RequestBody
            @Valid
            FinishOccupationDTO occupationDTO
    ) {
        this.occupationService.finishOccupation(occupationId, occupationDTO);

        return ResponseEntity.noContent().build();
    }
}
