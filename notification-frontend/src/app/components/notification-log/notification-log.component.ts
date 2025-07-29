import { Component, OnInit } from '@angular/core';
import { NotificationService, NotificationLogResponse, NotificationPage, NotificationStats } from '../../services/notification.service';

@Component({
  selector: 'app-notification-log',
  templateUrl: './notification-log.component.html',
  styleUrls: ['./notification-log.component.css']
})
export class NotificationLogComponent implements OnInit {
  notifications: NotificationLogResponse[] = [];
  stats: NotificationStats = { totalNotifications: 0, sentNotifications: 0, failedNotifications: 0 };
  loading = true;
  error = '';
  currentPage = 0;
  pageSize = 10;
  totalItems = 0;
  totalPages = 0;

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.loadNotifications();
    this.loadStats();
  }

  loadNotifications(page: number = 0): void {
    this.loading = true;
    this.error = '';
    this.currentPage = page;

    this.notificationService.getNotifications(page, this.pageSize).subscribe({
      next: (response: NotificationPage) => {
        this.notifications = response.notifications;
        this.currentPage = response.currentPage;
        this.totalItems = response.totalItems;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load notifications. Please try again.';
        this.loading = false;
        console.error('Error loading notifications:', err);
      }
    });
  }

  loadStats(): void {
    this.notificationService.getNotificationStats().subscribe({
      next: (stats: NotificationStats) => {
        this.stats = stats;
      },
      error: (err) => {
        console.error('Error loading notification stats:', err);
      }
    });
  }

  refreshData(): void {
    this.loadNotifications(this.currentPage);
    this.loadStats();
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.loadNotifications(this.currentPage - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.loadNotifications(this.currentPage + 1);
    }
  }

  getChannelBadgeClass(channel: string): string {
    switch (channel) {
      case 'EMAIL':
        return 'bg-primary';
      case 'SMS':
        return 'bg-success';
      case 'PUSH_NOTIFICATION':
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }

  getCategoryBadgeClass(category: string): string {
    switch (category) {
      case 'SPORTS':
        return 'bg-danger';
      case 'FINANCE':
        return 'bg-warning text-dark';
      case 'MOVIES':
        return 'bg-dark';
      default:
        return 'bg-secondary';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  }
}