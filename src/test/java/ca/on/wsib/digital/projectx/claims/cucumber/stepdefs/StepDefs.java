package ca.on.wsib.digital.projectx.claims.cucumber.stepdefs;

import ca.on.wsib.digital.projectx.claims.ClaimsApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = ClaimsApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
