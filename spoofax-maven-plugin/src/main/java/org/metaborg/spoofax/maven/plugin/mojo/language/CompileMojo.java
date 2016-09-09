package org.metaborg.spoofax.maven.plugin.mojo.language;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.metaborg.core.MetaborgException;
import org.metaborg.spoofax.maven.plugin.SpoofaxInit;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class CompileMojo extends AbstractSpoofaxLanguageMojo {
    private static final ILogger logger = LoggerUtils.logger(CompileMojo.class);

    @Parameter(property = "spoofax.compile.skip", defaultValue = "false") private boolean skip;


    @Override public void execute() throws MojoFailureException, MojoExecutionException {
        if(skip || skipAll) {
            return;
        }
        super.execute();
        discoverLanguages();

        final MavenProject project = mavenProject();
        if(project == null) {
            throw new MojoExecutionException("Maven project is null, cannot build project");
        }

        try {
            SpoofaxInit.spoofaxMeta().metaBuilder.compile(buildInput());
        } catch(MetaborgException e) {
            if(e.getCause() != null) {
                logger.error("Exception thrown during build", e);
                logger.error("BUILD FAILED");
            } else {
                final String message = e.getMessage();
                if(message != null && !message.isEmpty()) {
                    logger.error(message);
                }
                logger.error("BUILD FAILED");
            }
            throw new MojoFailureException("BUILD FAILED", e);
        }
    }
}
