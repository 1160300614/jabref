package org.jabref.gui;

import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.text.JTextComponent;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.dependency.jsr305.Nonnull;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.timing.Condition;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.swing.finder.WindowFinder.findDialog;
import static org.assertj.swing.timing.Pause.pause;

@Tag("GUITest")
public class IdFetcherDialogTest extends AbstractUITest {

    public static Stream<Object[]> instancesToTest() {
        return Stream.of(
                new Object[]{"BibTeX", "DOI", "10.1002/9781118257517"},
                new Object[]{"biblatex", "DOI", "10.1002/9781118257517"},
                new Object[]{"BibTeX", "ISBN", "9780321356680"},
                new Object[]{"biblatex", "ISBN", "9780321356680"}
        );
    }

    @ParameterizedTest
    @MethodSource("instancesToTest")
    public void insertEmptySearchID(String databaseMode, String fetcherType, String fetchID) {
        mainFrame.menuItemWithPath("File", "New " + databaseMode + " database").click();
        JTableFixture entryTable = mainFrame.table();

        entryTable.requireRowCount(0);
        mainFrame.menuItemWithPath("BibTeX", "New entry...").click();

        GenericTypeMatcher<JDialog> matcher = new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Select entry type".equals(dialog.getTitle());
            }
        };

        findDialog(matcher).withTimeout(10_000).using(robot()).button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(@Nonnull JButton jButton) {
                return "Generate".equals(jButton.getText());
            }
        }).click();

        GenericTypeMatcher<JDialog> matcherEmptyDialog = new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Empty search ID".equals(dialog.getTitle());
            }
        };

        findDialog(matcherEmptyDialog).withTimeout(10_000).using(robot()).button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(@Nonnull JButton jButton) {
                return "OK".equals(jButton.getText());
            }
        }).click();

        entryTable.requireRowCount(0);
    }

    @ParameterizedTest
    @MethodSource("instancesToTest")
    public void testFetcherDialog(String databaseMode, String fetcherType, String fetchID) {
        mainFrame.menuItemWithPath("File", "New " + databaseMode + " database").click();
        JTableFixture entryTable = mainFrame.table();

        entryTable.requireRowCount(0);
        mainFrame.menuItemWithPath("BibTeX", "New entry...").click();

        GenericTypeMatcher<JDialog> matcher = new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Select entry type".equals(dialog.getTitle());
            }
        };

        findDialog(matcher).withTimeout(10_000).using(robot()).comboBox(new GenericTypeMatcher<JComboBox>(JComboBox.class) {
            @Override
            protected boolean isMatching(@Nonnull JComboBox component) {
                return true;
            }
        }).selectItem(fetcherType);

        findDialog(matcher).withTimeout(10_000).using(robot()).textBox(new GenericTypeMatcher<JTextComponent>(JTextComponent.class) {
            @Override
            protected boolean isMatching(@Nonnull JTextComponent component) {
                return true;
            }
        }).enterText(fetchID);

        findDialog(matcher).withTimeout(10_000).using(robot()).button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(@Nonnull JButton jButton) {
                return "Generate".equals(jButton.getText());
            }
        }).click();

        pause(new Condition("entrySize") {
            @Override
            public boolean test() {
                return entryTable.rowCount() == 1;
            }
        }, 10_000);

        entryTable.requireRowCount(1);
    }

}
