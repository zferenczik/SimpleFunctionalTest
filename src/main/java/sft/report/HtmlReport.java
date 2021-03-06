/*******************************************************************************
 * Copyright (c) 2013, 2014 Sylvain Lézier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sylvain Lézier - initial implementation
 *******************************************************************************/
package sft.report;

import sft.*;
import sft.decorators.*;
import sft.report.decorators.*;
import sft.result.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;


public class HtmlReport extends Report {

    public static final String HTML_DEPENDENCIES_FOLDER = "sft-html-default";
    public final RelativeHtmlPathResolver pathResolver;
    public String useCaseTemplate =
            "<html>\n" +
                    "  <head>\n" +
                    "    <title>@@@useCase.name@@@</title>\n" +
                    "@@@useCase.css@@@" +
                    "@@@useCase.js@@@" +
                    "  </head>\n" +
                    "  <body class=\"useCase @@@useCase.issue@@@\">\n" +
                    "    <div class=\"container\">\n" +
                    "      <div class=\"page-header\">\n" +
                    "        <div class=\"text-center\">\n" +
                    "          <h1><span class=\"useCaseName\">@@@useCase.name@@@</span></h1>\n" +
                    "        </div>\n" +
                    "@@@useCaseCommentTemplate@@@" +
                    "      </div>\n" +
                    "@@@beforeUseCaseTemplate@@@" +
                    "@@@scenarioTemplates@@@" +
                    "@@@afterUseCaseTemplate@@@" +
                    "@@@relatedUseCasesTemplates@@@" +
                    "    </div>\n" +
                    "  </body>\n" +
                    "</html>";
    public String useCaseCommentTemplate =
            "        <div class=\"comment\">\n" +
                    "@@@comment.text@@@" +
                    "        </div>\n";
    public String beforeUseCaseTemplate =
            "      <div class=\"panel panel-default beforeUseCase @@@beforeUseCase.issue@@@\">\n" +
                    "        <div class=\"panel-body\">\n" +
                    "@@@contextInstructionTemplates@@@" +
                    "        </div>\n" +
                    "@@@exceptionTemplate@@@" +
                    "      </div>\n";
    public String scenarioTemplate =
            "      <div class=\"scenario @@@scenario.issue@@@ panel panel-default\">\n" +
                    "        <div class=\"panel-heading\">\n" +
                    "          <h3><span class=\"scenarioName\">@@@scenario.name@@@</span></h3>\n" +
                    "        </div>\n" +
                    "@@@scenarioCommentTemplate@@@" +
                    "@@@beforeScenarioTemplate@@@" +
                    "        <div class=\"panel-body\">\n" +
                    "@@@scenarioInstructionTemplates@@@" +
                    "        </div>\n" +
                    "@@@afterScenarioTemplate@@@" +
                    "@@@displayedContextsTemplates@@@" +
                    "@@@exceptionTemplate@@@" +
                    "      </div>\n";
    public String scenarioCommentTemplate =
            "        <div class=\"comment\">\n" +
                    "@@@comment.text@@@" +
                    "        </div>\n";
    public String exceptionTemplate =
            "      <div class=\"panel-body\">\n" +
                    "        <div class=\"exception\">\n" +
                    "          <a onClick=\"$(this).next().toggle()\" >@@@failure.className@@@: @@@failure.message@@@</a>\n" +
                    "          <pre class=\"stacktrace pre-scrollable\" >@@@failure.stacktrace@@@</pre>\n" +
                    "        </div>\n" +
                    "      </div>\n";
    public String beforeScenarioTemplate =
            "        <div class=\"beforeScenario panel-body\">\n" +
                    "@@@contextInstructionTemplates@@@" +
                    "          <hr/>\n" +
                    "        </div>";
    public String scenarioInstructionTemplate =
            "          @@@instruction.emptyLines@@@" +
                    "          <div class=\"instruction @@@instruction.issue@@@\">\n" +
                    "            <span>@@@instruction.text@@@</span>" +
                    "          </div>\n";
    public String afterScenarioTemplate =
            "        <div class=\"afterScenario panel-body\">\n" +
                    "          <hr/>\n " +
                    "@@@contextInstructionTemplates@@@" +
                    "        </div>";
    public String displayedContextsTemplate =
            "        <div class=\"displayableContext panel-body\">\n" +
                    "@@@displayedContextTemplates@@@" +
                    "        </div>\n";
    public String displayedContextTemplate =
            "        <div>\n" +
                    "@@@displayedContext.text@@@" +
                    "        </div>\n";
    public String afterUseCaseTemplate =
            "      <div class=\"panel panel-default afterUseCase @@@afterUseCase.issue@@@\">\n" +
                    "        <div class=\"panel-body\">\n" +
                    "@@@contextInstructionTemplates@@@" +
                    "        </div>\n" +
                    "@@@exceptionTemplate@@@" +
                    "      </div>\n";
    public String contextInstructionTemplate =
            "          <div>\n" +
                    "            <span>@@@instruction.text@@@</span>\n" +
                    "          </div>\n";

