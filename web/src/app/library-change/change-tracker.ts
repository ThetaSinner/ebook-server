import { Subject, Observable } from 'rxjs';

export enum ChangeType {
    BookCreated = 'BookCreated',
    BookUpdated = 'BookUpdated',
    BookDeleted = 'BookDeleted'
}

export interface LibraryChange {
    changeType: ChangeType;
    bookId: string;
}

export class ChangeTracker {
    private subject: Subject<LibraryChange> = new Subject();

    constructor(private source: EventSource) {
        this.source.addEventListener('change', (event: any) => {
            this.subject.next(JSON.parse(event.data));
        }, false);

        this.source.onerror = (event) => {
            console.error('problem getting change events', event);
        }
    }

    get changes(): Observable<LibraryChange> {
        return this.subject.asObservable();
    }
}