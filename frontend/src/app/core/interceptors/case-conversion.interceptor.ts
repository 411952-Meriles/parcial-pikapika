import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';

export const caseConversionInterceptor: HttpInterceptorFn = (req, next) => {
  // Convertir request body de camelCase a snake_case
  let modifiedReq = req;
  if (req.body && typeof req.body === 'object') {
    const snakeCaseBody = convertKeysToSnakeCase(req.body);
    modifiedReq = req.clone({ body: snakeCaseBody });
  }

  return next(modifiedReq).pipe(
    map((event) => {
      // Convertir response de snake_case a camelCase
      if (event instanceof HttpResponse && event.body) {
        const camelCaseBody = convertKeysToCamelCase(event.body);
        return event.clone({ body: camelCaseBody });
      }
      return event;
    })
  );
};

function convertKeysToSnakeCase(obj: any): any {
  if (obj === null || obj === undefined) {
    return obj;
  }

  if (Array.isArray(obj)) {
    return obj.map(item => convertKeysToSnakeCase(item));
  }

  if (typeof obj === 'object' && obj.constructor === Object) {
    const snakeCaseObj: any = {};
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        const snakeKey = camelToSnakeCase(key);
        snakeCaseObj[snakeKey] = convertKeysToSnakeCase(obj[key]);
      }
    }
    return snakeCaseObj;
  }

  return obj;
}

function convertKeysToCamelCase(obj: any): any {
  if (obj === null || obj === undefined) {
    return obj;
  }

  if (Array.isArray(obj)) {
    return obj.map(item => convertKeysToCamelCase(item));
  }

  if (typeof obj === 'object' && obj.constructor === Object) {
    const camelCaseObj: any = {};
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        const camelKey = snakeToCamelCase(key);
        camelCaseObj[camelKey] = convertKeysToCamelCase(obj[key]);
      }
    }
    return camelCaseObj;
  }

  return obj;
}

function camelToSnakeCase(str: string): string {
  return str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
}

function snakeToCamelCase(str: string): string {
  return str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
}