    public String relatedUseCasesTitleTemplate =
            "        <div class=\"panel-heading\">\n" +
                    "          <h3>@@@relatedUseCasesTitle@@@</h3>\n" +
                    "        </div>\n";
    public String relatedUseCasesTemplate =
            "      <div class=\"panel panel-default relatedUseCases\">\n" +
                    "@@@relatedUseCasesTitleTemplates@@@" +
                    "        <div class=\"panel-body\">\n" +
                    "          <ul>\n" +
                    "@@@relatedUseCaseTemplates@@@" +
                    "          </ul>\n" +
                    "        </div>\n" +
                    "      </div>\n";
    public String relatedUseCaseTemplate =
            "            <li class=\"relatedUseCase @@@relatedUseCase.issue@@@\">\n" +
                    "              <a href=\"@@@relatedUseCase.link@@@\"><span>@@@relatedUseCase.name@@@</span></a>\n" +
                    "            </li>\n";
    public String parameterTemplate = "<i class=\"value\">@@@parameter.value@@@</i>";
    public String ignoredClass = "ignored";
    public String failedClass = "failed";
    public String successClass = "succeeded";
    private HtmlResources htmlResources;
    private DefaultConfiguration configuration;
    private Map<Class<? extends Decorator>, Class<? extends HtmlDecorator>> decorators= new HashMap<Class<? extends Decorator>, Class<? extends HtmlDecorator>>();

    public HtmlReport(DefaultConfiguration configuration) {
        this.configuration = configuration;
        setResourcePath(HTML_DEPENDENCIES_FOLDER);
        setReportPath(configuration.getTargetFolder().path);
        pathResolver = new RelativeHtmlPathResolver();
        decorators.put(Style.class,HtmlStyle.class);
        decorators.put(Breadcrumb.class,HtmlBreadcrumb.class);
        decorators.put(Group.class,HtmlGroup.class);
        decorators.put(Table.class,HtmlTable.class);
        decorators.put(TableOfContent.class,HtmlTableOfContent.class);
        decorators.put(Synthesis.class,HtmlSynthesis.class);
        decorators.put(NullDecorator.class,HtmlNullDecorator.class);
    }


    public void addDecorator(Class<? extends Decorator> decoratorClass, Class<? extends HtmlDecorator> htmlDecoratorImplementationClass) {
        decorators.put(decoratorClass,htmlDecoratorImplementationClass);
    }

    @Override
    public void setReportPath(String reportPath) {
        if (reportPath != null && !reportPath.equals(getReportPath())) {
            super.setReportPath(reportPath);
            configuration.setTargetPath(reportPath);
        }
    }

    @Override
    public DecoratorReportImplementation getDecoratorImplementation(Decorator decorator) {
        if( decorators.containsKey(decorator.getClass())){
            Class<? extends HtmlDecorator> decoratorImplementationClass = decorators.get(decorator.getClass());
            try {
                Constructor<? extends HtmlDecorator> declaredConstructor = decoratorImplementationClass.getDeclaredConstructor(DefaultConfiguration.class);
                return declaredConstructor.newInstance(configuration);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace(System.err);
            }
        }
        System.out.println("Decorator " + decorator.getClass().getCanonicalName() + " not Managed by " + this.getClass().getCanonicalName() + " using default decorator");
        return new HtmlNullDecorator(configuration);
    }

    @Override
    public void setResourcePath(String resourcePath) {
        if (resourcePath != null && !resourcePath.equals(getResourcePath())) {
            super.setResourcePath(resourcePath);
            htmlResources = new HtmlResources(configuration, resourcePath);
        }
    }

