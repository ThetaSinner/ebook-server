import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'valueList'
})
export class ValueListPipe implements PipeTransform {

  transform(value: Array<string>, args?: any): any {
    return value.join(', ');
  }

}
