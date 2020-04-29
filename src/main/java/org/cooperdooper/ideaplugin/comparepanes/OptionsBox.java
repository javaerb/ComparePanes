/*
 *  $File: //depot/src/release/Tools/ComparePanesPlugin/src/com/liquidnet/ideaplugin/comparepanes/OptionsBox.java $ $Revision: #2 $ $DateTime: 2004/08/05 15:48:41 $
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
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class OptionsBox extends Box {

    private JCheckBox ignoreWhitespaceChoice;

    public OptionsBox() {
        super(BoxLayout.Y_AXIS);
        ignoreWhitespaceChoice = new JCheckBox("Ignore whitespace");
        add(ignoreWhitespaceChoice);
        add(new JLabel("NOTE: line ending differences are always ignored,", SwingConstants.LEADING));
        add(new JLabel("including omitted final line ending.", SwingConstants.LEADING));
    }

    public JCheckBox getIgnoreWhitespaceChoice() {
        return ignoreWhitespaceChoice;
    }
}