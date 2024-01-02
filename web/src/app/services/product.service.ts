import { Injectable } from '@angular/core';

import { api } from '../lib/api';
import { Observable, from, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor() {}

  fetchProducts(options?: FetchProductFilters): Observable<ProductListing> {
    const params: FetchProductFilters = {};

    if (options) {
      if (options.category) {
        params.category = options.category;
      }

      if (options.page) {
        params.page = options.page;
      }
    }

    return from(api.get('products', { params })).pipe(
      map((response) => {
        return {
          products: response.data.content,
          totalElements: response.data.totalElements,
          totalPages: response.data.totalPages,
        };
      })
    );
  }
}

interface FetchProductFilters {
  page?: number;
  category?: CategoryType;
}

export enum Category {
  BURGER = 'Hamburguers',
  DRINK = 'Bebidas',
  ENTRY = 'Entradas',
  DESSERT = 'Sobremesas',
  SIDE_DISHES = 'Acompanhamentos',
}

export type CategoryType = keyof typeof Category;

export interface Product {
  id: number;
  name: string;
  price: number;
  ingredients: string;
  category: Category;
  url: string | null;
}

interface ProductListing {
  totalPages: number;
  totalElements: number;
  products: Product[];
}
