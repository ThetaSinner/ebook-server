export default function applyFilterQuery(filterQuery, books) {
    return books.filter(book => {
        return book.title.indexOf(filterQuery) !== -1;
    });
}