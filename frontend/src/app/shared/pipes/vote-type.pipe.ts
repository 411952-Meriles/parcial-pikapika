import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'voteType',
  standalone: true
})
export class VoteTypePipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'POSITIVE':
        return 'Positivo';
      case 'NEGATIVE':
        return 'Negativo';
      case 'ABSTENCY':
        return 'Abstención';
      default:
        return value;
    }
  }
}