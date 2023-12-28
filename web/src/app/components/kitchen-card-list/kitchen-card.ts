type CardType = 'IN_PROGRESS' | 'TO_DO';

interface CardItems {
  id: number;
  amount: number;
  name: string;
  description?: string;
  observation?: string;
}

export interface KitchenCard {
  cardId: number;
  items: CardItems[];
  boardNumber: number;
  openedAt: string;
  type: CardType;
}
