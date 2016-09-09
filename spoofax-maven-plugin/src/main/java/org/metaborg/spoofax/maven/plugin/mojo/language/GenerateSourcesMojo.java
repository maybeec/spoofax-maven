package org.metaborg.spoofax.maven.plugin.mojo.language;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.action.CompileGoal;
import org.metaborg.core.build.BuildInput;
import org.metaborg.core.build.BuildInputBuilder;
import org.metaborg.core.messages.StreamMessagePrinter;
import org.metaborg.spoofax.core.resource.SpoofaxIgnoresSelector;
import org.metaborg.spoofax.maven.plugin.SpoofaxInit;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

@Mojo(name = "generate-sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE, requiresDependencyCollection = ResolutionScope.COMPILE)
public class GenerateSourcesMojo extends AbstractSpoofaxLanguageMojo {
    private static final ILogger logger = LoggerUtils.logger(GenerateSourcesMojo.class);

    @Parameter(property = "spoofax.generate-sources.skip", defaultValue = "false") private boolean skip;

    @Override public void execute() throws MojoFailureException, MojoExecutionException {
        if(skip || skipAll) {
            return;
        }
        super.execute();
        discoverLanguages();

        getLog().info("Generating Spoofax sources");

        try {
            SpoofaxInit.spoofaxMeta().metaBuilder.generateSources(buildInput(), null);
        } catch(Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }

        try {
            final BuildInputBuilder inputBuilder = new BuildInputBuilder(languageSpec());
            // @formatter:off
            final BuildInput input = inputBuilder
                .withDefaultIncludePaths(true)
                .withSourcesFromDefaultSourceLocations(true)
                .withSelector(new SpoofaxIgnoresSelector())
                .withMessagePrinter(new StreamMessagePrinter(SpoofaxInit.spoofax().sourceTextService, true, true, logger))
                .withThrowOnErrors(true)
                .withPardonedLanguageStrings(languageSpec().config().pardonedLanguages())
                .addTransformGoal(new CompileGoal())
                .build(SpoofaxInit.spoofax().dependencyService, SpoofaxInit.spoofax().languagePathService)
                ;
            // @formatter:on

            SpoofaxInit.spoofax().processorRunner.build(input, null, null).schedule().block();
        } catch(MetaborgException e) {
            if(e.getCause() != null) {
                logger.error("Exception thrown during generation", e);
                logger.error("GENERATION FAILED");
            } else {
                final String message = e.getMessage();
                if(message != null && !message.isEmpty()) {
                    logger.error(message);
                }
                logger.error("GENERATION FAILED");
            }
            throw new MojoFailureException("GENERATION FAILED", e);
        } catch(InterruptedException e) {
            // Ignore
        }
    }
}
