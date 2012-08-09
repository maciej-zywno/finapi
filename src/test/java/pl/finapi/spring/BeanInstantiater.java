package pl.finapi.spring;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.context.support.AbstractApplicationContext;

public class BeanInstantiater {
	
	private static final String DEBUG_BEAN_PROPERTY_REF = "ref: %s";

	private static final Logger LOG = Logger.getLogger(BeanInstantiater.class);

	private static final String DEBUG_RETRIEVING_BEAN = "Retrieving bean id=\"%s\"";
	private static final String DEBUG_RETRIEVED_BEAN = "Retrieved bean id=\"%s\" class=\"%s\"";
	private static final String DEBUG_BEAN_PROPERTY = "Bean property: %s\t%s\t%s\t%s\t%s";
	private static final String ERROR_LOADING_BEANS = "Error loading beans";
	public static final String SCOPE_SESSION = "session";

	public static void instantiateBeans(AbstractApplicationContext context) {
		instantiateBeans(context, Collections.<String> emptySet());
	}

	public static void instantiateBeans(AbstractApplicationContext context, Set<String> excludeBeans) {
		instantiateBeans(context, excludeBeans, false);
	}

	public static void instantiateBeans(AbstractApplicationContext context, boolean includeSessionScopedBeans) {
		instantiateBeans(context, Collections.<String> emptySet(), includeSessionScopedBeans);
	}

	public static void instantiateBeans(AbstractApplicationContext context, Set<String> excludeBeans, boolean includeSessionScopedBeans) {
		Set<String> newExcluded = new HashSet<String>(excludeBeans);
		addStandardExcludes(newExcluded);

		ContextInformation contextInfo = new ContextInformation(context);
		try {
			String[] beanNames = BeanFactoryUtils.beanNamesIncludingAncestors(context);
			Set<String> names = new TreeSet<String>(Arrays.asList(beanNames));
			names.removeAll(newExcluded);
			for (String beanName : names) {
				instantiateBean(contextInfo, beanName, includeSessionScopedBeans);
			}
		} catch (RuntimeException e) {
			LOG.error(ERROR_LOADING_BEANS, e);
			throw e;
		} finally {
			for (String beanParameterLog : contextInfo.getBeanParameterLogs()) {
				LOG.debug(beanParameterLog);
			}
		}
	}

	private static void addStandardExcludes(Set<String> newExcluded) {
		newExcluded.add("applicationEventMulticaster");
		newExcluded.add("lifecycleProcessor");
		newExcluded.add("messageSource");
		newExcluded.add("systemEnvironment");
		newExcluded.add("systemProperties");
		newExcluded.add("environment");
		newExcluded.add("importRegistry");
	}

