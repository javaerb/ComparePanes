package org.cooperdooper.ideaplugin.comparepanes;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class CompareAction extends AnAction {

    private static App _app;

    static void setApp(App app) {
        _app = app;
    }

    public void update(AnActionEvent e) {
        Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
        Proj proj = project.getComponent(Proj.class);
        Editor current = proj.getCurrentEditor();
        Editor previous = proj.getPreviousEditor();
        e.getPresentation().setEnabled(previous != null && current != null);
    }

    public void actionPerformed(AnActionEvent e) {
        if (_app.isAsk()) {
            Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
            boolean ok = new OptionsDialog(project, _app).showDialog();
            if (ok == false) {
                return;
            }
        }
        boolean ignoreWhitespace = _app.isIgnoreWhitespace();
        Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
        Proj proj = (Proj)project.getComponent(Proj.class);
        Editor currentEditor = proj.getCurrentEditor();
        Editor previousEditor = proj.getPreviousEditor();
        if (previousEditor != null && currentEditor != null) {
            int currentPos = currentEditor.getSelectionModel().getSelectionStart();
            int previousPos = previousEditor.getSelectionModel().getSelectionStart();
            BufferedReader currentReader = getContentReader(currentEditor, currentPos);
            BufferedReader previousReader = getContentReader(previousEditor, previousPos);

            LogicalPosition currentLP0 = currentEditor.offsetToLogicalPosition(currentPos);
            LogicalPosition previousLP0 = previousEditor.offsetToLogicalPosition(previousPos);
            LineState currentEditorLine = new LineState(currentReader, currentLP0);
            LineState previousEditorLine = new LineState(previousReader, previousLP0);
            while (true) {

                if (currentEditorLine.currentLine == null && previousEditorLine.currentLine == null) {
                    break;
                }

                if (currentEditorLine.currentLine != null) {
                    if (ignoreWhitespace && currentEditorLine.currentLine.length() == 0) {
                        // trailing blank lines
                        currentEditorLine.nextLine();
                        continue;
                    }
                    else if (previousEditorLine.currentLine == null) {
                        break;
                    }
                }
                if (previousEditorLine.currentLine != null) {
                    if (ignoreWhitespace && previousEditorLine.currentLine.length() == 0) {
                        // trailing blank lines
                        previousEditorLine.nextLine();
                    }
                    else if (currentEditorLine.currentLine == null) {
                        break;
                    }
                }
                else {
                    break;
                }

                // If we get here, neither editor is done yet, and both lines are non-null.

                // Short-circuit.
                if (currentEditorLine.currentLine.equals(previousEditorLine.currentLine)) {
                    currentEditorLine.nextLine();
                    previousEditorLine.nextLine();
                    continue;
                }
                int currentLen = currentEditorLine.currentLine.length();
                int previousLen = previousEditorLine.currentLine.length();
                int currentIndex = currentEditorLine.currentPosition.column;
                int previousIndex = previousEditorLine.currentPosition.column;

                while(currentIndex < currentLen || previousIndex < previousLen) {

                    char cc = 0; // Make compiler happy.
                    char cp;

                    if (currentIndex < currentLen) {
                        cc = currentEditorLine.currentLine.charAt(currentIndex);
                        if (ignoreWhitespace && Character.isWhitespace(cc)) {
                            currentIndex++;
                            continue;
                        }
                        else if (previousIndex >= previousLen) {
                            break;
                        }
                    }
                    if (previousIndex < previousLen) {
                        cp = previousEditorLine.currentLine.charAt(previousIndex);
                        if (ignoreWhitespace && Character.isWhitespace(cp)) {
                            previousIndex++;
                            continue;
                        }
                        else if (currentIndex >= currentLen) {
                            break;
                        }
                    }
                    else {
                        break;
                    }

                    // If we get here, neither line is done yet, and both cc and cp are set.
                    if (cp != cc) {
                        break;
                    }
                    else {
                        previousIndex++;
                        currentIndex++;
                    }
                }

                // Lines are only the same if both reached end.
                boolean diff = false;
                if (currentIndex < currentLen) {
                    currentEditorLine.moveTo(currentIndex);
                    diff = true;
                }
                else {
                    currentEditorLine.nextLine();
                }
                if (previousIndex < previousLen) {
                    previousEditorLine.moveTo(previousIndex);
                    diff = true;
                }
                else {
                    previousEditorLine.nextLine();
                }
                if (diff) {
                    break;
                }
            }

            currentPos = currentEditor.logicalPositionToOffset(currentEditorLine.currentPosition);
            previousPos = previousEditor.logicalPositionToOffset(previousEditorLine.currentPosition);
            if (previousPos == previousEditor.getDocument().getTextLength() && currentPos == currentEditor.getDocument().getTextLength()) {
                Messages.showMessageDialog(project, "No differences found from selections to end.", "No Differences", Messages.getInformationIcon());
            }
            else {
                moveTo(previousEditor, previousPos);
                moveTo(currentEditor, currentPos);
            }
        }
    }

    private static BufferedReader getContentReader(Editor currentEditor, int currentPos) {
        Document doc = currentEditor.getDocument();
        String selectionToEnd = doc.getText().substring(currentPos);
        return new BufferedReader(new StringReader(selectionToEnd));
    }

    private void moveTo(Editor editor, int pos) {
        int len = editor.getDocument().getTextLength();
        if (pos < len) {
            editor.getSelectionModel().setSelection(pos, pos + 1);
            editor.getCaretModel().moveToOffset(pos + 1);
        }
        else {
            editor.getSelectionModel().setSelection(pos, pos);
            editor.getCaretModel().moveToOffset(pos);
        }
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
    }

    private static class LineState {
        private BufferedReader reader;
        public String currentLine;
        public LogicalPosition currentPosition;

        public LineState(BufferedReader reader, LogicalPosition currentPosition) {
            this.reader = reader;
            this.currentPosition = currentPosition;
            read();
        }

        public void nextLine() {
            currentPosition = new LogicalPosition(currentPosition.line + 1, 0);
            read();
        }

        private void read() {
            try {
                currentLine = reader.readLine();
            }
            catch(IOException x) {
                // Never happens
                currentLine = null;
            }
        }

        public void moveTo(int position) {
            currentPosition = new LogicalPosition(currentPosition.line, position);

        }
    }
}
