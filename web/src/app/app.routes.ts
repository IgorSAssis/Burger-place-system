import { Routes } from '@angular/router';
import { AvailableBoardsComponent } from './pages/available-boards/available-boards.component';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { CustomerComponent } from './pages/customer/customer.component';
import { KitchenComponent } from './pages/kitchen/kitchen.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingPageComponent,
    title: 'Landing page',
  },
  {
    path: 'available-boards',
    component: AvailableBoardsComponent,
    title: 'Available boards page',
  },
  {
    path: 'customers',
    component: CustomerComponent,
    title: 'Customer area',
  },
  {
    path: 'kitchen',
    component: KitchenComponent,
    title: 'Kitchen area',
  },
];
