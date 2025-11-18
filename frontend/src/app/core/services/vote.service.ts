import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vote } from '../../shared/interfaces/vote.interface';

@Injectable({
  providedIn: 'root'
})
export class VoteService {

  private apiUrl = 'http://localhost:8080/api/proposals';

  constructor(private http: HttpClient) { }

  getVotesByProposal(proposalId: number): Observable<Vote[]> {
    return this.http.get<Vote[]>(`${this.apiUrl}/${proposalId}/votes`);
  }

  castVote(proposalId: number, vote: Vote): Observable<Vote> {
    return this.http.post<Vote>(`${this.apiUrl}/${proposalId}/votes`, vote);
  }
}
