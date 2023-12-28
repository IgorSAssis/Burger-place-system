import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'stringPadding',
  standalone: true,
})
export class StringPaddingPipe implements PipeTransform {
  transform(input: number, length: number, paddingString: string): string {
    return String(input).padStart(length, paddingString);
  }
}
