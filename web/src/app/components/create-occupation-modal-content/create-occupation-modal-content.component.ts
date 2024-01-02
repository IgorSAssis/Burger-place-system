import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-modal-content',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-occupation-modal-content.component.html',
  styleUrl: './create-occupation-modal-content.component.css',
})
export class CreateOccupationModalContentComponent {
  private _amountPeople: number;
  @Output()
  public amountPeopleEventEmitter: EventEmitter<number>;

  constructor() {
    this._amountPeople = 0;
    this.amountPeopleEventEmitter = new EventEmitter();
  }

  public get amountPeople() {
    return this._amountPeople;
  }

  public set amountPeople(amountPeople: number) {
    this._amountPeople = amountPeople;
  }

  createOccupation() {
    this.amountPeopleEventEmitter.emit(this._amountPeople);
  }
}
