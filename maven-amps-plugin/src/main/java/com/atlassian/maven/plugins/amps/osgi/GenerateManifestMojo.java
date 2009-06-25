package com.atlassian.maven.plugins.amps.osgi;

import com.atlassian.maven.plugins.amps.AbstractAmpsMojo;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.MavenContext;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.HashMap;
import java.util.Map;

import aQute.lib.osgi.Constants;

/**
 * @goal generate-manifest
 */
public class GenerateManifestMojo extends AbstractAmpsMojo {

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    /**
     * The Maven Session Object
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    protected MavenSession session;
    /**
     * The Maven PluginManager Object
     *
     * @component
     * @required
     */
    protected PluginManager pluginManager;

    /**
    * The BND instructions for the bundle.
    *
    * @parameter
    */
   private Map instructions = new HashMap();


    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenGoals goals = new MavenGoals(new MavenContext(project, session, pluginManager, getLog()));
        if (!instructions.isEmpty() || isAtlassianPlugin())
        {
            getLog().info("Generating a manifest for this plugin");

            if (!instructions.containsKey(Constants.EXPORT_PACKAGE))
            {
                instructions.put(Constants.EXPORT_PACKAGE, "");
            }
            goals.generateManifest(instructions);
        }
    }

    private boolean isAtlassianPlugin()
    {
        return project.getGroupId().startsWith("com.atlassian");
    }

}