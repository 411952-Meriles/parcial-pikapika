import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { ProposalService } from '../../core/services/proposal.service';

@Component({
  selector: 'app-proposal-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatCardModule
  ],
  templateUrl: './proposal-form.html',
  styleUrls: ['./proposal-form.css']
})
export class ProposalFormComponent implements OnInit {
  proposalForm: FormGroup;
  isSubmitting = false;
  submitMessage = '';
  submitMessageType: 'success' | 'error' | '' = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private proposalService: ProposalService
  ) {
    this.proposalForm = this.fb.group({
      title: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(50)
      ]],
      description: ['', [
        Validators.required,
        Validators.pattern(/\S/)
      ]],
      startDate: ['', [
        Validators.required,
        this.futureDateValidator
      ]],
      startTime: ['09:00', [
        Validators.required
      ]],
      endDate: ['', [
        Validators.required,
        this.futureDateValidator
      ]],
      endTime: ['17:00', [
        Validators.required
      ]]
    }, { validators: this.endDateAfterStartDateValidator });
  }

  ngOnInit() {}

  futureDateValidator(control: any) {
    if (!control.value) return null;
    
    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return selectedDate > today ? null : { pastDate: true };
  }

  endDateAfterStartDateValidator(form: any) {
    const startDate = form.get('startDate')?.value;
    const startTime = form.get('startTime')?.value;
    const endDate = form.get('endDate')?.value;
    const endTime = form.get('endTime')?.value;
    
    if (!startDate || !startTime || !endDate || !endTime) return null;
    
    const startDateTime = this.combineDateAndTime(startDate, startTime);
    const endDateTime = this.combineDateAndTime(endDate, endTime);
    
    return endDateTime > startDateTime ? null : { endDateBeforeStart: true };
  }

  onSubmit() {
    if (this.proposalForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';
      
      const formValue = this.proposalForm.value;
      const startDateTime = this.combineDateAndTime(formValue.startDate, formValue.startTime);
      const endDateTime = this.combineDateAndTime(formValue.endDate, formValue.endTime);
      
      const proposalData = {
        title: formValue.title,
        description: formValue.description,
        startDate: this.formatDateTime(startDateTime),
        endDate: this.formatDateTime(endDateTime)
      };

      this.proposalService.createProposal(proposalData).subscribe({
        next: (response: any) => {
          this.submitMessage = 'Propuesta creada exitosamente';
          this.submitMessageType = 'success';
          this.isSubmitting = false;
          
          setTimeout(() => {
            this.router.navigate(['/']);
          }, 2000);
        },
        error: (error: any) => {
          this.submitMessage = 'Error al crear la propuesta: ' + (error.error?.message || 'Error desconocido');
          this.submitMessageType = 'error';
          this.isSubmitting = false;
        }
      });
    }
  }

  private combineDateAndTime(date: Date, time: string): Date {
    const dateObj = new Date(date);
    const [hours, minutes] = time.split(':').map(num => parseInt(num, 10));
    
    dateObj.setHours(hours, minutes, 0, 0);
    return dateObj;
  }

  private formatDateTime(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }

  onCancel() {
    this.router.navigate(['/']);
  }

  getFieldError(fieldName: string): string {
    const field = this.proposalForm.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) return `${this.getFieldLabel(fieldName)} es obligatorio`;
      if (field.errors['minlength']) return `Mínimo ${field.errors['minlength'].requiredLength} caracteres`;
      if (field.errors['maxlength']) return `Máximo ${field.errors['maxlength'].requiredLength} caracteres`;
      if (field.errors['pattern']) return `${this.getFieldLabel(fieldName)} no puede estar vacío`;
      if (field.errors['pastDate']) return 'La fecha debe ser futura';
    }
    return '';
  }

  getFormError(): string {
    if (this.proposalForm.errors?.['endDateBeforeStart'] && this.proposalForm.touched) {
      return 'La fecha de fin debe ser posterior a la fecha de inicio';
    }
    return '';
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      title: 'El título',
      description: 'La descripción',
      startDate: 'La fecha de inicio',
      startTime: 'La hora de inicio',
      endDate: 'La fecha de fin',
      endTime: 'La hora de fin'
    };
    return labels[fieldName] || fieldName;
  }
}