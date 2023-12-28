import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FilterBlockComponent } from '../../components/filter-block/filter-block.component';
import { FilterItems } from '../../components/filter-block/filter-items';
import { BreadcrumbComponent } from '../../components/breadcrumb/breadcrumb.component';
import { BoardListComponent } from '../../components/board-list/board-list.component';
import { Board } from '../../components/board-list/board';

@Component({
  selector: 'app-available-boards',
  standalone: true,
  imports: [
    RouterLink,
    FilterBlockComponent,
    BreadcrumbComponent,
    BoardListComponent,
  ],
  templateUrl: './available-boards.component.html',
  styleUrl: './available-boards.component.css',
})
export class AvailableBoardsComponent {
  private _capacityFilterItems: FilterItems[] = [
    { id: 'capacity-01', text: '2 pessoas' },
    { id: 'capacity-02', text: '4 pessoas' },
    { id: 'capacity-03', text: '8 pessoas' },
    { id: 'capacity-04', text: '10 pessoas' },
    { id: 'capacity-05', text: '+10 pessoas' },
  ];
  private _localizationFilterItems: FilterItems[] = [
    { id: 'VARANDA', text: 'Varanda' },
    { id: 'SACADA', text: 'Sacada' },
    { id: 'TERRACO', text: 'Terraço' },
    { id: 'AREA_INTERNA', text: 'Area interna' },
  ];
  private _statusFilterItems: FilterItems[] = [
    { id: 'situation-opt-1', text: 'Ocupada' },
    { id: 'situation-opt-2', text: 'Livre' },
  ];
  private _boards: Board[] = [
    {
      id: 1,
      boardNumber: 1,
      capacity: 2,
      localization: 'Area interna',
      occupied: true,
    },
    {
      id: 2,
      boardNumber: 2,
      capacity: 2,
      localization: 'Area interna',
      occupied: false,
    },
    {
      id: 3,
      boardNumber: 3,
      capacity: 2,
      localization: 'Area interna',
      occupied: false,
    },
    {
      id: 4,
      boardNumber: 4,
      capacity: 2,
      localization: 'Area interna',
      occupied: false,
    },
    {
      id: 5,
      boardNumber: 5,
      capacity: 2,
      localization: 'Area interna',
      occupied: false,
    },
    {
      id: 6,
      boardNumber: 6,
      capacity: 2,
      localization: 'Area interna',
      occupied: false,
    },
    {
      id: 7,
      boardNumber: 7,
      capacity: 2,
      localization: 'Area interna',
      occupied: false,
    },
    {
      id: 8,
      boardNumber: 8,
      capacity: 2,
      localization: 'Area interna',
      occupied: false,
    },
    {
      id: 9,
      boardNumber: 9,
      capacity: 2,
      localization: 'Area interna',
      occupied: false,
    },
  ];

  public get capacityFilterItems() {
    return this._capacityFilterItems;
  }

  public get localizationFilterItems() {
    return this._localizationFilterItems;
  }

  public get statusFilterItems() {
    return this._statusFilterItems;
  }

  public get boards() {
    return this._boards;
  }
}
