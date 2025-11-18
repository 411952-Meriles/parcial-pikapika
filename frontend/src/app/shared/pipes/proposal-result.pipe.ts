import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'proposalResult',
  standalone: true
})
export class ProposalResultPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'Approved':
        return 'Aprobada';
      case 'Rejected':
        return 'Rechazada';
      case 'Draw':
        return 'Empate';
      default:
        return value;
    }
  }
}