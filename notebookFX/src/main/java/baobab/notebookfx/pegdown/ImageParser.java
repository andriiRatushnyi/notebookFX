package baobab.notebookfx.pegdown;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringBuilderVar;
import org.pegdown.Parser;
import org.pegdown.plugins.InlinePluginParser;

public class ImageParser extends Parser implements InlinePluginParser {

    public ImageParser() {
        super(ALL, 1000l, DefaultParseRunnerProvider);
    }

    @Override
    public Rule[] inlinePluginRules() {
        return new Rule[]{InlinePlugin()};
    }

    public Rule InlinePlugin() {
        StringBuilderVar text = new StringBuilderVar();
        return NodeSequence(
                "IMG#",
                OneOrMore(
                    TestNot("#IMG"),
                    BaseParser.ANY,
                    text.append(matchedChar())
                ),
                push(new ImageNode(text.getString())),
                "#IMG"
        );
    }

}
