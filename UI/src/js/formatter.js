const dateOptions = { year: 'numeric', month: 'short', day: 'numeric' };
export function formatDate(date) {
    if (!date) {
        return null;
    }

    if (typeof date === 'string') {
        try {
            date = new Date(date);
        }
        catch (err) {
            return '';
        }
    }

    return date.toLocaleDateString('en-GB', dateOptions)
}

const shortDateOptions = { year: 'numeric', month: 'short' };
export function formatDateShort(date) {
    if (!date) {
        return null;
    }

    if (typeof date === 'string') {
        try {
            date = new Date(date);
        }
        catch (err) {
            return '';
        }
    }
    
    return date.toLocaleDateString('en-GB', shortDateOptions)
}