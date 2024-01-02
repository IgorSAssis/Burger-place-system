import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-edit-order-item-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './edit-order-item-modal.component.html',
  styleUrl: './edit-order-item-modal.component.css',
})
export class EditOrderItemModalComponent {
  private _showEditOrderItemButton: boolean;

  @Output()
  public editOrderItemActionEvent: EventEmitter<EditOrderItemAction>;

  constructor() {
    this._showEditOrderItemButton = true;
    this.editOrderItemActionEvent = new EventEmitter();
  }

  public get showEditOrderItemButton() {
    return this._showEditOrderItemButton;
  }

  hideEditButton() {
    this._showEditOrderItemButton = false;
  }

  doAction(action: EditOrderItemAction) {
    this.editOrderItemActionEvent.emit(action);
  }
}

export type EditOrderItemAction = 'CANCEL' | 'EDIT';
