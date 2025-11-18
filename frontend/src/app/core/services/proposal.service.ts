import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Proposal } from '../../shared/interfaces/proposal.interface';

@Injectable({
  providedIn: 'root'
})
export class ProposalService {

  private apiUrl = 'http://localhost:8080/api/proposals';

  constructor(private http: HttpClient) { }

  getProposals(): Observable<Proposal[]> {
    return this.http.get<Proposal[]>(this.apiUrl);
  }

  getProposal(id: number): Observable<Proposal> {
    return this.http.get<Proposal>(`${this.apiUrl}/${id}`);
  }

  createProposal(proposal: Partial<Proposal>): Observable<Proposal> {
    return this.http.post<Proposal>(this.apiUrl, proposal);
  }

  updateProposal(id: number, proposal: Proposal): Observable<Proposal> {
    return this.http.put<Proposal>(`${this.apiUrl}/${id}`, proposal);
  }

  deleteProposal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  castVote(proposalId: number, voteData: { vote: string }, userId: number): Observable<any> {
    const headers = { 'x-user-id': userId.toString() };
    return this.http.post<any>(`${this.apiUrl}/${proposalId}/votes`, voteData, { headers });
  }
}
