import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-order-item-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './create-or-edit-order-item-modal.component.html',
  styleUrl: './create-or-edit-order-item-modal.component.css',
})
export class CreateOrEditOrderItemModalComponent {
  private _type: ModalType;
  private _itemAmount: number;
  private _itemObservation: string | null;

  @Output()
  public submitEvent: EventEmitter<CreateOrEditOrderItemOutputData>;

  constructor() {
    this._itemAmount = 1;
    this._itemObservation = null;
    this._type = 'CREATE';
    this.submitEvent = new EventEmitter();
  }

  public get itemAmount() {
    return this._itemAmount;
  }

  public set itemAmount(amount: number) {
    this._itemAmount = amount;
  }

  public get itemObservation() {
    return this._itemObservation;
  }

  public set itemObservation(observation: string | null) {
    this._itemObservation = observation;
  }

  public get type() {
    return this._type;
  }

  public set type(type: ModalType) {
    this._type = type;
  }

  onClick() {
    this.submitEvent.emit({
      itemAmount: this._itemAmount,
      itemObservation: this._itemObservation,
    });
  }
}

export interface CreateOrEditOrderItemOutputData {
  itemAmount: number;
  itemObservation: string | null;
}

export type ModalType = 'CREATE' | 'EDIT';
