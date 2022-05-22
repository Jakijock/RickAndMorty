package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/test.feature",
        glue = {"stepdefs"},
        tags = "(@success or @fail)"
)
public class CucumberRunner {
}
