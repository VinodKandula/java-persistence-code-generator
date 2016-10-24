/**
 * 
 */
package in.sivalabs.jpcg.core;

import java.io.InputStream;

import in.sivalabs.jpcg.core.configuration.Configuration;

/**
 * @author Siva
 *
 */
public class JPCG
{
	private Configuration configuration = null;
	
	public JPCG(Configuration configuration)
	{
		this.configuration = configuration;
	}
	
	public static JPCG getInstance(String configFile)
	{
		Configuration config = ConfigurationBuilder.getConfiguration(configFile);
		return new JPCG(config);
	}
	
	public static JPCG getInstance(InputStream is)
	{
		Configuration config = ConfigurationBuilder.getConfiguration(is);
		return new JPCG(config);
	}
	
	public Configuration getConfiguration()
	{
		return configuration;
	}

	public void generateArtifacts(ArtifactGenerator... artifactGenerators)
	{
		if(artifactGenerators != null && artifactGenerators.length > 0)
		{
			for (ArtifactGenerator artifactGenerator : artifactGenerators)
			{
				artifactGenerator.generate();
			}
		}
	}
}
