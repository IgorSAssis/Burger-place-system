import { Component } from '@angular/core';
import { SvgImageComponent } from '../../components/svg-image/svg-image.component';
import { CommonModule } from '@angular/common';
import { BreadcrumbComponent } from '../../components/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule, SvgImageComponent, BreadcrumbComponent],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css',
})
export class CustomerComponent {
  expandedItems: boolean[] = [false, false, false];
  activeMenu: number = 0;

  toggleExpanded(index: number) {
    this.expandedItems = this.expandedItems.map((expanded, _index) => {
      return _index === index ? !expanded : false;
    });
  }

  changeActiveMenu(index: number) {
    this.activeMenu = index;
  }
}
