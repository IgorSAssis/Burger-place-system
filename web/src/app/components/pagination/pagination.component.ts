import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule, SvgImageComponent],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css',
})
export class PaginationComponent {
  private readonly MAX_WINDOW_SIZE = 8;

  private _totalPages: number;
  private _windowSize: number;
  private _pages: number[];
  private _activePage: number;
  @Output()
  private activePageEvent: EventEmitter<number>;

  constructor() {
    this._totalPages = 0;
    this._activePage = 0;
    this.activePageEvent = new EventEmitter();
    this._windowSize = 0;
    this._pages = [];
  }

  public get pages() {
    return this._pages;
  }

  public get totalPages() {
    return this._totalPages;
  }

  @Input()
  public set totalPages(totalPages: number) {
    this._totalPages = totalPages;
    this._windowSize =
      totalPages >= this.MAX_WINDOW_SIZE ? this.MAX_WINDOW_SIZE : totalPages;

    this._pages = Array.from(
      { length: this._windowSize },
      (_, index) => index + 1
    );
  }

  isPageActive(page: number) {
    return this._activePage === page - 1;
  }

  isFirstWindow() {
    return (
      this._windowSize < this.MAX_WINDOW_SIZE ||
      this._pages[7] <= this._windowSize
    );
  }

  isLastWindow() {
    return (
      this._pages[7] === this.totalPages ||
      this.pages.length < this.MAX_WINDOW_SIZE
    );
  }

  activatePage(page: number) {
    this._activePage = page - 1;
    this.activePageEvent.emit(this._activePage);
  }

  nextPage() {
    if (this._activePage + 1 !== this.totalPages) {
      if (this._activePage + 1 === this._pages[7]) {
        const nextWindowSize = this._totalPages - this._pages[7];

        this._pages = Array.from(
          { length: nextWindowSize > 8 ? this._windowSize : nextWindowSize },
          (_, index) => index + 1 * (this._activePage + 2)
        );
      }

      this._activePage++;
      this.activePageEvent.emit(this._activePage);
    }
  }

  prevPage() {
    if (this._activePage !== 0) {
      if (this._activePage < this._pages[0]) {
        const firstElement = this._activePage - 7;
        this._pages = Array.from(
          { length: this._windowSize },
          (_, index) => index + 1 * firstElement
        );
      }

      this._activePage--;
      this.activePageEvent.emit(this._activePage);
    }
  }

  firstWindow() {
    this._activePage = 0;
    this._pages = Array.from(
      { length: this._windowSize },
      (_, index) => index + 1
    );
    this.activePageEvent.emit(this._activePage);
  }

  lastWindow() {
    this._activePage = this.totalPages - 1;
    this.activePageEvent.emit(this._activePage);
    const pagesToRender = (this._activePage + 1) % this.MAX_WINDOW_SIZE;

    if (pagesToRender === 0) {
      this._pages = Array.from(
        { length: this._windowSize },
        (_, index) => index + 1 * (this.totalPages - 7)
      );
      return;
    }

    const lastElementPreviousWindow = this._activePage - (pagesToRender - 1);

    this._pages = Array.from(
      { length: pagesToRender },
      (_, index) => lastElementPreviousWindow + (index + 1)
    );
  }
}
