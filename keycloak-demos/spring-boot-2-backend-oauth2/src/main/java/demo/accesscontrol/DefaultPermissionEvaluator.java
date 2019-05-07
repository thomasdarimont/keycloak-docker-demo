package demo.accesscontrol;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class DefaultPermissionEvaluator implements PermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
		log.info("check permission user={} target={} permission={}", auth.getName(), targetDomainObject, permission);
		return true;
	}

	@Override
	public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
		DomainObjectReference dor = new DomainObjectReference(targetType.toString(), targetId.toString());
		return hasPermission(auth, dor, permission);
	}
}
