import { Component, Input } from '@angular/core';
import { FilterItems } from './filter-items';
import { CommonModule } from '@angular/common';

type InputType = 'radio' | 'checkbox';

@Component({
  selector: 'app-filter-block',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './filter-block.component.html',
  styleUrl: './filter-block.component.css',
})
export class FilterBlockComponent {
  private _title: string | undefined;
  private _inputType: InputType | undefined;
  private _groupName: string | undefined;
  private _items: FilterItems[] = [];

  @Input()
  public set title(title: string) {
    this._title = title;
  }

  public get title() {
    return this._title ?? '';
  }

  @Input()
  public set items(items: FilterItems[]) {
    this._items = items;
  }

  public get items() {
    return this._items;
  }

  @Input()
  public set inputType(type: InputType) {
    this._inputType = type;
  }

  public get inputType() {
    return this._inputType ?? 'radio';
  }

  @Input()
  public set groupName(groupName: string) {
    this._groupName = groupName;
  }

  public get groupName() {
    return this._groupName ?? '';
  }
}
