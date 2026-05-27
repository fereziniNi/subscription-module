package br.ifsp.demo.suits.ui;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("br.ifsp.demo")
@IncludeTags("EquivalenceClassUi")
public class EquivalenceClassUiTestSuite {
}