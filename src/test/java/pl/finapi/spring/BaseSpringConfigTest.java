package pl.finapi.spring;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class BaseSpringConfigTest {

	@Test
	public void shouldInstantiateAllBeans() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(getContextFileNames());
		try {
			BeanInstantiater.instantiateBeans(context, true);
		} finally {
			context.close();
		}

	}

	protected abstract String[] getContextFileNames();
}
