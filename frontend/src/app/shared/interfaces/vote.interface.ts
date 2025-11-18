import { VoteType } from '../enums/vote-type.enum';

export interface Vote {
  id: number;
  vote: VoteType;
  userId: number;
  proposalId: number;
}
