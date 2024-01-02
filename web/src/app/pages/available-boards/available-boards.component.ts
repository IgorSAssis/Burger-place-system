import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  Board,
  BoardLocation,
  BoardLocationType,
  BoardService,
  CapacityFilter,
  FetchBoardsFilters,
  LocationFilter,
} from '../../services/board.service';

import { BreadcrumbComponent } from '../../components/breadcrumb/breadcrumb.component';
import { SvgImageComponent } from '../../components/svg-image/svg-image.component';
import { ModalService } from '../../services/modal.service';
import {
  CreateOccupationDTO,
  OccupationService,
} from '../../services/occupation.service';
import { PaginationComponent } from '../../components/pagination/pagination.component';

@Component({
  selector: 'app-available-boards',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    BreadcrumbComponent,
    SvgImageComponent,
    FormsModule,
    PaginationComponent,
  ],
  templateUrl: './available-boards.component.html',
  styleUrl: './available-boards.component.css',
})
export class AvailableBoardsComponent implements OnInit {
  private occupationService: OccupationService = inject(OccupationService);
  private boardService: BoardService = inject(BoardService);
  private modalService: ModalService = inject(ModalService);
  private router: Router = inject(Router);

  private _capacityFilterItems: CapacityFilter[];
  private _locationFilterItems: LocationFilter[];

  private _boards: Board[];
  private _activeLocationFilterId: BoardLocationType | null;
  private _activeCapacityFilter: number | null;
  private _totalPages: number;
  private _currentPage: number;

  constructor() {
    this._currentPage = 0;
    this._totalPages = 0;
    this._boards = [];
    this._activeCapacityFilter = null;
    this._activeLocationFilterId = null;
    this._locationFilterItems = this.boardService.getAvailableLocationFilters();
    this._capacityFilterItems = this.boardService.getAvailableCapacityFilters();
  }

  ngOnInit(): void {
    this.fetchBoards();
  }

  public get capacityFilterItems() {
    return this._capacityFilterItems;
  }

  public get locationFilterItems() {
    return this._locationFilterItems;
  }

  public get boards() {
    return this._boards;
  }

  public get activeCapacityFilterValue() {
    return this._activeCapacityFilter;
  }

  public set activeCapacityFilterValue(capacity: number | null) {
    this._activeCapacityFilter = capacity;
  }

  public get activeLocationFilterId() {
    return this._activeLocationFilterId;
  }

  public set activeLocationFilterId(id: BoardLocationType | null) {
    this._activeLocationFilterId = id;
  }

  public get totalPages() {
    return this._totalPages;
  }

  resetFilters() {
    this._activeCapacityFilter = null;
    this._activeLocationFilterId = null;
    this.fetchBoards();
  }

  applyFilters() {
    this.fetchBoards();
  }

  activateCapacityFilter(capacity: number) {
    this._activeCapacityFilter = capacity;
  }

  activateLocationFilter(id: string) {
    if (id in BoardLocation) {
      this._activeLocationFilterId = id as BoardLocationType;
    }
  }

  getBoardLocationText(type: BoardLocationType) {
    return BoardLocation[type];
  }

  onSelectBoard(board: Board) {
    console.log(board);
    this.modalService.openCreateOccupationModal().subscribe({
      next: (peopleCount) => {
        this.createOccupation(board.id, peopleCount);
      },
    });
  }

  changeCurrentPage(page: number) {
    this._currentPage = page;
    this.fetchBoards();
  }

  private fetchBoards() {
    const options: FetchBoardsFilters = {
      page: this._currentPage,
    };

    if (this._activeCapacityFilter) {
      options.capacity = this._activeCapacityFilter;
    }

    if (this._activeLocationFilterId) {
      options.location = this._activeLocationFilterId;
    }

    this.boardService.fetchBoards(options).subscribe({
      next: (data) => {
        console.log(data.boards);
        this._boards = data.boards;
        this._totalPages = data.totalPages;
      },
      error: (error) => console.error(error),
      complete: () => console.log('Successfull'),
    });
  }

  private createOccupation(boardId: number, peopleCount: number) {
    if (peopleCount <= 0) {
      return alert('A quantidade de pessoas deve ser maior ou igual a 1');
    }

    const beginOccupation = new Date();
    beginOccupation.setSeconds(beginOccupation.getSeconds() - 1);

    const formattedBeginOccupationDate = `${beginOccupation.getFullYear()}-${(
      beginOccupation.getMonth() + 1
    )
      .toString()
      .padStart(2, '0')}-${beginOccupation
      .getDate()
      .toString()
      .padStart(2, '0')}T${beginOccupation
      .getHours()
      .toString()
      .padStart(2, '0')}:${beginOccupation
      .getMinutes()
      .toString()
      .padStart(2, '0')}:${beginOccupation
      .getSeconds()
      .toString()
      .padStart(2, '0')}`;

    console.log(formattedBeginOccupationDate);

    const createOccupationDTO: CreateOccupationDTO = {
      beginOccupation: formattedBeginOccupationDate,
      boardId,
      peopleCount,
    };

    this.occupationService.createOccupation(createOccupationDTO).subscribe({
      next: (occupationId) => {
        console.log(occupationId);
        this.fetchBoards();
        this.modalService.closeModal();
        this.router.navigate([`customers/${occupationId}`]);
      },
      error: (error) => {
        console.log(error);
      },
    });
  }
}
