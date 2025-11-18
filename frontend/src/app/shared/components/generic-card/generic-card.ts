import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Proposal } from '../../interfaces/proposal.interface';
import { VoteType } from '../../enums/vote-type.enum';
import { VoteTypePipe } from '../../pipes/vote-type.pipe';
import { ProposalResultPipe } from '../../pipes/proposal-result.pipe';
import { ProposalService } from '../../../core/services/proposal.service';

@Component({
  selector: 'app-generic-card',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    VoteTypePipe,
    ProposalResultPipe
  ],
  templateUrl: './generic-card.html',
  styleUrls: ['./generic-card.css']
})
export class GenericCardComponent implements OnInit {
  @Input() selectedProposal: Proposal | null = null;
  
  voteForm: FormGroup;
  isSubmitting = false;
  voteTypes = Object.values(VoteType);
  voteMessage = '';
  voteMessageType: 'success' | 'error' | '' = '';

  constructor(
    private fb: FormBuilder,
    private proposalService: ProposalService
  ) {
    this.voteForm = this.fb.group({
      userId: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
      vote: ['', [Validators.required]]
    });
  }

  ngOnInit() {}

  isProposalOpen(): boolean {
    if (!this.selectedProposal) return false;
    
    const now = new Date();
    const startDate = new Date(this.selectedProposal.startDate);
    const endDate = new Date(this.selectedProposal.endDate);
    
    return now >= startDate && now <= endDate;
  }

  hasProposalStarted(): boolean {
    if (!this.selectedProposal) return false;
    
    const now = new Date();
    const startDate = new Date(this.selectedProposal.startDate);
    
    return now >= startDate;
  }

  onSubmitVote() {
    if (this.voteForm.valid && this.selectedProposal) {
      this.isSubmitting = true;
      const { userId, vote } = this.voteForm.value;
      
      this.proposalService.castVote(this.selectedProposal.id, { vote }, Number(userId))
        .subscribe({
          next: (response: any) => {
            this.voteMessage = 'Voto emitido exitosamente';
            this.voteMessageType = 'success';
            this.voteForm.reset();
            this.isSubmitting = false;
          },
          error: (error: any) => {
            this.voteMessage = 'Error al emitir el voto: ' + (error.error?.message || 'Error desconocido');
            this.voteMessageType = 'error';
            this.isSubmitting = false;
          }
        });
    }
  }

  getResultText(): string {
    const positive = this.selectedProposal?.positiveVotes || 0;
    const negative = this.selectedProposal?.negativeVotes || 0;
    
    if (positive > negative) return 'Approved';
    if (negative > positive) return 'Rejected';
    return 'Draw';
  }

  getResultIcon(): string {
    const result = this.getResultText();
    switch (result) {
      case 'Approved': return 'A';
      case 'Rejected': return 'R';
      case 'Draw': return 'E';
      default: return '?';
    }
  }

  getResultClass(): string {
    const result = this.getResultText();
    switch (result) {
      case 'Approved': return 'result-approved';
      case 'Rejected': return 'result-rejected';
      case 'Draw': return 'result-draw';
      default: return '';
    }
  }
}