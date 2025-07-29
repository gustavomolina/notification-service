import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface MessageRequest {
  category: string;
  content: string;
}

export interface MessageResponse {
  id: number;
  category: string;
  content: string;
  createdAt: string;
  notificationsSent: number;
}

export interface NotificationLogResponse {
  id: number;
  messageId: number;
  messageCategory: string;
  messageContent: string;
  userId: number;
  userName: string;
  userEmail: string;
  userPhoneNumber: string;
  channel: string;
  sent: boolean;
  createdAt: string;
  sentAt: string;
}

export interface NotificationPage {
  notifications: NotificationLogResponse[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

export interface NotificationStats {
  totalNotifications: number;
  sentNotifications: number;
  failedNotifications: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Sends a new message
   * 
   * @param message The message to send
   * @returns An observable of the created message
   */
  sendMessage(message: MessageRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/messages`, message);
  }

  /**
   * Gets all messages
   * 
   * @returns An observable of all messages
   */
  getMessages(): Observable<MessageResponse[]> {
    return this.http.get<MessageResponse[]>(`${this.apiUrl}/messages`);
  }

  /**
   * Gets a message by ID
   * 
   * @param id The ID of the message
   * @returns An observable of the message
   */
  getMessage(id: number): Observable<MessageResponse> {
    return this.http.get<MessageResponse>(`${this.apiUrl}/messages/${id}`);
  }

  /**
   * Gets all notifications with pagination
   * 
   * @param page The page number (0-based)
   * @param size The page size
   * @returns An observable of the notification page
   */
  getNotifications(page: number = 0, size: number = 10): Observable<NotificationPage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<NotificationPage>(`${this.apiUrl}/notifications`, { params });
  }

  /**
   * Gets notification statistics
   * 
   * @returns An observable of the notification statistics
   */
  getNotificationStats(): Observable<NotificationStats> {
    return this.http.get<NotificationStats>(`${this.apiUrl}/notifications/stats`);
  }

  /**
   * Gets notifications by sent status
   * 
   * @param sent The sent status to filter by
   * @returns An observable of the notifications
   */
  getNotificationsBySentStatus(sent: boolean): Observable<NotificationLogResponse[]> {
    return this.http.get<NotificationLogResponse[]>(`${this.apiUrl}/notifications/status/${sent}`);
  }
}