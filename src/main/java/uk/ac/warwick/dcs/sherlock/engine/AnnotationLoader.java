package uk.ac.warwick.dcs.sherlock.engine;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.annotation.SherlockModule;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.*;

public class AnnotationLoader {

	static final Logger logger = LoggerFactory.getLogger(AnnotationLoader.class);
	private Reflections ref;

	AnnotationLoader() {
		List<URL> moduleURLS = new LinkedList<>();
		String modulesPath = (SherlockEngine.modulesPath == null || SherlockEngine.modulesPath.equals("")) ? "module/" : SherlockEngine.modulesPath;
		if (!modulesPath.endsWith("/")) {
			modulesPath += "/";
		}

		if (SherlockEngine.enableExternalModules) {
			File modules = new File(modulesPath);
			if (!modules.exists()) {
				modules.mkdir();
			}

			moduleURLS.addAll(Arrays.stream(Objects.requireNonNull(modules.listFiles())).map(f -> {
				try {
					URL url = f.toURI().toURL();
					this.addURLToClasspath(url);
					return url;
				}
				catch (MalformedURLException e) {
					e.printStackTrace();
				}
				return null;
			}).collect(Collectors.toList()));

			//Load libs to
			File libs = new File(modulesPath + "libs/");
			if (!libs.exists()) {
				libs.mkdir();
			}
			Arrays.stream(Objects.requireNonNull(libs.listFiles())).forEach(f -> {
				try {
					this.addURLToClasspath(f.toURI().toURL());
				}
				catch (MalformedURLException e) {
					e.printStackTrace();
				}
			});
		}

		moduleURLS.addAll(ClasspathHelper.forPackage("uk.ac.warwick.dcs.sherlock.engine"));
		moduleURLS.addAll(ClasspathHelper.forPackage("uk.ac.warwick.dcs.sherlock.module"));
		moduleURLS.addAll(ClasspathHelper.forPackage("uk.ac.warwick.dcs.sherlock.launch"));

		ConfigurationBuilder config = new ConfigurationBuilder();
		config.addClassLoader(this.getClass().getClassLoader());
		config.setUrls(moduleURLS);
		config.setScanners(new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
		config.filterInputsBy(new FilterBuilder().include(".*class"));
		this.ref = new Reflections(config);
	}

	void registerModules() {
		this.ref.getTypesAnnotatedWith(SherlockModule.class).stream().peek(x -> logger.info("Registering Sherlock module: {}", x.getName())).forEach(SherlockEngine.eventBus::registerModule);
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

}
