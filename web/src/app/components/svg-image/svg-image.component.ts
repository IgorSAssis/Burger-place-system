import { Component, HostBinding, Input } from '@angular/core';

@Component({
  selector: 'app-svg-image',
  standalone: true,
  imports: [],
  templateUrl: './svg-image.component.html',
  styleUrl: './svg-image.component.css',
})
export class SvgImageComponent {
  @HostBinding('style.mask-image')
  private _path: string | undefined;
  @HostBinding('style.width')
  private _width: string | undefined;
  @HostBinding('style.height')
  private _height: string | undefined;
  @HostBinding('style.background-color')
  private _color: string | undefined;

  @Input()
  public set path(path: string) {
    this._path = `url("${path}")`;
  }

  @Input()
  public set width(width: string) {
    this._width = width;
  }

  @Input()
  public set height(height: string) {
    this._height = height;
  }

  @Input()
  public set color(color: string) {
    this._color = color;
  }
}
