package com.github.leecho.spring.cloud.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author Spencer Gibb
 * @author Jakub Narloch
 * @author Venil Noronha
 * @author Gang Li
 */
class FeignProxiesRegistrar implements ImportBeanDefinitionRegistrar,
		ResourceLoaderAware, EnvironmentAware, BeanFactoryAware {

	private Logger logger = LoggerFactory.getLogger(FeignProxiesRegistrar.class);

	// patterned after Spring Integration IntegrationComponentScanRegistrar
	// and RibbonClientsConfigurationRegistgrar

	private ResourceLoader resourceLoader;

	private Environment environment;

	private BeanFactory beanFactory;

	public FeignProxiesRegistrar() {
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata,
										BeanDefinitionRegistry registry) {
		registerFeignClients(metadata, registry);
	}

	public void registerFeignClients(AnnotationMetadata metadata,
									 BeanDefinitionRegistry registry) {
		ClassPathScanningCandidateComponentProvider scanner = getScanner();
		scanner.setResourceLoader(this.resourceLoader);

		Set<String> basePackages;
		Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableFeignProxies.class.getName());

		AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(FeignClient.class);
		final Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get("clients");

		Class<?> superClass = attrs == null ? null : (Class<?>) attrs.get("superClass");
		superClass = superClass.equals(Void.class) ? null : superClass;

		if (clients == null || clients.length == 0) {
			scanner.addIncludeFilter(annotationTypeFilter);
			basePackages = getBasePackages(metadata);
		} else {
			final Set<String> clientClasses = new HashSet<>();
			basePackages = new HashSet<>();
			for (Class<?> clazz : clients) {
				basePackages.add(ClassUtils.getPackageName(clazz));
				clientClasses.add(clazz.getCanonicalName());
			}
			AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
				@Override
				protected boolean match(ClassMetadata metadata) {
					String cleaned = metadata.getClassName().replaceAll("\\$", ".");
					return clientClasses.contains(cleaned);
				}
			};
			scanner.addIncludeFilter(new FeignProxiesRegistrar.AllTypeFilter(Arrays.asList(filter, annotationTypeFilter)));
		}

		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;

		for (String basePackage : basePackages) {
			Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
			for (BeanDefinition candidateComponent : candidateComponents) {
				if (candidateComponent instanceof AnnotatedBeanDefinition) {
					// verify annotated class is an interface
					AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
					AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
					Assert.isTrue(annotationMetadata.isInterface(), "@FeignClient can only be specified on an interface");

					Class<?> interfaceClass = null;
					try {
						interfaceClass = Class.forName(beanDefinition.getBeanClassName());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

					//Proxy Feign Client
					if (interfaceClass != null) {
						Object bean = beanFactory.getBean(interfaceClass);
						if (bean != null) {
							Enhancer enhancer = new Enhancer();
							if (superClass != null) {
								enhancer.setSuperclass(superClass);
							}
							enhancer.setCallback(new FeignProxyInvocationHandler(bean));
							enhancer.setInterfaces(new Class[]{interfaceClass, FeignProxyController.class});
							Object proxy = enhancer.create();
							defaultListableBeanFactory.registerSingleton(StringUtils.capitalize(interfaceClass.getSimpleName()) + "$FeignProxyController", proxy);
							if (logger.isDebugEnabled()) {
								logger.debug("Feign client {} proxy by {}", interfaceClass, proxy);
							}
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("Feign client {} no implementor founded", interfaceClass);
							}
						}
					}
				}
			}
		}
	}

	private String resolve(String value) {
		if (StringUtils.hasText(value)) {
			return this.environment.resolvePlaceholders(value);
		}
		return value;
	}


	protected ClassPathScanningCandidateComponentProvider getScanner() {
		return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				boolean isCandidate = false;
				if (beanDefinition.getMetadata().isIndependent()) {
					if (!beanDefinition.getMetadata().isAnnotation()) {
						isCandidate = true;
					}
				}
				return isCandidate;
			}
		};
	}

	protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
		Map<String, Object> attributes = importingClassMetadata
				.getAnnotationAttributes(EnableFeignProxies.class.getCanonicalName());

		Set<String> basePackages = new HashSet<>();
		for (String pkg : (String[]) attributes.get("value")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (String pkg : (String[]) attributes.get("basePackages")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}

		if (basePackages.isEmpty()) {
			basePackages.add(
					ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}
		return basePackages;
	}

	private String getQualifier(Map<String, Object> client) {
		if (client == null) {
			return null;
		}
		String qualifier = (String) client.get("qualifier");
		if (StringUtils.hasText(qualifier)) {
			return qualifier;
		}
		return null;
	}

	private String getClientName(Map<String, Object> client) {
		if (client == null) {
			return null;
		}
		String value = (String) client.get("value");
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("name");
		}
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("serviceId");
		}
		if (StringUtils.hasText(value)) {
			return value;
		}

		throw new IllegalStateException("Either 'name' or 'value' must be provided in @"
				+ FeignClient.class.getSimpleName());
	}


	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * Helper class to create a {@link TypeFilter} that matches if all the delegates
	 * match.
	 *
	 * @author Oliver Gierke
	 */
	private static class AllTypeFilter implements TypeFilter {

		private final List<TypeFilter> delegates;

		/**
		 * Creates a new {@link AllTypeFilter} to match if all the given delegates match.
		 *
		 * @param delegates must not be {@literal null}.
		 */
		public AllTypeFilter(List<TypeFilter> delegates) {
			Assert.notNull(delegates, "This argument is required, it must not be null");
			this.delegates = delegates;
		}

		@Override
		public boolean match(MetadataReader metadataReader,
							 MetadataReaderFactory metadataReaderFactory) throws IOException {

			for (TypeFilter filter : this.delegates) {
				if (!filter.match(metadataReader, metadataReaderFactory)) {
					return false;
				}
			}

			return true;
		}
	}
}