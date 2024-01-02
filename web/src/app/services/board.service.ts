import { Injectable } from '@angular/core';
import { Observable, from, map, of } from 'rxjs';
import { api } from '../lib/api';

@Injectable({
  providedIn: 'root',
})
export class BoardService {
  constructor() {}

  fetchBoards(options?: FetchBoardsFilters): Observable<BoardListing> {
    const params: FetchBoardsFilters = {};

    if (options) {
      params.page = typeof options.page === 'undefined' ? 0 : options.page;

      if (options.capacity) {
        params.capacity = options.capacity;
      }

      if (options.location) {
        params.location = options.location;
      }
    }

    return from(api.get('boards', { params })).pipe(
      map((response) => {
        return {
          boards: response.data.content,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages,
        };
      })
    );
  }

  getAvailableCapacityFilters(): CapacityFilter[] {
    return [
      { text: '2 pessoas', capacity: 2 },
      { text: '4 pessoas', capacity: 4 },
      { text: '8 pessoas', capacity: 8 },
      { text: '10 pessoas', capacity: 10 },
    ];
  }

  getAvailableLocationFilters(): LocationFilter[] {
    return Object.entries(BoardLocation);
  }
}

export interface FetchBoardsFilters {
  page?: number;
  location?: BoardLocationType;
  capacity?: number;
}

export enum BoardLocation {
  VARANDA = 'Varanda',
  SACADA = 'Sacada',
  TERRACO = 'Terraço',
  AREA_INTERNA = 'Area interna',
}

export type BoardLocationType = keyof typeof BoardLocation;

export interface Board {
  id: number;
  number: number;
  capacity: number;
  location: BoardLocationType;
  occupied: boolean;
}

interface BoardListing {
  boards: Board[];
  totalElements: number;
  totalPages: number;
}

export interface CapacityFilter {
  text: string;
  capacity: number;
}

export type LocationFilter = [string, BoardLocation];
