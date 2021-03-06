package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.*;

public class ConfluenceProductHandler extends AbstractWebappProductHandler
{
    public ConfluenceProductHandler(MavenProject project, MavenGoals goals)
    {
        super(project, goals, new ConfluencePluginProvider());
    }

    public String getId()
    {
        return "confluence";
    }

    @Override
    protected boolean isStaticPlugin()
    {
        // assume all Confluence plugins should be installed as bundled plugins -- a pretty good assumption
        return false;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.confluence", "confluence-webapp", "RELEASE");
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.confluence.plugins", "confluence-plugin-test-resources", "LATEST");
    }

    public int getDefaultHttpPort()
    {
        return 1990;
    }

    @Override
    public Map<String, String> getSystemProperties(Product ctx)
    {
        return Collections.singletonMap("confluence.home", getHomeDirectory(ctx).getPath());
    }

    @Override
    public File getUserInstalledPluginsDirectory(final File webappDir, File homeDir)
    {
        // indicates plugins should be bundled
        return null;
    }

    @Override
    public List<ProductArtifact> getExtraContainerDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public String getBundledPluginPath(Product ctx)
    {
        return "WEB-INF/classes/com/atlassian/confluence/setup/atlassian-bundled-plugins.zip";
    }

    @Override
    public void processHomeDirectory(Product ctx, File homeDir) throws MojoExecutionException
    {
        ConfigFileUtils.replace(new File(homeDir, "confluence.cfg.xml"), "@project-dir@", homeDir.getParent());
        ConfigFileUtils.replace(new File(homeDir, "confluence.cfg.xml"), "/confluence-home/", "/home/");

        File script = new File(new File(homeDir, "database"), "confluencedb.script");
        if (script.exists())
        {
            ConfigFileUtils.replace(new File(new File(homeDir, "database"), "confluencedb.script"),
                    "<baseUrl>http://localhost:8080</baseUrl>",
                    "<baseUrl>http://" + ctx.getServer() + ":" + ctx.getHttpPort() + "/" + ctx.getContextPath().replaceAll("^/|/$", "") + "</baseUrl>");
        }
        else
        {
            ConfigFileUtils.replace(new File(new File(homeDir, "database"), "confluencedb.log"),
                    "<baseUrl>http://localhost:8080</baseUrl>",
                    "<baseUrl>http://" + ctx.getServer() + ":" + ctx.getHttpPort() + "/" + ctx.getContextPath().replaceAll("^/|/$", "") + "</baseUrl>");
        }
    }

    @Override
    public List<ProductArtifact> getDefaultLibPlugins()
    {
        return Collections.emptyList();
    }

    @Override
    public List<ProductArtifact> getDefaultBundledPlugins()
    {
        return Collections.emptyList();
    }

    private static class ConfluencePluginProvider extends AbstractPluginProvider
    {

        @Override
        protected Collection<ProductArtifact> getSalArtifacts(String salVersion)
        {
            return Arrays.asList(
                new ProductArtifact("com.atlassian.sal", "sal-api", salVersion),
                new ProductArtifact("com.atlassian.sal", "sal-confluence-plugin", salVersion));
        }

        @Override
        protected Collection<ProductArtifact> getPdkInstallArtifacts(String pdkInstallVersion)
        {
            return Collections.emptyList();
        }
    }
}
