package org.o2i2b2.utils.springframework;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class ExposablePropertyPaceholderConfigurer extends PropertyPlaceholderConfigurer {

	  private Map<String, String> resolvedProps;
	  private Properties properties;
	  
	  
	  @Override
	  protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
	      Properties props) throws BeansException {
	      super.processProperties(beanFactoryToProcess, props);
	      
	      properties = props;
//	      resolvedProps = new HashMap<String, String>();
//	      for (Object key : props.keySet()) {
//	          String keyStr = key.toString();
//	          properties.put(key, arg1)
////	          resolvedProps.put(keyStr, parseStringValue(props.getProperty(keyStr), props, new HashSet()));
//	      }
	  }

	  public Properties getProperties() {
		  return properties;
	  }
	  
	  public Map<String, String> getResolvedProps() {
	      return Collections.unmodifiableMap(resolvedProps);
	  }
}
