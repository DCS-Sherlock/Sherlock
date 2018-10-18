package uk.ac.warwick.dcs.sherlock.core;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.*;

public class ModuleLoader {

	private Reflections ref;

	public ModuleLoader() {
		File modules = new File("modules/");
		if (!modules.exists()) {
			if (modules.mkdir()) {
				//get default jar and put in here
			}
		}

		List<URL> moduleURLS = Arrays.stream(Objects.requireNonNull(modules.listFiles())).map(f -> {
			try {
				URL url = f.toURI().toURL();
				this.addURLToClasspath(url);
				return url;
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());

		moduleURLS.addAll(ClasspathHelper.forPackage("uk.ac.warwick.dcs.sherlock"));
		this.ref = new Reflections(new ConfigurationBuilder().addClassLoader(this.getClass().getClassLoader()).setUrls(moduleURLS).filterInputsBy(new FilterBuilder().include(".*class")));
	}

	private URLClassLoader addURLToClasspath(URL u) {
		try {
			URLClassLoader urlClassLoader = (URLClassLoader) this.getClass().getClassLoader();
			Class<URLClassLoader> urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(urlClassLoader, u);
			return urlClassLoader;
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Set<Class<?>> getModules() {
		return this.ref.getTypesAnnotatedWith(SherlockModule.class);
	}

}
