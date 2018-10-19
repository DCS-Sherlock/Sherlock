package uk.ac.warwick.dcs.sherlock.engine;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import uk.ac.warwick.dcs.sherlock.api.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.EventHandler;

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

	ModuleLoader() {
		File modules = new File("module/");
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

		moduleURLS.addAll(ClasspathHelper.forPackage("uk.ac.warwick.dcs.sherlock.module"));
		this.ref = new Reflections(new ConfigurationBuilder().addClassLoader(this.getClass().getClassLoader()).setUrls(moduleURLS).setScanners(new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner()).filterInputsBy(new FilterBuilder().include(".*class")));
	}

	private void addURLToClasspath(URL u) {
		try {
			URLClassLoader urlClassLoader = (URLClassLoader) this.getClass().getClassLoader();
			Class<URLClassLoader> urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(urlClassLoader, u);
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public Set<Class<?>> getModules() {
		this.ref.getTypesAnnotatedWith(SherlockModule.class).stream().forEach(x -> x.getAnnotation(SherlockModule.class));
		return null;
	}

	public void registerEventHandlers() {
		Set<Method> methods = this.ref.getMethodsAnnotatedWith(EventHandler.class);

		for (Method m : methods)
		{
			System.out.println(Arrays.toString(m.getParameterTypes()));
		}
	}

}
