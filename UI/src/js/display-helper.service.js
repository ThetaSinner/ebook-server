import * as _ from 'lodash';

export default class DisplayHelper {
    toCommaList(arr) {
        if (!arr) {
            return '';
        }

        let result = _.join(_.castArray(arr), ', ');

        return _.trimEnd(result, ', ');
    }

    fromCommaList(str) {
        let result = _.split(str, ',');

        result.forEach((value, index, arr) => {
            arr[index] = _.trim(value);
        });

        return result.length == 1 ? result[0] : result;
    }
}
