package com.agimatec.mojo.sizewatch;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Maven2 plugin that allows to verify the size of a file (artifact) for min. and max. values.
 * <p/>
 * configuration example:
 * <pre>
 * &lt;plugin>
 * &lt;groupId>agimatec.maven.plugins&lt;/groupId>
 * &lt;artifactId>maven-sizewatch-plugin&lt;/artifactId>
 * &lt;!--&lt;version>1.0&lt;/version>-->
 * &lt;executions>
 * &lt;execution>
 * &lt;phase>verify&lt;/phase>
 * &lt;goals>
 * &lt;goal>verifysize&lt;/goal>
 * &lt;/goals>
 * &lt;configuration>
 *   &lt;!--&lt;failonerror>true&lt;/failonerror>-->
 *   &lt;!--&lt;minsize>0&lt;/minsize>-->
 *   &lt;maxsize>1000&lt;/maxsize>
 *   &lt;!--&lt;artifact>target/myartifact-1.0-SNAPSHOT.jar&lt;/artifact>-->
 * &lt;/configuration>
 * &lt;/execution>
 * &lt;/executions>
 * &lt;/plugin>
 * </pre>
 *
 * @goal verifysize
 */
public class VerifySizeMojo extends AbstractMojo {
    /**
     * the max size allowed
     *
     * @parameter default-value="10000"
     */
    long maxsize;

    /**
     * the max size allowed
     *
     * @parameter default-value="0"
     */
    long minsize;

    /** @parameter default-value="true" */
    boolean failonerror;

    /**
     * the artifact file to check
     *
     * @parameter default-value="${project.build.directory}/${project.build.finalName}.${project.packaging}"
     */
    File artifact;

    // other properties (tested)
    // ${project.artifactId}-${project.version}
    public void execute() throws MojoExecutionException {
        if (artifact.exists()) {
            long length = artifact.length();
            if (length < minsize) {
                reportError(artifact + " size (" + length + ") too small! Min. is " +
                        minsize);
            } else if (length > maxsize) {
                reportError(artifact + " size (" + length + ") too large! Max. is " +
                        maxsize);
            } else {
                getLog().info(artifact + " size (" + length + ") is OK (" + (
                        minsize == maxsize || minsize == 0 ? ("max. " + maxsize) :
                                ("between " + minsize + " and " + maxsize)) + " byte).");
            }
        } else {
            reportError(artifact + " does not exist!");
        }
    }

    private void reportError(String msg) throws MojoExecutionException {
        getLog().error(msg);
        if (failonerror) throw new MojoExecutionException(msg);
    }
}
