package de.tdlabs.keycoak.ext.storage.demo;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@JBossLog
public class DemoUserStorageProvider implements UserStorageProvider,
  UserLookupProvider, UserQueryProvider, CredentialInputUpdater, CredentialInputValidator {

  private final KeycloakSession session;
  private final ComponentModel model;
  private final UserRepository repository;

  public DemoUserStorageProvider(KeycloakSession session, ComponentModel model, UserRepository repository) {
    this.session = session;
    this.model = model;
    this.repository = repository;
  }

  @Override
  public boolean supportsCredentialType(String credentialType) {
    return CredentialModel.PASSWORD.equals(credentialType);
  }

  @Override
  public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
    return supportsCredentialType(credentialType);
  }

  @Override
  public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {

    log.debugv("isValid user credential: userId={0}", user.getId());

    if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
      return false;
    }

    UserCredentialModel cred = (UserCredentialModel) input;
    return repository.validateCredentials(user.getUsername(), cred.getValue());
  }

  @Override
  public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {

    log.debugv("updating credential: realm={0} user={1}", realm.getId(), user.getUsername());

    if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
      return false;
    }

    UserCredentialModel cred = (UserCredentialModel) input;
    return repository.updateCredentials(user.getUsername(), cred.getValue());
  }

  @Override
  public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
  }

  @Override
  public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
    return Collections.emptySet();
  }

  @Override
  public void preRemove(RealmModel realm) {

    log.debugv("pre-remove realm");
  }

  @Override
  public void preRemove(RealmModel realm, GroupModel group) {

    log.debugv("pre-remove group");
  }

  @Override
  public void preRemove(RealmModel realm, RoleModel role) {

    log.debugv("pre-remove role");
  }

  @Override
  public void close() {
    log.debugv("closing");
  }

  @Override
  public UserModel getUserById(String id, RealmModel realm) {

    log.debugv("lookup user by id: realm={0} userId={1}", realm.getId(), id);

    String externalId = StorageId.externalId(id);
    return new UserAdapter(session, realm, model, repository.findUserById(externalId));
  }

  @Override
  public UserModel getUserByUsername(String username, RealmModel realm) {

    log.debugv("lookup user by username: realm={0} username={1}", realm.getId(), username);

    return new UserAdapter(session, realm, model, repository.findUserByUsernameOrEmail(username));
  }

  @Override
  public UserModel getUserByEmail(String email, RealmModel realm) {

    log.debugv("lookup user by username: realm={0} email={1}", realm.getId(), email);

    return getUserByUsername(email, realm);
  }

  @Override
  public int getUsersCount(RealmModel realm) {
    return repository.getUsersCount();
  }

  @Override
  public List<UserModel> getUsers(RealmModel realm) {

    log.debugv("list users: realm={0}", realm.getId());

    return repository.getAllUsers().stream()
      .map(user -> new UserAdapter(session, realm, model, user))
      .collect(Collectors.toList());
  }

  @Override
  public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {

    log.debugv("list users: realm={0} firstResult={1} maxResults={2}", realm.getId(), firstResult, maxResults);

    return getUsers(realm);
  }

  @Override
  public List<UserModel> searchForUser(String search, RealmModel realm) {

    log.debugv("search for users: realm={0} search={1}", realm.getId(), search);

    return repository.findUsers(search).stream()
      .map(user -> new UserAdapter(session, realm, model, user))
      .collect(Collectors.toList());
  }

  @Override
  public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {

    log.debugv("search for users: realm={0} search={1} firstResult={2} maxResults={3}", realm.getId(), search, firstResult, maxResults);

    return searchForUser(search, realm);
  }

  @Override
  public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {

    log.debugv("search for users with params: realm={0} params={1}", realm.getId(), params);

    return null;
  }

  @Override
  public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {

    log.debugv("search for users with params: realm={0} params={1} firstResult={2} maxResults={3}", realm.getId(), params, firstResult, maxResults);

    return null;
  }

  @Override
  public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {

    log.debugv("search for group members with params: realm={0} groupId={1} firstResult={2} maxResults={3}", realm.getId(), group.getId(), firstResult, maxResults);

    return Collections.emptyList();
  }

  @Override
  public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {

    log.debugv("search for group members: realm={0} groupId={1} firstResult={2} maxResults={3}", realm.getId(), group.getId());

    return Collections.emptyList();
  }

  @Override
  public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {

    log.debugv("search for group members: realm={0} attrName={1} attrValue={2}", realm.getId(), attrName, attrValue);

    return null;
  }
}
