import { Injectable } from '@angular/core';

declare var Keycloak: any;

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private keycloakAuth: any;

  constructor() { }

  init(): Promise<any> {
    return new Promise((resolve, reject) => {
      const config = {
        'url': 'http://sso.tdlabs.local:8899/u/auth',
        'realm': 'acme',
        'clientId': 'app-angular'
      };
      this.keycloakAuth = new Keycloak(config);
      this.keycloakAuth.init({ 
        onLoad: 'login-required', 
        pkceMethod: 'S256'
      }).success(() => {
          resolve();
        })
        .error(() => {
          reject();
        });
    });
  }

  getToken(): string {
    return this.keycloakAuth.token;
  }

  getUsername(): string {
    return this.keycloakAuth.idTokenParsed.preferred_username;
  }

  logout(): void {
     this.keycloakAuth.logout();

  }
}
