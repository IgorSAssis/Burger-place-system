import { Routes } from '@angular/router';
import { AvailableBoardsComponent } from './pages/available-boards/available-boards.component';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';

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
];
