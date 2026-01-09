import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RagService } from '../rag.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-upload.component.html',
  styleUrl: './file-upload.component.css'
})
export class FileUploadComponent {
  private ragService = inject(RagService);

  isDragging = false;
  files = signal<{ name: string; size: number; status: 'pending' | 'uploading' | 'done' | 'error' }[]>([]);

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    if (event.dataTransfer?.files) {
      this.handleFiles(event.dataTransfer.files);
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.handleFiles(input.files);
    }
  }

  handleFiles(fileList: FileList) {
    Array.from(fileList).forEach(file => {
      const newFile = { name: file.name, size: file.size, status: 'uploading' as const };

      this.files.update(current => [...current, newFile]);

      this.ragService.uploadFile(file).subscribe({
        next: () => {
          this.updateStatus(file.name, 'done');
        },
        error: (err) => {
          console.error('Upload failed', err);
          this.updateStatus(file.name, 'error');
        }
      });
    });
  }

  removeFile(index: number) {
    const fileToRemove = this.files()[index];

    // If the file was successfully uploaded, we must remove it from the AI memory
    if (fileToRemove.status === 'done') {
      this.ragService.deleteFile(fileToRemove.name).subscribe({
        next: () => console.log(`Deleted ${fileToRemove.name} from context`),
        error: (e) => console.error('Failed to delete from backend', e)
      });
    }

    // Update UI immediately
    this.files.update(current => current.filter((_, i) => i !== index));
  }

  private updateStatus(fileName: string, status: 'done' | 'error') {
    this.files.update(files =>
      files.map(f => f.name === fileName ? { ...f, status } : f)
    );
  }
}