    public void report(UseCaseResult useCaseResult) throws IOException, IllegalAccessException {
        final Decorator decorator = useCaseResult.useCase.useCaseDecorator;
        String useCaseReport = getDecoratorImplementation(decorator).applyOnUseCase(useCaseResult, decorator.parameters);

        File htmlFile = configuration.getTargetFolder().createFileFromClass(useCaseResult.useCase.classUnderTest, ".html");
        Writer html = new OutputStreamWriter(new FileOutputStream(htmlFile));
        html.write(useCaseReport);
        html.close();
        System.out.println("Report wrote: " + htmlFile.getCanonicalPath());
    }

    public String applyOnUseCase(UseCaseResult useCaseResult) {
        Class<?> classUnderTest = useCaseResult.useCase.classUnderTest;
        try {
            return new TemplateString(useCaseTemplate)
                    .replace("@@@useCase.name@@@", useCaseResult.useCase.getName())
                    .replace("@@@useCase.css@@@", htmlResources.getIncludeCssDirectives(classUnderTest))
                    .replace("@@@useCase.js@@@", htmlResources.getIncludeJsDirectives(classUnderTest))
                    .replace("@@@useCase.issue@@@", htmlResources.convertIssue(useCaseResult.getIssue()))
                    .replace("@@@useCaseCommentTemplate@@@", getUseCaseComment(useCaseResult.useCase))
                    .replace("@@@beforeUseCaseTemplate@@@", getBeforeUseCase(useCaseResult))
                    .replace("@@@scenarioTemplates@@@", getScenarios(useCaseResult))
                    .replace("@@@afterUseCaseTemplate@@@", getAfterUseCase(useCaseResult))
                    .replace("@@@relatedUseCasesTemplates@@@", getRelatedUseCases(useCaseResult))
                    .getText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String applyOnScenario(ScenarioResult scenarioResult) {
        return new TemplateString(scenarioTemplate)
                .replace("@@@scenario.issue@@@", htmlResources.convertIssue(scenarioResult.issue))
                .replace("@@@scenario.name@@@", scenarioResult.scenario.getName())
                .replace("@@@scenarioCommentTemplate@@@", getScenarioComment(scenarioResult))
                .replace("@@@beforeScenarioTemplate@@@", getBeforeScenario(scenarioResult))
                .replace("@@@scenarioInstructionTemplates@@@", getScenarioInstructions(scenarioResult))
                .replace("@@@afterScenarioTemplate@@@", getAfterScenario(scenarioResult))
                .replace("@@@displayedContextsTemplates@@@", extractDisplayedContexts(scenarioResult))
                .replace("@@@exceptionTemplate@@@", getStackTrace(scenarioResult.failure))
                .getText();
    }

    public String generateFixtureCall(FixtureCallResult fixtureCallResult) {
        return new TemplateString(scenarioInstructionTemplate)
                .replace("@@@instruction.emptyLines@@@", generateEmptyLines(fixtureCallResult.fixtureCall.emptyLine))
                .replace("@@@instruction.issue@@@", htmlResources.convertIssue(fixtureCallResult.issue))
                .replace("@@@instruction.text@@@", generateInstructionWithParameter(fixtureCallResult.fixtureCall))
                .getText();
    }

    private String generateEmptyLines(int emptyLine) {
        String result = "";
        for (int i = 0; i < emptyLine; i++) {
            result += "<br/>";
        }
        return result;
    }

    private String getRelatedUseCases(UseCaseResult useCaseResult) {
        if (!useCaseResult.subUseCaseResults.isEmpty()) {
            return getRelatedUseCase(useCaseResult);
        }
        return "";
    }

    private String getRelatedUseCase(UseCaseResult useCaseResult) {
        String result = "";
        Decorator decorator = null;
        ArrayList<SubUseCaseResult> subUseCaseResults = new ArrayList<SubUseCaseResult>();
        for (SubUseCaseResult subUseCaseResult : useCaseResult.subUseCaseResults) {
            if (decorator == null) {
                decorator = subUseCaseResult.subUseCase.decorator;
            } else if (!decorator.comply(subUseCaseResult.subUseCase.decorator)) {
                result += getDecoratorImplementation(decorator).applyOnSubUseCases(subUseCaseResults, decorator.parameters);
                decorator = subUseCaseResult.subUseCase.decorator;
                subUseCaseResults = new ArrayList<SubUseCaseResult>();
            }
            subUseCaseResults.add(subUseCaseResult);
        }
        if (!subUseCaseResults.isEmpty()) {
            result += getDecoratorImplementation(decorator).applyOnSubUseCases(subUseCaseResults, decorator.parameters);
        }
        return result;
    }

    public String applyOnSubUseCases(String title, List<SubUseCaseResult> subUseCaseResults) {
        String relatedUseCase = "";
        for (SubUseCaseResult subUseCaseResult : subUseCaseResults) {
            relatedUseCase += new TemplateString(relatedUseCaseTemplate)
                    .replace("@@@relatedUseCase.issue@@@", htmlResources.convertIssue(subUseCaseResult.useCaseResult.getIssue()))
                    .replace("@@@relatedUseCase.link@@@", getRelativeUrl(subUseCaseResult.useCaseResult.useCase, subUseCaseResult.subUseCase.parentUseCase))
                    .replace("@@@relatedUseCase.name@@@", subUseCaseResult.useCaseResult.useCase.getName())
                    .getText();
        }

        final TemplateString replace = new TemplateString(relatedUseCasesTemplate)
                .replace("@@@relatedUseCaseTemplates@@@", relatedUseCase);

        if (title != null) {
            return replace.replace("@@@relatedUseCasesTitleTemplates@@@", new TemplateString(relatedUseCasesTitleTemplate).replace("@@@relatedUseCasesTitle@@@", title).getText()).getText();
        } else {
            return replace.replace("@@@relatedUseCasesTitleTemplates@@@", "").getText();
        }
    }

    private String getScenarios(UseCaseResult useCaseResult) {
        String result = "";
        Decorator decorator = new NullDecorator(configuration);

        final ArrayList<ScenarioResult> scenarioResults= new ArrayList<ScenarioResult>();

        for( ScenarioResult scenarioResult : useCaseResult.scenarioResults ){
            final Scenario scenario = scenarioResult.scenario;
            if(decorator != null && !decorator.comply(scenario.decorator)) {
                result += getDecoratorImplementation(decorator).applyOnScenarios(scenarioResults, decorator.parameters);
                scenarioResults.clear();
            }
            decorator = scenario.decorator;
            scenarioResults.add(scenarioResult);
        }
        result += getDecoratorImplementation(decorator).applyOnScenarios(scenarioResults, decorator.parameters);
        return result;
    }

    private String getScenarioInstructions(ScenarioResult scenarioResult) {
        String result = "";
        Decorator decorator = null;

        final ArrayList<FixtureCallResult> fixtureCallResults = new ArrayList<FixtureCallResult>();

        for (FixtureCallResult fixtureCallResult : scenarioResult.fixtureCallResults) {
            final Fixture fixture = fixtureCallResult.fixtureCall.fixture;
            if (decorator != null && !decorator.comply(fixture.decorator)) {
                result += getDecoratorImplementation(decorator).applyOnFixtures(fixtureCallResults, decorator.parameters);
                fixtureCallResults.clear();
            }
            decorator = fixture.decorator;
            fixtureCallResults.add(fixtureCallResult);
        }
        result += getDecoratorImplementation(decorator).applyOnFixtures(fixtureCallResults, decorator.parameters);
        return result;
    }


    private String getBeforeScenario(ScenarioResult scenarioResult) {
        if (scenarioResult.scenario.useCase.beforeScenario != null) {
            String instructions = getContextInstructions(scenarioResult.scenario.useCase.beforeScenario);
            return new TemplateString(beforeScenarioTemplate)
                    .replace("@@@contextInstructionTemplates@@@", instructions)
                    .getText();
        }
        return "";
    }

    private String getAfterScenario(ScenarioResult scenarioResult) {
        if (scenarioResult.scenario.useCase.afterScenario != null) {
            String instructions = getContextInstructions(scenarioResult.scenario.useCase.afterScenario);
            return new TemplateString(afterScenarioTemplate)
                    .replace("@@@contextInstructionTemplates@@@", instructions)
                    .getText();
        }
        return "";
    }

    private String getScenarioComment(ScenarioResult scenarioResult) {
        String comment = "";
        if (scenarioResult.scenario.getComment() != null) {
            comment = new TemplateString(scenarioCommentTemplate)
                    .replace("@@@comment.text@@@", scenarioResult.scenario.getComment())
                    .getText();
        }
        return comment;
    }

    private String extractDisplayedContexts(ScenarioResult scenarioResult) {
        List<String> values = scenarioResult.contextToDisplay;
        if (!values.isEmpty()) {
            String htmlText = "";
            for (String value : values) {
                htmlText += extractDisplayedContext(value);
            }
            return new TemplateString(displayedContextsTemplate)
                    .replace("@@@displayedContextTemplates@@@", htmlText)
                    .getText();
        }
        return "";
    }

    private String getStackTrace(Throwable failure) {
        if (failure != null) {
            StringWriter stringWriter = new StringWriter();
            failure.printStackTrace(new PrintWriter(stringWriter));
            return new TemplateString(exceptionTemplate)
                    .replace("@@@failure.className@@@", failure.getClass().getSimpleName())
                    .replace("@@@failure.message@@@", failure.getMessage())
                    .replace("@@@failure.stacktrace@@@", stringWriter.toString())
                    .getText();
        }
        return "";
    }

    private String extractDisplayedContext(String value) {
        return new TemplateString(displayedContextTemplate)
                .replace("@@@displayedContext.text@@@", value)
                .getText();
    }

    private String getBeforeUseCase(UseCaseResult useCaseResult) {
        UseCase useCase = useCaseResult.useCase;
        if (useCase.beforeUseCase != null) {
            return new TemplateString(beforeUseCaseTemplate)
                    .replace("@@@beforeUseCase.issue@@@", htmlResources.convertIssue(useCaseResult.beforeResult.issue))
                    .replace("@@@contextInstructionTemplates@@@", getContextInstructions(useCase.beforeUseCase))
                    .replace("@@@exceptionTemplate@@@", getStackTrace(useCaseResult.beforeResult))
                    .getText();
        } else {
            return "";
        }
    }

    private String getAfterUseCase(UseCaseResult useCaseResult) throws IOException {
        UseCase useCase = useCaseResult.useCase;
        if (useCase.afterUseCase != null) {
            return new TemplateString(afterUseCaseTemplate)
                    .replace("@@@afterUseCase.issue@@@", htmlResources.convertIssue(useCaseResult.afterResult.issue))
                    .replace("@@@contextInstructionTemplates@@@", getContextInstructions(useCase.afterUseCase))
                    .replace("@@@exceptionTemplate@@@", getStackTrace(useCaseResult.afterResult))
                    .getText();
        } else {
            return "";
        }
    }

    private String getStackTrace(ContextResult contextResult) {
        if (contextResult.isSuccessful()) {
            return "";
        } else {
            return getStackTrace(contextResult.exception);
        }
    }

    private String getContextInstructions(ContextHandler context) {
        String instructions = "";
        for (FixtureCall fixtureCall : context.fixtureCalls) {
            instructions += new TemplateString(contextInstructionTemplate)
                    .replace("@@@instruction.text@@@", generateInstructionWithParameter(fixtureCall))
                    .getText();
        }
        return instructions;
    }

    public String generateInstructionWithParameter(FixtureCall testFixture) {
        String instruction = testFixture.fixture.getText();
        for (Map.Entry<String, String> parameter : testFixture.getParameters().entrySet()) {
            String value = Matcher.quoteReplacement(getParameter(parameter.getValue()));
            instruction = instruction.replaceAll("\\$\\{" + parameter.getKey() + "\\}", value);
        }
        return instruction;
    }

    private String getParameter(String value) {
        return new TemplateString(parameterTemplate)
                .replace("@@@parameter.value@@@", value)
                .getText();
    }

    private String getUseCaseComment(UseCase useCase) {
        String comment = "";
        if (useCase.haveComment()) {
            return new TemplateString(useCaseCommentTemplate)
                    .replace("@@@comment.text@@@", useCase.getComment())
                    .getText();
        }
        return comment;
    }

    private String getRelativeUrl(UseCase subUseCase, UseCase parentUseCase) {
        String source = pathResolver.getPathOf(parentUseCase.classUnderTest, ".html");
        String target = pathResolver.getPathOf(subUseCase.classUnderTest, ".html");
        return pathResolver.getRelativePathToFile(source, target);
    }

    public HtmlResources getHtmlResources() {
        return htmlResources;
    }

    public String getIssueConverter(Issue issue) {
        switch (issue) {
            case IGNORED:
                return ignoredClass;
            case FAILED:
                return failedClass;
            default:
            case SUCCEEDED:
                return successClass;
        }
    }

    public String applyOnFixtures(List<FixtureCallResult> fixtureCallResults) {
        String result = "";
        for (FixtureCallResult fixture : fixtureCallResults) {
            result += generateFixtureCall(fixture);
        }
        return result;
    }

    public String applyOnScenarios(List<ScenarioResult> scenarioResults) {
        String scenarioTxt = "";
        for (ScenarioResult scenarioResult : scenarioResults) {
            scenarioTxt += applyOnScenario(scenarioResult);
        }
        return scenarioTxt;

    }
}