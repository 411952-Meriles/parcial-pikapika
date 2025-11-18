import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { caseConverterInterceptor } from './core/interceptors/case-converter.interceptor';
import { providePrimeNG } from 'primeng/config';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

export const appConfig: ApplicationConfig = {
  providers: [
        provideZoneChangeDetection({ eventCoalescing: true }), 
        provideRouter(routes),
        providePrimeNG(),
        provideAnimationsAsync(),
        provideHttpClient(withInterceptors([caseConverterInterceptor]))
    ]
};
