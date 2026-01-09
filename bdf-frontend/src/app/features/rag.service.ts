import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RagService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  uploadFile(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ message: string }>(`${this.apiUrl}/upload`, formData);
  }

  deleteFile(filename: string) {
    // Send filename as a query parameter
    return this.http.delete<{ message: string }>(`${this.apiUrl}/delete?filename=${filename}`);
  }

  chat(query: string) {
    return this.http.post<{ answer: string }>(`${this.apiUrl}/chat`, { query })
      .pipe(map(response => response.answer));
  }
}
