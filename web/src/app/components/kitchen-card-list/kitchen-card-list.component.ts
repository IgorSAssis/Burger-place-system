import { Component, Input } from '@angular/core';
import { KitchenCard } from './kitchen-card';
import { CommonModule } from '@angular/common';
import { StringPaddingPipe } from '../../pipes/string-padding.pipe';

@Component({
  selector: 'app-kitchen-card-list',
  standalone: true,
  imports: [CommonModule, StringPaddingPipe],
  templateUrl: './kitchen-card-list.component.html',
  styleUrl: './kitchen-card-list.component.css',
})
export class KitchenCardListComponent {
  private _kitchenCards: KitchenCard[] = [];

  @Input()
  public set kitchenCards(cards: KitchenCard[]) {
    this._kitchenCards = cards;
  }

  public get kitchenCards() {
    return this._kitchenCards;
  }
}
