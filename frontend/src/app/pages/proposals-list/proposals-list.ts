import { Component, OnInit } from '@angular/core';
import { GenericTableComponent } from '../../shared/components/generic-table/generic-table';
import { GenericCardComponent } from '../../shared/components/generic-card/generic-card';
import { Proposal } from '../../shared/interfaces/proposal.interface';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { ProposalService } from '../../core/services/proposal.service';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'app-proposals-list',
  standalone: true,
  imports: [CommonModule, GenericTableComponent, GenericCardComponent, MatButtonModule, RouterModule],
  templateUrl: './proposals-list.html',
  styleUrls: ['./proposals-list.css']
})
export class ProposalsListComponent implements OnInit {
  proposals$: Observable<Proposal[]> = of([]);
  
  displayedColumns = ['title', 'description', 'startDate', 'endDate', 'actions'];
  columnHeaders = {
    title: 'Título',
    description: 'Descripción',
    startDate: 'Fecha de Inicio',
    endDate: 'Fecha de Fin',
    actions: 'Acciones'
  };
  
  isViewingDetail = false;
  proposalForDetail: Proposal | null = null;

  constructor(private proposalService: ProposalService) {}

  ngOnInit(): void {
    this.proposals$ = this.proposalService.getProposals();
  }

  handleAction(event: { action: string, element: Proposal }) {
    if (event.action === 'view') {
      this.viewDetail(event.element.id);
    }
  }

  viewDetail(id: number) {
    this.proposalService.getProposal(id).subscribe(proposal => {
      this.proposalForDetail = proposal;
      this.isViewingDetail = true;
    });
  }

  hideDetail() {
    this.isViewingDetail = false;
    this.proposalForDetail = null;
  }
}