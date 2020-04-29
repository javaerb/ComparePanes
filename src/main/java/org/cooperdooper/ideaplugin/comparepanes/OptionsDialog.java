/*
 *  $File: //depot/src/release/Tools/ComparePanesPlugin/src/com/liquidnet/ideaplugin/comparepanes/OptionsDialog.java $ $Revision: #2 $ $DateTime: 2004/08/05 15:48:41 $
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

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JSeparator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

public class OptionsDialog extends DialogWrapper {

    private App _app;
    private OptionsBox _box;
    private JCheckBox _remember;

    public OptionsDialog(Project project, App app) {
        super(project, false);
        _app = app;
        setTitle("Compare Panes Options");
        init();
    }

    protected JComponent createCenterPanel() {
        Box center = Box.createVerticalBox();
        _box = new OptionsBox();
        _box.getIgnoreWhitespaceChoice().setSelected(_app.isIgnoreWhitespace());
        center.add(_box);
        center.add(new JSeparator(JSeparator.HORIZONTAL));
        _remember = new JCheckBox("Don't ask again");
        center.add(_remember);
        return center;
    }

    boolean showDialog() {
        show();
        boolean ok = getExitCode() == OK_EXIT_CODE;
        if (ok) {
            _app.setIgnoreWhitespace(_box.getIgnoreWhitespaceChoice().isSelected());
            // Let's only do this if they say OK.
            _app.setAsk(_remember.isSelected() == false);
        }
        dispose();
        return ok;
    }
}