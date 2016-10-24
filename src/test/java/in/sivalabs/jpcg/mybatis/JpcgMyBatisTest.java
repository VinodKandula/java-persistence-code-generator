/**
 * 
 */
package in.sivalabs.jpcg.mybatis;

import java.io.InputStream;

import in.sivalabs.jpcg.core.JPCG;
import in.sivalabs.jpcg.core.configuration.Configuration;
import in.sivalabs.jpcg.mybatis.generators.MyBatisJavaArtifactsGenerator;
import in.sivalabs.jpcg.mybatis.generators.MyBatisSqlMapGenerator;

/**
 * @author Siva
 *
 */
public class JpcgMyBatisTest 
{

	public static void main(String[] args) throws Exception 
	{
		String configFile = "/config.xml";
		InputStream stream = JpcgMyBatisTest.class.getClass().getResourceAsStream(configFile);
		
		JPCG jpcg = JPCG.getInstance(stream);
		Configuration configuration = jpcg.getConfiguration();
		
		MyBatisJavaArtifactsGenerator javaArtifactsGenerator = new MyBatisJavaArtifactsGenerator(configuration);
		MyBatisSqlMapGenerator sqlMapGenerator = new MyBatisSqlMapGenerator(configuration);

		jpcg.generateArtifacts(javaArtifactsGenerator, sqlMapGenerator);
		
	}

}
