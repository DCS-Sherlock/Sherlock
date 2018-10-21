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
import uk.ac.warwick.dcs.sherlock.api.annotations.RequestProcessor;
import uk.ac.warwick.dcs.sherlock.api.annotations.ResponseHandler;
import uk.ac.warwick.dcs.sherlock.api.annotations.SherlockModule;

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

		if (SherlockEngine.enableExternalModules) {
			File modules = new File("module/");
			if (!modules.exists()) {
				if (modules.mkdir()) {
					//get default jar and put in here
				}
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

	void registerModules() {
		this.ref.getTypesAnnotatedWith(SherlockModule.class).stream().peek(x -> logger.info("Registering Sherlock module: {}", x.getName())).forEach(SherlockEngine.eventBus::registerModule);
	}

	void registerRequestProcessors() {
		this.ref.getTypesAnnotatedWith(RequestProcessor.class).stream().peek(x -> logger.info("Registering Sherlock request processor: {}", x.getName()))
				.forEach(SherlockEngine.requestBus::registerProcessor);
	}

	void registerResponseHandlers() {
		this.ref.getMethodsAnnotatedWith(ResponseHandler.class).stream().peek(x -> logger.info("Registering Sherlock request response handler: {}", x.getName()))
				.forEach(SherlockEngine.requestBus::registerResponseHandler);
	}

}
