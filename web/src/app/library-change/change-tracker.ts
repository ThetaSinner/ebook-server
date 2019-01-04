import { Subject, Observable } from 'rxjs';

export interface LibraryChange {
    changeType: any;
    bookId: string;
}

export class ChangeTracker {
    private subject: Subject<LibraryChange> = new Subject();

    constructor(private source: EventSource) {
        this.source.addEventListener('change', (event: any) => {
            this.subject.next(JSON.parse(event.data));
        }, false);

        this.source.onerror = (event) => {
            console.error('error event', event);
        }
    }

    get changes(): Observable<LibraryChange> {
        return this.subject.asObservable();
    }
}