package org.bonitasoft.engine.classloader;

public class DefaultParentClassLoaderResolver implements ParentClassLoaderResolver {

	public ClassLoader getParent(ClassLoaderService classLoaderService, String childClassLoaderType, long childClassLoaderId) throws ClassLoaderException {
		return classLoaderService.getGlobalClassLoader();
	}
}
