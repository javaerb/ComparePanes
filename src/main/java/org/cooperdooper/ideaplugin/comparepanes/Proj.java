/*
 * $File: //depot/src/release/Tools/ComparePanesPlugin/src/com/liquidnet/ideaplugin/comparepanes/Proj.java $ $Revision: #5 $ $DateTime: 2004/01/29 12:49:34 $
 *
 * Compare Panes plugin for IDEA: compare 2 panes from caret to end.
 *
 * Copyright (c) 2003, Liquidnet Holdings, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   -> Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *   -> Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *   -> Neither the name of Liquidnet Holdings, Inc. nor the names of its
 *      contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.cooperdooper.ideaplugin.comparepanes;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;

public class Proj implements ProjectComponent, EditorFactoryListener {

    private Editor _previousEditor;
    private Editor _currentEditor;

    public String getComponentName() {
        return "Liquidnet.ComparePanes.Proj";
    }

    public void initComponent() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        editorFactory.addEditorFactoryListener(this); // Each project does this separately.
    }

    public void disposeComponent() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        editorFactory.removeEditorFactoryListener(this); // Each project does this separately.
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    public void editorCreated(EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        _previousEditor = _currentEditor;
        _currentEditor = editor;
        EditorFocusListener listener = new EditorFocusListener(editor);
        editor.getContentComponent().addFocusListener(listener);
    }

    public void editorReleased(EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        if (editor == _currentEditor) {
            _currentEditor = _previousEditor;
        }
        // In either case, do this, because either previous is now current,
        // or previous just went away.
        _previousEditor = null;
    }

    public Editor getCurrentEditor() {
        return _currentEditor;
    }

    public Editor getPreviousEditor() {
        return _previousEditor;
    }

    private class EditorFocusListener implements FocusListener {

        private Editor _editor;

        public EditorFocusListener(Editor editor) {
            _editor = editor;
        }

        public void focusGained(FocusEvent e) {
            if (_editor != _currentEditor) {
                _previousEditor = _currentEditor;
                _currentEditor = _editor;
            }
        }

        public void focusLost(FocusEvent e) {}
    }
}