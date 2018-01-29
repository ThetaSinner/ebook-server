const TestData = {
    testLibraryA: {
        books: [
            {
                id: 'asd987asdf908-asdf98-asdfhjkl23j',
                isbn: '2039482394820',
                title: 'bubbles',
                authors: ['Gabe Newell', 'Charlie Cavendish'],
                publisher: 'Action Press',
                datePublished: new Date(),
                description: 'This is a book about bubbles by some random people who I don\'t think write books',
                metadata: {
                    tags: ['fiction', 'futuristic'],
                    rating: 4
                }
            },
            {
                id: 'dsfg098dsfg89-sadfkiu89o-sd9f898sa',
                isbn: '9802340982349',
                title: 'grapes',
                authors: 'Andy Warhol',
                publisher: 'Puffin',
                datePublished: new Date(),
                description: 'An abstract novel on the topic of grapes by a strange and wonderful artist',
                metadata: {
                    tags: 'non-fiction',
                    rating: 2
                }
            }
        ]
    },
    testLibraryB: {
        books: [
            {
                id: 'tgd987asdf568-asdf28-asdfhtkl23j',
                isbn: '2039482355820',
                title: 'The taming of the boo',
                authors: ['Boo Meister'],
                publisher: 'Orion',
                datePublished: new Date(),
                description: 'This is a great modern retelling.',
                metadata: {
                    tags: ['fiction', 'romance'],
                    rating: 3
                }
            },
            {
                id: 'tgd987modf568-asdf28-as3phtkl23j',
                isbn: '2038882355820',
                title: 'The wheening of the winkle',
                authors: ['Aaron Simon'],
                publisher: 'Addison-Wesley',
                datePublished: new Date(),
                description: 'I wonder what on earth this could be about.',
                metadata: {
                    tags: ['fiction', 'sci-fi'],
                    rating: 4
                }
            },
            {
                id: 'dsfg498dsfg89-sadfkiu89o-lt9f898sa',
                isbn: '9802369982349',
                title: 'Who killed Tyrion',
                authors: 'Meister Martin',
                publisher: 'Bloomsbury',
                datePublished: new Date(),
                description: 'An even worse story than you might imagine',
                metadata: {
                    tags: 'fantasy',
                    rating: 1
                }
            }
        ]
    }
};

export default class EBookDataService {
    constructor(serverUrl) {
        this.serverUrl = serverUrl;
    }

    loadLibrary(libraryName) {
        return Promise.resolve(TestData[libraryName]);
    }
}
