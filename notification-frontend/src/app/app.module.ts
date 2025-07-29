import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { MessageFormComponent } from './components/message-form/message-form.component';
import { NotificationLogComponent } from './components/notification-log/notification-log.component';

@NgModule({
  declarations: [
    AppComponent,
    MessageFormComponent,
    NotificationLogComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }