import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService, MessageRequest } from '../../services/notification.service';

@Component({
  selector: 'app-message-form',
  templateUrl: './message-form.component.html',
  styleUrls: ['./message-form.component.css']
})
export class MessageFormComponent implements OnInit {
  messageForm!: FormGroup;
  categories = ['SPORTS', 'FINANCE', 'MOVIES'];
  submitting = false;
  success = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.messageForm = this.fb.group({
      category: ['', Validators.required],
      content: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.messageForm.invalid) {
      return;
    }

    this.submitting = true;
    this.success = false;
    this.error = '';

    const message: MessageRequest = {
      category: this.messageForm.value.category,
      content: this.messageForm.value.content
    };

    this.notificationService.sendMessage(message).subscribe({
      next: (response) => {
        this.submitting = false;
        this.success = true;
        this.messageForm.reset();
        setTimeout(() => {
          this.success = false;
        }, 3000);
      },
      error: (err) => {
        this.submitting = false;
        this.error = err.error?.message || 'An error occurred while sending the message. Please try again.';
      }
    });
  }
}