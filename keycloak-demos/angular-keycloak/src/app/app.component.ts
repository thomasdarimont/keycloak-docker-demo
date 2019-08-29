import { Component } from '@angular/core';
import { KeycloakService } from './keycloak.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'angular-keycloak';
  keycloakService: KeycloakService;

  constructor(keycloakService : KeycloakService) {
    this.keycloakService = keycloakService;
  }
}
