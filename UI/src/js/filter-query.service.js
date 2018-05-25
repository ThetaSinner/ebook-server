export default function applyFilterQuery(filterQuery, books) {
    try {
        return evalQuery(filterQuery, books);
    }
    catch (e) {
        /* eslint-disable no-console */
        console.error('Filter query failed', e);
        /* eslint-enable no-console */
    }
    
    return books;
}

function evalQuery(input, books) {
    return books.filter(book => {
        let evalExpr = input;

        const expr = /([^!&|\s()]+)/gi;

        const matches = input.match(expr);

        matches.forEach(match => {
            const expr = /:(\S+)/i;
            const keyMatch = match.match(expr);

            if (!keyMatch) {
                const sub = new RegExp(match, 'i').test(book.title) ? 'true' : 'false';
                evalExpr = evalExpr.replace(match, sub);
            }
            else {
                const val = match.replace(keyMatch[0], '');
                const prop = book[keyMatch[1]];

                if (!prop) {
                    evalExpr = evalExpr.replace(match, 'false');
                }
                else if (Array.isArray(prop)) {
                    const sub = prop.reduce((acc, v) => {
                        return acc || new RegExp(val, 'i').test(v);
                    }, false) ? 'true' : 'false';

                    evalExpr = evalExpr.replace(match, sub);
                }
                else {
                    const sub = new RegExp(val, 'i').test(prop) ? 'true' : 'false';
                    evalExpr = evalExpr.replace(match, sub);
                }
            }
        });

        evalExpr.replace('&', '&&');
        evalExpr.replace('|', '||');

        return eval(evalExpr);
    });
}
