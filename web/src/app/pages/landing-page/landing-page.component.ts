import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SvgImageComponent } from '../../components/svg-image/svg-image.component';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [RouterLink, SvgImageComponent],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css',
})
export class LandingPageComponent {}
