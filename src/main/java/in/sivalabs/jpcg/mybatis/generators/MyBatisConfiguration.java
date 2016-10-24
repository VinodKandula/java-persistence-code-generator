/**
 * 
 */
package in.sivalabs.jpcg.mybatis.generators;

import in.sivalabs.jpcg.core.configuration.Configuration;

/**
 * @author Siva
 *
 */
public class MyBatisConfiguration
{
	private Configuration configuration;
	
	public MyBatisConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
	}
	
	public String getJavaDomainPackage()
	{
		return this.configuration.getJavaBasePackage()+".entities";
	}
	
	public String getJavaMappersPackage()
	{
		return this.configuration.getJavaBasePackage()+".mappers";
	}
	
	public String getJavaServicesPackage()
	{
		return this.configuration.getJavaBasePackage()+".services";
	}
}
