const dateOptions = { year: 'numeric', month: 'short', day: 'numeric' };
export function formatDate(date) {
    if (!date) {
        return null;
    }

    return date.toLocaleDateString('en-GB', dateOptions)
}

const shortDateOptions = { year: 'numeric', month: 'short' };
export function formatDateShort(date) {
    if (!date) {
        return null;
    }

    return date.toLocaleDateString('en-GB', shortDateOptions)
}