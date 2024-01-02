import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  OrderItemListing,
  OrderItemService,
  OrderItemStatusType,
} from '../../services/order-item.service';

import { BreadcrumbComponent } from '../../components/breadcrumb/breadcrumb.component';
import { SvgImageComponent } from '../../components/svg-image/svg-image.component';

import { StringPaddingPipe } from '../../pipes/string-padding.pipe';

@Component({
  selector: 'app-kitchen',
  standalone: true,
  imports: [
    CommonModule,
    BreadcrumbComponent,
    SvgImageComponent,
    StringPaddingPipe,
  ],
  templateUrl: './kitchen.component.html',
  styleUrl: './kitchen.component.css',
})
export class KitchenComponent implements OnInit {
  private orderItemService: OrderItemService = inject(OrderItemService);

  private _readyDeliverOrderItems: OrderItemListing;
  private _inProgressOrderItems: OrderItemListing;
  private _pendingOrderItems: OrderItemListing;
  private _canceledOrderItems: OrderItemListing;

  private _expandedSections: boolean[];

  constructor() {
    this._expandedSections = [true, true, true, true];
    this._readyDeliverOrderItems = { orderItems: [], totalElements: 0 };
    this._inProgressOrderItems = { orderItems: [], totalElements: 0 };
    this._pendingOrderItems = { orderItems: [], totalElements: 0 };
    this._canceledOrderItems = { orderItems: [], totalElements: 0 };
  }

  public get inProgressOrderItems() {
    return this._inProgressOrderItems;
  }

  public get pendingOrderItems() {
    return this._pendingOrderItems;
  }

  public get readyDeliverOrderItems() {
    return this._readyDeliverOrderItems;
  }

  public get canceledOrderItems() {
    return this._canceledOrderItems;
  }

  public get expandedSections() {
    return this._expandedSections;
  }

  ngOnInit(): void {
    this.fetchReadyToDeliverOrderItems();
    this.fetchInProgressOrderItems();
    this.fetchPendingOrderItems();
    this.fetchCanceledOrderItems();
  }

  deliverOrderItem(itemId: number, occupationId: number) {
    this.orderItemService.deliverOrderItem(itemId, occupationId).subscribe({
      next: () => {
        this.fetchReadyToDeliverOrderItems();
      },
      error: (error) => {
        console.log(error);
      },
      complete: () => {
        console.log('Successfull');
      },
    });
  }

  finishOrderItem(itemId: number, occupationId: number) {
    this.orderItemService
      .finishPreparationOrderItem(itemId, occupationId)
      .subscribe({
        next: () => {
          this.fetchReadyToDeliverOrderItems();
          this.fetchInProgressOrderItems();
        },
        error: (error) => {
          console.log(error);
        },
        complete: () => {
          console.log('Successfull');
        },
      });
  }

  startOrderItem(itemId: number, occupationId: number) {
    this.orderItemService
      .startPreparationOrderItem(itemId, occupationId)
      .subscribe({
        next: () => {
          this.fetchPendingOrderItems();
          this.fetchInProgressOrderItems();
        },
        error: (error) => {
          console.log(error);
        },
        complete: () => {
          console.log('Successfull');
        },
      });
  }

  toggleOrderItemSection(index: number) {
    this._expandedSections[index] = !this._expandedSections[index];
  }

  private fetchOrderItems(
    status: OrderItemStatusType,
    nextCallback: (data: OrderItemListing) => void
  ) {
    this.orderItemService.fetchOrderItems(status).subscribe({
      next: nextCallback,
      error: (error) => console.error(error),
      complete: () => console.log('Successfull'),
    });
  }

  private fetchInProgressOrderItems() {
    this.fetchOrderItems('EM_ANDAMENTO', (data) => {
      this._inProgressOrderItems = data;
    });
  }

  private fetchPendingOrderItems() {
    this.fetchOrderItems('RECEBIDO', (data) => {
      this._pendingOrderItems = data;
    });
  }

  private fetchReadyToDeliverOrderItems() {
    this.fetchOrderItems('PRONTO', (data) => {
      this._readyDeliverOrderItems = data;
    });
  }

  private fetchCanceledOrderItems() {
    this.fetchOrderItems('CANCELADO', (data) => {
      this._canceledOrderItems = data;
    });
  }
}
