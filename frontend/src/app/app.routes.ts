import { Routes } from '@angular/router';
import { ProposalsListComponent } from './pages/proposals-list/proposals-list';
import { ProposalFormComponent } from './pages/proposal-form/proposal-form';

export const routes: Routes = [
    { path: 'proposals', component: ProposalsListComponent },
    { path: 'proposals/new', component: ProposalFormComponent },
    { path: '', redirectTo: '/proposals', pathMatch: 'full' }
];