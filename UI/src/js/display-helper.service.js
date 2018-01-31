import * as _ from 'lodash';

export default class DisplayHelper {
    toCommaList(arr) {
        let result = _.join(_.castArray(arr), ', ');

        return _.trimEnd(result, ', ');
    }

    fromCommaList(str) {
        let result = _.split(str);

        return result.length == 1 ? result[0] : result;
    }
}