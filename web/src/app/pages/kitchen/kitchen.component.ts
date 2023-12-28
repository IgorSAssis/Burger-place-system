import { Component } from '@angular/core';
import { BreadcrumbComponent } from '../../components/breadcrumb/breadcrumb.component';
import { SvgImageComponent } from '../../components/svg-image/svg-image.component';
import { KitchenCardListComponent } from '../../components/kitchen-card-list/kitchen-card-list.component';
import { KitchenCard } from '../../components/kitchen-card-list/kitchen-card';

@Component({
  selector: 'app-kitchen',
  standalone: true,
  imports: [BreadcrumbComponent, SvgImageComponent, KitchenCardListComponent],
  templateUrl: './kitchen.component.html',
  styleUrl: './kitchen.component.css',
})
export class KitchenComponent {
  private _inProgressCards: KitchenCard[] = [
    {
      cardId: 1,
      items: [
        {
          id: 1,
          amount: 2,
          name: 'Duas carnes',
          description:
            'Hamburguer duplo de alcatra, tomate, alface, queijo mussarela, queijo cheddar',
          observation: 'sem maionese',
        },
        {
          id: 2,
          amount: 2,
          name: 'Porção de batata frita',
        },
      ],
      boardNumber: 14,
      openedAt: '14:00',
      type: 'IN_PROGRESS',
    },
    {
      cardId: 2,
      items: [
        {
          id: 3,
          amount: 2,
          name: 'Duas carnes',
          description:
            'Hamburguer duplo de alcatra, tomate, alface, queijo mussarela, queijo cheddar',
        },
        {
          id: 4,
          amount: 2,
          name: 'Porção de batata frita',
        },
      ],
      boardNumber: 17,
      openedAt: '13:00',
      type: 'IN_PROGRESS',
    },
    {
      cardId: 3,
      items: [
        {
          id: 5,
          amount: 2,
          name: 'Duas carnes',
          description:
            'Hamburguer duplo de alcatra, tomate, alface, queijo mussarela, queijo cheddar',
        },
        {
          id: 6,
          amount: 2,
          name: 'Porção de batata frita',
        },
      ],
      boardNumber: 17,
      openedAt: '15:00',
      type: 'IN_PROGRESS',
    },
    {
      cardId: 4,
      items: [
        {
          id: 7,
          amount: 2,
          name: 'Duas carnes',
          description:
            'Hamburguer duplo de alcatra, tomate, alface, queijo mussarela, queijo cheddar',
        },
        {
          id: 8,
          amount: 2,
          name: 'Porção de batata frita',
        },
      ],
      boardNumber: 10,
      openedAt: '11:00',
      type: 'IN_PROGRESS',
    },
  ];

  private _toDoCards: KitchenCard[] = [
    {
      cardId: 5,
      items: [
        {
          id: 11,
          amount: 2,
          name: 'Duas carnes',
          description:
            'Hamburguer duplo de alcatra, tomate, alface, queijo mussarela, queijo cheddar',
          observation: 'sem maionese',
        },
        {
          id: 12,
          amount: 2,
          name: 'Porção de batata frita',
        },
      ],
      boardNumber: 20,
      openedAt: '14:00',
      type: 'TO_DO',
    },
    {
      cardId: 6,
      items: [
        {
          id: 13,
          amount: 2,
          name: 'Duas carnes',
          description:
            'Hamburguer duplo de alcatra, tomate, alface, queijo mussarela, queijo cheddar',
        },
        {
          id: 14,
          amount: 2,
          name: 'Porção de batata frita',
        },
      ],
      boardNumber: 21,
      openedAt: '13:00',
      type: 'TO_DO',
    },
  ];

  public get inProgressCards() {
    return this._inProgressCards;
  }

  public get todoCards() {
    return this._toDoCards;
  }
}
