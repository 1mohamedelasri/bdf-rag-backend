import { Component, signal, inject } from '@angular/core'; // Add inject
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RagService } from '../rag.service'; // Import service

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent {
  private ragService = inject(RagService); // Inject service

  messages = signal<{ role: 'user' | 'ai', text: string }[]>([
    { role: 'ai', text: 'Hello! I am your document assistant. Upload a file on the right to get started.' }
  ]);

  inputMessage = '';
  isLoading = false;

  sendMessage() {
    if (!this.inputMessage.trim() || this.isLoading) return;

    const userText = this.inputMessage;
    this.inputMessage = ''; // Clear input immediately

    // 1. Show User Message
    this.messages.update(msgs => [...msgs, { role: 'user', text: userText }]);
    this.isLoading = true;

    // 2. Call Real Backend
    this.ragService.chat(userText).subscribe({
      next: (answer) => {
        this.messages.update(msgs => [...msgs, { role: 'ai', text: answer }]);
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.messages.update(msgs => [...msgs, { role: 'ai', text: 'Error connecting to the brain. Is the backend running?' }]);
        this.isLoading = false;
      }
    });
  }
}
