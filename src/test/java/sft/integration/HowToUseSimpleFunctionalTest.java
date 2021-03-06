package sft.integration;

import org.junit.runner.RunWith;
import sft.Decorate;
import sft.SimpleFunctionalTest;
import sft.Using;
import sft.decorators.Group;
import sft.integration.extend.Extend;
import sft.integration.hack.Hack;
import sft.integration.set.Settings;
import sft.integration.use.Usages;

/*
<div>
    SimpleFunctionalTest is a java testing framework plugin for jUnit.<br/><br/>

    It is designed to be:
    <ul>
        <li>easy to adopt</li>
        <li>easy to use</li>
        <li>easy to extend</li>
    </ul>

    This framework aimed any developer that want to introduce functional testing in java project.
</div>
<h2>Features</h2>
<div>
    <h3>Easy to adopt</h3>
    <div>
        SFT is full java.<br/>
        SFT is a jUnit plugin.<br/>
        You just have to <a href="#" onclick="$(pom_extract).toggle(); return false;">import SFT dependency</a> to your project and mark class hosting functional test with&nbsp;@RunWith(SimpleFunctionalTest.class)
        <div id="pom_extract" style="display:none">
            in your pom.xml insert :
<pre>...
&lt;dependencies&gt;
    ...
    &lt;dependency&gt;
          &lt;groupId&gt;com.github.slezier&lt;/groupId&gt;
          &lt;artifactId&gt;SimpleFunctionalTest&lt;/artifactId&gt;
          &lt;version&gt;X.X.X&lt;/version&gt;
          &lt;scope&gt;test&lt;/scope&gt;
    &lt;/dependency&gt;
    ...
&lt;/dependencies&gt;
...</pre>
            or download the relevant jar in search.maven.org/remotecontent?filepath=com/github/slezier/SimpleFunctionalTest/ and insert it in your classpath
        </div>
    </div>
    <h3>Easy to use</h3>
    <div>
        Implicit conversion :
        <ul>
            <li>classes implements UseCases</li>
            <li>public test methods implements Scenarios</li>
            <li>non-public methods implements Fixtures (code that glue the system under test)</li>
            <li>public fields implements SubUseCases</li>
            <li>camelCase and underscore java name are humanized</li>
            <li>beforeClass and afterClass JUnit annotation rules use case context</li>
            <li>before and after JUnit annotation rules scenario context</li>
        </ul>
        Functional results are published in HTML.
    </div>
    <h3>Easy to extend</h3>
    <div>
        SFT try to follows this library development guidance:
        <ul>
            <li>Use: 80% of features can be directly used with less code as possible</li>
            <li>Set:&nbsp;other features can be used by settings</li>
            <li>Hack:&nbsp;additional features can be developed using interfaces and injection points</li>
            <li>Extend:&nbsp;additional features can be shared under plugin</li>
            <li>Enhance:&nbsp;for all other needs, welcome aboard: propose feature request to SFT</li>
    </div>

</div>
<h2> Work In Progress </h2>
<ul>
    <li>Having @Displayable displayed even if the scenario fails</li>
</ul>
<h2> TODO </h2>
<ul>
    <li>JUnit: use JUnit Rule instead of SimpleFunctionalTest helper??</li>
    <li>Hack: enhance hacking (extract interface by responsibility)</li>
    <li>Hack/Extend: enhance extension (allow injection)</li>
    <li>Settings/Hack: runner extension: concurrent testing</li>
    <li>Settings/Hack: runner extension: inheritance</li>
    <li>Settings/Hack: runner extension: using mock</li>
    <li>Settings/Hack/Extend: write extension: test result in md</li>
    <li>Settings/Hack/Extend: write extension: test result in Word</li>
    <li>Settings/Hack/Extend: write extension: test result in LaTeX</li>
    <li>JUnit version support</li>
</ul>

*/
@RunWith(SimpleFunctionalTest.class)
@Using(SftDocumentationConfiguration.class)
public class HowToUseSimpleFunctionalTest {
    private static final String EXPLORE = "Explore";

    @Decorate(decorator = Group.class,parameters = EXPLORE)
    public Usages usages= new Usages();
    @Decorate(decorator = Group.class,parameters = EXPLORE)
    public Settings settings = new Settings();
    @Decorate(decorator = Group.class,parameters = EXPLORE)
    public Hack hack = new Hack();
    @Decorate(decorator = Group.class,parameters = EXPLORE)
    public Extend extend = new Extend();
}