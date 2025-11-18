import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-generic-table',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule],
  templateUrl: './generic-table.html',
  styleUrls: ['./generic-table.css']
})
export class GenericTableComponent {
  @Input() data: any[] = [];
  @Input() displayedColumns: string[] = [];
  @Input() columnHeaders: { [key: string]: string } = {};
  @Output() actionClicked = new EventEmitter<{action: string, element: any}>();

  onActionClick(action: string, element: any) {
    this.actionClicked.emit({ action, element });
  }
}
