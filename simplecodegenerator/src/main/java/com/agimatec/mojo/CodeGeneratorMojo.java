package com.agimatec.mojo;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A very simple code generator plugin for maven.
 *
 * @goal process-template
 * @phase generate-sources
 */
public class CodeGeneratorMojo
        extends AbstractMojo {

    /**
     * Template file.
     *
     * @parameter
     * @required
     */
    private File template;
    /**
     * Location of the generated file.
     *
     * @parameter
     * @required
     */
    private File targetFile;

    /**
     * @parameter
     * @required
     */
    private List<String> entries;

    public void execute()
            throws MojoExecutionException {
        Map<String,List> model = new HashMap<String,List>();
        model.put("entries",entries);
        Configuration freemarkerConfiguration = new Configuration();
        try {
            freemarkerConfiguration.setDirectoryForTemplateLoading(template.getParentFile());
        } catch (IOException e) {
            throw new MojoExecutionException("Error when setting " +
                    "template directory. Check the given template path:" + template.getAbsolutePath(), e);
        }
        freemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper());
        Template templateEngine = null;
        try {
            templateEngine = freemarkerConfiguration.getTemplate(template.getName());
        } catch (IOException e) {
            throw new MojoExecutionException("Could not load template file: " + template.getAbsolutePath(), e);
        }
        if (!targetFile.exists()) {
            try {
                FileUtils.forceMkdir(targetFile.getParentFile());
                targetFile.createNewFile();
            } catch (IOException e) {
                throw new MojoExecutionException("Could not create target file:" + targetFile.getAbsolutePath(), e);
            }
        }
        FileWriter out = null;
        try {
            out = new FileWriter(targetFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write into target file:" + targetFile.getAbsolutePath(), e);
        }
        try {
            templateEngine.process(model, out);
        } catch (TemplateException e) {

            throw new MojoExecutionException("Error when processing the template: \n"+e.getFTLInstructionStack(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write into target file:" + targetFile.getAbsolutePath(), e);
        }
    }
}