	private static void instantiateBean(ContextInformation contextInfo, String beanName, boolean includeSessionScopedBeans) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format(DEBUG_RETRIEVING_BEAN, beanName));
		}
		Object bean = null;
		BeanDefinition beanDefinition = contextInfo.getBeanDefinition(beanName);
		if (!beanDefinition.isAbstract()
				&& (SCOPE_SINGLETON.equals(beanDefinition.getScope()) || (includeSessionScopedBeans && SCOPE_SESSION.equals(beanDefinition
						.getScope())))) {
			bean = contextInfo.getBean(beanName);

			Validate.notNull(bean);
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format(DEBUG_RETRIEVED_BEAN, beanName, bean.getClass().getCanonicalName()));
			}
		}
		if (LOG.isDebugEnabled()) {
			logBeanParameters(contextInfo, retrieveBeanClassName(bean, beanDefinition), beanName, beanDefinition);
		}
	}

	private static void logBeanParameters(ContextInformation contextInfo, String beanClassName, String beanName,
			BeanDefinition beanDefinition) {
		List<PropertyValue> propertyValueList = beanDefinition.getPropertyValues().getPropertyValueList();
		if (propertyValueList.size() > 0) {
			for (PropertyValue propertyValue : propertyValueList) {
				Object value = propertyValue.getValue();
				contextInfo.addBeanParameterLog((String.format(DEBUG_BEAN_PROPERTY, beanClassName, beanName, propertyValue.getName(),
						value == null ? null : value.getClass().getSimpleName(), retrieveSimpleValue(contextInfo, value))));
			}
		} else {
			contextInfo.addBeanParameterLog((String.format(DEBUG_BEAN_PROPERTY, beanClassName, beanName, null, null, null)));
		}
	}

	private static String retrieveBeanClassName(Object bean, BeanDefinition beanDefinition) {
		String beanClassName = beanDefinition.getBeanClassName();
		if (beanClassName == null && bean != null) {
			beanClassName = bean.getClass().getCanonicalName();
		}
		return beanClassName;
	}

	private static Object retrieveSimpleValue(ContextInformation contextInfo, Object value) {
		if (value == null) {
			return null;
		}
		Object simpleValue = value;
		if (value instanceof BeanDefinitionHolder) {
			BeanDefinitionHolder holder = (BeanDefinitionHolder) value;
			simpleValue = String.format(DEBUG_BEAN_PROPERTY_REF, holder.getBeanName());
			BeanDefinitionHolder anonymous = (BeanDefinitionHolder) value;
			BeanDefinition anonymousBeanDefinition = anonymous.getBeanDefinition();
			logBeanParameters(contextInfo, anonymousBeanDefinition.getBeanClassName(), anonymous.getBeanName(), anonymousBeanDefinition);
		} else if (value instanceof RuntimeBeanReference) {
			RuntimeBeanReference reference = (RuntimeBeanReference) value;
			simpleValue = String.format(DEBUG_BEAN_PROPERTY_REF, reference.getBeanName());
		} else if (value instanceof TypedStringValue) {
			TypedStringValue stringValue = (TypedStringValue) value;
			simpleValue = stringValue.getValue();
		} else if (value instanceof ManagedList) {
			ManagedList<?> list = (ManagedList<?>) value;
			ArrayList<Object> arrayList = new ArrayList<Object>();
			for (Object listValue : list) {
				arrayList.add(retrieveSimpleValue(contextInfo, listValue));
			}
			simpleValue = arrayList;
		} else if (value instanceof ManagedMap) {
			ManagedMap<?, ?> map = (ManagedMap<?, ?>) value;
			TreeMap<Object, Object> treeMap = new TreeMap<Object, Object>();
			for (Object key : map.keySet()) {
				treeMap.put(retrieveSimpleValue(contextInfo, key), retrieveSimpleValue(contextInfo, map.get(key)));
			}
			simpleValue = treeMap;
		} else if (value instanceof ManagedProperties) {
			ManagedProperties properties = (ManagedProperties) value;
			Properties simpleProperties = new Properties(properties);
			for (Object key : properties.keySet()) {
				simpleProperties.put(retrieveSimpleValue(contextInfo, key), retrieveSimpleValue(contextInfo, properties.get(key)));
			}
			simpleValue = simpleProperties;
		}
		return simpleValue;
	}
}

class ContextInformation {
	private final AbstractApplicationContext context;
	private final ConfigurableListableBeanFactory beanFactory;
	private final Collection<String> beanParameterLogs;

	public ContextInformation(AbstractApplicationContext context) {
		this.context = context;
		this.beanFactory = context.getBeanFactory();
		this.beanParameterLogs = new TreeSet<String>();
	}

	public Collection<String> getBeanParameterLogs() {
		return beanParameterLogs;
	}

	public void addBeanParameterLog(String beanParameterLog) {
		beanParameterLogs.add(beanParameterLog);
	}

	public Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public BeanDefinition getBeanDefinition(String beanName) {
		return beanFactory.getBeanDefinition(beanName);
	}

	public AbstractApplicationContext getContext() {
		return context;
	}

	public ConfigurableListableBeanFactory getBeanFactory() {
		return beanFactory;
	}

}
