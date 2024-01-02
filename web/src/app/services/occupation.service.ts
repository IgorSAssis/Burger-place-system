import { Injectable } from '@angular/core';
import { Observable, from, map } from 'rxjs';

import { api } from '../lib/api';

import { BoardLocationType } from './board.service';
import { OrderItem } from './order-item.service';

@Injectable({
  providedIn: 'root',
})
export class OccupationService {
  constructor() {}

  createOccupation(dto: CreateOccupationDTO): Observable<number> {
    console.log('Creating new occupation');
    console.log(dto);

    return from(api.post('occupations', dto)).pipe(
      map((response) => {
        return response.data.id;
      })
    );
  }

  fetchOccupationById(occupationId: number): Observable<Occupation> {
    return from(api.get(`/occupations/${occupationId}`)).pipe(
      map((response) => {
        return {
          id: response.data.id,
          beginOccupation: response.data.beginOccupation,
          endOccupation: response.data.endOccupation,
          paymentForm: response.data.paymentForm,
          peopleCount: response.data.peopleCount,
          board: {
            number: response.data.board.number,
            location: response.data.board.location,
          },
          orderItems: response.data.orderItems,
          customers: response.data.customers,
        };
      })
    );
  }

  addOrderItems(
    occupationId: number,
    dto: CreateOrderItemsDTO
  ): Observable<void> {
    return from(api.post(`occupations/${occupationId}/items`, dto)).pipe(
      map(() => {})
    );
  }

  cancelOrderItem(occupationId: number, itemId: number): Observable<void> {
    return from(
      api.patch(`occupations/${occupationId}/items/${itemId}/cancel`)
    ).pipe(map(() => {}));
  }

  updateOrderItem(
    occupationId: number,
    itemId: number,
    dto: UpdateOrderItemDTO
  ): Observable<void> {
    return from(
      api.put(`occupations/${occupationId}/items/${itemId}`, dto)
    ).pipe(map(() => {}));
  }
}

export interface CreateOccupationDTO {
  beginOccupation: string;
  peopleCount: number;
  boardId: number;
}

export interface Occupation {
  id: number;
  beginOccupation: Date;
  endOccupation: Date | null;
  paymentForm: string | null;
  peopleCount: number;
  board: {
    number: number;
    location: BoardLocationType;
  };
  orderItems: OrderItem[];
  customers: [];
}

export interface CreateOrderItemsDTO {
  orderItems: Array<{
    amount: number;
    productId: number;
    observation: string | null;
  }>;
}

export interface UpdateOrderItemDTO {
  amount: number;
  observation: string | null;
}
