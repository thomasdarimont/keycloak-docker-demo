package de.tdlabs.keycloak.ext.events.forwarder;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;

public class InstanceNameHolder {

	static final String INSTANCE_NAME = generateInstaceName();

	static String toComponentIdString(Object object) {

		if (object == null) {
			return null;
		}

		return object.getClass().getSimpleName() + System.identityHashCode(object);
	}

	private static String generateInstaceName() {

		String instanceName = System.getenv("INSTANCE_NAME");
		if (instanceName != null) {
			return instanceName;
		}

		String hostIp = System.getenv("HOST_IP");

		try {
			InetAddress localhost = Inet4Address.getLocalHost();
			if (hostIp == null) {
				hostIp = localhost.getHostAddress();
			}
		} catch (Exception ignore) {
			// ignored
		}

		String vmName = ManagementFactory.getRuntimeMXBean().getName();

		return vmName + ":" + hostIp;
	}
}
