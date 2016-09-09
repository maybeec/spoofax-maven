package org.metaborg.spoofax.maven.plugin.mojo.language;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.build.CleanInput;
import org.metaborg.core.build.CleanInputBuilder;
import org.metaborg.spoofax.core.resource.SpoofaxIgnoresSelector;
import org.metaborg.spoofax.maven.plugin.SpoofaxInit;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN, requiresDependencyResolution = ResolutionScope.COMPILE,
    requiresDependencyCollection = ResolutionScope.COMPILE)
public class CleanMojo extends AbstractSpoofaxLanguageMojo {
    private static final ILogger logger = LoggerUtils.logger(CleanMojo.class);

    @Parameter(property = "clean.skip", defaultValue = "false") private boolean skip;


    @Override public void execute() throws MojoFailureException, MojoExecutionException {
        if(skip) {
            return;
        }
        super.execute();
        discoverLanguages();

        final CleanInput input;
        try {
            final CleanInputBuilder inputBuilder = new CleanInputBuilder(languageSpec());
            // @formatter:off
            input = inputBuilder
                .withSelector(new SpoofaxIgnoresSelector())
                .build(SpoofaxInit.spoofax().dependencyService)
                ;
            // @formatter:on
        } catch(MetaborgException e) {
            throw new MojoExecutionException("Building clean input failed unexpectedly", e);
        }

        try {
            SpoofaxInit.spoofax().processorRunner.clean(input, null, null).schedule().block();
            SpoofaxInit.spoofaxMeta().metaBuilder.clean(buildInput());
        } catch(MetaborgException e) {
            if(e.getCause() != null) {
                logger.error("Exception thrown during clean", e);
                logger.error("CLEAN FAILED");
            } else {
                final String message = e.getMessage();
                if(message != null && !message.isEmpty()) {
                    logger.error(message);
                }
                logger.error("CLEAN FAILED");
            }
            throw new MojoFailureException("CLEAN FAILED", e);
        } catch(InterruptedException e) {
            // Ignore
        }
    }
}
