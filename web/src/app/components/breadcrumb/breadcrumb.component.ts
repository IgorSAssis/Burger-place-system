import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-breadcrumb',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './breadcrumb.component.html',
  styleUrl: './breadcrumb.component.css',
})
export class BreadcrumbComponent {
  private _currentURI: string | undefined;

  @Input()
  public set currentURI(iri: string) {
    this._currentURI = iri;
  }

  public get currentURI() {
    return this._currentURI ?? '';
  }
}
