/*
 * $File: //depot/src/release/Tools/ComparePanesPlugin/src/com/liquidnet/ideaplugin/comparepanes/App.java $ $Revision: #3 $ $DateTime: 2004/08/05 15:48:41 $
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.*;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.IconLoader;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.DataConversionException;

public class App implements ApplicationComponent, Configurable, JDOMExternalizable {

    private static final String ASK_ATTRIBUTE_NAME = "askForOptions";
    private static final String IGNORE_WHITE_ATTRIBUTE_NAME = "ignoreWhitespace";

    private JRadioButton _askChoice;
    private JRadioButton _rememberChoice;
    private JCheckBox _ignoreWhitespaceChoice;
    private ButtonGroup _group;

    private boolean _ask = true;
    private boolean _ignoreWhitespace = false;

    private boolean _modified;
    private final ActionListener _modifyListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            boolean newAsk = _askChoice.isSelected();
            boolean newIgnoreWhitespace = _ignoreWhitespaceChoice.isSelected();
            _modified = (newAsk != _ask || newIgnoreWhitespace != _ignoreWhitespace);
        }
    };
    private Icon ICON;

    public String getComponentName() {
        return "Liquidnet.ComparePanes.App";
    }

    public void initComponent() {
        CompareAction.setApp(this);
        ImageIcon smallIcon = (ImageIcon)IconLoader.getIcon("/actions/diff.png");
        Image image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(smallIcon.getImage(), 0, 0, 32, 32, null);
        ICON = new ImageIcon(image);
    }

    public void disposeComponent() {
    }

    public String getDisplayName() {
        return "Compare Panes";
    }

    public Icon getIcon() {
        return ICON;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        _group = new ButtonGroup();
        _askChoice = new JRadioButton("Ask for options every time");
        _group.add(_askChoice);
        _askChoice.addActionListener(_modifyListener);
        _askChoice.setAlignmentX(0f);
        _askChoice.setHorizontalAlignment(SwingConstants.LEADING);
        panel.add(_askChoice);
        _rememberChoice = new JRadioButton("Use these options:");
        _group.add(_rememberChoice);
        _rememberChoice.addActionListener(_modifyListener);
        _rememberChoice.setAlignmentX(0f);
        _rememberChoice.setHorizontalAlignment(SwingConstants.LEADING);
        panel.add(_rememberChoice);

        final OptionsBox optionsSubpanel = new OptionsBox();
        optionsSubpanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        _ignoreWhitespaceChoice = optionsSubpanel.getIgnoreWhitespaceChoice();
        _ignoreWhitespaceChoice.addActionListener(_modifyListener);

        setPanelEnabled(optionsSubpanel, false);
        _rememberChoice.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
                setPanelEnabled(optionsSubpanel, selected);
            }
        });

        optionsSubpanel.setAlignmentX(0f);
        panel.add(optionsSubpanel);
        JPanel outer = new JPanel(null);
        outer.add(panel);
        panel.setSize(panel.getPreferredSize());
        return outer;
    }

    private void setPanelEnabled(Box optionsSubpanel, boolean selected) {
        int numComponents = optionsSubpanel.getComponentCount();
        for(int i = 0; i < numComponents; i++) {
            optionsSubpanel.getComponent(i).setEnabled(selected);
        }
    }

    public boolean isModified() {
        return _modified;
    }

    public void apply() throws ConfigurationException {
        _ask = _askChoice.isSelected();
        _ignoreWhitespace = _ignoreWhitespaceChoice.isSelected();
        _modified = false;
    }

    public void reset() {
        _group.setSelected(_ask ? _askChoice.getModel() : _rememberChoice.getModel(), true);
        _ignoreWhitespaceChoice.setSelected(_ignoreWhitespace);
        _modified = false;
    }

    public void disposeUIResources() {
        _askChoice = null;
        _rememberChoice = null;
        _group = null;
        _ignoreWhitespaceChoice = null;
    }

    boolean isAsk() {
        return _ask;
    }

    void setAsk(boolean ask) {
        _ask = ask;
    }

    boolean isIgnoreWhitespace() {
        return _ignoreWhitespace;
    }

    void setIgnoreWhitespace(boolean ignoreWhitespace) {
        _ignoreWhitespace = ignoreWhitespace;
    }

    public void readExternal(Element element) throws InvalidDataException {

        try {
            Attribute askAttribute = element.getAttribute(ASK_ATTRIBUTE_NAME);
            if (askAttribute != null) {
                _ask = (askAttribute.getBooleanValue());
            }
            Attribute whiteAttribute = element.getAttribute(IGNORE_WHITE_ATTRIBUTE_NAME);
            if (whiteAttribute != null) {
                _ignoreWhitespace = (whiteAttribute.getBooleanValue());
            }
        }
        catch(DataConversionException x) {
            throw new InvalidDataException(x.getMessage());
        }
    }

    public void writeExternal(Element element) throws WriteExternalException {

        element.removeChildren(null);
        element.setAttribute(ASK_ATTRIBUTE_NAME, String.valueOf(_ask));
        // Save this in either case, for default of dialog.
        element.setAttribute(IGNORE_WHITE_ATTRIBUTE_NAME, String.valueOf(_ignoreWhitespace));
    }
}
