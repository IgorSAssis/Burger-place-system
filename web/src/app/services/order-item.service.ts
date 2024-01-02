import { Injectable } from '@angular/core';
import { Observable, from, map } from 'rxjs';
import { api } from '../lib/api';

@Injectable({
  providedIn: 'root',
})
export class OrderItemService {
  constructor() {}

  fetchOrderItems(status: OrderItemStatusType): Observable<OrderItemListing> {
    return from(api.get('order-items', { params: { status } })).pipe(
      map((response) => {
        return {
          totalElements: response.data.totalElements,
          orderItems: response.data.content,
        };
      })
    );
  }

  deliverOrderItem(itemId: number, occupationId: number) {
    return from(
      api.patch(`/occupations/${occupationId}/items/${itemId}/deliver`)
    ).pipe(map(() => {}));
  }

  finishPreparationOrderItem(itemId: number, occupationId: number) {
    return from(
      api.patch(
        `/occupations/${occupationId}/items/${itemId}/finish-preparation`
      )
    ).pipe(map(() => {}));
  }

  startPreparationOrderItem(itemId: number, occupationId: number) {
    return from(
      api.patch(
        `/occupations/${occupationId}/items/${itemId}/start-preparation`
      )
    ).pipe(map(() => {}));
  }
}

export interface KitchenOrderItem {
  id: number;
  productName: string;
  ingredients: string;
  amount: number;
  observation: string | null;
  boardNumber: number;
  status: OrderItemStatusType;
  occupationId: number;
}

export interface OrderItemListing {
  orderItems: KitchenOrderItem[];
  totalElements: number;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  ingredients: string;
  itemValue: number;
  amount: number;
  status: OrderItemStatusType;
  observation: string | null;
}

export enum OrderItemStatus {
  RECEBIDO = 'Recebido',
  EM_ANDAMENTO = 'Em andamento',
  PRONTO = 'Pronto',
  ENTREGUE = 'Entregue',
  CANCELADO = 'Cancelado',
}

export type OrderItemStatusType = keyof typeof OrderItemStatus;
