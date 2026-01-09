// file: src/app/app.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ChatComponent } from './features/chat/chat.component';
import { FileUploadComponent } from './features/upload/file-upload.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ChatComponent, FileUploadComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
}
