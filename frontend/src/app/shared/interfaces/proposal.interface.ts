import { Vote } from './vote.interface';

export interface Proposal {
  id: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  positiveVotes?: number;
  negativeVotes?: number;
}